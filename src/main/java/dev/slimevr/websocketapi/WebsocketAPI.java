package dev.slimevr.websocketapi;

import com.google.flatbuffers.FlatBufferBuilder;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.ProtobufBridge;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import dev.slimevr.vr.trackers.*;
import dev.slimevr.vr.trackers.TrackerPosition;
import dev.slimevr.vr.trackers.TrackerRole;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.logging.LogManager;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import slimevr_protocol.datatypes.Quat;
import slimevr_protocol.datatypes.Vec3f;
import slimevr_protocol.misc.Acknowledgement;
import slimevr_protocol.server.*;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;


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
				case InboundUnion.ConnectionRequest: {
					HandshakeRequest req = (HandshakeRequest) inboundPacket.packet(new HandshakeRequest());
					if (req != null) this.onHandshakeRequest(conn, req);
					break;
				}
				case InboundUnion.AssignTrackerRequest: {
					AssignTrackerRequest req = (AssignTrackerRequest) inboundPacket.packet(new AssignTrackerRequest());
					if (req != null) this.onAssignTrackerRequest(conn, req);
					break;
				}
				case InboundUnion.ResetRequest: {
					ResetRequest req = (ResetRequest) inboundPacket.packet(new ResetRequest());
					if (req != null) this.onResetRequest(conn, req);
					break;
				}
				case InboundUnion.SettingsRequest: {
					SettingsRequest req = (SettingsRequest) inboundPacket.packet(new SettingsRequest());
					if (req != null) this.onSettingsRequest(conn, req);
					break;
				}
				case InboundUnion.ChangeSettingsRequest: {
					ChangeSettingsRequest req = (ChangeSettingsRequest) inboundPacket.packet(new ChangeSettingsRequest());
					if (req != null) this.onChangeSettingsRequest(conn, req);
					break;
				}
				case InboundUnion.SkeletonConfigRequest: {
					SkeletonConfigRequest req = (SkeletonConfigRequest) inboundPacket.packet(new SkeletonConfigRequest());
					if (req != null) this.onSkeletonConfigRequest(conn, req);
					break;
				}
				case InboundUnion.ChangeSkeletonConfigRequest: {
					ChangeSkeletonConfigRequest req = (ChangeSkeletonConfigRequest) inboundPacket.packet(new ChangeSkeletonConfigRequest());
					if (req != null) this.onChangeSkeletonConfigRequest(conn, req);
					break;
				}
				default:
					LogManager.log.warning("[WebSocketAPI] Received unknown packet type: " + inboundPacket.packetType());
			}
		} catch (Exception e) {
			LogManager.log.severe("[WebSocketAPI] Failed to parse packet: " + inboundPacket.packetType(), e);
		}


		if (inboundPacket.acknowledgeMe()) {
			FlatBufferBuilder fbb = new FlatBufferBuilder(40);
			int acknowledgement = Acknowledgement.createAcknowledgement(fbb, inboundPacket.packetCounter());
			int outbound = this.createOutboundPacket(fbb, OutboundUnion.slimevr_protocol_misc_Acknowledgement, acknowledgement);
			fbb.finish(outbound);
			conn.send(fbb.dataBuffer());
		}
	}

	public void onSkeletonConfigRequest(WebSocket conn, SkeletonConfigRequest req) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(300);

		int[] partsOffsets = new int[SkeletonConfigValue.values().length];


		for (int index = 0; index < SkeletonConfigValue.values().length; index++) {
			SkeletonConfigValue val = SkeletonConfigValue.values[index];
			int part  = SkeletonPart.createSkeletonPart(fbb, val.id, this.server.humanPoseProcessor.getSkeletonConfig(val));
			partsOffsets[index] = part;
		}


		int parts = SkeletonConfigResponse.createSkeletonPartsVector(fbb, partsOffsets);
		int config = SkeletonConfigResponse.createSkeletonConfigResponse(fbb, parts);
		int outbound = this.createOutboundPacket(fbb, OutboundUnion.SkeletonConfigResponse, config);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onChangeSkeletonConfigRequest(WebSocket conn, ChangeSkeletonConfigRequest req) {
		SkeletonConfigValue joint = SkeletonConfigValue.getById(req.id());

//		float current = server.humanPoseProcessor.getSkeletonConfig(SkeletonConfigValue.getById(req.id()));
		server.humanPoseProcessor.setSkeletonConfig(joint, req.value());
		server.humanPoseProcessor.getSkeletonConfig().saveToConfig(server.config);
		server.saveConfig();
	}

	public void onSettingsRequest(WebSocket conn, SettingsRequest req) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		WindowsNamedPipeBridge bridge = this.server.getVRBridge(WindowsNamedPipeBridge.class);

		int steamvrTrackerSettings = SteamVRTrackersSetting.createSteamVRTrackersSetting(fbb,
				bridge.getShareSetting(TrackerRole.WAIST),
				bridge.getShareSetting(TrackerRole.CHEST),
				bridge.getShareSetting(TrackerRole.LEFT_FOOT) && bridge.getShareSetting(TrackerRole.RIGHT_FOOT),
				bridge.getShareSetting(TrackerRole.LEFT_KNEE) &&bridge.getShareSetting(TrackerRole.RIGHT_KNEE),
				bridge.getShareSetting(TrackerRole.LEFT_ELBOW) && bridge.getShareSetting(TrackerRole.RIGHT_ELBOW)
		);

		int filterSettings = FilteringSettings.createFilteringSettings(
				fbb,
				TrackerFilters.valueOf(this.server.config.getString("filters.type", "NONE")).id,
				(int)(server.config.getFloat("filters.amount", 0.3f) * 100),
				server.config.getInt("filters.tickCount", 2)
		);

		int settings = SettingsResponse.createSettingsResponse(fbb, steamvrTrackerSettings, filterSettings);
		int outbound = this.createOutboundPacket(fbb, OutboundUnion.SettingsResponse, settings);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onChangeSettingsRequest(WebSocket conn, ChangeSettingsRequest req) {
		if (req.steamVrTrackers() != null) {
			WindowsNamedPipeBridge bridge = this.server.getVRBridge(WindowsNamedPipeBridge.class);
			bridge.changeShareSettings(TrackerRole.WAIST, req.steamVrTrackers().waist());
			bridge.changeShareSettings(TrackerRole.CHEST, req.steamVrTrackers().chest());
			bridge.changeShareSettings(TrackerRole.LEFT_FOOT, req.steamVrTrackers().legs());
			bridge.changeShareSettings(TrackerRole.RIGHT_FOOT, req.steamVrTrackers().legs());
			bridge.changeShareSettings(TrackerRole.LEFT_KNEE, req.steamVrTrackers().knees());
			bridge.changeShareSettings(TrackerRole.RIGHT_KNEE, req.steamVrTrackers().knees());
			bridge.changeShareSettings(TrackerRole.LEFT_ELBOW, req.steamVrTrackers().elbows());
			bridge.changeShareSettings(TrackerRole.RIGHT_ELBOW, req.steamVrTrackers().elbows());
		}

		if (req.filtering() != null) {
			TrackerFilters type = TrackerFilters.fromId(req.filtering().type());
			if (type != null) {
				this.server.updateTrackersFilters(type, (float)req.filtering().intensity() / 100.0f, req.filtering().ticks());
			}
		}
	}

	public void onResetRequest(WebSocket con, ResetRequest req) {
		if (req.quick()) {
			this.server.resetTrackersYaw();
		} else {
			this.server.resetTrackers();
		}
		LogManager.log.severe("[WebSocketAPI] Reset performed");
	}

	public void onAssignTrackerRequest(WebSocket con, AssignTrackerRequest req) {
		Tracker tracker = this.server.getAllTrackers().get(req.id());
		if (tracker == null)
			return ;

		tracker.setBodyPosition(TrackerPosition.getById(req.mountingPosition()));
		if (tracker instanceof IMUTracker) {
			IMUTracker imu = (IMUTracker) tracker;
			TrackerMountingRotation rot = TrackerMountingRotation.fromAngle(req.mountingRotation());
			if (rot != null)
				imu.setMountingRotation(rot);
		}
		this.server.trackerUpdated(tracker);
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
			DeviceStatus.addEditable(fbb, tracker.userEditable());

			if (tracker.getBodyPosition() != null) {
				DeviceStatus.addMountingPosition(fbb, tracker.getBodyPosition().id);
			}

			if (tracker instanceof IMUTracker) {
				IMUTracker imu = (IMUTracker) tracker;
				if (imu.getMountingRotation() != null)
					DeviceStatus.addMountingRotation(fbb, (short)imu.getMountingRotation().angle);
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
