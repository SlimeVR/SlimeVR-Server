package dev.slimevr.protocol.rpc.settings

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.VRServer
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.config.AutoBoneConfig
import dev.slimevr.config.DriftCompensationConfig
import dev.slimevr.config.FiltersConfig
import dev.slimevr.config.HIDConfig
import dev.slimevr.config.LegTweaksConfig
import dev.slimevr.config.OSCConfig
import dev.slimevr.config.ResetsConfig
import dev.slimevr.config.SkeletonConfig
import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.config.TapDetectionConfig
import dev.slimevr.config.VMCConfig
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.filtering.TrackerFilters.Companion.getByConfigkey
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles
import dev.slimevr.tracking.processor.config.SkeletonConfigValues
import dev.slimevr.tracking.trackers.TrackerRole
import solarxr_protocol.rpc.AutoBoneSettings
import solarxr_protocol.rpc.DriftCompensationSettings
import solarxr_protocol.rpc.FilteringSettings
import solarxr_protocol.rpc.HIDSettings
import solarxr_protocol.rpc.OSCRouterSettings
import solarxr_protocol.rpc.OSCSettings
import solarxr_protocol.rpc.OSCTrackersSetting
import solarxr_protocol.rpc.ResetsSettings
import solarxr_protocol.rpc.SettingsResponse
import solarxr_protocol.rpc.StayAlignedSettings
import solarxr_protocol.rpc.SteamVRTrackersSetting
import solarxr_protocol.rpc.TapDetectionSettings
import solarxr_protocol.rpc.VMCOSCSettings
import solarxr_protocol.rpc.VRCOSCSettings
import solarxr_protocol.rpc.settings.LegTweaksSettings
import solarxr_protocol.rpc.settings.ModelRatios
import solarxr_protocol.rpc.settings.ModelSettings
import solarxr_protocol.rpc.settings.ModelToggles
import solarxr_protocol.rpc.settings.SkeletonHeight

fun createOSCRouterSettings(
	fbb: FlatBufferBuilder,
	config: OSCConfig,
): Int {
	val addressStringOffset = fbb.createString(config.address)

	val oscSettingOffset = OSCSettings
		.createOSCSettings(
			fbb,
			config.enabled,
			config.portIn,
			config.portOut,
			addressStringOffset,
		)

	OSCRouterSettings.startOSCRouterSettings(fbb)
	OSCRouterSettings.addOscSettings(fbb, oscSettingOffset)

	return OSCRouterSettings.endOSCRouterSettings(fbb)
}

fun createVRCOSCSettings(
	fbb: FlatBufferBuilder,
	config: VRCOSCConfig,
): Int {
	val addressStringOffset = fbb.createString(config.address)
	val generalSettingOffset = OSCSettings
		.createOSCSettings(
			fbb,
			config.enabled,
			config.portIn,
			config.portOut,
			addressStringOffset,
		)
	val oscSettingOffset = OSCTrackersSetting
		.createOSCTrackersSetting(
			fbb,
			config.getOSCTrackerRole(TrackerRole.HEAD, false),
			config.getOSCTrackerRole(TrackerRole.CHEST, false),
			config.getOSCTrackerRole(TrackerRole.WAIST, false),
			config.getOSCTrackerRole(TrackerRole.LEFT_KNEE, false) &&
				config.getOSCTrackerRole(TrackerRole.RIGHT_KNEE, false),
			config.getOSCTrackerRole(TrackerRole.LEFT_FOOT, false) &&
				config.getOSCTrackerRole(TrackerRole.RIGHT_FOOT, false),
			config.getOSCTrackerRole(TrackerRole.LEFT_ELBOW, false) &&
				config.getOSCTrackerRole(TrackerRole.RIGHT_ELBOW, false),
			config.getOSCTrackerRole(TrackerRole.LEFT_HAND, false) &&
				config.getOSCTrackerRole(TrackerRole.RIGHT_HAND, false),
		)
	VRCOSCSettings.startVRCOSCSettings(fbb)
	VRCOSCSettings.addOscSettings(fbb, generalSettingOffset)
	VRCOSCSettings.addTrackers(fbb, oscSettingOffset)
	VRCOSCSettings.addOscqueryEnabled(fbb, config.oscqueryEnabled)

	return VRCOSCSettings.endVRCOSCSettings(fbb)
}

