package dev.slimevr.routing

import dev.slimevr.config.BoneRoutingConfig
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.RoutingOutputState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private fun active(vararg outputs: RoutingOutput): OutputStates = RoutingOutput.entries.associateWith {
	if (it in outputs) RoutingOutputState.ACTIVE else RoutingOutputState.INACTIVE
}

private val NONE_ACTIVE = active()
private val ALL_ON = active(*RoutingOutput.entries.toTypedArray())

private fun routesOf(vararg pairs: Pair<BodyPart, Set<RoutingOutput>>): Routes = mapOf(*pairs)

private val AUTO_ROUTES = routesOf(
	BodyPart.HIP to setOf(RoutingOutput.DRIVER),
	BodyPart.LEFT_FOOT to setOf(RoutingOutput.DRIVER),
)

private val HAND_TABLE = mapOf(BodyPart.LEFT_HAND to setOf(RoutingOutput.DRIVER))

class ManualRoutesTest {
	@Test
	fun `a bone can be routed to every output at once`() {
		val routes = routesOf(
			BodyPart.HIP to setOf(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC),
		)

		val sanitized = effectiveRoutes(routes, active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC))

		assertEquals(
			setOf(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC),
			sanitized[BodyPart.HIP],
		)
	}

	@Test
	fun `an unavailable output is dropped even when explicitly routed`() {
		val routes = routesOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC))

		val sanitized = effectiveRoutes(routes, active(RoutingOutput.VRC_OSC))

		assertEquals(setOf(RoutingOutput.VRC_OSC), sanitized[BodyPart.HIP])
	}

	@Test
	fun `an output that cannot take the bone is dropped`() {
		val routes = routesOf(BodyPart.NECK to setOf(RoutingOutput.VRC_OSC, RoutingOutput.VMC))

		val sanitized = effectiveRoutes(routes, active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC))

		assertEquals(setOf(RoutingOutput.VMC), sanitized[BodyPart.NECK])
	}

	@Test
	fun `a bone left with no output is not kept`() {
		val routes = routesOf(BodyPart.HIP to setOf(RoutingOutput.VRC_OSC))

		assertEquals(emptyMap(), effectiveRoutes(routes, NONE_ACTIVE))
	}
}

class RoutingChangeTest {
	@Test
	fun `a fresh config is automatic with no manual table`() {
		val config = BoneRoutingConfig()

		assertEquals(true, config.automatic)
		assertNull(config.manualRoutes)
	}

	@Test
	fun `switching to manual the first time seeds from the automatic routes`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = null)

		val next = applyRoutingChange(config, automatic = false, routes = emptyMap(), seed = AUTO_ROUTES)

		assertEquals(false, next.automatic)
		assertEquals(AUTO_ROUTES, next.manualRoutes.orEmpty())
	}

	@Test
	fun `switching to manual again keeps the table the user built`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = HAND_TABLE)

		val next = applyRoutingChange(config, automatic = false, routes = AUTO_ROUTES, seed = AUTO_ROUTES)

		assertEquals(HAND_TABLE, next.manualRoutes)
	}

	@Test
	fun `switching to manual again keeps an empty table the user cleared`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = emptyMap())

		val next = applyRoutingChange(config, automatic = false, routes = AUTO_ROUTES, seed = AUTO_ROUTES)

		assertEquals(emptyMap(), next.manualRoutes)
	}

	@Test
	fun `editing while manual stores the new table`() {
		val config = BoneRoutingConfig(automatic = false, manualRoutes = HAND_TABLE)
		val edited = mapOf(BodyPart.HIP to setOf(RoutingOutput.VRC_OSC))

		val next = applyRoutingChange(config, automatic = false, routes = edited, seed = AUTO_ROUTES)

		assertEquals(edited, next.manualRoutes)
	}

	@Test
	fun `switching back to automatic keeps the manual table`() {
		val config = BoneRoutingConfig(automatic = false, manualRoutes = HAND_TABLE)

		val next = applyRoutingChange(config, automatic = true, routes = emptyMap(), seed = AUTO_ROUTES)

		assertEquals(true, next.automatic)
		assertEquals(HAND_TABLE, next.manualRoutes)
	}

	@Test
	fun `staying automatic never creates a manual table`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = null)

		val next = applyRoutingChange(config, automatic = true, routes = AUTO_ROUTES, seed = AUTO_ROUTES)

		assertNull(next.manualRoutes)
	}

	@Test
	fun `a full round trip through automatic does not lose the manual table`() {
		var config = BoneRoutingConfig(automatic = true, manualRoutes = null)

		config = applyRoutingChange(config, automatic = false, routes = emptyMap(), seed = AUTO_ROUTES)
		config = applyRoutingChange(config, automatic = false, routes = HAND_TABLE, seed = AUTO_ROUTES)
		config = applyRoutingChange(config, automatic = true, routes = HAND_TABLE, seed = AUTO_ROUTES)
		config = applyRoutingChange(config, automatic = false, routes = AUTO_ROUTES, seed = AUTO_ROUTES)

		assertEquals(HAND_TABLE, config.manualRoutes)
	}

	@Test
	fun `the seed carries outputs that are merely off`() {
		val allOn = RoutingOutput.entries.associateWith { RoutingOutputState.ACTIVE }
		val seed = computeAutomaticRoutes(setOf(BodyPart.HIP), allOn)

		val config = applyRoutingChange(
			BoneRoutingConfig(automatic = true, manualRoutes = null),
			automatic = false,
			routes = emptyMap(),
			seed = seed,
		)

		assertEquals(setOf(RoutingOutput.DRIVER), config.manualRoutes.orEmpty()[BodyPart.HIP])
	}
}

