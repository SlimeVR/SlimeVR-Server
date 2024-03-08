package dev.slimevr.osc

import com.illposed.osc.OSCBundle
import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.OSCMessageListener
import com.illposed.osc.OSCSerializeException
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector
import com.illposed.osc.transport.OSCPortIn
import com.illposed.osc.transport.OSCPortOut
import com.jme3.math.FastMath
import com.jme3.system.NanoTimer
import dev.slimevr.VRServer
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress

private const val OFFSET_SLERP_FACTOR = 0.5f // Guessed from eyeing VRChat

/**
 * VRChat OSCTracker documentation: https://docs.vrchat.com/docs/osc-trackers
 */
class VRCOSCHandler(
	private val server: VRServer,
	private val humanPoseManager: HumanPoseManager,
	private val config: VRCOSCConfig,
	private val computedTrackers: List<Tracker>,
) : OSCHandler {
	private val vrsystemTrackersAddresses = arrayOf(
		"/tracking/vrsystem/head/pose",
		"/tracking/vrsystem/leftwrist/pose",
		"/tracking/vrsystem/rightwrist/pose"
	)
	private val oscTrackersAddresses = arrayOf(
		"/tracking/trackers/*/position",
		"/tracking/trackers/*/rotation"
	)
	private val uprightAddress = "/avatar/parameters/Upright"
	private var oscReceiver: OSCPortIn? = null
	private var oscSender: OSCPortOut? = null
	private var addtionalOscSenders = FastList<OSCPortOut>()
	private var oscMessage: OSCMessage? = null
	private var vrcHmd: Tracker? = null
	private var headTracker: Tracker? = null
	private var oscTrackersDevice: Device? = null
	private val oscArgs = FastList<Float?>(3)
	private val trackersEnabled: BooleanArray = BooleanArray(computedTrackers.size)
	private var lastPortIn = 0
	private var lastPortOut = 0
	private var lastAddress: InetAddress? = null
	private var timeAtLastError: Long = 0
	private var receivingPositionOffset = Vector3.NULL
	private var postReceivingPositionOffset = Vector3.NULL
	private var receivingRotationOffset = Quaternion.IDENTITY
	private var receivingRotationOffsetGoal = Quaternion.IDENTITY
	private val postReceivingOffset = EulerAngles(EulerOrder.YXZ, 0f, FastMath.PI, 0f).toQuaternion()
	private var timeAtLastReceivedRotationOffset = System.currentTimeMillis()
	private var fpsTimer: NanoTimer? = null
	private var vrcOscQueryHandler: VRCOSCQueryHandler? = null

	init {
		refreshSettings(false)
	}

	override fun refreshSettings(refreshRouterSettings: Boolean) {
		// Sets which trackers are enabled and force head and hands to false
		for (i in computedTrackers.indices) {
			if (computedTrackers[i].trackerPosition !== TrackerPosition.HEAD || computedTrackers[i].trackerPosition !== TrackerPosition.LEFT_HAND || computedTrackers[i].trackerPosition !== TrackerPosition.RIGHT_HAND) {
				trackersEnabled[i] = config
					.getOSCTrackerRole(
						computedTrackers[i].trackerPosition!!.trackerRole!!,
						false
					)
			} else {
				trackersEnabled[i] = false
			}
		}

		// Close OSCQuery and its related senders
		vrcOscQueryHandler?.close()

		updateOscReceiver(config.portIn, vrsystemTrackersAddresses + oscTrackersAddresses + uprightAddress)
		updateOscSender(config.portOut, config.address)

		vrcOscQueryHandler = if (config.enabled) {
			// New OSCQuery
			VRCOSCQueryHandler(this)
		} else {
			// Disable OSCQuery
			null
		}

		if (refreshRouterSettings) {
			server.oSCRouter.refreshSettings(false)
		}
	}

	/**
	 * Adds an OSC Sender from OSCQuery
	 */
	fun addOSCSender(oscPortOut: Int, oscIP: String) {
		if (config.enabled) {
			// Instantiate the OSC sender
			try {
				val addr = InetAddress.getByName(oscIP)
				val newSender = OSCPortOut(InetSocketAddress(addr, oscPortOut))
				LogManager.info("[VRCOSCHandler] New sender sending to port $oscPortOut at address $oscIP")
				newSender.connect()
				addtionalOscSenders.add(newSender)
			} catch (e: IOException) {
				LogManager
					.severe(
						"[VRCOSCHandler] Error connecting to port $portOut at the address $address: $e"
					)
			}
		}
	}

	/**
	 * Removes OSCQuery senders
	 */
	fun removeAdditionalOscSenders() {
		if (addtionalOscSenders.isNotEmpty()) LogManager.debug("[VRCOSCHandler] Removing additional OSC Senders.")

		for (sender in addtionalOscSenders) {
			try {
				sender.close()
			} catch (e: IOException) {
				LogManager.severe("[VRCOSCHandler] Error closing the OSC sender: $e")
			}
		}
		addtionalOscSenders.clear()
	}

	override fun updateOscReceiver(portIn: Int, args: Array<String>) {
		// Stop listening
		val wasListening = oscReceiver != null && oscReceiver!!.isListening
		if (wasListening) {
			oscReceiver!!.stopListening()
		}

		if (config.enabled) {
			// Instantiates the OSC receiver
			try {
				oscReceiver = OSCPortIn(portIn)
				if (lastPortIn != portIn || !wasListening) {
					LogManager.info("[VRCOSCHandler] Listening to port $portIn")
				}
				lastPortIn = portIn
			} catch (e: IOException) {
				LogManager
					.severe(
						"[VRCOSCHandler] Error listening to the port $portIn: $e"
					)
			}

			// Starts listening for VRC or OSCTrackers messages
			oscReceiver?.let {
				val listener = OSCMessageListener { event: OSCMessageEvent ->
					handleReceivedMessage(event)
				}
				for (address in args) {
					it.dispatcher.addListener(
						OSCPatternAddressMessageSelector(address),
						listener
					)
				}
				it.startListening()
			}
		}
	}

	override fun updateOscSender(portOut: Int, address: String) {
		// Stop sending
		val wasConnected = oscSender != null && oscSender!!.isConnected
		if (wasConnected) {
			try {
				oscSender!!.close()
			} catch (e: IOException) {
				LogManager.severe("[VRCOSCHandler] Error closing the OSC sender: $e")
			}
		}

		if (config.enabled) {
			// Instantiate the OSC sender
			try {
				val addr = InetAddress.getByName(address)
				oscSender = OSCPortOut(InetSocketAddress(addr, portOut))
				if (lastPortOut != portOut && lastAddress !== addr || !wasConnected) {
					LogManager.info("[VRCOSCHandler] Sending to port $portOut at address $address")
				}
				lastPortOut = portOut
				lastAddress = addr
				oscSender!!.connect()
			} catch (e: IOException) {
				LogManager
					.severe(
						"[VRCOSCHandler] Error connecting to port $portOut at the address $address: $e"
					)
			}
		}
	}

	private fun handleReceivedMessage(event: OSCMessageEvent) {
		if (event.message.address.equals(uprightAddress)) {
			// LEGACY
			// Receiving HMD y pos from VRChat
			if (vrcHmd == null) {
				val vrcDevice = server.deviceManager.createDevice("VRChat OSC", null, "VRChat")
				server.deviceManager.addDevice(vrcDevice)
				vrcHmd = Tracker(
					device = vrcDevice,
					id = VRServer.getNextLocalTrackerId(),
					name = "VRC Upright",
					displayName = "VRC Upright",
					trackerPosition = null, // TrackerPosition.HEAD,
					trackerNum = 0,
					hasPosition = true,
					userEditable = true,
					isComputed = true,
					usesTimeout = true
				)
				vrcDevice.trackers[0] = vrcHmd!!
				server.registerTracker(vrcHmd!!)
			}

			// Sets tracker status to OK
			vrcHmd!!.status = TrackerStatus.OK

			// Sets the tracker y position to
			// the vrc Upright parameter (0-1) * the user's height
			vrcHmd!!
				.position = Vector3(
				0f,
				event
					.message
					.arguments[0] as Float * humanPoseManager.userHeightFromConfig,
				0f
			)
			vrcHmd!!.dataTick()
		} else if (vrsystemTrackersAddresses.contains(event.message.address)) {
			// Receiving Head and Wrist pose data thanks to OSCQuery
			LogManager.debug("Received VRSystem data: " + event.message.address)
			// TODO
		} else {
			// Receiving OSC Trackers data. This is not receiving from VRChat.
			// This cannot be replaced by OSCQuery
			if (oscTrackersDevice == null) {
				// Instantiate OSC Trackers device
				oscTrackersDevice = server.deviceManager.createDevice("OSC Tracker", null, "OSC Trackers")
				server.deviceManager.addDevice(oscTrackersDevice!!)
			}

			// Extract the x in "/tracking/trackers/x.../..."
			val trackerStringValue = event.message.address.toString().subSequence(19, 20)
			if (trackerStringValue == "h") {
				// Head data
				val slimeHead = headTracker
				if (event.message.address.toString() == "/tracking/trackers/head/position") {
					// Position offset
					receivingPositionOffset = Vector3(
						event.message.arguments[0] as Float,
						event.message.arguments[1] as Float,
						-(event.message.arguments[2] as Float)
					)

					if (slimeHead != null && slimeHead.hasPosition) {
						postReceivingPositionOffset = slimeHead.position
					}
				} else {
					// Rotation offset
					val (w, x, y, z) = EulerAngles(EulerOrder.YXZ, event.message.arguments[0] as Float * FastMath.DEG_TO_RAD, event.message.arguments[1] as Float * FastMath.DEG_TO_RAD, event.message.arguments[2] as Float * FastMath.DEG_TO_RAD).toQuaternion()
					receivingRotationOffsetGoal = Quaternion(w, -x, -y, z).inv()

					receivingRotationOffsetGoal = if (slimeHead != null && slimeHead.hasRotation) {
						slimeHead.getRotation().project(Vector3.POS_Y).unit() * receivingRotationOffsetGoal
					} else {
						receivingRotationOffsetGoal
					}

					// If greater than 300ms, snap to rotation
					if (System.currentTimeMillis() - timeAtLastReceivedRotationOffset > 300) {
						receivingRotationOffset = receivingRotationOffsetGoal
					}

					// Update time variable
					timeAtLastReceivedRotationOffset = System.currentTimeMillis()
				}
			} else {
				// Trackers data (1-8)
				val trackerId = trackerStringValue[0].digitToInt()
				var tracker = oscTrackersDevice!!.trackers[trackerId]

				if (tracker == null) {
					tracker = Tracker(
						device = oscTrackersDevice,
						id = VRServer.getNextLocalTrackerId(),
						name = "OSC Tracker #$trackerId",
						displayName = "OSC Tracker #$trackerId",
						trackerNum = trackerId,
						trackerPosition = null,
						hasRotation = true,
						hasPosition = true,
						userEditable = true,
						isComputed = true,
						needsReset = true,
						usesTimeout = true
					)
					oscTrackersDevice!!.trackers[trackerId] = tracker
					server.registerTracker(tracker)
				}

				// Sets the tracker status to OK
				tracker.status = TrackerStatus.OK

				if (event.message.address.toString() == "/tracking/trackers/$trackerId/position") {
					tracker.position = receivingRotationOffset.sandwich(
						Vector3(
							event.message.arguments[0] as Float,
							event.message.arguments[1] as Float,
							-(event.message.arguments[2] as Float)
						) - receivingPositionOffset
					) + postReceivingPositionOffset
				} else {
					val (w, x, y, z) = EulerAngles(EulerOrder.YXZ, event.message.arguments[0] as Float * FastMath.DEG_TO_RAD, event.message.arguments[1] as Float * FastMath.DEG_TO_RAD, event.message.arguments[2] as Float * FastMath.DEG_TO_RAD).toQuaternion()
					val rot = Quaternion(w, -x, -y, z)
					tracker.setRotation(receivingRotationOffset * rot * postReceivingOffset)
				}

				tracker.dataTick()
			}
		}
	}

	override fun update() {
		// Gets timer from vrServer
		if (fpsTimer == null) {
			fpsTimer = VRServer.instance.fpsTimer
		}
		// Update received trackers' offset rotation slerp
		if (receivingRotationOffset != receivingRotationOffsetGoal) {
			receivingRotationOffset = receivingRotationOffset.interpR(receivingRotationOffsetGoal, OFFSET_SLERP_FACTOR * (fpsTimer?.timePerFrame ?: 1f))
		}

		// Update current time
		val currentTime = System.currentTimeMillis().toFloat()

		// Send OSC data
		if (oscSender != null && oscSender!!.isConnected) {
			// Create new bundle
			val bundle = OSCBundle()

			for (i in computedTrackers.indices) {
				if (trackersEnabled[i]) {
					// Send regular trackers' positions
					val (x, y, z) = computedTrackers[i].position
					oscArgs.clear()
					oscArgs.add(x)
					oscArgs.add(y)
					oscArgs.add(-z)
					bundle.addPacket(
						OSCMessage(
							"/tracking/trackers/${getVRCOSCTrackersId(computedTrackers[i].trackerPosition)}/position",
							oscArgs.clone()
						)
					)

					// Send regular trackers' rotations
					val (w, x1, y1, z1) = computedTrackers[i].getRotation()
					// We flip the X and Y components of the quaternion because
					// we flip the z direction when communicating from
					// our right-handed API to VRChat's left-handed API.
					// X quaternion represents a rotation from y to z
					// Y quaternion represents a rotation from z to x
					// When we negate the z direction, X and Y quaternion
					// components must be negated.
					val (_, x2, y2, z2) = Quaternion(
						w,
						-x1,
						-y1,
						z1
					).toEulerAngles(EulerOrder.YXZ)
					oscArgs.clear()
					oscArgs.add(x2 * FastMath.RAD_TO_DEG)
					oscArgs.add(y2 * FastMath.RAD_TO_DEG)
					oscArgs.add(z2 * FastMath.RAD_TO_DEG)
					bundle.addPacket(
						OSCMessage(
							"/tracking/trackers/${getVRCOSCTrackersId(computedTrackers[i].trackerPosition)}/rotation",
							oscArgs.clone()
						)
					)
				}
				if (computedTrackers[i].trackerPosition === TrackerPosition.HEAD) {
					// Send HMD position
					val (x, y, z) = computedTrackers[i].position
					oscArgs.clear()
					oscArgs.add(x)
					oscArgs.add(y)
					oscArgs.add(-z)
					bundle.addPacket(
						OSCMessage(
							"/tracking/trackers/head/position",
							oscArgs.clone()
						)
					)
				}
			}

			try {
				for (sender in addtionalOscSenders + oscSender) {
					sender?.send(bundle)
				}
			} catch (e: IOException) {
				// Avoid spamming AsynchronousCloseException too many
				// times per second
				if (currentTime - timeAtLastError > 100) {
					timeAtLastError = System.currentTimeMillis()
					LogManager.warning("[VRCOSCHandler] Error sending OSC message to VRChat: $e")
				}
			} catch (e: OSCSerializeException) {
				if (currentTime - timeAtLastError > 100) {
					timeAtLastError = System.currentTimeMillis()
					LogManager.warning("[VRCOSCHandler] Error sending OSC message to VRChat: $e")
				}
			}
		}
	}

	private fun getVRCOSCTrackersId(trackerPosition: TrackerPosition?): Int {
		// Needs to range from 1-8.
		// Don't change as third party applications may rely
		// on this for mapping trackers to body parts.
		return when (trackerPosition) {
			TrackerPosition.HIP -> 1
			TrackerPosition.LEFT_FOOT -> 2
			TrackerPosition.RIGHT_FOOT -> 3
			TrackerPosition.LEFT_UPPER_LEG -> 4
			TrackerPosition.RIGHT_UPPER_LEG -> 5
			TrackerPosition.UPPER_CHEST -> 6
			TrackerPosition.LEFT_UPPER_ARM -> 7
			TrackerPosition.RIGHT_UPPER_ARM -> 8
			else -> -1
		}
	}

	fun setHeadTracker(headTracker: Tracker?) {
		this.headTracker = headTracker
	}

	/**
	 * Sends the expected HMD rotation upon reset to align the trackers in VRC
	 */
	fun yawAlign(headRot: Quaternion) {
		if (oscSender != null && oscSender!!.isConnected) {
			val (_, _, y, _) = headRot.toEulerAngles(EulerOrder.YXZ)
			oscArgs.clear()
			oscArgs.add(0f)
			oscArgs.add(-y * FastMath.RAD_TO_DEG)
			oscArgs.add(0f)
			oscMessage = OSCMessage(
				"/tracking/trackers/head/rotation",
				oscArgs
			)
			try {
				for (sender in addtionalOscSenders + oscSender) {
					sender?.send(oscMessage)
				}
			} catch (e: IOException) {
				LogManager
					.warning("[VRCOSCHandler] Error sending OSC message to VRChat: $e")
			} catch (e: OSCSerializeException) {
				LogManager
					.warning("[VRCOSCHandler] Error sending OSC message to VRChat: $e")
			}
		}
	}

	override fun getOscSender(): OSCPortOut {
		return oscSender!!
	}

	override fun getPortOut(): Int {
		return lastPortOut
	}

	override fun getAddress(): InetAddress {
		return lastAddress!!
	}

	override fun getOscReceiver(): OSCPortIn {
		return oscReceiver!!
	}

	override fun getPortIn(): Int {
		return lastPortIn
	}
}