fun createVMCOSCSettings(
	fbb: FlatBufferBuilder,
	config: VMCConfig,
): Int {
	val addressStringOffset = fbb.createString(config.address)
	val generalSettingOffset = OSCSettings
		.createOSCSettings(
			fbb,
			config.enabled,
			config.portIn,
			config.portOut,
			addressStringOffset,
		)

	val vrmJson = config.vrmJson
	var vrmJsonOffset = 0
	if (vrmJson != null) vrmJsonOffset = fbb.createString(vrmJson)

	VMCOSCSettings.startVMCOSCSettings(fbb)
	VMCOSCSettings.addOscSettings(fbb, generalSettingOffset)
	if (vrmJson != null) VMCOSCSettings.addVrmJson(fbb, vrmJsonOffset)
	VMCOSCSettings.addAnchorHip(fbb, config.anchorHip)
	VMCOSCSettings.addMirrorTracking(fbb, config.mirrorTracking)

	return VMCOSCSettings.endVMCOSCSettings(fbb)
}

fun createFilterSettings(
	fbb: FlatBufferBuilder,
	filtersConfig: FiltersConfig,
): Int = FilteringSettings
	.createFilteringSettings(
		fbb,
		getByConfigkey(filtersConfig.type)!!.id,
		filtersConfig.amount,
	)

fun createDriftCompensationSettings(
	fbb: FlatBufferBuilder,
	driftCompensationConfig: DriftCompensationConfig,
): Int = DriftCompensationSettings
	.createDriftCompensationSettings(
		fbb,
		driftCompensationConfig.enabled,
		driftCompensationConfig.prediction,
		driftCompensationConfig.amount,
		driftCompensationConfig.maxResets,
	)

fun createTapDetectionSettings(
	fbb: FlatBufferBuilder,
	tapDetectionConfig: TapDetectionConfig,
): Int = TapDetectionSettings
	.createTapDetectionSettings(
		fbb,
		tapDetectionConfig.fullResetDelay,
		tapDetectionConfig.fullResetEnabled,
		tapDetectionConfig.fullResetTaps,
		tapDetectionConfig.yawResetDelay,
		tapDetectionConfig.yawResetEnabled,
		tapDetectionConfig.yawResetTaps,
		tapDetectionConfig.mountingResetDelay,
		tapDetectionConfig.mountingResetEnabled,
		tapDetectionConfig.mountingResetTaps,
		tapDetectionConfig.setupMode,
		tapDetectionConfig.numberTrackersOverThreshold,
	)

fun createSteamVRSettings(fbb: FlatBufferBuilder, bridge: ISteamVRBridge?): Int {
	var steamvrTrackerSettings = 0
	if (bridge != null) {
		steamvrTrackerSettings = SteamVRTrackersSetting
			.createSteamVRTrackersSetting(
				fbb,
				bridge.getShareSetting(TrackerRole.WAIST),
				bridge.getShareSetting(TrackerRole.CHEST),
				bridge.getAutomaticSharedTrackers(),

				bridge.getShareSetting(TrackerRole.LEFT_FOOT),
				bridge.getShareSetting(TrackerRole.RIGHT_FOOT),
				bridge.getShareSetting(TrackerRole.LEFT_KNEE),
				bridge.getShareSetting(TrackerRole.RIGHT_KNEE),
				bridge.getShareSetting(TrackerRole.LEFT_ELBOW),
				bridge.getShareSetting(TrackerRole.RIGHT_ELBOW),
				bridge.getShareSetting(TrackerRole.LEFT_HAND),
				bridge.getShareSetting(TrackerRole.RIGHT_HAND),
			)
	}
	return steamvrTrackerSettings
}

