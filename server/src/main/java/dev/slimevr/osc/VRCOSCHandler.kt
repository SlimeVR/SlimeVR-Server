package dev.slimevr.osc

import com.illposed.osc.MessageSelector
import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.OSCMessageListener
import com.illposed.osc.OSCSerializeException
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector
import com.illposed.osc.transport.OSCPortIn
import com.illposed.osc.transport.OSCPortOut
import com.jme3.math.FastMath
import dev.slimevr.VRServer
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.platform.SteamVRBridge
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.io.IOException
import java.net.InetAddress
import java.util.*

/**
 * VRChat OSCTracker documentation: https://docs.vrchat.com/docs/osc-trackers
 */
class VRCOSCHandler(
	private val server: VRServer,
	private val humanPoseManager: HumanPoseManager,
	private val steamvrBridge: SteamVRBridge?,
	private val config: VRCOSCConfig,
	private val computedTrackers: List<Tracker>,
) : OSCHandler {
	private var oscReceiver: OSCPortIn? = null
	private var oscSender: OSCPortOut? = null
	private var oscMessage: OSCMessage? = null
	private lateinit var vrcHmd: Tracker
	private val oscArgs = FastList<Float?>(3)
	private val trackersEnabled: BooleanArray = BooleanArray(computedTrackers.size)
	private var lastPortIn = 0
	private var lastPortOut = 0
	private var lastAddress: InetAddress? = null
	private var timeAtLastError: Long = 0
	private var receivingInitialized = false
	private val timer = Timer()

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

		// Stops listening and closes OSC port
		val wasListening = oscReceiver != null && oscReceiver!!.isListening
		if (wasListening) {
			oscReceiver!!.stopListening()
		}
		val wasConnected = oscSender != null && oscSender!!.isConnected
		if (wasConnected) {
			try {
				oscSender!!.close()
			} catch (e: IOException) {
				LogManager.severe("[VRCOSCHandler] Error closing the OSC sender: $e")
			}
		}
		if (config.enabled) {
			// Instantiates the OSC receiver
			try {
				val port = config.portIn
				oscReceiver = OSCPortIn(
					port
				)
				if (lastPortIn != port || !wasListening) {
					LogManager.info("[VRCOSCHandler] Listening to port $port")
				}
				lastPortIn = port
			} catch (e: IOException) {
				LogManager
					.severe(
						"[VRCOSCHandler] Error listening to the port " +
							config.portIn +
							": " +
							e
					)
			}

			// Starts listening for the Upright parameter from VRC
			if (oscReceiver != null) {
				val listener = OSCMessageListener { event: OSCMessageEvent -> handleReceivedMessage(event) }
				val selector: MessageSelector = OSCPatternAddressMessageSelector(
					"/avatar/parameters/Upright"
				)
				oscReceiver!!.dispatcher.addListener(selector, listener)
				// Delay so we can actually detect if SteamVR is running
				scheduleStartListening(1000)
			}

			// Instantiate the OSC sender
			try {
				val address = InetAddress.getByName(config.address)
				val port = config.portOut
				oscSender = OSCPortOut(
					address,
					port
				)
				if (lastPortOut != port && lastAddress !== address || !wasConnected) {
					LogManager
						.info(
							"[VRCOSCHandler] Sending to port " +
								port +
								" at address " +
								address.toString()
						)
				}
				lastPortOut = port
				lastAddress = address
				oscSender!!.connect()
			} catch (e: IOException) {
				LogManager
					.severe(
						"[VRCOSCHandler] Error connecting to port " +
							config.portOut +
							" at the address " +
							config.address +
							": " +
							e
					)
			}
		}
		if (refreshRouterSettings && server.oscRouter != null) server.oscRouter.refreshSettings(false)
	}

	private fun scheduleStartListening(delay: Long) {
		val resetTask: TimerTask = object : TimerTask() {
			override fun run() {
				oscReceiver!!.startListening()
			}
		}
		timer.schedule(resetTask, delay)
	}

	private fun handleReceivedMessage(event: OSCMessageEvent) {
		if (steamvrBridge != null && !steamvrBridge.isConnected) {
			if (!receivingInitialized) {
				val vrcDevice = server.deviceManager.createDevice("VRChat OSC", null, "VRChat")
				server.deviceManager.addDevice(vrcDevice)
				vrcHmd = Tracker(
					device = vrcDevice,
					id = VRServer.getNextLocalTrackerId(),
					name = "VRC HMD",
					displayName = "VRC HMD",
					trackerPosition = TrackerPosition.HEAD,
					trackerNum = 0,
					hasPosition = true,
					userEditable = false,
					isComputed = true,
					usesTimeout = true
				)
				vrcDevice.trackers[0] = vrcHmd
				server.registerTracker(vrcHmd)

				receivingInitialized = true
			}

			// Sets HMD status to OK
			vrcHmd.status = TrackerStatus.OK

			// Sets the HMD y position to
			// the vrc Upright parameter (0-1) * the user's height
			vrcHmd
				.position = Vector3(
				0f,
				event
					.message
					.arguments[0] as Float * humanPoseManager.userHeightFromConfig,
				0f
			)
			vrcHmd.dataTick()
		}
	}

	override fun update() {
		val currentTime = System.currentTimeMillis().toFloat()

		// Send OSC data
		if (oscSender != null && oscSender!!.isConnected) {
			var id = 0
			for (i in computedTrackers.indices) {
				if (trackersEnabled[i]) {
					id++
					// Send regular trackers' positions
					val (x, y, z) = computedTrackers[i].position
					oscArgs.clear()
					oscArgs.add(x)
					oscArgs.add(y)
					oscArgs.add(-z)
					oscMessage = OSCMessage(
						"/tracking/trackers/$id/position",
						oscArgs
					)
					try {
						oscSender!!.send(oscMessage)
					} catch (e: IOException) {
						// Avoid spamming AsynchronousCloseException too many
						// times per second
						if (currentTime - timeAtLastError > 100) {
							timeAtLastError = System.currentTimeMillis()
							LogManager
								.warning(
									"[VRCOSCHandler] Error sending OSC message to VRChat: " +
										e
								)
						}
					} catch (e: OSCSerializeException) {
						if (currentTime - timeAtLastError > 100) {
							timeAtLastError = System.currentTimeMillis()
							LogManager
								.warning(
									"[VRCOSCHandler] Error sending OSC message to VRChat: " +
										e
								)
						}
					}

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
					oscMessage = OSCMessage(
						"/tracking/trackers/$id/rotation",
						oscArgs
					)
					try {
						oscSender!!.send(oscMessage)
					} catch (_: IOException) {
						// Don't do anything.
						// Previous code already logs the exception.
					} catch (_: OSCSerializeException) {
					}
				}
				if (computedTrackers[i].trackerPosition === TrackerPosition.HEAD) {
					// Send HMD position
					val (x, y, z) = computedTrackers[i].position
					oscArgs.clear()
					oscArgs.add(x)
					oscArgs.add(y)
					oscArgs.add(-z)
					oscMessage = OSCMessage(
						"/tracking/trackers/head/position",
						oscArgs
					)
					try {
						oscSender!!.send(oscMessage)
					} catch (_: IOException) {
						// Don't do anything.
						// Previous code already logs the exception.
					} catch (_: OSCSerializeException) {
					}
				}
			}
		}
	}

	/**
	 * Sends the expected HMD rotation upon reset to align the trackers in VRC
	 */
	fun yawAlign() {
		if (oscSender != null && oscSender!!.isConnected) {
			for (shareableTracker in computedTrackers) {
				if (shareableTracker.trackerPosition === TrackerPosition.HEAD) {
					val (_, _, y) = shareableTracker.getRotation().toEulerAngles(EulerOrder.XYZ)
					oscArgs.clear()
					oscArgs.add(0f)
					oscArgs.add(-y * FastMath.RAD_TO_DEG)
					oscArgs.add(0f)
					oscMessage = OSCMessage(
						"/tracking/trackers/head/rotation",
						oscArgs
					)
					try {
						oscSender!!.send(oscMessage)
					} catch (e: IOException) {
						LogManager
							.warning("[VRCOSCHandler] Error sending OSC message to VRChat: $e")
					} catch (e: OSCSerializeException) {
						LogManager
							.warning("[VRCOSCHandler] Error sending OSC message to VRChat: $e")
					}
				}
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
