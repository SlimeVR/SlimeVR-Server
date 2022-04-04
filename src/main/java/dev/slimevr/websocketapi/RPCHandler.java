package dev.slimevr.websocketapi;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.logging.LogManager;
import org.java_websocket.WebSocket;
import slimevr_protocol.InboundPacket;
import slimevr_protocol.InboundUnion;
import slimevr_protocol.OutboundUnion;
import slimevr_protocol.rpc.*;

public class RPCHandler {

	private final ProtocolAPI api;

	public RPCHandler(ProtocolAPI api) {
		this.api = api;

		api.registerPacketListener(InboundUnion.slimevr_protocol_rpc_ResetRequest, this::onResetRequest);
		api.registerPacketListener(InboundUnion.slimevr_protocol_rpc_AssignTrackerRequest, this::onAssignTrackerRequest);
		api.registerPacketListener(InboundUnion.slimevr_protocol_rpc_SettingsRequest, this::onSettingsRequest);
		api.registerPacketListener(InboundUnion.slimevr_protocol_rpc_ChangeSettingsRequest, this::onChangeSettingsRequest);
	}

	public void onResetRequest(GenericConnection conn, InboundPacket inboundPacket) {
		ResetRequest req = (ResetRequest) inboundPacket.packet(new ResetRequest());
		if (req == null) return;

		if (req.quick()) {
			this.api.server.resetTrackersYaw();
		} else {
			this.api.server.resetTrackers();
		}
		LogManager.log.severe("[WebSocketAPI] Reset performed");
	}

	public void onAssignTrackerRequest(GenericConnection conn, InboundPacket inboundPacket) {
		AssignTrackerRequest req = (AssignTrackerRequest) inboundPacket.packet(new AssignTrackerRequest());
		if (req == null) return;

		Tracker tracker = this.api.server.getAllTrackers().get(req.id());
		if (tracker == null)
			return ;

		tracker.setBodyPosition(TrackerPosition.getById(req.bodyPosition()));
		if (tracker instanceof IMUTracker) {
			IMUTracker imu = (IMUTracker) tracker;
			TrackerMountingRotation rot = TrackerMountingRotation.fromAngle(req.mountingRotation());
			if (rot != null)
				imu.setMountingRotation(rot);
		}
		this.api.server.trackerUpdated(tracker);
	}

	public void onSettingsRequest(GenericConnection conn, InboundPacket inboundPacket) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		WindowsNamedPipeBridge bridge = this.api.server.getVRBridge(WindowsNamedPipeBridge.class);

		int steamvrTrackerSettings = SteamVRTrackersSetting.createSteamVRTrackersSetting(fbb,
				bridge.getShareSetting(TrackerRole.WAIST),
				bridge.getShareSetting(TrackerRole.CHEST),
				bridge.getShareSetting(TrackerRole.LEFT_FOOT) && bridge.getShareSetting(TrackerRole.RIGHT_FOOT),
				bridge.getShareSetting(TrackerRole.LEFT_KNEE) &&bridge.getShareSetting(TrackerRole.RIGHT_KNEE),
				bridge.getShareSetting(TrackerRole.LEFT_ELBOW) && bridge.getShareSetting(TrackerRole.RIGHT_ELBOW)
		);

		int filterSettings = FilteringSettings.createFilteringSettings(
				fbb,
				TrackerFilters.valueOf(this.api.server.config.getString("filters.type", "NONE")).id,
				(int)(this.api.server.config.getFloat("filters.amount", 0.3f) * 100),
				this.api.server.config.getInt("filters.tickCount", 2)
		);

		int settings = SettingsResponse.createSettingsResponse(fbb, steamvrTrackerSettings, filterSettings);
		int outbound = this.api.createOutboundPacket(fbb, OutboundUnion.slimevr_protocol_rpc_SettingsResponse, settings);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onChangeSettingsRequest(GenericConnection conn, InboundPacket inboundPacket) {

		ChangeSettingsRequest req = (ChangeSettingsRequest) inboundPacket.packet(new ChangeSettingsRequest());
		if (req == null) return;

		if (req.steamVrTrackers() != null) {
			WindowsNamedPipeBridge bridge = this.api.server.getVRBridge(WindowsNamedPipeBridge.class);
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
				this.api.server.updateTrackersFilters(type, (float)req.filtering().intensity() / 100.0f, req.filtering().ticks());
			}
		}
	}
}