fun createModelSettings(
	fbb: FlatBufferBuilder,
	humanPoseManager: HumanPoseManager,
	legTweaksConfig: LegTweaksConfig,
	skeletonConfig: SkeletonConfig,
): Int {
	val togglesOffset = ModelToggles
		.createModelToggles(
			fbb,
			humanPoseManager.getToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL),
			humanPoseManager.getToggle(SkeletonConfigToggles.EXTENDED_PELVIS_MODEL),
			humanPoseManager.getToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL),
			humanPoseManager.getToggle(SkeletonConfigToggles.FORCE_ARMS_FROM_HMD),
			humanPoseManager.getToggle(SkeletonConfigToggles.FLOOR_CLIP),
			humanPoseManager.getToggle(SkeletonConfigToggles.SKATING_CORRECTION),
			humanPoseManager.getToggle(SkeletonConfigToggles.TOE_SNAP),
			humanPoseManager.getToggle(SkeletonConfigToggles.FOOT_PLANT),
			humanPoseManager.getToggle(SkeletonConfigToggles.SELF_LOCALIZATION),
			humanPoseManager.getToggle(SkeletonConfigToggles.USE_POSITION),
			humanPoseManager.getToggle(SkeletonConfigToggles.ENFORCE_CONSTRAINTS),
			humanPoseManager.getToggle(SkeletonConfigToggles.CORRECT_CONSTRAINTS),
		)
	val ratiosOffset = ModelRatios
		.createModelRatios(
			fbb,
			humanPoseManager.getValue(SkeletonConfigValues.WAIST_FROM_CHEST_HIP_AVERAGING),
			humanPoseManager.getValue(SkeletonConfigValues.WAIST_FROM_CHEST_LEGS_AVERAGING),
			humanPoseManager.getValue(SkeletonConfigValues.HIP_FROM_CHEST_LEGS_AVERAGING),
			humanPoseManager.getValue(SkeletonConfigValues.HIP_FROM_WAIST_LEGS_AVERAGING),
			humanPoseManager.getValue(SkeletonConfigValues.HIP_LEGS_AVERAGING),
			humanPoseManager.getValue(SkeletonConfigValues.KNEE_TRACKER_ANKLE_AVERAGING),
			humanPoseManager.getValue(SkeletonConfigValues.KNEE_ANKLE_AVERAGING),
		)
	val legTweaksOffset = LegTweaksSettings
		.createLegTweaksSettings(
			fbb,
			legTweaksConfig.correctionStrength,
		)
	val skeletonConfigOffset = SkeletonHeight
		.createSkeletonHeight(
			fbb,
			skeletonConfig.hmdHeight,
			skeletonConfig.floorHeight,
		)
	return ModelSettings
		.createModelSettings(
			fbb,
			togglesOffset,
			ratiosOffset,
			legTweaksOffset,
			skeletonConfigOffset,
		)
}

fun createAutoBoneSettings(
	fbb: FlatBufferBuilder,
	autoBoneConfig: AutoBoneConfig,
): Int = AutoBoneSettings
	.createAutoBoneSettings(
		fbb,
		autoBoneConfig.cursorIncrement,
		autoBoneConfig.minDataDistance,
		autoBoneConfig.maxDataDistance,
		autoBoneConfig.numEpochs,
		autoBoneConfig.printEveryNumEpochs,
		autoBoneConfig.initialAdjustRate,
		autoBoneConfig.adjustRateDecay,
		autoBoneConfig.slideErrorFactor,
		autoBoneConfig.offsetSlideErrorFactor,
		autoBoneConfig.footHeightOffsetErrorFactor,
		autoBoneConfig.bodyProportionErrorFactor,
		autoBoneConfig.heightErrorFactor,
		autoBoneConfig.positionErrorFactor,
		autoBoneConfig.positionOffsetErrorFactor,
		autoBoneConfig.calcInitError,
		autoBoneConfig.randomizeFrameOrder,
		autoBoneConfig.scaleEachStep,
		autoBoneConfig.sampleCount,
		autoBoneConfig.sampleRateMs,
		autoBoneConfig.saveRecordings,
		autoBoneConfig.useSkeletonHeight,
		autoBoneConfig.randSeed,
	)

