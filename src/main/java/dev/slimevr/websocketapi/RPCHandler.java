package dev.slimevr.websocketapi;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.logging.LogManager;
import slimevr_protocol.MessageBundle;
import slimevr_protocol.datatypes.TransactionId;
import slimevr_protocol.rpc.*;

import java.util.function.BiConsumer;

public class RPCHandler extends ProtocolHandler<RpcMessageHeader> {

	private final ProtocolAPI api;

	private long currTransactionId = 0;

	public RPCHandler(ProtocolAPI api) {
		super();
		this.api = api;

		registerPacketListener(RpcMessage.ResetRequest, this::onResetRequest);
		registerPacketListener(RpcMessage.AssignTrackerRequest, this::onAssignTrackerRequest);
		registerPacketListener(RpcMessage.SettingsRequest, this::onSettingsRequest);
		registerPacketListener(RpcMessage.ChangeSettingsRequest, this::onChangeSettingsRequest);
	}

	public void onResetRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		ResetRequest req = (ResetRequest) messageHeader.message(new ResetRequest());
		if (req == null) return;

		if (req.resetType() == ResetType.Quick)
			this.api.server.resetTrackersYaw();
		if (req.resetType() == ResetType.Full)
			this.api.server.resetTrackers();
		LogManager.log.severe("[WebSocketAPI] Reset performed");
	}

	public void onAssignTrackerRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		AssignTrackerRequest req = (AssignTrackerRequest) messageHeader.message(new AssignTrackerRequest());
		if (req == null) return;

		Tracker tracker = this.api.server.getAllTrackers().get(req.trackerId().trackerNum());
		if (tracker == null)
			return ;

		tracker.setBodyPosition(TrackerPosition.getById(req.bodyPosition()));
		if (tracker instanceof IMUTracker) {
			IMUTracker imu = (IMUTracker) tracker;
//			TrackerMountingRotation rot = TrackerMountingRotation.fromAngle(req.mountingRotation());
//			if (rot != null)
//				imu.setMountingRotation(rot);
		}
		this.api.server.trackerUpdated(tracker);
	}

	public void onSettingsRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
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
		int outbound = createRPCMessage(fbb, RpcMessage.SettingsResponse, settings);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onChangeSettingsRequest(GenericConnection conn, RpcMessageHeader messageHeader) {

		ChangeSettingsRequest req = (ChangeSettingsRequest) messageHeader.message(new ChangeSettingsRequest());
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


	@Override
	public void onMessage(GenericConnection conn, RpcMessageHeader message) {
		BiConsumer<GenericConnection, RpcMessageHeader> consumer = this.handlers[message.messageType()];
		if (consumer != null)
			consumer.accept(conn, message);
		else
			LogManager.log.info("[ProtocolAPI] Unhandled RPC packet received id: " + message.messageType());
	}

	public int createRPCMessage(FlatBufferBuilder fbb, byte messageType, int messageOffset) {
		int [] data = new int[1];

		int txId = TransactionId.createTransactionId(fbb, currTransactionId++);

		RpcMessageHeader.startRpcMessageHeader(fbb);
		RpcMessageHeader.addMessage(fbb, messageOffset);
		RpcMessageHeader.addMessageType(fbb, messageType);
		RpcMessageHeader.addTxId(fbb, txId);
		data[0] = RpcMessageHeader.endRpcMessageHeader(fbb);

		int messages = MessageBundle.createRpcMsgsVector(fbb, data);
		return createMessage(fbb, -1, messages);
	}

	@Override
	public int messagesCount() {
		return RpcMessage.names.length;
	}
}
