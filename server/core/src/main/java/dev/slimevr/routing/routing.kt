package dev.slimevr.routing

import dev.slimevr.AppContextProvider
import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.CompiledSkeleton
import dev.slimevr.config.BoneRoutingConfig
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.isActive
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.RoutingOutputState
import solarxr_protocol.rpc.VRCOSCOutputState
import kotlin.collections.map

typealias Routes = Map<BoneId, Set<RoutingOutput>>
typealias OutputStates = Map<RoutingOutput, RoutingOutputState>

/** Every output treated as on, for what the user asked for rather than what is sent. */
private val ALL_ACTIVE: OutputStates = RoutingOutput.entries.associateWith { RoutingOutputState.ACTIVE }

private val PRIORITY_CHAIN = listOf(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC)

fun acceptedBones(output: RoutingOutput, definition: CompiledSkeleton): Set<BoneId> = definition.acceptedBones(output)

fun conflictingOutputs(output: RoutingOutput): Set<RoutingOutput> = when (output) {
	in PRIORITY_CHAIN -> PRIORITY_CHAIN.toSet() - output
	else -> emptySet()
}

/** Bones an output needs to work at all, so the user cannot turn them off. */
fun requiredBones(output: RoutingOutput, definition: CompiledSkeleton): Set<BoneId> = definition.requiredBones(output)

/**
 * Bones automatic never routes on its own. Sending a hand to the driver takes over the
 * real controllers, so it stays the user's call whatever the mode.
 */
fun overridableBones(output: RoutingOutput, definition: CompiledSkeleton): Set<BoneId> = definition.overridableBones intersect acceptedBones(output, definition) subtract requiredBones(output, definition)

fun manualRoutesAsBoneIds(manualRoutes: Map<String, Set<RoutingOutput>>?, definition: CompiledSkeleton): Routes = manualRoutes.orEmpty().mapNotNull { (key, outputs) -> definition.registry[key]?.let { it to outputs } }.toMap()

fun Routes.toManualRoutesConfig(definition: CompiledSkeleton): Map<String, Set<RoutingOutput>> = mapNotNull { (boneId, outputs) -> definition.registry.keyOf(boneId)?.let { it to outputs } }.toMap()

fun overrideRoutes(config: BoneRoutingConfig, definition: CompiledSkeleton): Routes = manualRoutesAsBoneIds(config.manualRoutes, definition).filterKeys { it in definition.overridableBones }

fun isActive(states: OutputStates, output: RoutingOutput): Boolean = states[output] == RoutingOutputState.ACTIVE

@OptIn(ExperimentalCoroutinesApi::class)
fun driverStateFlow(appContext: AppContextProvider): Flow<RoutingOutputState> = combine(
	appContext.config.settings.context.state.map { it.data.driverConfig.enabled },
	appContext.server.context.state.map { it.solarxr }.distinctUntilChanged().flatMapLatest { solarXRBridges ->
		combine(solarXRBridges.values.map { it.context.state }) { states ->
			states.any { it.driverName != null }
		}
	},
) { enabled, solarXRDriverConnected ->
	when {
		!appContext.featureFlags.supportsDriver -> RoutingOutputState.UNSUPPORTED
		!enabled -> RoutingOutputState.INACTIVE
		solarXRDriverConnected -> RoutingOutputState.ACTIVE
		else -> RoutingOutputState.ENABLED
	}
}.distinctUntilChanged()

private fun vrcOscStateFlow(appContext: AppContextProvider): Flow<RoutingOutputState> = combine(
	appContext.config.settings.context.state.map { it.data.vrcOscConfig.enabled },
	appContext.vrcOscManager.context.state.map { it.status.outputState == VRCOSCOutputState.READY },
) { enabled, hasTarget ->
	when {
		!enabled -> RoutingOutputState.INACTIVE
		hasTarget -> RoutingOutputState.ACTIVE
		else -> RoutingOutputState.ENABLED
	}
}.distinctUntilChanged()

private fun vmcStateFlow(appContext: AppContextProvider): Flow<RoutingOutputState> = appContext.config.settings.context.state
	.map { if (it.data.vmcConfig.enabled) RoutingOutputState.ACTIVE else RoutingOutputState.INACTIVE }
	.distinctUntilChanged()

