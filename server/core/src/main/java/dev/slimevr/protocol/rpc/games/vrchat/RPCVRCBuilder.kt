package dev.slimevr.protocol.rpc.games.vrchat

import com.google.flatbuffers.FlatBufferBuilder
import solarxr_protocol.rpc.*

fun buildVRCConfigValues(fbb: FlatBufferBuilder, values: dev.slimevr.games.vrchat.VRCConfigValues): Int {
	VRCConfigValues.startVRCConfigValues(fbb)
	VRCConfigValues.addCalibrationRange(fbb, values.calibrationRange.toFloat())
	VRCConfigValues.addCalibrationVisuals(fbb, values.calibrationVisuals)
	VRCConfigValues.addSpineMode(fbb, values.spineMode.id)
	VRCConfigValues.addLegacyMode(fbb, values.legacyMode)
	VRCConfigValues.addShoulderTrackingDisabled(fbb, values.shoulderTrackingDisabled)
	VRCConfigValues.addTrackerModel(fbb, values.trackerModel.id)
	VRCConfigValues.addAvatarMeasurementType(fbb, values.avatarMeasurementType.id)
	VRCConfigValues.addUserHeight(fbb, values.userHeight.toFloat())
	VRCConfigValues.addShoulderWidthCompensation(fbb, values.shoulderWidthCompensation)
	return VRCConfigValues.endVRCConfigValues(fbb)
}

fun buildVRCConfigValidity(fbb: FlatBufferBuilder, validity: dev.slimevr.games.vrchat.VRCConfigValidity): Int {
	VRCConfigValidity.startVRCConfigValidity(fbb)
	VRCConfigValidity.addCalibrationRangeOk(fbb, validity.calibrationRangeOk)
	VRCConfigValidity.addCalibrationVisualsOk(fbb, validity.calibrationVisualsOk)
	VRCConfigValidity.addSpineModeOk(fbb, validity.spineModeOk)
	VRCConfigValidity.addLegacyModeOk(fbb, validity.legacyModeOk)
	VRCConfigValidity.addShoulderTrackingOk(fbb, validity.shoulderTrackingOk)
	VRCConfigValidity.addTrackerModelOk(fbb, validity.trackerModelOk)
	VRCConfigValidity.addUserHeightOk(fbb, validity.userHeightOk)
	VRCConfigValidity.addAvatarMeasurementTypeOk(fbb, validity.avatarMeasurementTypeOk)
	VRCConfigValidity.addShoulderWidthCompensationOk(fbb, validity.shoulderWidthCompensationOk)
	return VRCConfigValidity.endVRCConfigValidity(fbb)
}

fun buildVRCConfigRecommendedValues(fbb: FlatBufferBuilder, values: dev.slimevr.games.vrchat.VRCConfigRecommendedValues): Int {
	val spineModeOffset = VRCConfigRecommendedValues
		.createSpineModeVector(
			fbb,
			values.spineMode.map { it.id.toByte() }.toByteArray(),
		)

	VRCConfigRecommendedValues.startVRCConfigRecommendedValues(fbb)
	VRCConfigRecommendedValues.addCalibrationRange(fbb, values.calibrationRange.toFloat())
	VRCConfigRecommendedValues.addCalibrationVisuals(fbb, values.calibrationVisuals)
	VRCConfigRecommendedValues.addSpineMode(fbb, spineModeOffset)
	VRCConfigRecommendedValues.addLegacyMode(fbb, values.legacyMode)
	VRCConfigRecommendedValues.addShoulderTrackingDisabled(fbb, values.shoulderTrackingDisabled)
	VRCConfigRecommendedValues.addTrackerModel(fbb, values.trackerModel.id)
	VRCConfigRecommendedValues.addAvatarMeasurementType(fbb, values.avatarMeasurementType.id)
	VRCConfigRecommendedValues.addUserHeight(fbb, values.userHeight.toFloat())
	VRCConfigRecommendedValues.addShoulderWidthCompensation(fbb, values.shoulderWidthCompensation)
	return VRCConfigRecommendedValues.endVRCConfigRecommendedValues(fbb)
}

fun buildVRCConfigStateResponse(
	fbb: FlatBufferBuilder,
	isSupported: Boolean,
	validity: dev.slimevr.games.vrchat.VRCConfigValidity?,
	values: dev.slimevr.games.vrchat.VRCConfigValues?,
	recommended: dev.slimevr.games.vrchat.VRCConfigRecommendedValues?,
	muted: List<String>,
): Int {
	if (!isSupported) {
		VRCConfigStateChangeResponse.startVRCConfigStateChangeResponse(fbb)
		VRCConfigStateChangeResponse.addIsSupported(fbb, false)
		return VRCConfigStateChangeResponse.endVRCConfigStateChangeResponse(fbb)
	}

	if (validity == null || values == null || recommended == null) {
		error("invalid state - all should be set")
	}

	val validityOffset = buildVRCConfigValidity(fbb, validity)
	val valuesOffset = buildVRCConfigValues(fbb, values)
	val recommendedOffset = buildVRCConfigRecommendedValues(fbb, recommended)
	val mutedOffset = VRCConfigStateChangeResponse.createMutedVector(fbb, muted.map { fbb.createString(it) }.toIntArray())

	VRCConfigStateChangeResponse.startVRCConfigStateChangeResponse(fbb)
	VRCConfigStateChangeResponse.addIsSupported(fbb, true)
	VRCConfigStateChangeResponse.addValidity(fbb, validityOffset)
	VRCConfigStateChangeResponse.addState(fbb, valuesOffset)
	VRCConfigStateChangeResponse.addRecommended(fbb, recommendedOffset)
	VRCConfigStateChangeResponse.addMuted(fbb, mutedOffset)
	return VRCConfigStateChangeResponse.endVRCConfigStateChangeResponse(fbb)
}
