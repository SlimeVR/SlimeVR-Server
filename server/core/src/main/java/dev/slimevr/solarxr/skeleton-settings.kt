package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.SkeletonConfig
import dev.slimevr.config.SkeletonFilteringConfig
import dev.slimevr.config.SkeletonRatiosConfig
import dev.slimevr.config.SkeletonTogglesConfig
import dev.slimevr.skeleton.SkeletonActions
import solarxr_protocol.rpc.ChangeSkeletonSettingsRequest
import solarxr_protocol.rpc.FilteringType
import solarxr_protocol.rpc.SetPauseTrackingRequest
import solarxr_protocol.rpc.SkeletonFiltering
import solarxr_protocol.rpc.SkeletonRatios
import solarxr_protocol.rpc.SkeletonSettingsRequest
import solarxr_protocol.rpc.SkeletonSettingsResponse
import solarxr_protocol.rpc.SkeletonToggles
import solarxr_protocol.rpc.TrackingPauseStateRequest
import solarxr_protocol.rpc.TrackingPauseStateResponse

class SkeletonSettingsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<SkeletonSettingsRequest> {
			val config = settings.context.state.value.data.skeletonConfig
			receiver.sendRpc(
				SkeletonSettingsResponse(
					toggles = SkeletonToggles(
						floorClip = config.toggles.floorClip,
						skatingCorrection = config.toggles.skatingCorrection,
						toeSnap = config.toggles.toeSnap,
						footPlant = config.toggles.footPlant,
						mocapMode = config.toggles.mocapMode,
						useTrackerPositions = config.toggles.useTrackerPositions,
						enforceConstraints = config.toggles.enforceConstraints,
						correctConstraints = config.toggles.correctConstraints,
					),
					ratios = SkeletonRatios(
						imputeSpineFromUpperToLower = config.ratios.imputeSpineFromUpperToLower,
						imputeSpineCurvature = config.ratios.imputeSpineCurvature,
						interpolateHipWithUpperLegs = config.ratios.interpolateHipWithUpperLegs,
						interpolateUpperLegsTwistWithLowerLegs = config.ratios.interpolateUpperLegsTwistWithLowerLegs,
						skatingCorrectionStrength = config.ratios.skatingCorrectionStrength,
					),
					filtering = SkeletonFiltering(
						type = config.filtering.type,
						amount = config.filtering.amount,
					),
				),
			)
		}.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeSkeletonSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						skeletonConfig = SkeletonConfig(
							toggles = req.toggles?.let {
								SkeletonTogglesConfig(
									floorClip = it.floorClip == true,
									skatingCorrection = it.skatingCorrection == true,
									toeSnap = it.toeSnap == true,
									footPlant = it.footPlant == true,
									mocapMode = it.mocapMode == true,
									useTrackerPositions = it.useTrackerPositions == true,
									enforceConstraints = it.enforceConstraints == true,
									correctConstraints = it.correctConstraints == true,
								)
							} ?: skeletonConfig.toggles,
							ratios = req.ratios?.let {
								SkeletonRatiosConfig(
									imputeSpineFromUpperToLower = it.imputeSpineFromUpperToLower ?: 0f,
									imputeSpineCurvature = it.imputeSpineCurvature ?: 0f,
									interpolateHipWithUpperLegs = it.interpolateHipWithUpperLegs ?: 0f,
									interpolateUpperLegsTwistWithLowerLegs = it.interpolateUpperLegsTwistWithLowerLegs ?: 0f,
									skatingCorrectionStrength = it.skatingCorrectionStrength ?: 0f,
								)
							} ?: skeletonConfig.ratios,
							filtering = req.filtering?.let {
								SkeletonFilteringConfig(
									type = it.type ?: FilteringType.NONE,
									amount = it.amount ?: 0f,
								)
							} ?: skeletonConfig.filtering,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<SetPauseTrackingRequest> {
			receiver.appContext.skeleton.context.dispatch(
				SkeletonActions.PauseTracking(
					it.pauseTracking ?: false,
				),
			)

			receiver.sendRpc(
				TrackingPauseStateResponse(
					trackingPaused = it.pauseTracking,
				),
			)
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<TrackingPauseStateRequest> {
			receiver.sendRpc(
				TrackingPauseStateResponse(
					trackingPaused = receiver.appContext.skeleton.context.state.value.paused,
				),
			)
		}.launchIn(receiver.context.scope)
	}
}
