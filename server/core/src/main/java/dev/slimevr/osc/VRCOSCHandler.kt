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
import java.net.NetworkInterface
import kotlin.collections.iterator

private const val OFFSET_SLERP_FACTOR = 0.5f // Guessed from eyeing VRChat

/**
 * VRChat OSCTracker documentation: https://docs.vrchat.com/docs/osc-trackers
 */
class VRCOSCHandler(
	private val server: VRServer,
	private val config: VRCOSCConfig,
	private val computedTrackers: List<Tracker>,
) : OSCHandler {
	private val vrsystemTrackersAddresses = arrayOf(
		"/tracking/vrsystem/head/pose",
		"/tracking/vrsystem/leftwrist/pose",
		"/tracking/vrsystem/rightwrist/pose",
	)
	private val oscTrackersAddresses = arrayOf(
		"/tracking/trackers/*/position",
		"/tracking/trackers/*/rotation",
	)
	private var oscReceiver: OSCPortIn? = null
	private var oscSender: OSCPortOut? = null
	private var oscQuerySender: OSCPortOut? = null
	private var oscMessage: OSCMessage? = null
	private var headTracker: Tracker? = null
	private var oscTrackersDevice: Device? = null
	private var vrsystemTrackersDevice: Device? = null
	private val oscArgs = FastList<Float?>(3)
	private val trackersEnabled: BooleanArray = BooleanArray(computedTrackers.size)
	private var oscPortIn = 0
	private var oscPortOut = 0
	private var oscIp: InetAddress? = null
	private var oscQueryPortOut = 0
	private var oscQueryIp: InetAddress? = null
	private var oscQueryIpMatch = false
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
			if (computedTrackers[i].trackerPosition != TrackerPosition.HEAD || computedTrackers[i].trackerPosition != TrackerPosition.LEFT_HAND || computedTrackers[i].trackerPosition != TrackerPosition.RIGHT_HAND) {
				trackersEnabled[i] = config
					.getOSCTrackerRole(
						computedTrackers[i].trackerPosition!!.trackerRole!!,
						false,
					)
			} else {
				trackersEnabled[i] = false
			}
		}

		updateOscReceiver(config.portIn, vrsystemTrackersAddresses + oscTrackersAddresses)
		updateOscSender(config.portOut, config.address)

		if (config.enabled && config.oscqueryEnabled) {
			if (vrcOscQueryHandler == null) {
				try {
					vrcOscQueryHandler = VRCOSCQueryHandler(server, this)
				} catch (e: Throwable) {
					LogManager.severe("Unable to initialize OSCQuery: $e", e)
				}
			}
		} else {
			vrcOscQueryHandler?.close()
			vrcOscQueryHandler = null
		}

		if (refreshRouterSettings) {
			server.oSCRouter.refreshSettings(false)
		}
	}

	fun ipIsLocal(a: InetAddress): Boolean {
		if (a.isLoopbackAddress) {
			return true
		}
		for (netInt in NetworkInterface.getNetworkInterfaces()) {
			if (netInt.isUp && !netInt.isLoopback && !netInt.isVirtual) {
				for (netAddr in netInt.interfaceAddresses) {
					if (a == netAddr.address || a.address.contentEquals(netAddr.address.address) || a.hostName == netAddr.address.hostName) {
						return true
					}
				}
			}
		}
		return false
	}

	fun ipEquals(a: InetAddress?, b: InetAddress?): Boolean = a == b ||
		a != null &&
		b != null &&
		(
			a.address.contentEquals(b.address) ||
				a.hostName == b.hostName ||
				(ipIsLocal(a) && ipIsLocal(b))
			)

	/**
	 * Adds an OSC Sender from OSCQuery
	 */
	fun addOSCQuerySender(oscPortOut: Int, oscIP: String) {
		if (!config.enabled) {
			closeOscQuerySender()
			return
		}

		try {
			// If we already have the best matching client
			if (oscQuerySender != null && oscQueryIpMatch) {
				return
			}

			val addr = InetAddress.getByName(oscIP)
			val ipMatch = ipEquals(addr, this.oscIp)

			// If we already have an OSC sender
			if (oscQuerySender != null) {
				val portMatch = oscPortOut == this.oscPortOut
				// Original IP will be matching because of the check done earlier
				val origPortMatch = oscQueryPortOut == this.oscPortOut

				// If the IP doesn't match and (the port doesn't match or the original
				//  port did match), ignore this client
				if (!ipMatch && (!portMatch || origPortMatch)) {
					return
				}

				// If this is the same address as what we're already sending to, keep
				//  using the same OSC sender
				if (ipEquals(addr, oscQueryIp)) {
					return
				}

				// So if the IP matches or the port matches and the original one didn't,
				//  we will select this new address for OSC
			}

			closeOscQuerySender()

			if (ipMatch) {
				LogManager.info("[VRCOSCHandler] OSCQuery sender sending to port $oscPortOut at address $oscIP (matches configured address)")
			} else {
				LogManager.info("[VRCOSCHandler] OSCQuery sender sending to port $oscPortOut at address $oscIP")
			}
			oscQuerySender = OSCPortOut(InetSocketAddress(addr, oscPortOut))
			oscQueryIp = addr
			oscQueryIpMatch = ipMatch
			oscQueryPortOut = oscPortOut

			// Avoids additional security checks, but not necessary for UDP, therefore
			//  isConnected doesn't really indicate that we are actively "connected"
			oscQuerySender?.connect()
		} catch (e: IOException) {
			LogManager.severe("[VRCOSCHandler] Error connecting OSCQuery sender to port $oscPortOut at the address $oscIP: $e")
			closeOscQuerySender()
		}
	}

	/**
	 * Close/remove the OSC sender
	 */
	fun closeOscSender() {
		try {
			oscSender?.close()
			oscSender = null
		} catch (e: IOException) {
			LogManager.severe("[VRCOSCHandler] Error closing the OSC sender: $e")
		}
	}

	/**
	 * Close/remove the OSCQuery sender
	 */
	fun closeOscQuerySender() {
		try {
			oscQuerySender?.close()
			oscQuerySender = null
		} catch (e: IOException) {
			LogManager.severe("[VRCOSCHandler] Error closing the OSCQuery sender: $e")
		}
	}

	/**
	 * Close/remove the OSC receiver
	 */
	fun closeOscReceiver() {
		try {
			oscReceiver?.close()
			oscReceiver = null
		} catch (e: IOException) {
			LogManager.severe("[VRCOSCHandler] Error closing the OSC receiver: $e")
		}
	}

	override fun updateOscReceiver(portIn: Int, oscAddresses: Array<String>) {
		if (!config.enabled) {
			closeOscReceiver()
			return
		}

		// If already configured and listening, nothing new needs to be configured
		if (oscPortIn == portIn && oscReceiver?.isListening == true) return

		try {
			closeOscReceiver()

			// Instantiate the new OSC receiver
			LogManager.info("[VRCOSCHandler] Listening to port $portIn")
			val newOscReceiver = OSCPortIn(portIn)
			oscReceiver = newOscReceiver
			oscPortIn = portIn

			val listener = OSCMessageListener { event: OSCMessageEvent ->
				handleReceivedMessage(event)
			}
			for (address in oscAddresses) {
				newOscReceiver.dispatcher.addListener(
					OSCPatternAddressMessageSelector(address),
					listener,
				)
			}

			newOscReceiver.startListening()

			// Advertise our new receiving port over OSCQuery
			vrcOscQueryHandler?.updateOSCQuery(portIn.toUShort())
		} catch (e: IOException) {
			LogManager.severe("[VRCOSCHandler] Error listening to the port $portIn: $e")
			closeOscReceiver()
		}
	}

	override fun updateOscSender(portOut: Int, ip: String) {
		if (!config.enabled) {
			closeOscSender()
			return
		}

		try {
			// If already configured, nothing new needs to be configured
			val addr = InetAddress.getByName(ip)
			val resetQuery = if (ipEquals(addr, oscIp) && oscPortOut == portOut) {
				// Technically we are fine if `isConnected` is false, but we can just
				//  assume we're always gonna be connected
				if (oscSender?.isConnected == true) {
					return
				}
				false
			} else {
				// If the new IP doesn't match the current OSCQuery IP, close the
				//  OSCQuery sender after updating the config variables
				!ipEquals(addr, oscQueryIp)
			}

			closeOscSender()

			LogManager.info("[VRCOSCHandler] Sending to port $portOut at address $ip")
			val newOscSender = OSCPortOut(InetSocketAddress(addr, portOut))
			oscSender = newOscSender
			oscIp = addr
			oscPortOut = portOut

			// Avoids additional security checks, but not necessary for UDP, therefore
			//  isConnected doesn't really indicate that we are actively "connected"
			newOscSender.connect()

			if (resetQuery) {
				closeOscQuerySender()
			}
		} catch (e: IOException) {
			LogManager
				.severe(
					"[VRCOSCHandler] Error connecting to port $portOut at the address $ip: $e",
				)
			closeOscSender()
			return
		}
	}

	private fun handleReceivedMessage(event: OSCMessageEvent) {
		// TODO: Track the IP who sent this, we can list them as a send target and
		//  resolve the VRChat IP/port without scanning OSCQuery
		if (vrsystemTrackersAddresses.contains(event.message.address)) {
			// Receiving Head and Wrist pose data thanks to OSCQuery
			// Create device if it doesn't exist
			if (vrsystemTrackersDevice == null) {
				// Instantiate OSC Trackers device
				vrsystemTrackersDevice = server.deviceManager.createDevice("VRC VRSystem", null, "VRChat")
				server.deviceManager.addDevice(vrsystemTrackersDevice!!)
			}

			// Look at xxx in "/tracking/vrsystem/xxx/pose" to know TrackerPosition
			var name = "VRChat "
			val trackerPosition = when (event.message.address.split('/')[3]) {
				"head" -> {
					name += "head"
					TrackerPosition.HEAD
				}

				"leftwrist" -> {
					name += "left hand"
					TrackerPosition.LEFT_HAND
				}

				"rightwrist" -> {
					name += "right hand"
					TrackerPosition.RIGHT_HAND
				}

				else -> {
					LogManager.warning("[VRCOSCHandler] Received invalid body part in message \"${event.message.address}\"")
					return
				}
			}

			// Try to get the tracker
			var tracker = vrsystemTrackersDevice!!.trackers[trackerPosition.ordinal]

			// Build the tracker if it doesn't exist
			if (tracker == null) {
				tracker = Tracker(
					device = vrsystemTrackersDevice,
					id = VRServer.getNextLocalTrackerId(),
					name = name,
					displayName = name,
					trackerNum = trackerPosition.ordinal,
					trackerPosition = trackerPosition,
					hasRotation = true,
					hasPosition = true,
					userEditable = true,
					isComputed = true,
					allowReset = trackerPosition != TrackerPosition.HEAD,
					usesTimeout = true,
				)
				vrsystemTrackersDevice!!.trackers[trackerPosition.ordinal] = tracker
				server.registerTracker(tracker)
			}

			// Sets the tracker status to OK
			tracker.status = TrackerStatus.OK

			// Update tracker position
			tracker.position = Vector3(
				event.message.arguments[0] as Float,
				event.message.arguments[1] as Float,
				-(event.message.arguments[2] as Float),
			)

			// Update tracker rotation
			val (w, x, y, z) = EulerAngles(
				EulerOrder.YXZ,
				event.message.arguments[3] as Float * FastMath.DEG_TO_RAD,
				event.message.arguments[4] as Float * FastMath.DEG_TO_RAD,
				event.message.arguments[5] as Float * FastMath.DEG_TO_RAD,
			).toQuaternion()
			val rot = Quaternion(w, -x, -y, z)
			tracker.setRotation(rot)

			tracker.dataTick()
		} else {
			// Receiving OSC Trackers data. This is not from VRChat.
			if (oscTrackersDevice == null) {
				// Instantiate OSC Trackers device
				oscTrackersDevice = server.deviceManager.createDevice("OSC Tracker", null, "OSC Trackers")
				server.deviceManager.addDevice(oscTrackersDevice!!)
			}

			// Extract the xxx in "/tracking/trackers/xxx/..."
			val splitAddress = event.message.address.split('/')
			val trackerStringValue = splitAddress[3]
			val dataType = event.message.address.split('/')[4]
			if (trackerStringValue == "head") {
				// Head data
				if (dataType == "position") {
					// Position offset
					receivingPositionOffset = Vector3(
						event.message.arguments[0] as Float,
						event.message.arguments[1] as Float,
						-(event.message.arguments[2] as Float),
					)

					headTracker?.let {
						if (it.hasPosition) {
							postReceivingPositionOffset = it.position
						}
					}
				} else {
					// Rotation offset
					val (w, x, y, z) = EulerAngles(EulerOrder.YXZ, event.message.arguments[0] as Float * FastMath.DEG_TO_RAD, event.message.arguments[1] as Float * FastMath.DEG_TO_RAD, event.message.arguments[2] as Float * FastMath.DEG_TO_RAD).toQuaternion()
					receivingRotationOffsetGoal = Quaternion(w, -x, -y, z).inv()

					headTracker.let {
						receivingRotationOffsetGoal = if (it != null && it.hasRotation) {
							it.getRotation().project(Vector3.POS_Y).unit() * receivingRotationOffsetGoal
						} else {
							receivingRotationOffsetGoal
						}
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
				val trackerId = trackerStringValue.toInt()
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
						allowReset = true,
						usesTimeout = true,
					)
					oscTrackersDevice!!.trackers[trackerId] = tracker
					server.registerTracker(tracker)
				}

				// Sets the tracker status to OK
				tracker.status = TrackerStatus.OK

				if (dataType == "position") {
					// Update tracker position
					tracker.position = receivingRotationOffset.sandwich(
						Vector3(
							event.message.arguments[0] as Float,
							event.message.arguments[1] as Float,
							-(event.message.arguments[2] as Float),
						) -
							receivingPositionOffset,
					) +
						postReceivingPositionOffset
				} else {
					// Update tracker rotation
					val (w, x, y, z) = EulerAngles(
						EulerOrder.YXZ,
						event.message.arguments[0] as Float * FastMath.DEG_TO_RAD,
						event.message.arguments[1] as Float * FastMath.DEG_TO_RAD,
						event.message.arguments[2] as Float * FastMath.DEG_TO_RAD,
					).toQuaternion()
					val rot = Quaternion(w, -x, -y, z)
					tracker.setRotation(receivingRotationOffset * rot * postReceivingOffset)
				}

				tracker.dataTick()
			}
		}
	}

	override fun update() {
		if (!config.enabled) {
			return
		}

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
		if (oscSender != null || oscQuerySender != null) {
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
							oscArgs.clone(),
						),
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
						z1,
					).toEulerAngles(EulerOrder.YXZ)
					oscArgs.clear()
					oscArgs.add(x2 * FastMath.RAD_TO_DEG)
					oscArgs.add(y2 * FastMath.RAD_TO_DEG)
					oscArgs.add(z2 * FastMath.RAD_TO_DEG)
					bundle.addPacket(
						OSCMessage(
							"/tracking/trackers/${getVRCOSCTrackersId(computedTrackers[i].trackerPosition)}/rotation",
							oscArgs.clone(),
						),
					)
				}
				if (computedTrackers[i].trackerPosition == TrackerPosition.HEAD) {
					// Send HMD position
					val (x, y, z) = computedTrackers[i].position
					oscArgs.clear()
					oscArgs.add(x)
					oscArgs.add(y)
					oscArgs.add(-z)
					bundle.addPacket(
						OSCMessage(
							"/tracking/trackers/head/position",
							oscArgs.clone(),
						),
					)
				}
			}

			try {
				// Prioritize OSCQuery since we can't validate oscSender
				if (oscQuerySender != null) {
					oscQuerySender?.send(bundle)
				} else {
					oscSender?.send(bundle)
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
		if (oscSender != null || oscQuerySender != null) {
			val (_, _, y, _) = headRot.toEulerAngles(EulerOrder.YXZ)
			oscArgs.clear()
			oscArgs.add(0f)
			oscArgs.add(-y * FastMath.RAD_TO_DEG)
			oscArgs.add(0f)
			oscMessage = OSCMessage(
				"/tracking/trackers/head/rotation",
				oscArgs,
			)
			try {
				// Prioritize OSCQuery since we can't validate oscSender
				if (oscQuerySender != null) {
					oscQuerySender?.send(oscMessage)
				} else {
					oscSender?.send(oscMessage)
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

	override fun getOscSender(): OSCPortOut = oscSender!!

	override fun getPortOut(): Int = oscPortOut

	override fun getAddress(): InetAddress = oscIp!!

	override fun getOscReceiver(): OSCPortIn = oscReceiver!!

	override fun getPortIn(): Int = oscPortIn
}
