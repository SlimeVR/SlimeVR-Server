package dev.slimevr.protocol.rpc.settings

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.config.StayAlignedConfig
import solarxr_protocol.rpc.StayAlignedSettings

object RPCSettingsBuilderKotlin {

	fun createStayAlignedSettings(
		fbb: FlatBufferBuilder,
		config: StayAlignedConfig,
	): Int =
		StayAlignedSettings
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
}
