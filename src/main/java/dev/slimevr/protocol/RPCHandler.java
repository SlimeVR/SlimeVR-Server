package dev.slimevr.protocol;

import com.fazecast.jSerialComm.SerialPort;
import com.google.flatbuffers.FlatBufferBuilder;
import com.jme3.math.Quaternion;
import dev.slimevr.autobone.AutoBone.Epoch;
import dev.slimevr.autobone.AutoBoneListener;
import dev.slimevr.autobone.AutoBoneProcessType;
import dev.slimevr.config.FiltersConfig;
import dev.slimevr.config.OverlayConfig;
import dev.slimevr.filtering.TrackerFilters;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.serial.SerialListener;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigOffsets;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigToggles;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValues;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerPosition;
import dev.slimevr.vr.trackers.TrackerRole;
import io.eiren.util.logging.LogManager;
import solarxr_protocol.MessageBundle;
import solarxr_protocol.datatypes.TransactionId;
import solarxr_protocol.rpc.*;
import solarxr_protocol.rpc.settings.ModelRatios;
import solarxr_protocol.rpc.settings.ModelSettings;
import solarxr_protocol.rpc.settings.ModelToggles;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;


public class RPCHandler extends ProtocolHandler<RpcMessageHeader>
	implements SerialListener, AutoBoneListener {

	private final ProtocolAPI api;

	private long currTransactionId = 0;

	public RPCHandler(ProtocolAPI api) {
		super();
		this.api = api;

		registerPacketListener(RpcMessage.ResetRequest, this::onResetRequest);
		registerPacketListener(RpcMessage.AssignTrackerRequest, this::onAssignTrackerRequest);
		registerPacketListener(RpcMessage.SettingsRequest, this::onSettingsRequest);
		registerPacketListener(RpcMessage.ChangeSettingsRequest, this::onChangeSettingsRequest);

		registerPacketListener(RpcMessage.RecordBVHRequest, this::onRecordBVHRequest);

		registerPacketListener(RpcMessage.SkeletonResetAllRequest, this::onSkeletonResetAllRequest);
		registerPacketListener(RpcMessage.SkeletonConfigRequest, this::onSkeletonConfigRequest);
		registerPacketListener(
			RpcMessage.ChangeSkeletonConfigRequest,
			this::onChangeSkeletonConfigRequest
		);

		registerPacketListener(RpcMessage.SetWifiRequest, this::onSetWifiRequest);
		registerPacketListener(
			RpcMessage.SerialTrackerRebootRequest,
			this::SerialTrackerRebootRequest
		);
		registerPacketListener(
			RpcMessage.SerialTrackerGetInfoRequest,
			this::SerialTrackerGetInfoRequest
		);
		registerPacketListener(
			RpcMessage.SerialTrackerFactoryResetRequest,
			this::SerialTrackerFactoryResetRequest
		);
		registerPacketListener(RpcMessage.OpenSerialRequest, this::onOpenSerialRequest);
		registerPacketListener(RpcMessage.CloseSerialRequest, this::onCloseSerialRequest);

		registerPacketListener(RpcMessage.AutoBoneProcessRequest, this::onAutoBoneProcessRequest);

		registerPacketListener(
			RpcMessage.OverlayDisplayModeChangeRequest,
			this::onOverlayDisplayModeChangeRequest
		);
		registerPacketListener(
			RpcMessage.OverlayDisplayModeRequest,
			this::onOverlayDisplayModeRequest
		);

		this.api.server.getSerialHandler().addListener(this);
		this.api.server.getAutoBoneHandler().addListener(this);
	}

	private void onOverlayDisplayModeRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(32);
		OverlayConfig config = this.api.server.getConfigManager().getVrConfig().getOverlay();
		int response = OverlayDisplayModeResponse
			.createOverlayDisplayModeResponse(fbb, config.isVisible(), config.isMirrored());
		int outbound = this.createRPCMessage(fbb, RpcMessage.OverlayDisplayModeResponse, response);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	private void onOverlayDisplayModeChangeRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		OverlayDisplayModeChangeRequest req = (OverlayDisplayModeChangeRequest) messageHeader
			.message(new OverlayDisplayModeChangeRequest());
		if (req == null)
			return;
		OverlayConfig config = this.api.server.getConfigManager().getVrConfig().getOverlay();
		config.setMirrored(req.isMirrored());
		config.setVisible(req.isVisible());

		this.api.server.getConfigManager().saveConfig();
	}

	public void onSetWifiRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		SetWifiRequest req = (SetWifiRequest) messageHeader.message(new SetWifiRequest());
		if (req == null)
			return;

		if (
			req.password() == null
				|| req.ssid() == null
				|| !this.api.server.getSerialHandler().isConnected()
		)
			return;
		this.api.server.getSerialHandler().setWifi(req.ssid(), req.password());
	}

	public void onOpenSerialRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		OpenSerialRequest req = (OpenSerialRequest) messageHeader.message(new OpenSerialRequest());
		if (req == null)
			return;

		conn.getContext().setUseSerial(true);

		this.api.server.getSerialHandler().openSerial();

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);
		SerialUpdateResponse.startSerialUpdateResponse(fbb);
		SerialUpdateResponse.addClosed(fbb, !this.api.server.getSerialHandler().isConnected());
		int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
		int outbound = this.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onCloseSerialRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		CloseSerialRequest req = (CloseSerialRequest) messageHeader
			.message(new CloseSerialRequest());
		if (req == null)
			return;

		conn.getContext().setUseSerial(false);

		this.api.server.getSerialHandler().closeSerial();

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);
		SerialUpdateResponse.startSerialUpdateResponse(fbb);
		SerialUpdateResponse.addClosed(fbb, !this.api.server.getSerialHandler().isConnected());
		int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
		int outbound = this.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onSkeletonResetAllRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		SkeletonResetAllRequest req = (SkeletonResetAllRequest) messageHeader
			.message(new SkeletonResetAllRequest());
		if (req == null)
			return;

		this.api.server.humanPoseProcessor.getSkeletonConfig().resetConfigs();
		this.api.server.getConfigManager().saveConfig();

		// might not be a good idea maybe let the client ask again
		FlatBufferBuilder fbb = new FlatBufferBuilder(300);
		int config = RPCBuilder.createSkeletonConfig(fbb, this.api.server.humanPoseProcessor);
		int outbound = this.createRPCMessage(fbb, RpcMessage.SkeletonConfigResponse, config);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onSkeletonConfigRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		SkeletonConfigRequest req = (SkeletonConfigRequest) messageHeader
			.message(new SkeletonConfigRequest());
		if (req == null)
			return;

		FlatBufferBuilder fbb = new FlatBufferBuilder(300);
		int config = RPCBuilder.createSkeletonConfig(fbb, this.api.server.humanPoseProcessor);
		int outbound = this.createRPCMessage(fbb, RpcMessage.SkeletonConfigResponse, config);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onChangeSkeletonConfigRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		ChangeSkeletonConfigRequest req = (ChangeSkeletonConfigRequest) messageHeader
			.message(new ChangeSkeletonConfigRequest());
		if (req == null)
			return;

		SkeletonConfigOffsets joint = SkeletonConfigOffsets.getById(req.bone());

		this.api.server.humanPoseProcessor.setSkeletonConfig(joint, req.value());
		this.api.server.humanPoseProcessor.getSkeletonConfig().save();
		this.api.server.getConfigManager().saveConfig();
	}

	public void onRecordBVHRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		RecordBVHRequest req = (RecordBVHRequest) messageHeader.message(new RecordBVHRequest());
		if (req == null)
			return;

		if (req.stop()) {
			if (this.api.server.getBvhRecorder().isRecording())
				this.api.server.getBvhRecorder().endRecording();
		} else {
			if (!this.api.server.getBvhRecorder().isRecording())
				this.api.server.getBvhRecorder().startRecording();
		}

		FlatBufferBuilder fbb = new FlatBufferBuilder(40);
		int status = RecordBVHStatus
			.createRecordBVHStatus(fbb, this.api.server.getBvhRecorder().isRecording());
		int outbound = this.createRPCMessage(fbb, RpcMessage.RecordBVHStatus, status);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onResetRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		ResetRequest req = (ResetRequest) messageHeader.message(new ResetRequest());
		if (req == null)
			return;

		if (req.resetType() == ResetType.Quick)
			this.api.server.resetTrackersYaw();
		if (req.resetType() == ResetType.Full)
			this.api.server.resetTrackers();
		LogManager.severe("[WebSocketAPI] Reset performed");
	}

	public void onAssignTrackerRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		AssignTrackerRequest req = (AssignTrackerRequest) messageHeader
			.message(new AssignTrackerRequest());
		if (req == null)
			return;

		Tracker tracker = this.api.server.getTrackerById(req.trackerId().unpack());
		if (tracker == null)
			return;
		tracker = tracker.get();

		TrackerPosition pos = TrackerPosition.getByBodyPart(req.bodyPosition()).orElse(null);
		tracker.setBodyPosition(pos);

		if (req.mountingRotation() != null) {
			if (tracker instanceof IMUTracker) {
				IMUTracker imu = (IMUTracker) tracker;
				imu
					.setMountingRotation(
						new Quaternion(
							req.mountingRotation().x(),
							req.mountingRotation().y(),
							req.mountingRotation().z(),
							req.mountingRotation().w()
						)
					);
			}
		}

		if (req.displayName() != null) {
			if (tracker instanceof IMUTracker) {
				IMUTracker imu = (IMUTracker) tracker;
				imu.setCustomName(req.displayName());
			}
		}

		this.api.server.trackerUpdated(tracker);
	}

	public void onSettingsRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		WindowsNamedPipeBridge bridge = this.api.server.getVRBridge(WindowsNamedPipeBridge.class);

		int steamvrTrackerSettings = SteamVRTrackersSetting
			.createSteamVRTrackersSetting(
				fbb,
				bridge.getShareSetting(TrackerRole.WAIST),
				bridge.getShareSetting(TrackerRole.CHEST),
				bridge.getShareSetting(TrackerRole.LEFT_FOOT)
					&& bridge.getShareSetting(TrackerRole.RIGHT_FOOT),
				bridge.getShareSetting(TrackerRole.LEFT_KNEE)
					&& bridge.getShareSetting(TrackerRole.RIGHT_KNEE),
				bridge.getShareSetting(TrackerRole.LEFT_ELBOW)
					&& bridge.getShareSetting(TrackerRole.RIGHT_ELBOW)
			);

		FiltersConfig filtersConfig = this.api.server
			.getConfigManager()
			.getVrConfig()
			.getFilters();

		int filterSettings = FilteringSettings
			.createFilteringSettings(
				fbb,
				TrackerFilters.getByConfigkey(filtersConfig.getType()).id,
				filtersConfig.getAmount()
			);

		int modelSettings;
		{
			var config = this.api.server.humanPoseProcessor.getSkeletonConfig();
			int togglesOffset = ModelToggles
				.createModelToggles(
					fbb,
					config.getToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL),
					config.getToggle(SkeletonConfigToggles.EXTENDED_PELVIS_MODEL),
					config.getToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL),
					config.getToggle(SkeletonConfigToggles.FORCE_ARMS_FROM_HMD),
					config.getToggle(SkeletonConfigToggles.SKATING_CORRECTION),
					config.getToggle(SkeletonConfigToggles.FLOOR_CLIP)
				);
			int ratiosOffset = ModelRatios
				.createModelRatios(
					fbb,
					config.getValue(SkeletonConfigValues.WAIST_FROM_CHEST_HIP_AVERAGING),
					config.getValue(SkeletonConfigValues.WAIST_FROM_CHEST_LEGS_AVERAGING),
					config.getValue(SkeletonConfigValues.HIP_FROM_CHEST_LEGS_AVERAGING),
					config.getValue(SkeletonConfigValues.HIP_FROM_WAIST_LEGS_AVERAGING),
					config.getValue(SkeletonConfigValues.HIP_LEGS_AVERAGING),
					config.getValue(SkeletonConfigValues.KNEE_TRACKER_ANKLE_AVERAGING)
				);
			modelSettings = ModelSettings.createModelSettings(fbb, togglesOffset, ratiosOffset);
		}

		int settings = SettingsResponse
			.createSettingsResponse(fbb, steamvrTrackerSettings, filterSettings, modelSettings);
		int outbound = createRPCMessage(fbb, RpcMessage.SettingsResponse, settings);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onChangeSettingsRequest(GenericConnection conn, RpcMessageHeader messageHeader) {

		ChangeSettingsRequest req = (ChangeSettingsRequest) messageHeader
			.message(new ChangeSettingsRequest());
		if (req == null)
			return;

		if (req.steamVrTrackers() != null) {
			WindowsNamedPipeBridge bridge = this.api.server
				.getVRBridge(WindowsNamedPipeBridge.class);
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
				FiltersConfig filtersConfig = this.api.server
					.getConfigManager()
					.getVrConfig()
					.getFilters();
				filtersConfig.setType(type.configKey);
				filtersConfig.setAmount(req.filtering().amount());

				this.api.server.getConfigManager().saveConfig();

				filtersConfig.updateTrackersFilters();
			}
		}

		var modelSettings = req.modelSettings();
		if (modelSettings != null) {
			var cfg = this.api.server.humanPoseProcessor.getSkeletonConfig();
			var toggles = modelSettings.toggles();
			var ratios = modelSettings.ratios();

			if (toggles != null) {
				// Note: toggles.has____ returns the same as toggles._____ this
				// seems like a bug
				cfg
					.setToggle(
						SkeletonConfigToggles.EXTENDED_SPINE_MODEL,
						toggles.extendedSpine()
					);
				cfg
					.setToggle(
						SkeletonConfigToggles.EXTENDED_PELVIS_MODEL,
						toggles.extendedPelvis()
					);
				cfg
					.setToggle(
						SkeletonConfigToggles.EXTENDED_KNEE_MODEL,
						toggles.extendedKnee()
					);
				cfg
					.setToggle(
						SkeletonConfigToggles.FORCE_ARMS_FROM_HMD,
						toggles.forceArmsFromHmd()
					);
				cfg
					.setToggle(
						SkeletonConfigToggles.EXTENDED_SPINE_MODEL,
						toggles.extendedSpine()
					);
				cfg
					.setToggle(
						SkeletonConfigToggles.EXTENDED_PELVIS_MODEL,
						toggles.extendedPelvis()
					);
				cfg
					.setToggle(
						SkeletonConfigToggles.EXTENDED_KNEE_MODEL,
						toggles.extendedKnee()
					);
				cfg
					.setToggle(
						SkeletonConfigToggles.FORCE_ARMS_FROM_HMD,
						toggles.forceArmsFromHmd()
					);
				cfg
					.setToggle(
						SkeletonConfigToggles.FLOOR_CLIP,
						toggles.floorClip()
					);

				cfg
					.setToggle(
						SkeletonConfigToggles.SKATING_CORRECTION,
						toggles.skatingCorrection()
					);
			}

			if (ratios != null) {
				if (ratios.hasImputeWaistFromChestHip()) {
					cfg
						.setValue(
							SkeletonConfigValues.WAIST_FROM_CHEST_HIP_AVERAGING,
							ratios.imputeWaistFromChestHip()
						);
				}
				if (ratios.hasImputeWaistFromChestLegs()) {
					cfg
						.setValue(
							SkeletonConfigValues.WAIST_FROM_CHEST_LEGS_AVERAGING,
							ratios.imputeWaistFromChestLegs()
						);
				}
				if (ratios.hasImputeHipFromChestLegs()) {
					cfg
						.setValue(
							SkeletonConfigValues.HIP_FROM_CHEST_LEGS_AVERAGING,
							ratios.imputeHipFromChestLegs()
						);
				}
				if (ratios.hasImputeHipFromWaistLegs()) {
					cfg
						.setValue(
							SkeletonConfigValues.HIP_FROM_WAIST_LEGS_AVERAGING,
							ratios.imputeHipFromWaistLegs()
						);
				}
				if (ratios.hasInterpHipLegs()) {
					cfg
						.setValue(
							SkeletonConfigValues.HIP_LEGS_AVERAGING,
							ratios.interpHipLegs()
						);
				}
				if (ratios.hasInterpKneeTrackerAnkle()) {
					cfg
						.setValue(
							SkeletonConfigValues.KNEE_TRACKER_ANKLE_AVERAGING,
							ratios.interpKneeTrackerAnkle()
						);
				}
			}

			cfg.save();
			this.api.server.getConfigManager().saveConfig();
		}

	}

	@Override
	public void onMessage(GenericConnection conn, RpcMessageHeader message) {
		BiConsumer<GenericConnection, RpcMessageHeader> consumer = this.handlers[message
			.messageType()];
		if (consumer != null)
			consumer.accept(conn, message);
		else
			LogManager
				.info("[ProtocolAPI] Unhandled RPC packet received id: " + message.messageType());
	}

	public int createRPCMessage(FlatBufferBuilder fbb, byte messageType, int messageOffset) {
		int[] data = new int[1];

		RpcMessageHeader.startRpcMessageHeader(fbb);
		RpcMessageHeader.addMessage(fbb, messageOffset);
		RpcMessageHeader.addMessageType(fbb, messageType);
		RpcMessageHeader.addTxId(fbb, TransactionId.createTransactionId(fbb, currTransactionId++));
		data[0] = RpcMessageHeader.endRpcMessageHeader(fbb);

		int messages = MessageBundle.createRpcMsgsVector(fbb, data);

		MessageBundle.startMessageBundle(fbb);
		MessageBundle.addRpcMsgs(fbb, messages);
		return MessageBundle.endMessageBundle(fbb);
	}

	@Override
	public int messagesCount() {
		return RpcMessage.names.length;
	}

	@Override
	public void onSerialConnected(SerialPort port) {

		this.api.getAPIServers().forEach((server) -> {
			server
				.getAPIConnections()
				.filter(conn -> conn.getContext().useSerial())
				.forEach((conn) -> {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);

					SerialUpdateResponse.startSerialUpdateResponse(fbb);
					SerialUpdateResponse.addClosed(fbb, false);
					int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
					int outbound = this
						.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
					fbb.finish(outbound);

					conn.send(fbb.dataBuffer());
				});
		});
	}

	public void SerialTrackerRebootRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		SerialTrackerRebootRequest req = (SerialTrackerRebootRequest) messageHeader
			.message(new SerialTrackerRebootRequest());
		if (req == null)
			return;

		this.api.server.getSerialHandler().rebootRequest();

		this.api.getAPIServers().forEach((server) -> {
			server
				.getAPIConnections()
				.filter(conn2 -> conn2.getContext().useSerial())
				.forEach((conn2) -> {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);

					SerialUpdateResponse.startSerialUpdateResponse(fbb);
					SerialUpdateResponse.addClosed(fbb, false);
					int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
					int outbound = this
						.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
					fbb.finish(outbound);

					conn2.send(fbb.dataBuffer());
				});
		});
	}

	public void SerialTrackerGetInfoRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		SerialTrackerGetInfoRequest req = (SerialTrackerGetInfoRequest) messageHeader
			.message(new SerialTrackerGetInfoRequest());
		if (req == null)
			return;

		this.api.server.getSerialHandler().infoRequest();

		this.api.getAPIServers().forEach((server) -> {
			server
				.getAPIConnections()
				.filter(conn2 -> conn2.getContext().useSerial())
				.forEach((conn2) -> {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);

					SerialUpdateResponse.startSerialUpdateResponse(fbb);
					SerialUpdateResponse.addClosed(fbb, false);
					int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
					int outbound = this
						.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
					fbb.finish(outbound);

					conn2.send(fbb.dataBuffer());
				});
		});
	}

	public void SerialTrackerFactoryResetRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		SerialTrackerFactoryResetRequest req = (SerialTrackerFactoryResetRequest) messageHeader
			.message(new SerialTrackerFactoryResetRequest());
		if (req == null)
			return;

		this.api.server.getSerialHandler().factoryResetRequest();

		this.api.getAPIServers().forEach((server) -> {
			server
				.getAPIConnections()
				.filter(conn2 -> conn2.getContext().useSerial())
				.forEach((conn2) -> {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);

					SerialUpdateResponse.startSerialUpdateResponse(fbb);
					SerialUpdateResponse.addClosed(fbb, false);
					int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
					int outbound = this
						.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
					fbb.finish(outbound);

					conn2.send(fbb.dataBuffer());
				});
		});
	}

	@Override
	public void onSerialDisconnected() {
		this.api.getAPIServers().forEach((server) -> {
			server
				.getAPIConnections()
				.filter(conn -> conn.getContext().useSerial())
				.forEach((conn) -> {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);

					SerialUpdateResponse.startSerialUpdateResponse(fbb);
					SerialUpdateResponse.addClosed(fbb, true);
					int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
					int outbound = this
						.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
					fbb.finish(outbound);
					conn.send(fbb.dataBuffer());
					conn.getContext().setUseSerial(false);
				});
		});
	}

	@Override
	public void onSerialLog(String str) {
		this.api.getAPIServers().forEach((server) -> {
			server
				.getAPIConnections()
				.filter(conn -> conn.getContext().useSerial())
				.forEach((conn) -> {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);

					int logOffset = fbb.createString(str);

					SerialUpdateResponse.startSerialUpdateResponse(fbb);
					SerialUpdateResponse.addLog(fbb, logOffset);
					int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
					int outbound = this
						.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
					fbb.finish(outbound);

					conn.send(fbb.dataBuffer());
				});
		});
	}

	public void onAutoBoneProcessRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		AutoBoneProcessRequest req = (AutoBoneProcessRequest) messageHeader
			.message(new AutoBoneProcessRequest());
		if (req == null || conn.getContext().useAutoBone())
			return;

		conn.getContext().setUseAutoBone(true);
		this.api.server
			.getAutoBoneHandler()
			.startProcessByType(AutoBoneProcessType.getById(req.processType()));
	}

	@Override
	public void onAutoBoneProcessStatus(
		AutoBoneProcessType processType,
		String message,
		long current,
		long total,
		boolean completed,
		boolean success
	) {
		this.api.getAPIServers().forEach((server) -> {
			server
				.getAPIConnections()
				.filter(conn -> conn.getContext().useAutoBone())
				.forEach((conn) -> {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);

					Integer messageOffset = message != null ? fbb.createString(message) : null;

					AutoBoneProcessStatusResponse.startAutoBoneProcessStatusResponse(fbb);
					AutoBoneProcessStatusResponse.addProcessType(fbb, processType.id);
					if (messageOffset != null)
						AutoBoneProcessStatusResponse.addMessage(fbb, messageOffset);
					if (total > 0 && current >= 0) {
						AutoBoneProcessStatusResponse.addCurrent(fbb, current);
						AutoBoneProcessStatusResponse.addTotal(fbb, total);
					}
					AutoBoneProcessStatusResponse.addCompleted(fbb, completed);
					AutoBoneProcessStatusResponse.addSuccess(fbb, success);
					int update = AutoBoneProcessStatusResponse
						.endAutoBoneProcessStatusResponse(fbb);
					int outbound = this
						.createRPCMessage(fbb, RpcMessage.AutoBoneProcessStatusResponse, update);
					fbb.finish(outbound);

					conn.send(fbb.dataBuffer());
					if (completed) {
						conn.getContext().setUseAutoBone(false);
					}
				});
		});
	}

	@Override
	public void onAutoBoneRecordingEnd(PoseFrames recording) {
		// Do nothing, this is broadcasted by "onAutoBoneProcessStatus" uwu
	}

	@Override
	public void onAutoBoneEpoch(Epoch epoch) {
		this.api.getAPIServers().forEach((server) -> {
			server
				.getAPIConnections()
				.filter(conn -> conn.getContext().useAutoBone())
				.forEach((conn) -> {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);

					int[] skeletonPartOffsets = new int[epoch.configValues.size()];
					int i = 0;
					for (
						Entry<SkeletonConfigOffsets, Float> skeletonConfig : epoch.configValues
							.entrySet()
					) {
						skeletonPartOffsets[i++] = SkeletonPart
							.createSkeletonPart(
								fbb,
								skeletonConfig.getKey().id,
								skeletonConfig.getValue()
							);
					}

					int skeletonPartsOffset = AutoBoneEpochResponse
						.createAdjustedSkeletonPartsVector(fbb, skeletonPartOffsets);

					int update = AutoBoneEpochResponse
						.createAutoBoneEpochResponse(
							fbb,
							epoch.epoch,
							epoch.totalEpochs,
							epoch.epochError.getMean(),
							skeletonPartsOffset
						);
					int outbound = this
						.createRPCMessage(fbb, RpcMessage.AutoBoneEpochResponse, update);
					fbb.finish(outbound);

					conn.send(fbb.dataBuffer());
				});
		});
	}


	@Override
	public void onAutoBoneEnd(EnumMap<SkeletonConfigOffsets, Float> configValues) {
		// Do nothing, the last epoch from "onAutoBoneEpoch" should be all
		// that's needed
	}
}
