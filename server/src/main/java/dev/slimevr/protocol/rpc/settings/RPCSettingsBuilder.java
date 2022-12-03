package dev.slimevr.protocol.rpc.settings;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.config.FiltersConfig;
import dev.slimevr.config.OSCConfig;
import dev.slimevr.filtering.TrackerFilters;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.vr.processor.skeleton.SkeletonConfig;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigToggles;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValues;
import dev.slimevr.vr.trackers.TrackerRole;
import solarxr_protocol.rpc.FilteringSettings;
import solarxr_protocol.rpc.OSCTrackersSetting;
import solarxr_protocol.rpc.SteamVRTrackersSetting;
import solarxr_protocol.rpc.VRCOSCSettings;
import solarxr_protocol.rpc.settings.ModelRatios;
import solarxr_protocol.rpc.settings.ModelSettings;
import solarxr_protocol.rpc.settings.ModelToggles;


public class RPCSettingsBuilder {

	public static int createOSCSettings(
		FlatBufferBuilder fbb,
		OSCConfig config
	) {

		int trackersSettingOffset = OSCTrackersSetting
			.createOSCTrackersSetting(
				fbb,
				config.getOSCTrackerRole(TrackerRole.HEAD, false),
				config.getOSCTrackerRole(TrackerRole.CHEST, false),
				config.getOSCTrackerRole(TrackerRole.WAIST, false),
				config.getOSCTrackerRole(TrackerRole.LEFT_KNEE, false),
				config.getOSCTrackerRole(TrackerRole.LEFT_FOOT, false),
				config.getOSCTrackerRole(TrackerRole.LEFT_ELBOW, false),
				config.getOSCTrackerRole(TrackerRole.LEFT_HAND, false)
			);

		int addressStringOffset = fbb.createString(config.getAddress());
		VRCOSCSettings.startVRCOSCSettings(fbb);
		VRCOSCSettings.addEnabled(fbb, config.getEnabled());
		VRCOSCSettings.addPortIn(fbb, config.getPortIn());
		VRCOSCSettings.addPortOut(fbb, config.getPortOut());
		VRCOSCSettings
			.addAddress(
				fbb,
				addressStringOffset
			);
		VRCOSCSettings
			.addTrackers(
				fbb,
				trackersSettingOffset
			);

		return VRCOSCSettings.endVRCOSCSettings(fbb);
	}

	public static int createFilterSettings(
		FlatBufferBuilder fbb,
		FiltersConfig filtersConfig
	) {
		return FilteringSettings
			.createFilteringSettings(
				fbb,
				TrackerFilters.getByConfigkey(filtersConfig.getType()).id,
				filtersConfig.getAmount()
			);
	}

	public static int createSteamVRSettings(FlatBufferBuilder fbb, WindowsNamedPipeBridge bridge) {
		int steamvrTrackerSettings = 0;
		if (bridge != null) {
			steamvrTrackerSettings = SteamVRTrackersSetting
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
		}
		return steamvrTrackerSettings;
	}

	public static int createModelSettings(FlatBufferBuilder fbb, SkeletonConfig config) {
		int togglesOffset = ModelToggles
			.createModelToggles(
				fbb,
				config.getToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL),
				config.getToggle(SkeletonConfigToggles.EXTENDED_PELVIS_MODEL),
				config.getToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL),
				config.getToggle(SkeletonConfigToggles.FORCE_ARMS_FROM_HMD),
				config.getToggle(SkeletonConfigToggles.FLOOR_CLIP),
				config.getToggle(SkeletonConfigToggles.SKATING_CORRECTION)
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
		return ModelSettings.createModelSettings(fbb, togglesOffset, ratiosOffset);
	}
}
