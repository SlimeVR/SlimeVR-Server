package dev.slimevr.protocol.rpc.settings;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.config.FiltersConfig;
import dev.slimevr.config.OSCConfig;
import dev.slimevr.filtering.TrackerFilters;
import dev.slimevr.osc.VRCOSCHandler;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.protocol.GenericConnection;
import dev.slimevr.protocol.ProtocolAPI;
import dev.slimevr.protocol.rpc.RPCHandler;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigToggles;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValues;
import dev.slimevr.vr.trackers.TrackerRole;
import solarxr_protocol.rpc.ChangeSettingsRequest;
import solarxr_protocol.rpc.RpcMessage;
import solarxr_protocol.rpc.RpcMessageHeader;
import solarxr_protocol.rpc.SettingsResponse;


public record RPCSettingsHandler(RPCHandler rpcHandler, ProtocolAPI api) {


	public RPCSettingsHandler(RPCHandler rpcHandler, ProtocolAPI api) {
		this.rpcHandler = rpcHandler;
		this.api = api;

		rpcHandler.registerPacketListener(RpcMessage.SettingsRequest, this::onSettingsRequest);
		rpcHandler
			.registerPacketListener(
				RpcMessage.ChangeSettingsRequest,
				this::onChangeSettingsRequest
			);
	}

	public void onSettingsRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		WindowsNamedPipeBridge bridge = this.api.server.getVRBridge(WindowsNamedPipeBridge.class);

		int settings = SettingsResponse
			.createSettingsResponse(
				fbb,
				RPCSettingsBuilder.createSteamVRSettings(fbb, bridge),
				RPCSettingsBuilder
					.createFilterSettings(
						fbb,
						this.api.server.getConfigManager().getVrConfig().getFilters()
					),
				RPCSettingsBuilder
					.createOSCSettings(
						fbb,
						this.api.server.getConfigManager().getVrConfig().getVrcOSC()
					),
				RPCSettingsBuilder
					.createModelSettings(
						fbb,
						this.api.server.humanPoseProcessor.getSkeletonConfig()
					)
			);
		int outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SettingsResponse, settings);
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

			if (bridge != null) {
				bridge.changeShareSettings(TrackerRole.WAIST, req.steamVrTrackers().waist());
				bridge.changeShareSettings(TrackerRole.CHEST, req.steamVrTrackers().chest());
				bridge.changeShareSettings(TrackerRole.LEFT_FOOT, req.steamVrTrackers().feet());
				bridge.changeShareSettings(TrackerRole.RIGHT_FOOT, req.steamVrTrackers().feet());
				bridge.changeShareSettings(TrackerRole.LEFT_KNEE, req.steamVrTrackers().knees());
				bridge.changeShareSettings(TrackerRole.RIGHT_KNEE, req.steamVrTrackers().knees());
				bridge.changeShareSettings(TrackerRole.LEFT_ELBOW, req.steamVrTrackers().elbows());
				bridge.changeShareSettings(TrackerRole.RIGHT_ELBOW, req.steamVrTrackers().elbows());
				bridge.changeShareSettings(TrackerRole.LEFT_HAND, req.steamVrTrackers().hands());
				bridge.changeShareSettings(TrackerRole.RIGHT_HAND, req.steamVrTrackers().hands());
			}
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
				filtersConfig.updateTrackersFilters();
			}
		}

		if (req.vrcOsc() != null) {
			OSCConfig vrcOSCConfig = this.api.server.getConfigManager().getVrConfig().getVrcOSC();
			if (vrcOSCConfig != null) {
				VRCOSCHandler VRCOSCHandler = this.api.server.getVRCOSCHandler();
				var trackers = req.vrcOsc().trackers();

				vrcOSCConfig.setEnabled(req.vrcOsc().enabled());
				vrcOSCConfig.setPortIn(req.vrcOsc().portIn());
				vrcOSCConfig.setPortOut(req.vrcOsc().portOut());
				vrcOSCConfig.setAddress(req.vrcOsc().address());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.HEAD, trackers.head());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.CHEST, trackers.chest());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.WAIST, trackers.waist());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.LEFT_KNEE, trackers.knees());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.RIGHT_KNEE, trackers.knees());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.LEFT_FOOT, trackers.feet());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.RIGHT_FOOT, trackers.feet());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.LEFT_ELBOW, trackers.elbows());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.RIGHT_ELBOW, trackers.elbows());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.LEFT_HAND, trackers.hands());
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.RIGHT_HAND, trackers.hands());


				VRCOSCHandler.refreshSettings();
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
				cfg.setToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL, toggles.extendedSpine());
				cfg
					.setToggle(
						SkeletonConfigToggles.EXTENDED_PELVIS_MODEL,
						toggles.extendedPelvis()
					);
				cfg.setToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL, toggles.extendedKnee());
				cfg
					.setToggle(
						SkeletonConfigToggles.FORCE_ARMS_FROM_HMD,
						toggles.forceArmsFromHmd()
					);
				cfg.setToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL, toggles.extendedSpine());
				cfg
					.setToggle(
						SkeletonConfigToggles.EXTENDED_PELVIS_MODEL,
						toggles.extendedPelvis()
					);
				cfg.setToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL, toggles.extendedKnee());
				cfg
					.setToggle(
						SkeletonConfigToggles.FORCE_ARMS_FROM_HMD,
						toggles.forceArmsFromHmd()
					);
				cfg.setToggle(SkeletonConfigToggles.FLOOR_CLIP, toggles.floorClip());

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
					cfg.setValue(SkeletonConfigValues.HIP_LEGS_AVERAGING, ratios.interpHipLegs());
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
		}

		this.api.server.getConfigManager().saveConfig();
	}

}
