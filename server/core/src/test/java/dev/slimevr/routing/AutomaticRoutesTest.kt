package dev.slimevr.routing

import dev.slimevr.driver.DRIVER_SUPPORTED_BONES
import dev.slimevr.vmc.VMC_SUPPORTED_BONES
import dev.slimevr.vrcosc.VRC_OSC_SUPPORTED_BONES
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.RoutingOutputState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Builds an OutputStates with only the named outputs ACTIVE. */
private fun active(vararg outputs: RoutingOutput): OutputStates = RoutingOutput.entries.associateWith {
	if (it in outputs) RoutingOutputState.ACTIVE else RoutingOutputState.INACTIVE
}

private val NONE_ACTIVE = active()

/**
 * Bones a typical full body setup produces. Deliberately includes lower legs, which
 * no output can take, so the tests also cover candidates being dropped.
 */
private val COMMON_CANDIDATES = setOf(
	BodyPart.HIP,
	BodyPart.UPPER_CHEST,
	BodyPart.UPPER_CHEST,
	BodyPart.LEFT_UPPER_LEG,
	BodyPart.RIGHT_UPPER_LEG,
	BodyPart.LEFT_LOWER_LEG,
	BodyPart.RIGHT_LOWER_LEG,
)

private fun routedTo(
	routes: Map<BodyPart, Set<RoutingOutput>>,
	output: RoutingOutput,
): Set<BodyPart> = routes.filterValues { output in it }.keys

class AutomaticRoutesTest {
	@Test
	fun `the driver alone takes every bone it supports`() {
		val routes = computeAutomaticRoutes(COMMON_CANDIDATES, active(RoutingOutput.DRIVER))

		assertEquals(COMMON_CANDIDATES intersect DRIVER_SUPPORTED_BONES, routedTo(routes, RoutingOutput.DRIVER))
		assertEquals(emptySet(), routedTo(routes, RoutingOutput.VRC_OSC))
		assertEquals(emptySet(), routedTo(routes, RoutingOutput.VMC))
	}

	@Test
	fun `OSC becomes primary when no driver is connected`() {
		val routes = computeAutomaticRoutes(COMMON_CANDIDATES, active(RoutingOutput.VRC_OSC))

		assertEquals(COMMON_CANDIDATES intersect VRC_OSC_SUPPORTED_BONES, routedTo(routes, RoutingOutput.VRC_OSC))
		assertEquals(emptySet(), routedTo(routes, RoutingOutput.DRIVER))
	}

	@Test
	fun `OSC only gets bones the driver cannot take`() {
		val routes = computeAutomaticRoutes(
			COMMON_CANDIDATES,
			active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC),
		)

