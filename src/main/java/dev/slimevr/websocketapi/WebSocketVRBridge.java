package dev.slimevr.websocketapi;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.Bridge;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class WebSocketVRBridge extends WebsocketAPI implements Bridge {

	private final Vector3f vBuffer = new Vector3f();
	private final Quaternion qBuffer = new Quaternion();

	private final HMDTracker hmd;
	private final List<? extends ShareableTracker> shareTrackers;
	private final List<ComputedTracker> internalTrackers;

	private final HMDTracker internalHMDTracker = new HMDTracker("internal://HMD");
	private final AtomicBoolean newHMDData = new AtomicBoolean(false);

	public WebSocketVRBridge(
		HMDTracker hmd,
		List<? extends ShareableTracker> shareTrackers,
		VRServer server
	) {
		super(server, server.getProtocolAPI());
		this.hmd = hmd;
		this.shareTrackers = new FastList<>(shareTrackers);
		this.internalTrackers = new FastList<>(shareTrackers.size());
		for (Tracker t : shareTrackers) {
			ComputedTracker ct = new ComputedTracker(
				t.getTrackerId(),
				"internal://" + t.getName(),
				true,
				true
			);
			ct.setStatus(TrackerStatus.OK);
			ct.bodyPosition = t.getBodyPosition();
			this.internalTrackers.add(ct);
		}
	}

	@Override
	public void dataRead() {
		if (newHMDData.compareAndSet(true, false)) {
			hmd.position.set(internalHMDTracker.position);
			hmd.rotation.set(internalHMDTracker.rotation);
			hmd.dataTick();
		}
	}

	@Override
	public void dataWrite() {
		for (int i = 0; i < shareTrackers.size(); ++i) {
			Tracker t = shareTrackers.get(i);
			ComputedTracker it = this.internalTrackers.get(i);
			if (t.getPosition(vBuffer))
				it.position.set(vBuffer);
			if (t.getRotation(qBuffer))
				it.rotation.set(qBuffer);
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		super.onOpen(conn, handshake);
		// Register trackers
		for (int i = 0; i < internalTrackers.size(); ++i) {
			JSONObject message = new JSONObject();
			message.put("type", "config");
			message.put("tracker_id", "SlimeVR Tracker " + (i + 1));
			message.put("location", shareTrackers.get(i).getTrackerRole().name().toLowerCase());
			message.put("tracker_type", message.optString("location"));
			conn.send(message.toString());
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		// LogManager.info(message);
		try {
			JSONObject json = new JSONObject(message);
			if (json.has("type")) {
				switch (json.optString("type")) {
					case "pos":
						parsePosition(json, conn);
						return;
					case "action":
						parseAction(json, conn);
						return;
					case "config": // TODO Ignore it for now, it should only
									// register HMD in our test case with id
						// 0
						LogManager.info("[WebSocket] Config received: " + json);
						return;
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

	private void parsePosition(JSONObject json, WebSocket conn) throws JSONException {
		if (json.optInt("tracker_id") == 0) {
			// Read HMD information
			internalHMDTracker.position
				.set(
					(float) json.optDouble("x"),
					(float) json.optDouble("y") + 0.2f,
					(float) json.optDouble("z")
				); // TODO Wtf is this hack? VRWorkout issue?
			internalHMDTracker.rotation
				.set(
					(float) json.optDouble("qx"),
					(float) json.optDouble("qy"),
					(float) json.optDouble("qz"),
					(float) json.optDouble("qw")
				);
			internalHMDTracker.dataTick();
			newHMDData.set(true);

			// Send tracker info in reply
			for (int i = 0; i < internalTrackers.size(); ++i) {
				JSONObject message = new JSONObject();
				message.put("type", "pos");
				message.put("src", "full");
				message.put("tracker_id", "SlimeVR Tracker " + (i + 1));

				ComputedTracker t = internalTrackers.get(i);
				message.put("x", t.position.x);
				message.put("y", t.position.y);
				message.put("z", t.position.z);
				message.put("qx", t.rotation.getX());
				message.put("qy", t.rotation.getY());
				message.put("qz", t.rotation.getZ());
				message.put("qw", t.rotation.getW());

				conn.send(message.toString());
			}
		}
	}

	private void parseAction(JSONObject json, WebSocket conn) throws JSONException {
		switch (json.optString("name")) {
			case "calibrate":
				Main.vrServer.resetTrackersYaw();
				break;
			case "full_calibrate":
				Main.vrServer.resetTrackers();
				break;
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		LogManager
			.severe(
				"[WebSocket] Exception on connection "
					+ (conn != null
						? conn
							.getRemoteSocketAddress()
							.getAddress()
							.getHostAddress()
						: null),
				ex
			);
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
	public void addSharedTracker(ShareableTracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSharedTracker(ShareableTracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startBridge() {
		start();
	}
}
