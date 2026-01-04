package dev.slimevr.websocketapi

import dev.slimevr.VRServer
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.VRServer.Companion.instance
import dev.slimevr.bridge.Bridge
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

// --- Data Models for Serialization ---

@Serializable
data class OutgoingConfigMessage(
	val type: String = "config",
	@SerialName("tracker_id") val trackerId: String,
	val location: String?,
	@SerialName("tracker_type") val trackerType: String?,
)

@Serializable
data class OutgoingPositionMessage(
	val type: String = "pos",
	val src: String = "full",
	@SerialName("tracker_id") val trackerId: String,
	val x: Float,
	val y: Float,
	val z: Float,
	val qx: Float,
	val qy: Float,
	val qz: Float,
	val qw: Float,
)

@Serializable
data class IncomingActionMessage(
	val name: String,
)

// --- Bridge Implementation ---

class WebSocketVRBridge(
	computedTrackers: List<Tracker>,
	server: VRServer,
) : WebsocketAPI(server, server.protocolAPI),
	Bridge {

	private val computedTrackers: List<Tracker> = FastList(computedTrackers)
	private val internalTrackers: MutableList<Tracker> = FastList(computedTrackers.size)
	private val newHMDData = AtomicBoolean(false)

	// Global Json configuration
	private val json = Json {
		ignoreUnknownKeys = true
		encodeDefaults = true
	}

	private val internalHMDTracker = Tracker(
		null,
		0,
		"internal://HMD",
		"internal://HMD",
		TrackerPosition.HEAD,
		hasPosition = true,
		hasRotation = true,
		isInternal = true,
		isComputed = true,
	)
	private var hmdTracker: Tracker? = null

	init {
		for (t in computedTrackers) {
			val ct = Tracker(
				null,
				t.id,
				"internal://${t.name}",
				"internal://${t.name}",
				t.trackerPosition,
				hasPosition = true,
				hasRotation = true,
				userEditable = true,
				isInternal = true,
			)
			ct.status = TrackerStatus.OK
			internalTrackers.add(ct)
		}
	}

	override fun dataRead() {
		if (newHMDData.compareAndSet(true, false)) {
			if (hmdTracker == null) {
				val hmdDevice = server.deviceManager.createDevice("WebSocketVRBridge", null, null)
				hmdTracker = Tracker(
					null,
					getNextLocalTrackerId(),
					"WebSocketHMD",
					"WebSocketHMD",
					TrackerPosition.HEAD,
					hasPosition = true,
					hasRotation = true,
					userEditable = true,
					isComputed = true,
				)
				hmdTracker!!.status = TrackerStatus.OK
				hmdDevice.trackers[0] = hmdTracker!!
				server.registerTracker(hmdTracker!!)
			}

			hmdTracker!!.position = internalHMDTracker.position
			hmdTracker!!.setRotation(internalHMDTracker.getRotation())
			hmdTracker!!.dataTick()
		}
	}

	override fun dataWrite() {
		for (i in computedTrackers.indices) {
			val t = computedTrackers[i]
			val it = internalTrackers[i]
			if (t.hasPosition) it.position = t.position
			if (t.hasRotation) it.setRotation(t.getRotation())
		}
	}

	override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
		super.onOpen(conn, handshake)
		for (i in internalTrackers.indices) {
			val loc = computedTrackers[i].trackerPosition?.trackerRole?.name?.lowercase(Locale.getDefault())
			val message = OutgoingConfigMessage(
				trackerId = "SlimeVR Tracker ${i + 1}",
				location = loc,
				trackerType = loc,
			)
			conn.send(json.encodeToString(message))
		}
	}

	override fun onMessage(conn: WebSocket, message: String) {
		try {
			val root = json.parseToJsonElement(message).jsonObject
			val type = root["type"]?.jsonPrimitive?.content ?: return

			when (type) {
				"pos" -> parsePosition(root, conn)
				"action" -> parseAction(root)
				"config" -> LogManager.info("[WebSocket] Config received: $message")
				else -> LogManager.warning("[WebSocket] Unrecognized message: $message")
			}
		} catch (e: Exception) {
			LogManager.severe("[WebSocket] Exception parsing message: $message", e)
		}
	}

	private fun parsePosition(jsonObj: JsonObject, conn: WebSocket) {
		// HMD usually sends tracker_id as 0 (number)
		val trackerId = jsonObj["tracker_id"]?.jsonPrimitive?.intOrNull ?: -1

		if (trackerId == 0) {
			internalHMDTracker.position = Vector3(
				jsonObj["x"]?.jsonPrimitive?.float ?: 0f,
				(jsonObj["y"]?.jsonPrimitive?.float ?: 0f) + 0.2f,
				jsonObj["z"]?.jsonPrimitive?.float ?: 0f,
			)
			internalHMDTracker.setRotation(
				Quaternion(
					jsonObj["qw"]?.jsonPrimitive?.float ?: 1f,
					jsonObj["qx"]?.jsonPrimitive?.float ?: 0f,
					jsonObj["qy"]?.jsonPrimitive?.float ?: 0f,
					jsonObj["qz"]?.jsonPrimitive?.float ?: 0f,
				),
			)
			internalHMDTracker.dataTick()
			newHMDData.set(true)

			// Send tracker info in reply
			for (i in internalTrackers.indices) {
				val t = internalTrackers[i]
				val rot = t.getRotation()
				val reply = OutgoingPositionMessage(
					trackerId = "SlimeVR Tracker ${i + 1}",
					x = t.position.x,
					y = t.position.y,
					z = t.position.z,
					qx = rot.x,
					qy = rot.y,
					qz = rot.z,
					qw = rot.w,
				)
				conn.send(json.encodeToString(reply))
			}
		}
	}

	private fun parseAction(jsonObj: JsonObject) {
		val action = json.decodeFromJsonElement<IncomingActionMessage>(jsonObj)
		when (action.name) {
			"calibrate" -> instance.resetTrackersYaw(RESET_SOURCE_NAME)
			"full_calibrate" -> instance.resetTrackersFull(RESET_SOURCE_NAME)
			"mounting_calibrate" -> instance.resetTrackersMounting(RESET_SOURCE_NAME)
			"mounting_clear" -> instance.clearTrackersMounting(RESET_SOURCE_NAME)
			"toggle_pause_tracking" -> instance.togglePauseTracking(RESET_SOURCE_NAME)
		}
	}

	override fun onStart() {
		LogManager.info("[WebSocket] Web Socket VR Bridge started on port $port")
		connectionLostTimeout = 1
	}

	override fun addSharedTracker(tracker: Tracker?) {}
	override fun removeSharedTracker(tracker: Tracker?) {}
	override fun startBridge() = start()
	override fun isConnected(): Boolean = connections.isNotEmpty()

	companion object {
		private const val RESET_SOURCE_NAME = "WebSocketVRBridge"
	}
}
