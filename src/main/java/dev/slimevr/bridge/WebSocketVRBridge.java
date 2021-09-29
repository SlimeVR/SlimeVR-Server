package dev.slimevr.bridge;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.Main;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;

public class WebSocketVRBridge extends WebSocketServer implements Bridge {
	
	private final Vector3f vBuffer = new Vector3f();
	private final Quaternion qBuffer = new Quaternion();
	
	private final HMDTracker hmd;
	private final List<? extends Tracker> shareTrackers;
	private final List<ComputedTracker> internalTrackers;
	
	private final HMDTracker internalHMDTracker = new HMDTracker("itnernal://HMD");
	private final AtomicBoolean newHMDData = new AtomicBoolean(false);
	
	public WebSocketVRBridge(HMDTracker hmd, List<? extends Tracker> shareTrackers, VRServer server) {
		super(new InetSocketAddress(21110), Collections.<Draft>singletonList(new Draft_6455()));
		this.hmd = hmd;
		this.shareTrackers = new FastList<>(shareTrackers);
		this.internalTrackers = new FastList<>(shareTrackers.size());
		for(int i = 0; i < shareTrackers.size(); ++i) {
			Tracker t = shareTrackers.get(i);
			ComputedTracker ct = new ComputedTracker("internal://" + t.getName(), true, true);
			ct.setStatus(TrackerStatus.OK);
			ct.bodyPosition = t.getBodyPosition();
			this.internalTrackers.add(ct);
		}
	}

	@Override
	public void dataRead() {
		if(newHMDData.compareAndSet(true, false)) {
			hmd.position.set(internalHMDTracker.position);
			hmd.rotation.set(internalHMDTracker.rotation);
			hmd.dataTick();
		}
	}

	@Override
	public void dataWrite() {
		for(int i = 0; i < shareTrackers.size(); ++i) {
			Tracker t = shareTrackers.get(i);
			ComputedTracker it = this.internalTrackers.get(i);
			if(t.getPosition(vBuffer))
				it.position.set(vBuffer);
			if(t.getRotation(qBuffer))
				it.rotation.set(qBuffer);
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		LogManager.log.info("[WebSocket] New connection from: " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
		// Register trackers
		for(int i = 0; i < internalTrackers.size(); ++i) {
			JSONObject message = new JSONObject();
			message.put("type", "config");
			message.put("tracker_id", "SlimeVR Tracker " + (i + 1));
			message.put("location", internalTrackers.get(i).bodyPosition.designation);
			message.put("tracker_type", message.optString("location"));
			conn.send(message.toString());
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		LogManager.log.info("[WebSocket] Disconnected: " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ", (" + code + ") " + reason + ". Remote: " + remote);
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		StringBuilder sb = new StringBuilder(message.limit());
		while(message.hasRemaining()) {
			sb.append((char) message.get());
		}
		onMessage(conn, sb.toString());
	}
	
	@Override
	public void onMessage(WebSocket conn, String message) {
		//LogManager.log.info(message);
		try {
			JSONObject json = new JSONObject(message);
			if(json.has("type")) {
				switch(json.optString("type")) {
				case "pos":
					parsePosition(json, conn);
					return;
				case "action":
					parseAction(json, conn);
					return;
				case "config": // TODO Ignore it for now, it should only register HMD in our test case with id 0
					LogManager.log.info("[WebSocket] Config recieved: " + json.toString());
					return;
				}
			}
			LogManager.log.warning("[WebSocket] Unrecognized message from " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message);
		} catch(Exception e) {
			LogManager.log.severe("[WebSocket] Exception parsing message from " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ". Message: " + message, e);
		}
	}

	private void parsePosition(JSONObject json, WebSocket conn) throws JSONException {
		if(json.optInt("tracker_id") == 0) {
			// Read HMD information
			internalHMDTracker.position.set(json.optFloat("x"), json.optFloat("y") + 0.2f, json.optFloat("z")); // TODO Wtf is this hack? VRWorkout issue?
			internalHMDTracker.rotation.set(json.optFloat("qx"), json.optFloat("qy"), json.optFloat("qz"), json.optFloat("qw"));
			internalHMDTracker.dataTick();
			newHMDData.set(true);
			
			// Send tracker info in reply
			for(int i = 0; i < internalTrackers.size(); ++i) {
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
		switch(json.optString("name")) {
		case "calibrate":
			Main.vrServer.resetTrackersYaw();
			break;
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		LogManager.log.severe("[WebSocket] Exception on connection " + (conn != null ? conn.getRemoteSocketAddress().getAddress().getHostAddress() : null), ex);
	}

	@Override
	public void onStart() {
		LogManager.log.info("[WebSocket] Web Socket VR Bridge started on port " + getPort());
		setConnectionLostTimeout(0);
	    setConnectionLostTimeout(1);
	}

	@Override
	public void addSharedTracker(Tracker tracker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSharedTracker(Tracker tracker) {
		// TODO Auto-generated method stub
		
	}
}