/** Emits the output states as the driver connects and the output configs change. */
fun outputStatesFlow(appContext: AppContextProvider): Flow<OutputStates> = combine(
	driverStateFlow(appContext),
	vrcOscStateFlow(appContext),
	vmcStateFlow(appContext),
) { driver, vrcOsc, vmc ->
	mapOf(
		RoutingOutput.DRIVER to driver,
		RoutingOutput.VRC_OSC to vrcOsc,
		RoutingOutput.VMC to vmc,
	)
}

/** What the user asked for, including outputs that are switched off. */
fun intendedRoutesFlow(appContext: AppContextProvider): Flow<Routes> = combine(
	appContext.config.settings.context.state.map { it.data.boneRoutingConfig }.distinctUntilChanged(),
	appContext.boneRouting.context.state.map { it.routes },
) { config, sent ->
	val definition = appContext.skeleton.definition
	val requested = if (config.automatic) sent else manualRoutesAsBoneIds(config.manualRoutes, definition)
	effectiveRoutes(requested + overrideRoutes(config, definition), ALL_ACTIVE, definition)
}.distinctUntilChanged()

/**
 * What actually gets sent: the requested routes plus whatever each output requires,
 * minus anything an output cannot accept or cannot currently send.
 */
fun effectiveRoutes(routes: Routes, outputStates: OutputStates, definition: CompiledSkeleton): Routes {
	val result = mutableMapOf<BoneId, Set<RoutingOutput>>()
	for ((bone, outputs) in routes) {
		val kept = outputs.filterTo(mutableSetOf()) { isActive(outputStates, it) && bone in acceptedBones(it, definition) }
		if (kept.isNotEmpty()) result[bone] = kept
	}
	for (output in RoutingOutput.entries) {
		if (!isActive(outputStates, output)) continue
		for (bone in requiredBones(output, definition)) {
			result[bone] = result[bone].orEmpty() + output
		}
	}
	return result
}

fun applyRoutingChange(
	config: BoneRoutingConfig,
	automatic: Boolean,
	routes: Routes,
	definition: CompiledSkeleton,
): BoneRoutingConfig {
	// Switching modes only flips the switch: each mode keeps the picks left in it.
	if (automatic != config.automatic) return config.copy(automatic = automatic)

	val requested = routes
		.mapValues { (bone, outputs) -> outputs.filterTo(mutableSetOf()) { bone in acceptedBones(it, definition) && bone !in requiredBones(it, definition) } }
		.filterValues { it.isNotEmpty() }

	val overridableIds = definition.overridableBones
	return config.copy(
		manualRoutes = if (automatic) {
			// Automatic owns every other bone, so only the overrides come from the request.
			(
				manualRoutesAsBoneIds(config.manualRoutes, definition).filterKeys { it !in overridableIds } +
					requested.filterKeys { it in overridableIds }
				).toManualRoutesConfig(definition)
		} else {
			requested.toManualRoutesConfig(definition)
		},
	)
}

/** Tracker bones that count as present. Loopback trackers never enable an output. */
fun trackedBoneIds(trackers: Collection<TrackerState>): Set<BoneId?> = trackers
	.filter {
		it.status.isActive() &&
			it.origin != DeviceOrigin.DRIVER &&
			it.origin != DeviceOrigin.VRC &&
			it.origin != DeviceOrigin.VMC
	}
	.map { it.boneId }
	.toSet()

fun determineCandidateBones(trackedBoneIds: Set<BoneId?>, definition: CompiledSkeleton): Set<BoneId> = definition.candidateBones
	.filterTo(mutableSetOf()) { candidate -> definition.candidateSourcesOf(candidate).any { it in trackedBoneIds } }

/** Hands each bone to the best output that is on and can take it, so nothing is sent twice. */
fun computeAutomaticRoutes(candidateBones: Set<BoneId>, outputStates: OutputStates, definition: CompiledSkeleton): Routes {
	val chain = PRIORITY_CHAIN.filter { isActive(outputStates, it) }
	return candidateBones
		.mapNotNull { bone -> chain.firstOrNull { bone in acceptedBones(it, definition) }?.let { output -> bone to setOf(output) } }
		.toMap()
}
