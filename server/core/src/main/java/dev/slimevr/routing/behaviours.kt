package dev.slimevr.routing

import dev.slimevr.AppContextProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BoneRoutingBasicBehaviour(private val appContext: AppContextProvider) : BoneRoutingBehaviour {
	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: BoneRoutingManager) {
		val server = appContext.server
		val settings = appContext.config.settings

		combine(
			settings.context.state.map { it.data.boneRoutingConfig }.distinctUntilChanged(),
			outputStatesFlow(appContext),
			::Pair,
		)
			.onEach { (config, outputStates) ->
				if (!config.automatic) {
					receiver.context.dispatch(
						BoneRoutingActions.SetRoutes(
							effectiveRoutes(config.manualRoutes.orEmpty(), outputStates),
						),
					)
				}
			}
			.flatMapLatest { (config, outputStates) ->
				if (!config.automatic) return@flatMapLatest emptyFlow()

				server.context.state
					.map { it.trackers.values }
					.flatMapLatest { trackers ->
						// Tracker state emits on every rotation packet, but only bodyPart/status matter here.
						// Dedup per tracker first, or combine gets resumed once per packet per tracker.
						combine(
							trackers.map { tracker ->
								tracker.context.state.distinctUntilChanged { a, b -> a.bodyPart == b.bodyPart && a.status == b.status }
							},
						) { states -> trackedBodyParts(states.asList()) }
							.distinctUntilChanged()
					}
					.map { fineBodyParts -> Triple(config, outputStates, fineBodyParts) }
			}
			.onEach { (config, outputStates, fineBodyParts) ->
				val candidates = determineCandidateBones(fineBodyParts)
				receiver.context.dispatch(
					BoneRoutingActions.SetRoutes(
						effectiveRoutes(
							computeAutomaticRoutes(candidates, outputStates) + overrideRoutes(config),
							outputStates,
						),
					),
				)
			}
			.launchIn(receiver.context.scope)
	}
}
