package dev.slimevr.websocketapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.Bridge;
import dev.slimevr.tracking.trackers.Device;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerPosition;
import dev.slimevr.tracking.trackers.TrackerStatus;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class WebSocketVRBridge extends WebsocketAPI implements Bridge {
	private static final String resetSourceName = "WebSocketVRBridge";
	private final List<Tracker> computedTrackers;
	private final List<Tracker> internalTrackers;
	private final AtomicBoolean newHMDData = new AtomicBoolean(false);
	private final ObjectMapper mapper = new ObjectMapper();
	private final Tracker internalHMDTracker = new Tracker(
		null,
		0,
		"internal://HMD",
		"internal://HMD",
		TrackerPosition.HEAD,
		null,
		true,
		true,
		false,
		false,
		true,
		true
	);
	private Tracker hmdTracker;

	public WebSocketVRBridge(
		List<Tracker> computedTrackers,
		VRServer server
	) {
		super(server, server.protocolAPI);
		this.computedTrackers = new FastList<>(computedTrackers);
		this.internalTrackers = new FastList<>(computedTrackers.size());
		for (Tracker t : computedTrackers) {
			Tracker ct = new Tracker(
				null,
				t.getId(),
				"internal://" + t.getName(),
				"internal://" + t.getName(),
				t.getTrackerPosition(),
				null,
				true,
				true,
				false,
				true,
				true
			);
			ct.setStatus(TrackerStatus.OK);
			this.internalTrackers.add(ct);
		}
	}

	@Override
	public void dataRead() {
		if (newHMDData.compareAndSet(true, false)) {
			if (hmdTracker == null) {
				// Create HMD for websocket
				Device hmdDevice = server.deviceManager
					.createDevice("WebSocketVRBridge", null, null);
				hmdTracker = new Tracker(
					null,
					VRServer.getNextLocalTrackerId(),
					"WebSocketHMD",
					"WebSocketHMD",
					TrackerPosition.HEAD,
					null,
					true,
					true,
					false,
					true,
					false,
					true
				);
				hmdDevice.getTrackers().put(0, hmdTracker);
				server.registerTracker(hmdTracker);
			}

			hmdTracker.setPosition(internalHMDTracker.getPosition());
			hmdTracker.setRotation(internalHMDTracker.getRotation());
			hmdTracker.dataTick();
		}
	}

	@Override
	public void dataWrite() {
		for (int i = 0; i < computedTrackers.size(); ++i) {
			Tracker t = computedTrackers.get(i);
			Tracker it = this.internalTrackers.get(i);
			if (t.getHasPosition())
				it.setPosition(t.getPosition());
			if (t.getHasRotation())
				it.setRotation(t.getRotation());
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		super.onOpen(conn, handshake);
		// Register trackers
		for (int i = 0; i < internalTrackers.size(); ++i) {
			ObjectNode message = mapper.getNodeFactory().objectNode();
			message.put("type", "config");
			message.put("tracker_id", "SlimeVR Tracker " + (i + 1));
			message
				.put(
					"location",
					computedTrackers
						.get(i)
						.getTrackerPosition()
						.getTrackerRole()
						.name()
						.toLowerCase()
				);
			message.put("tracker_type", message.get("location").asText());
			conn.send(message.toString());
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		// LogManager.info(message);
		try {
			ObjectNode json = mapper.getNodeFactory().objectNode();
			if (json.has("type")) {
				switch (json.get("type").asText()) {
					case "pos" -> {
						parsePosition(json, conn);
						return;
					}
					case "action" -> {
						parseAction(json, conn);
						return;
					}
					case "config" -> { // TODO Ignore it for now, it should only
						// register HMD in our test case with id 0
						LogManager.info("[WebSocket] Config received: " + json);
						return;
					}
				}
			}
			LogManager
				.warning(
					"[WebSocket] Unrecognized message from "
						+ conn.getRemoteSocketAddress().getAddress().getHostAddress()
						+ ": "
						+ message
				);
		} catch (Exception e) {
			LogManager
				.severe(
					"[WebSocket] Exception parsing message from "
						+ conn.getRemoteSocketAddress().getAddress().getHostAddress()
						+ ". Message: "
						+ message,
					e
				);
		}
	}

	private void parsePosition(ObjectNode json, WebSocket conn) {
		if (json.get("tracker_id").asInt() == 0) {
			// Read HMD information
			internalHMDTracker
				.setPosition(
					new Vector3(
						(float) json.get("x").asDouble(),
						(float) json.get("y").asDouble() + 0.2f,
						(float) json.get("z").asDouble()
					)
				); // TODO Wtf is this hack? VRWorkout issue?
			internalHMDTracker
				.setRotation(
					new Quaternion(
						(float) json.get("qw").asDouble(),
						(float) json.get("qx").asDouble(),
						(float) json.get("qy").asDouble(),
						(float) json.get("qz").asDouble()
					)
				);
			internalHMDTracker.dataTick();
			newHMDData.set(true);

			// Send tracker info in reply
			for (int i = 0; i < internalTrackers.size(); ++i) {
				ObjectNode message = mapper.getNodeFactory().objectNode();
				message.put("type", "pos");
				message.put("src", "full");
				message.put("tracker_id", "SlimeVR Tracker " + (i + 1));

				Tracker t = internalTrackers.get(i);
				message.put("x", t.getPosition().getX());
				message.put("y", t.getPosition().getY());
				message.put("z", t.getPosition().getZ());
				message.put("qx", t.getRotation().getX());
				message.put("qy", t.getRotation().getY());
				message.put("qz", t.getRotation().getZ());
				message.put("qw", t.getRotation().getW());

				conn.send(message.toString());
			}
		}
	}

	private void parseAction(ObjectNode json, WebSocket conn) {
		switch (json.get("name").asText()) {
			case "calibrate" -> VRServer.Companion.getInstance().resetTrackersYaw(resetSourceName);
			case "full_calibrate" -> VRServer.Companion
				.getInstance()
				.resetTrackersFull(resetSourceName);
		}
	}

	@Override
	public void onStart() {
		LogManager.info("[WebSocket] Web Socket VR Bridge started on port " + getPort());
		setConnectionLostTimeout(0);
		setConnectionLostTimeout(1); // This has to be removed for Android
										// (keepalive did not work for me
										// @mgschwan)
	}

	@Override
	public void addSharedTracker(Tracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSharedTracker(Tracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startBridge() {
		start();
	}

	@Override
	public boolean isConnected() {
		return super.getConnections().size() > 0;
	}
}
