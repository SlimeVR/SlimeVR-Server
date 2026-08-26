package dev.slimevr.routing

import dev.slimevr.AppContextProvider
import dev.slimevr.config.BoneRoutingConfig
import dev.slimevr.driver.DRIVER_SUPPORTED_BONES
import dev.slimevr.driver.DriverBridgeSource
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.isActive
import dev.slimevr.vmc.VMC_SUPPORTED_BONES
import dev.slimevr.vrcosc.VRC_OSC_SUPPORTED_BONES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.RoutingOutputState
import solarxr_protocol.rpc.VRCOSCOutputState

typealias Routes = Map<BodyPart, Set<RoutingOutput>>
typealias OutputStates = Map<RoutingOutput, RoutingOutputState>

/** Every output treated as on, for what the user asked for rather than what is sent. */
private val ALL_ACTIVE: OutputStates = RoutingOutput.entries.associateWith { RoutingOutputState.ACTIVE }

private val PRIORITY_CHAIN = listOf(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC)

fun acceptedBones(output: RoutingOutput): Set<BodyPart> = when (output) {
	RoutingOutput.DRIVER -> DRIVER_SUPPORTED_BONES
	RoutingOutput.VRC_OSC -> VRC_OSC_SUPPORTED_BONES
	RoutingOutput.VMC -> VMC_SUPPORTED_BONES
}

fun conflictingOutputs(output: RoutingOutput): Set<RoutingOutput> = when (output) {
	in PRIORITY_CHAIN -> PRIORITY_CHAIN.toSet() - output
	else -> emptySet()
}

/** Bones an output needs to work at all, so the user cannot turn them off. */
fun requiredBones(output: RoutingOutput): Set<BodyPart> = when (output) {
	RoutingOutput.DRIVER, RoutingOutput.VRC_OSC -> emptySet()
	RoutingOutput.VMC -> VMC_SUPPORTED_BONES
}

/**
 * Bones automatic never routes on its own. Sending a hand to the driver takes over the
 * real controllers, so it stays the user's call whatever the mode.
 */
val OVERRIDABLE_BONES: Set<BodyPart> = setOf(BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND)

fun overridableBones(output: RoutingOutput): Set<BodyPart> = OVERRIDABLE_BONES intersect acceptedBones(output) subtract requiredBones(output)

fun overrideRoutes(config: BoneRoutingConfig): Routes = config.manualRoutes.orEmpty().filterKeys { it in OVERRIDABLE_BONES }

fun isActive(states: OutputStates, output: RoutingOutput): Boolean = states[output] == RoutingOutputState.ACTIVE

fun driverStateFlow(appContext: AppContextProvider): Flow<RoutingOutputState> = combine(
	appContext.config.settings.context.state.map { it.data.driverConfig.enabled },
	appContext.server.context.state.map { state ->
		state.drivers.values.any { it.source == DriverBridgeSource.DRIVER }
	},
	appContext.server.context.state.map { state -> state.solarxr.values.any { it.context.state.value.driverName != null } }.distinctUntilChanged(),
) { enabled, driverConnected, solarxrDriverConnected ->
	when {
		!appContext.featureFlags.supportsDriver -> RoutingOutputState.UNSUPPORTED
		!enabled -> RoutingOutputState.INACTIVE
		driverConnected || solarxrDriverConnected -> RoutingOutputState.ACTIVE
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
	val requested = if (config.automatic) sent else config.manualRoutes.orEmpty()
	effectiveRoutes(requested + overrideRoutes(config), ALL_ACTIVE)
}.distinctUntilChanged()

/**
 * What actually gets sent: the requested routes plus whatever each output requires,
 * minus anything an output cannot accept or cannot currently send.
 */
fun effectiveRoutes(routes: Routes, outputStates: OutputStates): Routes {
	val result = mutableMapOf<BodyPart, Set<RoutingOutput>>()
	for ((bone, outputs) in routes) {
		val kept = outputs.filterTo(mutableSetOf()) {
			isActive(outputStates, it) && bone in acceptedBones(it)
		}
		if (kept.isNotEmpty()) result[bone] = kept
	}
	for (output in RoutingOutput.entries) {
		if (!isActive(outputStates, output)) continue
		for (bone in requiredBones(output)) {
			result[bone] = result[bone].orEmpty() + output
		}
	}
	return result
}

fun applyRoutingChange(
	config: BoneRoutingConfig,
	automatic: Boolean,
	routes: Routes,
): BoneRoutingConfig {
	// Switching modes only flips the switch: each mode keeps the picks left in it.
	if (automatic != config.automatic) return config.copy(automatic = automatic)

	val requested = routes
		.mapValues { (bone, outputs) ->
			outputs.filterTo(mutableSetOf()) { bone in acceptedBones(it) && bone !in requiredBones(it) }
		}
		.filterValues { it.isNotEmpty() }

	return config.copy(
		manualRoutes = if (automatic) {
			// Automatic owns every other bone, so only the overrides come from the request.
			config.manualRoutes.orEmpty().filterKeys { it !in OVERRIDABLE_BONES } +
				requested.filterKeys { it in OVERRIDABLE_BONES }
		} else {
			requested
		},
	)
}

private val candidateToFineBodyParts = mapOf(
	BodyPart.UPPER_CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.CHEST),
	BodyPart.LEFT_UPPER_ARM to setOf(BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM),
	BodyPart.RIGHT_UPPER_ARM to setOf(BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_ARM),
	BodyPart.HIP to setOf(BodyPart.HIP, BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),
	BodyPart.LEFT_UPPER_LEG to setOf(BodyPart.LEFT_UPPER_LEG),
	BodyPart.RIGHT_UPPER_LEG to setOf(BodyPart.RIGHT_UPPER_LEG),
	BodyPart.LEFT_FOOT to setOf(BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG, BodyPart.LEFT_UPPER_LEG),
	BodyPart.RIGHT_FOOT to setOf(BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG, BodyPart.RIGHT_UPPER_LEG),
)

/** Tracker body parts that count as present. Loopback trackers never enable an output. */
fun trackedBodyParts(trackers: Collection<TrackerState>): Set<BodyPart?> = trackers
	.filter {
		it.status.isActive() &&
			it.origin != DeviceOrigin.DRIVER &&
			it.origin != DeviceOrigin.VRC &&
			it.origin != DeviceOrigin.VMC
	}
	.map { it.bodyPart }
	.toSet()

fun determineCandidateBones(fineBodyParts: Set<BodyPart?>): Set<BodyPart> = candidateToFineBodyParts
	.filterValues { it.any { bp -> bp in fineBodyParts } }
	.keys

/** Hands each bone to the best output that is on and can take it, so nothing is sent twice. */
fun computeAutomaticRoutes(candidateBones: Set<BodyPart>, outputStates: OutputStates): Routes {
	val chain = PRIORITY_CHAIN.filter { isActive(outputStates, it) }
	return candidateBones
		.mapNotNull { bone -> chain.firstOrNull { bone in acceptedBones(it) }?.let { bone to setOf(it) } }
		.toMap()
}
