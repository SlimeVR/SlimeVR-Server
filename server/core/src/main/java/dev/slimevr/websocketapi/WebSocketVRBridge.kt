package dev.slimevr.websocketapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
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
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class WebSocketVRBridge(
	computedTrackers: List<Tracker>,
	server: VRServer,
) : WebsocketAPI(server, server.protocolAPI),
	Bridge {
	private val computedTrackers: List<Tracker> = FastList(computedTrackers)
	private val internalTrackers: MutableList<Tracker> = FastList(computedTrackers.size)
	private val newHMDData = AtomicBoolean(false)
	private val mapper = ObjectMapper()
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
				// Create HMD for websocket
				val hmdDevice = server.deviceManager
					.createDevice("WebSocketVRBridge", null, null)
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
		// Register trackers
		for (i in internalTrackers.indices) {
			val message = mapper.nodeFactory.objectNode()
			message.put("type", "config")
			message.put("tracker_id", "SlimeVR Tracker " + (i + 1))
			message
				.put(
					"location",
					computedTrackers[i]
						.trackerPosition
						?.trackerRole
						?.name
						?.lowercase(Locale.getDefault()),
				)
			message.put("tracker_type", message["location"].asText())
			conn.send(message.toString())
		}
	}

	override fun onMessage(conn: WebSocket, message: String) {
		// LogManager.info(message);
		try {
			val json = mapper.readTree(message) as ObjectNode
			if (json.has("type")) {
				when (json["type"].asText()) {
					"pos" -> {
						parsePosition(json, conn)
						return
					}

					"action" -> {
						parseAction(json, conn)
						return
					}

					// TODO Ignore it for now, it should only register HMD in our test case with id 0
					"config" -> {
						LogManager.info("[WebSocket] Config received: $json")
						return
					}
				}
			}
			LogManager
				.warning(
					"[WebSocket] Unrecognized message from " +
						conn.remoteSocketAddress.address.hostAddress +
						": " +
						message,
				)
		} catch (e: Exception) {
			LogManager
				.severe(
					"[WebSocket] Exception parsing message from " +
						conn.remoteSocketAddress.address.hostAddress +
						". Message: " +
						message,
					e,
				)
		}
	}

	private fun parsePosition(json: ObjectNode, conn: WebSocket) {
		if (json["tracker_id"].asInt() == 0) {
			// Read HMD information
			internalHMDTracker
				.position = Vector3(
				json["x"].asDouble().toFloat(),
				json["y"].asDouble().toFloat() + 0.2f,
				json["z"].asDouble().toFloat(),
			)
			// TODO Wtf is this hack? VRWorkout issue?
			internalHMDTracker
				.setRotation(
					Quaternion(
						json["qw"].asDouble().toFloat(),
						json["qx"].asDouble().toFloat(),
						json["qy"].asDouble().toFloat(),
						json["qz"].asDouble().toFloat(),
					),
				)
			internalHMDTracker.dataTick()
			newHMDData.set(true)

			// Send tracker info in reply
			for (i in internalTrackers.indices) {
				val message = mapper.nodeFactory.objectNode()
				message.put("type", "pos")
				message.put("src", "full")
				message.put("tracker_id", "SlimeVR Tracker ${i + 1}")

				val t = internalTrackers[i]
				message.put("x", t.position.x)
				message.put("y", t.position.y)
				message.put("z", t.position.z)
				message.put("qx", t.getRotation().x)
				message.put("qy", t.getRotation().y)
				message.put("qz", t.getRotation().z)
				message.put("qw", t.getRotation().w)

				conn.send(message.toString())
			}
		}
	}

	private fun parseAction(json: ObjectNode, conn: WebSocket) {
		when (json["name"].asText()) {
			"calibrate" -> instance.resetTrackersYaw(RESET_SOURCE_NAME)
			"full_calibrate" -> instance.resetTrackersFull(RESET_SOURCE_NAME)
			"mounting_calibrate" -> instance.resetTrackersMounting(RESET_SOURCE_NAME)
			"mounting_clear" -> instance.clearTrackersMounting(RESET_SOURCE_NAME)
			"toggle_pause_tracking" -> instance.togglePauseTracking(RESET_SOURCE_NAME)
		}
	}

	override fun onStart() {
		LogManager.info("[WebSocket] Web Socket VR Bridge started on port $port")
		connectionLostTimeout = 0
		connectionLostTimeout = 1
		// This has to be removed for Android
		// (keepalive did not work for me @mgschwan)
	}

	override fun addSharedTracker(tracker: Tracker?) {
		// TODO Auto-generated method stub
	}

	override fun removeSharedTracker(tracker: Tracker?) {
		// TODO Auto-generated method stub
	}

	override fun startBridge() {
		start()
	}

	override fun isConnected(): Boolean = super.getConnections().isNotEmpty()

	companion object {
		private const val RESET_SOURCE_NAME = "WebSocketVRBridge"
	}
}