class ForcedRoutesTest {
	@Test
	fun `required bones are never stored, the server owns them`() {
		val config = applyRoutingChange(
			BoneRoutingConfig(automatic = false, manualRoutes = emptyMap()),
			automatic = false,
			routes = mapOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER, RoutingOutput.VMC)),
			seed = AUTO_ROUTES,
		)

		assertEquals(mapOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER)), config.manualRoutes)
	}

	@Test
	fun `a bone the user cannot edit is dropped entirely from storage`() {
		val config = applyRoutingChange(
			BoneRoutingConfig(automatic = false, manualRoutes = emptyMap()),
			automatic = false,
			routes = mapOf(BodyPart.NECK to setOf(RoutingOutput.VMC)),
			seed = AUTO_ROUTES,
		)

		assertEquals(emptyMap(), config.manualRoutes)
	}

	@Test
	fun `required bones come back when the routes are resolved`() {
		val config = BoneRoutingConfig(
			automatic = false,
			manualRoutes = mapOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER)),
		)

		val resolved = effectiveRoutes(
			config.manualRoutes.orEmpty(),
			active(RoutingOutput.DRIVER, RoutingOutput.VMC),
		)

		assertEquals(setOf(RoutingOutput.DRIVER, RoutingOutput.VMC), resolved[BodyPart.HIP])
		assertEquals(setOf(RoutingOutput.VMC), resolved[BodyPart.NECK])
	}

	@Test
	fun `an inactive output does not force anything`() {
		val config = BoneRoutingConfig(automatic = false, manualRoutes = emptyMap())

		val resolved = effectiveRoutes(config.manualRoutes.orEmpty(), active(RoutingOutput.DRIVER))

		assertEquals(emptyMap(), resolved)
	}

	@Test
	fun `the page shows required bones ticked whatever is stored`() {
		val shown = effectiveRoutes(emptyMap(), ALL_ON)

		assertEquals(setOf(RoutingOutput.VMC), shown[BodyPart.NECK])
	}

	@Test
	fun `automatic gets required bones through the same step as manual`() {
		val states = active(RoutingOutput.DRIVER, RoutingOutput.VMC)

		val automatic = effectiveRoutes(computeAutomaticRoutes(setOf(BodyPart.HIP), states), states)
		val manual = effectiveRoutes(mapOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER)), states)

		assertEquals(automatic[BodyPart.NECK], manual[BodyPart.NECK])
		assertEquals(automatic[BodyPart.HIP], manual[BodyPart.HIP])
	}

	@Test
	fun `everything VMC accepts it also requires`() {
		assertEquals(acceptedBones(RoutingOutput.VMC), requiredBones(RoutingOutput.VMC))
		assertEquals(emptySet(), requiredBones(RoutingOutput.DRIVER))
	}
}