		// The dedup contract: a bone never goes to both, so no duplicate trackers.
		assertTrue(routes.values.none { RoutingOutput.DRIVER in it && RoutingOutput.VRC_OSC in it })
		// OSC is left with only what it supports and the driver did not already take.
		assertEquals(
			(COMMON_CANDIDATES intersect VRC_OSC_SUPPORTED_BONES) - DRIVER_SUPPORTED_BONES,
			routedTo(routes, RoutingOutput.VRC_OSC),
		)
	}

	@Test
	fun `a bone neither can take goes nowhere`() {
		// NECK is VMC only.
		val routes = computeAutomaticRoutes(
			setOf(BodyPart.NECK),
			active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC),
		)

		assertEquals(emptySet(), routedTo(routes, RoutingOutput.DRIVER))
		assertEquals(emptySet(), routedTo(routes, RoutingOutput.VRC_OSC))
	}

	@Test
	fun `VMC takes every supported bone regardless of candidates`() {
		val states = active(RoutingOutput.VMC)

		val routes = effectiveRoutes(computeAutomaticRoutes(emptySet(), states), states)

		assertEquals(VMC_SUPPORTED_BONES, routedTo(routes, RoutingOutput.VMC))
	}

	@Test
	fun `VMC does not disturb the priority chain`() {
		val withoutVmcStates = active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC)
		val withVmcStates = active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC)

		val withoutVmc = effectiveRoutes(
			computeAutomaticRoutes(COMMON_CANDIDATES, withoutVmcStates),
			withoutVmcStates,
		)
		val withVmc = effectiveRoutes(
			computeAutomaticRoutes(COMMON_CANDIDATES, withVmcStates),
			withVmcStates,
		)

		assertEquals(routedTo(withoutVmc, RoutingOutput.DRIVER), routedTo(withVmc, RoutingOutput.DRIVER))
		assertEquals(routedTo(withoutVmc, RoutingOutput.VRC_OSC), routedTo(withVmc, RoutingOutput.VRC_OSC))
		assertEquals(VMC_SUPPORTED_BONES, routedTo(withVmc, RoutingOutput.VMC))
	}

	@Test
	fun `nothing is routed when every output is off`() {
		assertEquals(emptyMap(), computeAutomaticRoutes(COMMON_CANDIDATES, NONE_ACTIVE))
	}

	@Test
	fun `an unsupported output is never routed to`() {
		// Android: the driver IPC does not exist, so OSC has to take over.
		val states = mapOf(
			RoutingOutput.DRIVER to RoutingOutputState.UNSUPPORTED,
			RoutingOutput.VRC_OSC to RoutingOutputState.ACTIVE,
			RoutingOutput.VMC to RoutingOutputState.INACTIVE,
		)

		val routes = computeAutomaticRoutes(COMMON_CANDIDATES, states)

		assertEquals(emptySet(), routedTo(routes, RoutingOutput.DRIVER))
		assertEquals(COMMON_CANDIDATES intersect VRC_OSC_SUPPORTED_BONES, routedTo(routes, RoutingOutput.VRC_OSC))
	}

	@Test
	fun `outputs on the priority chain conflict with each other`() {
		// Both reach VRChat, so a bone on both arrives there twice.
		assertEquals(setOf(RoutingOutput.VRC_OSC), conflictingOutputs(RoutingOutput.DRIVER))
		assertEquals(setOf(RoutingOutput.DRIVER), conflictingOutputs(RoutingOutput.VRC_OSC))
	}

	@Test
	fun `VMC conflicts with nothing`() {
		// A separate app entirely, so sharing a bone with it duplicates nothing.
		assertEquals(emptySet(), conflictingOutputs(RoutingOutput.VMC))
	}

	@Test
	fun `automatic never produces a conflicting route`() {
		val routes = computeAutomaticRoutes(COMMON_CANDIDATES, active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC))

		for ((_, outputs) in routes) {
			for (output in outputs) {
				assertTrue(outputs.none { it != output && it in conflictingOutputs(output) })
			}
		}
	}

	@Test
	fun `an enabled output that is not transmitting does not hold on to bones`() {
		// The driver is on but nothing is connected, OSC is on but has found no target.
		// Neither transmits, so neither should be handed anything.
		val states = mapOf(
			RoutingOutput.DRIVER to RoutingOutputState.ENABLED,
			RoutingOutput.VRC_OSC to RoutingOutputState.ENABLED,
			RoutingOutput.VMC to RoutingOutputState.INACTIVE,
		)

		assertEquals(emptyMap(), computeAutomaticRoutes(COMMON_CANDIDATES, states))
	}

	@Test
	fun `an enabled output lets the next one in the chain take the bones`() {
		// The driver would win the chain, but it is not transmitting, so OSC takes over
		// rather than the bones being reserved for something with nobody listening.
		val states = mapOf(
			RoutingOutput.DRIVER to RoutingOutputState.ENABLED,
			RoutingOutput.VRC_OSC to RoutingOutputState.ACTIVE,
			RoutingOutput.VMC to RoutingOutputState.INACTIVE,
		)

		val routes = computeAutomaticRoutes(COMMON_CANDIDATES, states)

		assertEquals(emptySet(), routedTo(routes, RoutingOutput.DRIVER))
		assertEquals(COMMON_CANDIDATES intersect VRC_OSC_SUPPORTED_BONES, routedTo(routes, RoutingOutput.VRC_OSC))
	}
}
