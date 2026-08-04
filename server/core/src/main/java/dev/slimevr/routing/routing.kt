package dev.slimevr.routing

import dev.slimevr.AppContextProvider
import dev.slimevr.config.BoneRoutingConfig
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.driver.DRIVER_SUPPORTED_BONES
import dev.slimevr.tracker.TrackerState
import dev.slimevr.vmc.VMC_SUPPORTED_BONES
import dev.slimevr.vrcosc.VRC_OSC_SUPPORTED_BONES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import solarxr_protocol.datatypes.BodyPart
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

fun isActive(states: OutputStates, output: RoutingOutput): Boolean = states[output] == RoutingOutputState.ACTIVE

fun platformSupports(appContext: AppContextProvider, output: RoutingOutput): Boolean = when (output) {
	RoutingOutput.DRIVER -> appContext.featureFlags.supportsDriver
	RoutingOutput.VRC_OSC, RoutingOutput.VMC -> true
}

/** Emits the output states as the driver connects and the output configs change. */
fun outputStatesFlow(appContext: AppContextProvider): Flow<OutputStates> {
	val settings = appContext.config.settings

	return combine(
		appContext.server.context.state.map { it.drivers.isNotEmpty() }.distinctUntilChanged(),
		settings.context.state.map { it.data.vrcOscConfig.enabled }.distinctUntilChanged(),
		settings.context.state.map { it.data.vmcConfig.enabled }.distinctUntilChanged(),
		appContext.vrcOscManager.context.state
			.map { it.status.outputState == VRCOSCOutputState.READY }
			.distinctUntilChanged(),
	) { driverConnected, vrcOscEnabled, vmcEnabled, vrcOscHasTarget ->
		RoutingOutput.entries.associateWith { output ->
			if (!platformSupports(appContext, output)) {
				RoutingOutputState.UNSUPPORTED
			} else {
				when (output) {
					RoutingOutput.DRIVER -> if (driverConnected) RoutingOutputState.ACTIVE else RoutingOutputState.INACTIVE
					RoutingOutput.VRC_OSC -> when {
						!vrcOscEnabled -> RoutingOutputState.INACTIVE
						vrcOscHasTarget -> RoutingOutputState.ACTIVE
						else -> RoutingOutputState.ENABLED
					}
					// Writes to a fixed address whether or not anything is there, so being
					// on is the same as transmitting.
					// FIXME: Maybe we should assume that we need to receive data from VMC for it to be considered working?
					RoutingOutput.VMC -> if (vmcEnabled) RoutingOutputState.ACTIVE else RoutingOutputState.INACTIVE
				}
			}
		}
	}
}

/** What the user asked for, including outputs that are switched off. */
fun intendedRoutesFlow(appContext: AppContextProvider): Flow<Routes> = combine(
	appContext.config.settings.context.state.map { it.data.boneRoutingConfig }.distinctUntilChanged(),
	appContext.boneRouting.context.state.map { it.routes },
) { config, sent ->
	effectiveRoutes(if (config.automatic) sent else config.manualRoutes.orEmpty(), ALL_ACTIVE)
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

/**
 * The table a first switch to manual starts from: what automatic would route if every
 * output this platform has were switched on. An output that is merely off still gets its
 * bones, so switching it on later actually sends something.
 *
 * TODO: maybe this is bad, or annoying logic. we could default to just empty routes when switching to manual
 */
fun seedManualRoutes(appContext: AppContextProvider): Routes {
	val config = appContext.config.settings.context.state.value.data.boneRoutingConfig
	val trackers = appContext.server.context.state.value.trackers.values.map { it.context.state.value }
	val allEnabled = RoutingOutput.entries.associateWith {
		if (platformSupports(appContext, it)) RoutingOutputState.ACTIVE else RoutingOutputState.UNSUPPORTED
	}

	return computeAutomaticRoutes(determineCandidateBones(config, trackedBodyParts(trackers)), allEnabled)
}

fun applyRoutingChange(
	config: BoneRoutingConfig,
	automatic: Boolean,
	routes: Routes,
	seed: Routes,
): BoneRoutingConfig {
	fun sanitized(source: Routes): Routes = source
		.mapValues { (bone, outputs) ->
			outputs.filterTo(mutableSetOf()) { bone in acceptedBones(it) && bone !in requiredBones(it) }
		}
		.filterValues { it.isNotEmpty() }

	return config.copy(
		automatic = automatic,
		manualRoutes = when {
			automatic -> config.manualRoutes
			config.automatic -> config.manualRoutes ?: sanitized(seed)
			else -> sanitized(routes)
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
		(it.status == TrackerStatus.OK || it.status == TrackerStatus.SLEEPING) &&
			it.origin != DeviceOrigin.DRIVER &&
			it.origin != DeviceOrigin.VRC
	}
	.map { it.bodyPart }
	.toSet()

fun determineCandidateBones(config: BoneRoutingConfig, fineBodyParts: Set<BodyPart?>): Set<BodyPart> {
	val candidates = candidateToFineBodyParts
		.filterValues { it.any { bp -> bp in fineBodyParts } }
		.keys
	// Hands aren't toggled automatically
	val handBones = config.manualRoutes.orEmpty().keys.filter {
		it == BodyPart.LEFT_HAND || it == BodyPart.RIGHT_HAND
	}
	return candidates + handBones
}

/** Hands each bone to the best output that is on and can take it, so nothing is sent twice. */
fun computeAutomaticRoutes(candidateBones: Set<BodyPart>, outputStates: OutputStates): Routes {
	val chain = PRIORITY_CHAIN.filter { isActive(outputStates, it) }
	return candidateBones
		.mapNotNull { bone -> chain.firstOrNull { bone in acceptedBones(it) }?.let { bone to setOf(it) } }
		.toMap()
}
