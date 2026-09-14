package dev.slimevr.routing

import dev.slimevr.AppContextProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BoneRoutingBasicBehaviour(private val appContext: AppContextProvider) : BoneRoutingBehaviour {
	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: BoneRoutingManager) {
		val server = appContext.server
		val settings = appContext.config.settings
		val definition = appContext.skeleton.definition

		combine(
			settings.context.state.map { it.data.boneRoutingConfig }.distinctUntilChanged(),
			outputStatesFlow(appContext),
			::Pair,
		)
			.onEach { (config, outputStates) ->
				if (!config.automatic) {
					receiver.context.dispatch(
						BoneRoutingActions.SetRoutes(
							effectiveRoutes(manualRoutesAsBoneIds(config.manualRoutes, definition), outputStates, definition),
						),
					)
				}
			}
			.flatMapLatest { (config, outputStates) ->
				if (!config.automatic) return@flatMapLatest emptyFlow()

				server.context.state
					.map { it.trackers.values }
					.flatMapLatest { trackers ->
						if (trackers.isEmpty()) return@flatMapLatest flowOf(emptySet())

						// Tracker state emits on every rotation packet, but only the assigned bone/status
						// matter here. Dedup per tracker first, or combine gets resumed once per packet
						// per tracker.
						combine(
							trackers.map { tracker ->
								tracker.context.state.distinctUntilChanged { a, b -> a.boneId == b.boneId && a.status == b.status }
							},
						) { states -> trackedBoneIds(states.asList()) }
							.distinctUntilChanged()
					}
					.map { boneIds -> Triple(config, outputStates, boneIds) }
			}
			.onEach { (config, outputStates, boneIds) ->
				val candidates = determineCandidateBones(boneIds, definition)
				receiver.context.dispatch(
					BoneRoutingActions.SetRoutes(
						effectiveRoutes(
							computeAutomaticRoutes(candidates, outputStates, definition) + overrideRoutes(config, definition),
							outputStates,
							definition,
						),
					),
				)
			}
			.launchIn(receiver.context.scope)
	}
}
