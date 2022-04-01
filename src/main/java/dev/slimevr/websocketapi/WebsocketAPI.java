package dev.slimevr.websocketapi;

import com.google.flatbuffers.FlatBufferBuilder;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.ReferenceAdjustedTracker;
import dev.slimevr.vr.trackers.Tracker;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.logging.LogManager;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import slimevr_protocol.datatypes.Quat;
import slimevr_protocol.datatypes.Vec3f;
import slimevr_protocol.server.*;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;


public class WebsocketAPI extends WebSocketServer {

	public VRServer server;

	private final HashMap<Integer, APIApplication> applications;

	private long lastMillis = 0;

	private int outbountPacketCount = 0;

	public WebsocketAPI(VRServer server) {
		super(new InetSocketAddress(21110), Collections.<Draft>singletonList(new Draft_6455()));
		this.server = server;
		this.applications = new HashMap<>();
		System.out.println("INIT");
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		LogManager.log.info("[WebSocketAPI] New connection from: " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		LogManager.log.info("[WebSocketAPI] Disconnected: " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ", (" + code + ") " + reason + ". Remote: " + remote);
		this.applications.remove(conn.hashCode());
	}

	@Override
	public void onMessage(WebSocket conn, String message) {}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		InboundPacket inboundPacket = InboundPacket.getRootAsInboundPacket(message);

		APIApplication application = this.applications.get(conn.hashCode());

		try {
			switch (inboundPacket.packetType()) {
				case InboundUnion.HandshakeRequest: {
					HandshakeRequest req = (HandshakeRequest) inboundPacket.packet(new HandshakeRequest());
					if (req != null) this.onHandshakeRequest(conn, req);
					break;
				}
				default:
					LogManager.log.warning("[WebSocketAPI] Received unknown packet type: " + inboundPacket.packetType());
			}
		} catch (Exception e) {
			LogManager.log.severe("[WebSocketAPI] Failed to parse packet: " + inboundPacket.packetType(), e);
		}
	}


	public void onHandshakeRequest(WebSocket con, HandshakeRequest req) {
		APIApplication app = new APIApplication();
		app.applicationType = req.applicationType();
		this.applications.put(con.hashCode(), app);
	}

	@ThreadSafe
	public void updateTrackersList() {
		if (System.currentTimeMillis() - lastMillis <= 100) {
			return;
		}
		FlatBufferBuilder fbb = new FlatBufferBuilder(300);
		int[] trackersIds = new int[this.server.getTrackersCount()];

		for (int index = 0; index < this.server.getTrackersCount(); index++) {
			Tracker tracker = this.server.getAllTrackers().get(index);

			if(tracker instanceof ReferenceAdjustedTracker)
				tracker = ((ReferenceAdjustedTracker<? extends Tracker>) tracker).getTracker();

			int trackerName =  fbb.createString(tracker.getName());

			Vector3f pos3f = new Vector3f();
			Quaternion quatRot = new Quaternion();
			tracker.getPosition(pos3f);
			tracker.getRotation(quatRot);

			DeviceStatus.startDeviceStatus(fbb);
			DeviceStatus.addId(fbb, index); // or tacker id ?
			DeviceStatus.addName(fbb, trackerName);
			DeviceStatus.addPosition(fbb, Vec3f.createVec3f(fbb, pos3f.x, pos3f.y, pos3f.z));
			DeviceStatus.addRotation(fbb, Quat.createQuat(fbb, quatRot.getX(), quatRot.getY(), quatRot.getZ(), quatRot.getW()));
			DeviceStatus.addComputed(fbb, tracker.isComputed());


			if (tracker.getBodyPosition() != null && tracker.getBodyPosition().trackerRole != null) {
				DeviceStatus.addRole(fbb, tracker.getBodyPosition().trackerRole.id);

			}

			if (tracker instanceof IMUTracker) {
				IMUTracker imu = (IMUTracker) tracker;

				imu.getMountingRotation();
				// put back to 0 - 256 so we can fit it in a byte instead of float
				DeviceStatus.addBattery(fbb, (int)((imu.getBatteryLevel() / 100) * 255));
				DeviceStatus.addPing(fbb, imu.ping);
				DeviceStatus.addStatus(fbb, imu.getStatus().id + 1);
				DeviceStatus.addSignal(fbb, (short)imu.signalStrength);
				DeviceStatus.addTps(fbb, (int)Math.floor(imu.getTPS()));
			}
			trackersIds[index] = DeviceStatus.endDeviceStatus(fbb);
		}

		int trackers = TrackersList.createTrackersVector(fbb, trackersIds);

		TrackersList.startTrackersList(fbb);
		TrackersList.addTrackers(fbb, trackers);
		int list = TrackersList.endTrackersList(fbb);

		int outbound = this.createOutboundPacket(fbb, OutboundUnion.TrackersList, list);
		fbb.finish(outbound);

		ByteBuffer buf = fbb.dataBuffer();
		this.sendToApps(buf);

		lastMillis = System.currentTimeMillis();
	}


	public int createOutboundPacket(FlatBufferBuilder fbb, byte packetType, int packetOffset) {
		return OutboundPacket.createOutboundPacket(fbb, this.outbountPacketCount++, false, packetType, packetOffset);
	}


	public void sendToApps(ByteBuffer buf) {
		this.getConnections()
			.stream()
			.filter((conn) -> this.applications.containsKey(conn.hashCode()))
			.forEach((conn) -> conn.send(buf));
	}


	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		LogManager.log.info("[WebSocketAPI] Web Socket API started on port " + getPort());
		setConnectionLostTimeout(0);

		this.server.addOnTick(this::updateTrackersList);
	}
}