/**
 * Writes values from AutoBoneSettings to an AutoBoneConfig.
 *
 * @param autoBoneSettings The settings to read from.
 * @param autoBoneConfig The config to write to.
 * @return The autoBoneConfig parameter.
 */
fun readAutoBoneSettings(
	autoBoneSettings: AutoBoneSettings,
	autoBoneConfig: AutoBoneConfig,
): AutoBoneConfig {
	if (autoBoneSettings.hasCursorIncrement()) {
		autoBoneConfig.cursorIncrement = autoBoneSettings.cursorIncrement()
	}
	if (autoBoneSettings.hasMinDataDistance()) {
		autoBoneConfig.minDataDistance = autoBoneSettings.minDataDistance()
	}
	if (autoBoneSettings.hasMaxDataDistance()) {
		autoBoneConfig.maxDataDistance = autoBoneSettings.maxDataDistance()
	}
	if (autoBoneSettings.hasNumEpochs()) {
		autoBoneConfig.numEpochs = autoBoneSettings.numEpochs()
	}
	if (autoBoneSettings.hasPrintEveryNumEpochs()) {
		autoBoneConfig.printEveryNumEpochs = autoBoneSettings.printEveryNumEpochs()
	}
	if (autoBoneSettings.hasInitialAdjustRate()) {
		autoBoneConfig.initialAdjustRate = autoBoneSettings.initialAdjustRate()
	}
	if (autoBoneSettings.hasAdjustRateDecay()) {
		autoBoneConfig.adjustRateDecay = autoBoneSettings.adjustRateDecay()
	}
	if (autoBoneSettings.hasSlideErrorFactor()) {
		autoBoneConfig.slideErrorFactor = autoBoneSettings.slideErrorFactor()
	}
	if (autoBoneSettings.hasOffsetSlideErrorFactor()) {
		autoBoneConfig.offsetSlideErrorFactor =
			autoBoneSettings.offsetSlideErrorFactor()
	}
	if (autoBoneSettings.hasFootHeightOffsetErrorFactor()) {
		autoBoneConfig
			.footHeightOffsetErrorFactor =
			autoBoneSettings.footHeightOffsetErrorFactor()
	}
	if (autoBoneSettings.hasBodyProportionErrorFactor()) {
		autoBoneConfig
			.bodyProportionErrorFactor = autoBoneSettings.bodyProportionErrorFactor()
	}
	if (autoBoneSettings.hasHeightErrorFactor()) {
		autoBoneConfig.heightErrorFactor = autoBoneSettings.heightErrorFactor()
	}
	if (autoBoneSettings.hasPositionErrorFactor()) {
		autoBoneConfig.positionErrorFactor = autoBoneSettings.positionErrorFactor()
	}
	if (autoBoneSettings.hasPositionOffsetErrorFactor()) {
		autoBoneConfig
			.positionOffsetErrorFactor = autoBoneSettings.positionOffsetErrorFactor()
	}
	if (autoBoneSettings.hasCalcInitError()) {
		autoBoneConfig.calcInitError = autoBoneSettings.calcInitError()
	}
	if (autoBoneSettings.hasRandomizeFrameOrder()) {
		autoBoneConfig.randomizeFrameOrder = autoBoneSettings.randomizeFrameOrder()
	}
	if (autoBoneSettings.hasScaleEachStep()) {
		autoBoneConfig.scaleEachStep = autoBoneSettings.scaleEachStep()
	}
	if (autoBoneSettings.hasSampleCount()) {
		autoBoneConfig.sampleCount = autoBoneSettings.sampleCount()
	}
	if (autoBoneSettings.hasSampleRateMs()) {
		autoBoneConfig.sampleRateMs = autoBoneSettings.sampleRateMs()
	}
	if (autoBoneSettings.hasSaveRecordings()) {
		autoBoneConfig.saveRecordings = autoBoneSettings.saveRecordings()
	}
	if (autoBoneSettings.hasUseSkeletonHeight()) {
		autoBoneConfig.useSkeletonHeight = autoBoneSettings.useSkeletonHeight()
	}
	if (autoBoneSettings.hasRandSeed()) {
		autoBoneConfig.randSeed = autoBoneSettings.randSeed()
	}

	return autoBoneConfig
}

