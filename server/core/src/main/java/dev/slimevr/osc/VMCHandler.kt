package dev.slimevr.osc

import com.illposed.osc.OSCBundle
import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.OSCMessageListener
import com.illposed.osc.OSCSerializeException
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector
import com.illposed.osc.transport.OSCPortIn
import com.illposed.osc.transport.OSCPortOut
import dev.slimevr.VRServer
import dev.slimevr.VRServer.Companion.currentLocalTrackerId
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.config.VMCConfig
import dev.slimevr.osc.UnityBone.Companion.getByStringVal
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.IDENTITY
import io.github.axisangles.ktmath.Vector3
import io.github.axisangles.ktmath.Vector3.Companion.NULL
import io.github.axisangles.ktmath.Vector3.Companion.POS_Y
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 * VMC documentation: https://protocol.vmc.info/english
 *
 *
 * Notes: VMC uses local rotation from hip (unlike SlimeVR, which uses rotations
 * from head). VMC works with Unity's coordinate system, which means
 * Quaternions' z and w components and Vectors' z components need to be inverse
 */
class VMCHandler(
	private val server: VRServer,
	private val humanPoseManager: HumanPoseManager,
	private val config: VMCConfig,
) : OSCHandler {
	private var oscReceiver: OSCPortIn? = null
	private var oscSender: OSCPortOut? = null
	private val computedTrackers: MutableList<Tracker> = FastList()
	private val oscArgs = FastList<Any?>()
	private val startTime = System.currentTimeMillis()
	private val byTrackerNameTracker: MutableMap<String, Tracker> = HashMap()
	private var yawOffset = IDENTITY
	private var inputUnityArmature: UnityArmature? = null
	private var outputUnityArmature: UnityArmature? = null
	private var vrmHeight = 0f
	private var trackerDevice: Device? = null
	private var timeAtLastError: Long = 0
	private var timeAtLastSend: Long = 0
	private var anchorHip = false
	private var mirrorTracking = false
	private var lastPortIn = 0
	private var lastPortOut = 0
	private var lastAddress: InetAddress? = null

	init {
		refreshSettings(false)
	}

	override fun refreshSettings(refreshRouterSettings: Boolean) {
		anchorHip = config.anchorHip
		mirrorTracking = config.mirrorTracking

		updateOscReceiver(
			config.portIn,
			arrayOf(
				"/VMC/Ext/Bone/Pos",
				"/VMC/Ext/Hmd/Pos",
				"/VMC/Ext/Con/Pos",
				"/VMC/Ext/Tra/Pos",
				"/VMC/Ext/Root/Pos",
			),
		)
		updateOscSender(config.portOut, config.address)

		if (config.enabled) {
			// Load VRM data
			if (outputUnityArmature != null && config.vrmJson != null) {
				val vrmReader = VRMReader(config.vrmJson!!)
				for (unityBone in UnityBone.entries) {
					val node = outputUnityArmature!!.getHeadNodeOfBone(unityBone)
					if (node != null) {
						val offset = if (unityBone == UnityBone.HIPS) {
							// For the hip bone, add average of upper leg offsets (which are negative). The hip bone's offset is global.
							vrmReader.getOffsetForBone(UnityBone.HIPS) +
								((vrmReader.getOffsetForBone(UnityBone.LEFT_UPPER_LEG) + vrmReader.getOffsetForBone(UnityBone.RIGHT_UPPER_LEG)) / 2f)
						} else {
							vrmReader.getOffsetForBone(unityBone)
						}

						node.localTransform.translation = offset
					}
				}
				// Make sure to account for the upper legs because of the hip.
				vrmHeight = (
					vrmReader.getOffsetForBone(UnityBone.HIPS) +
						((vrmReader.getOffsetForBone(UnityBone.LEFT_UPPER_LEG) + vrmReader.getOffsetForBone(UnityBone.RIGHT_UPPER_LEG))) +
						vrmReader.getOffsetForBone(UnityBone.SPINE) +
						vrmReader.getOffsetForBone(UnityBone.CHEST) +
						vrmReader.getOffsetForBone(UnityBone.UPPER_CHEST) +
						vrmReader.getOffsetForBone(UnityBone.NECK) +
						vrmReader.getOffsetForBone(UnityBone.HEAD)
					).y
			}
		}

		if (refreshRouterSettings) server.oSCRouter.refreshSettings(false)
	}

	override fun updateOscReceiver(portIn: Int, args: Array<String>) {
		// Stops listening and closes OSC port
		val wasListening = oscReceiver != null && oscReceiver!!.isListening
		if (wasListening) {
			oscReceiver!!.stopListening()
		}

		if (config.enabled) {
			// Instantiates the OSC receiver
			try {
				oscReceiver = OSCPortIn(portIn)
				if (lastPortIn != portIn || !wasListening) {
					LogManager.info("[VMCHandler] Listening to port $portIn")
				}
				lastPortIn = portIn
			} catch (e: IOException) {
				LogManager
					.severe(
						"[VMCHandler] Error listening to the port $portIn: $e",
					)
			}

			// Starts listening for VMC messages
			if (oscReceiver != null) {
				val listener = OSCMessageListener { event: OSCMessageEvent -> this.handleReceivedMessage(event) }

				for (address in args) {
					oscReceiver!!
						.dispatcher
						.addListener(OSCPatternAddressMessageSelector(address), listener)
				}

				oscReceiver!!.startListening()
			}
		}
	}

	override fun updateOscSender(portOut: Int, ip: String) {
		// Stop sending
		val wasConnected = oscSender != null && oscSender!!.isConnected
		if (wasConnected) {
			try {
				oscSender!!.close()
			} catch (e: IOException) {
				LogManager.severe("[VMCHandler] Error closing the OSC sender: $e")
			}
		}

		if (config.enabled) {
			// Instantiate the OSC sender
			try {
				val addr = InetAddress.getByName(ip)
				oscSender = OSCPortOut(InetSocketAddress(addr, portOut))
				if ((lastPortOut != portOut && lastAddress != addr) || !wasConnected) {
					LogManager
						.info(
							"[VMCHandler] Sending to port $portOut at address $ip",
						)
				}
				lastPortOut = portOut
				lastAddress = addr

				oscSender!!.connect()
				outputUnityArmature = UnityArmature(false)
			} catch (e: IOException) {
				LogManager
					.severe(
						"[VMCHandler] Error connecting to port $portOut at the address $ip: $e",
					)
			}
		}
	}

	private fun handleReceivedMessage(event: OSCMessageEvent) {
		when (event.message.address) {
			// Is bone (rotation)
			"/VMC/Ext/Bone/Pos" -> {
				var trackerPosition: TrackerPosition? = null
				val bone = getByStringVal(event.message.arguments[0].toString())
				if (bone != null) trackerPosition = bone.trackerPosition

				// If received bone is part of SlimeVR's skeleton
				if (trackerPosition != null) {
					handleReceivedTracker(
						"VMC-Bone-" + event.message.arguments[0],
						trackerPosition,
						null,
						Quaternion(
							-(event.message.arguments[7] as Float),
							event.message.arguments[4] as Float,
							event.message.arguments[5] as Float,
							-(event.message.arguments[6] as Float),
						),
						true,
						getByStringVal(
							event.message.arguments[0].toString(),
						),
					)
				}
			}

			// Is tracker (position + rotation)
			"/VMC/Ext/Hmd/Pos", "/VMC/Ext/Con/Pos", "/VMC/Ext/Tra/Pos" ->
				handleReceivedTracker(
					"VMC-Tracker-" + event.message.arguments[0],
					null,
					Vector3(
						event.message.arguments[1] as Float,
						event.message.arguments[2] as Float,
						-(event.message.arguments[3] as Float),
					),
					Quaternion(
						-(event.message.arguments[7] as Float),
						event.message.arguments[4] as Float,
						event.message.arguments[5] as Float,
						-(event.message.arguments[6] as Float),
					),
					false,
					null,
				)

			// Is VMC tracking root (offsets all rotations)
			"/VMC/Ext/Root/Pos" -> {
				if (inputUnityArmature != null) {
					inputUnityArmature!!
						.setRootPose(
							Vector3(
								event.message.arguments[1] as Float,
								event.message.arguments[2] as Float,
								-(event.message.arguments[3] as Float),
							),
							Quaternion(
								-(event.message.arguments[7] as Float),
								event.message.arguments[4] as Float,
								event.message.arguments[5] as Float,
								-(event.message.arguments[6] as Float),
							),
						)
				}
			}
		}
	}

	private fun handleReceivedTracker(
		name: String,
		trackerPosition: TrackerPosition?,
		position: Vector3?,
		rotation: Quaternion,
		localRotation: Boolean,
		unityBone: UnityBone?,
	) {
		// Create device if it doesn't exist
		var rot = rotation
		if (trackerDevice == null) {
			trackerDevice = server.deviceManager.createDevice("VMC receiver", "1.0", "VMC")
			server.deviceManager.addDevice(trackerDevice!!)
		}

		// Try to get tracker
		var tracker = byTrackerNameTracker[name]

		// Create tracker if trying to get it returned null
		if (tracker == null) {
			tracker = Tracker(
				trackerDevice,
				getNextLocalTrackerId(),
				name,
				"VMC Tracker #$currentLocalTrackerId",
				trackerPosition,
				hasPosition = position != null,
				hasRotation = true,
				userEditable = true,
				isComputed = position != null,
				usesTimeout = true,
				needsReset = position != null,
			)
			trackerDevice!!.trackers[trackerDevice!!.trackers.size] = tracker
			byTrackerNameTracker[name] = tracker
			server.registerTracker(tracker)
		}
		tracker.status = TrackerStatus.OK

		// Set position
		if (position != null) {
			tracker.position = position
		}

		// Set rotation
		if (localRotation) {
			// Instantiate unityHierarchy if not done
			if (inputUnityArmature == null) inputUnityArmature = UnityArmature(true)
			inputUnityArmature!!.setLocalRotationForBone(unityBone!!, rot)
			rot = inputUnityArmature!!.getGlobalRotationForBone(unityBone)
			rot = yawOffset.times(rot)
		}
		tracker.setRotation(rot)

		tracker.dataTick()
	}

	override fun update() {
		// Update unity hierarchy
		if (inputUnityArmature != null) inputUnityArmature!!.update()

		val currentTime = System.currentTimeMillis()
		if (currentTime - timeAtLastSend > 3) { // 200hz to not crash VSF
			timeAtLastSend = currentTime
			// Send OSC data
			if (oscSender != null && oscSender!!.isConnected) {
				// Create new OSC Bundle
				val oscBundle = OSCBundle()

				// Add our relative time
				oscArgs.clear()
				oscArgs.add((System.currentTimeMillis() - startTime) / 1000f)
				oscBundle.addPacket(OSCMessage("/VMC/Ext/T", oscArgs.clone()))

				if (humanPoseManager.isSkeletonPresent) {
					// Indicate tracking is available
					oscArgs.clear()
					oscArgs.add(1)
					oscBundle.addPacket(OSCMessage("/VMC/Ext/OK", oscArgs.clone()))

					oscArgs.clear()
					oscArgs.add("root")
					addTransformToArgs(NULL, IDENTITY)
					oscBundle.addPacket(OSCMessage("/VMC/Ext/Root/Pos", oscArgs.clone()))

					for (unityBone in UnityBone.entries) {
						// Get opposite bone if tracking must be mirrored
						val boneType = (if (mirrorTracking) UnityBone.tryGetOppositeArmBone(unityBone) else unityBone).boneType

						if (boneType == null) continue

						// Get SlimeVR bone
						val bone = humanPoseManager.getBone(boneType)

						// Update unity hierarchy from bone's global rotation
						val boneRotation = if (mirrorTracking) {
							// Mirror tracking horizontally
							val rotBuf = bone.getGlobalRotation() * bone.rotationOffset.inv()
							Quaternion(rotBuf.w, rotBuf.x, -rotBuf.y, -rotBuf.z)
						} else {
							bone.getGlobalRotation() * bone.rotationOffset.inv()
						}
						outputUnityArmature?.setGlobalRotationForBone(unityBone, boneRotation)
					}

					if (!anchorHip) {
						// Anchor from head
						outputUnityArmature?.let { unityArmature ->
							// Scale the SlimeVR head position with the VRM model
							val slimevrScaledHeadPos = humanPoseManager.getBone(BoneType.HEAD).getTailPosition() *
								(vrmHeight / humanPoseManager.userHeightFromConfig)

							// Get the VRM head and hip positions
							val vrmHeadPos = unityArmature.getHeadNodeOfBone(UnityBone.HEAD)!!.parent!!.worldTransform.translation
							val vrmHipPos = unityArmature.getHeadNodeOfBone(UnityBone.HIPS)!!.worldTransform.translation

							// Calculate the new VRM hip position by subtracting the difference head-hip distance from the SlimeVR head
							val calculatedVrmHipPos = slimevrScaledHeadPos - (vrmHeadPos - vrmHipPos)

							// Set the VRM's hip position
							unityArmature.getHeadNodeOfBone(UnityBone.HIPS)?.localTransform?.translation = calculatedVrmHipPos
						}
					}

					// Update Unity skeleton
					outputUnityArmature?.update()

					// Add Unity humanoid bones transforms
					for (unityBone in UnityBone.entries) {
						// Don't send bones for which we don't have an equivalent
						// Don't send fingers if we don't have any tracker for them
						// Don't send arm bones if we're tracking from the controller
						if (unityBone.boneType != null &&
							(!UnityBone.isLeftFingerBone(unityBone) || humanPoseManager.skeleton.hasLeftFingerTracker || (mirrorTracking && humanPoseManager.skeleton.hasRightFingerTracker)) &&
							(!UnityBone.isRightFingerBone(unityBone) || humanPoseManager.skeleton.hasRightFingerTracker || (mirrorTracking && humanPoseManager.skeleton.hasLeftFingerTracker)) &&
							!(humanPoseManager.isTrackingLeftArmFromController && (UnityBone.isLeftArmBone(unityBone) || unityBone == UnityBone.LEFT_SHOULDER)) &&
							!(humanPoseManager.isTrackingRightArmFromController && (UnityBone.isRightArmBone(unityBone) || unityBone == UnityBone.RIGHT_SHOULDER))
						) {
							oscArgs.clear()
							oscArgs.add(unityBone.stringVal)
							outputUnityArmature?.let {
								addTransformToArgs(
									it.getLocalTranslationForBone(unityBone),
									it.getLocalRotationForBone(unityBone),
								)
							}

							oscBundle.addPacket(OSCMessage("/VMC/Ext/Bone/Pos", oscArgs.clone()))
						}
					}
				}

				for (tracker in computedTrackers) {
					if (!tracker.status.reset) {
						oscArgs.clear()

						val name = tracker.name
						oscArgs.add(name)

						addTransformToArgs(
							tracker.position,
							tracker.getRotation(),
						)

						var address: String
						val role = tracker.trackerPosition
						address = if (role == TrackerPosition.HEAD) {
							"/VMC/Ext/Hmd/Pos"
						} else if (role == TrackerPosition.LEFT_HAND || role == TrackerPosition.RIGHT_HAND
						) {
							"/VMC/Ext/Con/Pos"
						} else {
							"/VMC/Ext/Tra/Pos"
						}
						oscBundle
							.addPacket(
								OSCMessage(
									address,
									oscArgs.clone(),
								),
							)
					}
				}

				// Send OSC packets as bundle
				try {
					oscSender!!.send(oscBundle)
				} catch (e: IOException) {
					// Avoid spamming AsynchronousCloseException too many
					// times per second
					if (System.currentTimeMillis() - timeAtLastError > 100) {
						timeAtLastError = System.currentTimeMillis()
						LogManager
							.warning(
								"[VMCHandler] Error sending OSC packets: " +
									e,
							)
					}
				} catch (e: OSCSerializeException) {
					if (System.currentTimeMillis() - timeAtLastError > 100) {
						timeAtLastError = System.currentTimeMillis()
						LogManager
							.warning(
								"[VMCHandler] Error sending OSC packets: " +
									e,
							)
					}
				}
			}
		}
	}

	/**
	 * Set the Quaternion to shift the received VMC tracking rotations' yaw
	 *
	 * @param reference the head's rotation
	 */
	fun alignVMCTracking(reference: Quaternion) {
		yawOffset = reference.project(POS_Y).unit()
	}

	/**
	 * Add a computed tracker to the list of trackers to send.
	 *
	 * @param computedTracker the computed tracker
	 */
	fun addComputedTracker(computedTracker: Tracker) {
		computedTrackers.add(computedTracker)
	}

	private fun addTransformToArgs(pos: Vector3, rot: Quaternion) {
		oscArgs.add(pos.x)
		oscArgs.add(pos.y)
		oscArgs.add(-pos.z)
		oscArgs.add(rot.x)
		oscArgs.add(rot.y)
		oscArgs.add(-rot.z)
		oscArgs.add(-rot.w)
	}

	override fun getOscSender(): OSCPortOut = oscSender!!

	override fun getPortOut(): Int = lastPortOut

	override fun getAddress(): InetAddress = lastAddress!!

	override fun getOscReceiver(): OSCPortIn = oscReceiver!!

	override fun getPortIn(): Int = lastPortIn
}
