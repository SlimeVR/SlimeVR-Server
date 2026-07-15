package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.SkeletonConfig
import dev.slimevr.config.SkeletonFilteringConfig
import dev.slimevr.config.SkeletonRatiosConfig
import dev.slimevr.config.SkeletonTogglesConfig
import dev.slimevr.skeleton.SkeletonActions
import solarxr_protocol.rpc.ChangeSkeletonSettingsRequest
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
						forceArmsFromHmd = config.toggles.forceArmsFromHmd,
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
						interpolateHipWithKnees = config.ratios.interpolateHipWithKnees,
						interpolateComputedKneesWithAnkles = config.ratios.interpolateComputedKneesWithAnkles,
						interpolateKneesWithAnkles = config.ratios.interpolateKneesWithAnkles,
						skatingCorrectionStrength = config.ratios.skatingCorrectionStrength,
					),
					filtering = SkeletonFiltering(
						type = config.filtering.type,
						amount = config.filtering.amount,
					),
				),
			)
		}

		// Receive config
		receiver.rpcDispatcher.on<ChangeSkeletonSettingsRequest> { req ->
			val oldConfig = settings.context.state.value.data.skeletonConfig
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						skeletonConfig = SkeletonConfig(
							toggles = req.toggles?.let {
								SkeletonTogglesConfig(
									forceArmsFromHmd = it.forceArmsFromHmd == true,
									floorClip = it.floorClip == true,
									skatingCorrection = it.skatingCorrection == true,
									toeSnap = it.toeSnap == true,
									footPlant = it.footPlant == true,
									mocapMode = it.mocapMode == true,
									useTrackerPositions = it.useTrackerPositions == true,
									enforceConstraints = it.enforceConstraints == true,
									correctConstraints = it.correctConstraints == true,
								)
							} ?: oldConfig.toggles,
							ratios = req.ratios?.let {
								SkeletonRatiosConfig(
									imputeSpineFromUpperToLower = it.imputeSpineFromUpperToLower
										?: oldConfig.ratios.imputeSpineFromUpperToLower,
									imputeSpineCurvature = it.imputeSpineCurvature
										?: oldConfig.ratios.imputeSpineCurvature,
									interpolateHipWithKnees = it.interpolateHipWithKnees
										?: oldConfig.ratios.interpolateHipWithKnees,
									interpolateComputedKneesWithAnkles = it.interpolateComputedKneesWithAnkles
										?: oldConfig.ratios.interpolateComputedKneesWithAnkles,
									interpolateKneesWithAnkles = it.interpolateKneesWithAnkles
										?: oldConfig.ratios.interpolateKneesWithAnkles,
									skatingCorrectionStrength = it.skatingCorrectionStrength
										?: oldConfig.ratios.skatingCorrectionStrength,
								)
							} ?: oldConfig.ratios,
							filtering = req.filtering?.let {
								SkeletonFilteringConfig(
									type = it.type ?: oldConfig.filtering.type,
									amount = it.amount ?: oldConfig.filtering.amount,
								)
							} ?: oldConfig.filtering,
						),
					)
				},
			)
		}

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
		}

		receiver.rpcDispatcher.on<TrackingPauseStateRequest> {
			receiver.sendRpc(
				TrackingPauseStateResponse(
					trackingPaused = receiver.appContext.skeleton.context.state.value.paused,
				),
			)
		}
	}
}