fun createArmsResetModeSettings(
	fbb: FlatBufferBuilder,
	resetsConfig: ResetsConfig,
): Int = ResetsSettings
	.createResetsSettings(
		fbb,
		resetsConfig.resetMountingFeet,
		resetsConfig.mode.id,
		resetsConfig.yawResetSmoothTime,
		resetsConfig.saveMountingReset,
		resetsConfig.resetHmdPitch,
	)

fun createSettingsResponse(fbb: FlatBufferBuilder, server: VRServer): Int {
	val bridge = server.getVRBridge(ISteamVRBridge::class.java)

	return SettingsResponse
		.createSettingsResponse(
			fbb,
			createSteamVRSettings(fbb, bridge),
			createFilterSettings(
				fbb,
				server.configManager.vrConfig.filters,
			),
			createDriftCompensationSettings(
				fbb,
				server.configManager.vrConfig.driftCompensation,
			),
			createOSCRouterSettings(
				fbb,
				server.configManager.vrConfig.oscRouter,
			),
			createVRCOSCSettings(
				fbb,
				server.configManager.vrConfig.vrcOSC,
			),
			createVMCOSCSettings(
				fbb,
				server.configManager.vrConfig.vmc,
			),
			createModelSettings(
				fbb,
				server.humanPoseManager,
				server.configManager.vrConfig.legTweaks,
				server.configManager.vrConfig.skeleton,
			),
			createTapDetectionSettings(
				fbb,
				server.configManager.vrConfig.tapDetection,
			),
			createAutoBoneSettings(
				fbb,
				server.configManager.vrConfig.autoBone,
			),
			createArmsResetModeSettings(
				fbb,
				server.configManager.vrConfig.resetsConfig,
			),
			createStayAlignedSettings(
				fbb,
				server.configManager.vrConfig.stayAlignedConfig,
			),
			createHIDSettings(fbb, server.configManager.vrConfig.hidConfig),
		)
}

fun createStayAlignedSettings(
	fbb: FlatBufferBuilder,
	config: StayAlignedConfig,
): Int = StayAlignedSettings
	.createStayAlignedSettings(
		fbb,
		config.enabled,
		false, // deprecated
		config.hideYawCorrection,
		config.standingRelaxedPose.enabled,
		config.standingRelaxedPose.upperLegAngleInDeg,
		config.standingRelaxedPose.lowerLegAngleInDeg,
		config.standingRelaxedPose.footAngleInDeg,
		config.sittingRelaxedPose.enabled,
		config.sittingRelaxedPose.upperLegAngleInDeg,
		config.sittingRelaxedPose.lowerLegAngleInDeg,
		config.sittingRelaxedPose.footAngleInDeg,
		config.flatRelaxedPose.enabled,
		config.flatRelaxedPose.upperLegAngleInDeg,
		config.flatRelaxedPose.lowerLegAngleInDeg,
		config.flatRelaxedPose.footAngleInDeg,
		config.setupComplete,
	)

fun createHIDSettings(
	fbb: FlatBufferBuilder,
	config: HIDConfig,
): Int = HIDSettings
	.createHIDSettings(
		fbb,
		config.trackersOverHID,
	)
