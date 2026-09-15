package dev.slimevr.routing

import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.boneId
import dev.slimevr.config.BoneRoutingConfig
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.RoutingOutputState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val registry = BoneRegistry.standard()

private fun active(vararg outputs: RoutingOutput): OutputStates = RoutingOutput.entries.associateWith {
	if (it in outputs) RoutingOutputState.ACTIVE else RoutingOutputState.INACTIVE
}

private val NONE_ACTIVE = active()
private val ALL_ON = active(*RoutingOutput.entries.toTypedArray())

private fun routesOf(vararg pairs: Pair<BodyPart, Set<RoutingOutput>>): Routes = pairs.associate { (bodyPart, outputs) -> bodyPart.boneId to outputs }

private fun manualRoutesOf(manualRoutes: Map<String, Set<RoutingOutput>>?): Routes = manualRoutesAsBoneIds(manualRoutes, registry)

private val AUTO_ROUTES = routesOf(
	BodyPart.HIP to setOf(RoutingOutput.DRIVER),
	BodyPart.LEFT_FOOT to setOf(RoutingOutput.DRIVER),
)

private val HAND_TABLE = routesOf(BodyPart.LEFT_HAND to setOf(RoutingOutput.DRIVER))

class ManualRoutesTest {
	@Test
	fun `a bone can be routed to every output at once`() {
		val routes = routesOf(
			BodyPart.HIP to setOf(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC),
		)

		val sanitized = effectiveRoutes(routes, active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC), registry)

		assertEquals(
			setOf(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC),
			sanitized[BodyPart.HIP.boneId],
		)
	}

	@Test
	fun `an unavailable output is dropped even when explicitly routed`() {
		val routes = routesOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC))

		val sanitized = effectiveRoutes(routes, active(RoutingOutput.VRC_OSC), registry)

		assertEquals(setOf(RoutingOutput.VRC_OSC), sanitized[BodyPart.HIP.boneId])
	}

	@Test
	fun `an output that cannot take the bone is dropped`() {
		val routes = routesOf(BodyPart.NECK to setOf(RoutingOutput.VRC_OSC, RoutingOutput.VMC))

		val sanitized = effectiveRoutes(routes, active(RoutingOutput.DRIVER, RoutingOutput.VRC_OSC, RoutingOutput.VMC), registry)

		assertEquals(setOf(RoutingOutput.VMC), sanitized[BodyPart.NECK.boneId])
	}

	@Test
	fun `a bone left with no output is not kept`() {
		val routes = routesOf(BodyPart.HIP to setOf(RoutingOutput.VRC_OSC))

		assertEquals(emptyMap(), effectiveRoutes(routes, NONE_ACTIVE, registry))
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
	fun `switching to manual the first time starts from an empty table`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = null)

		val next = applyRoutingChange(config, automatic = false, routes = AUTO_ROUTES, registry = registry)

		assertEquals(false, next.automatic)
		assertEquals(emptyMap(), next.manualRoutes.orEmpty())
	}

	@Test
	fun `switching to manual again keeps the table the user built`() {
		val built = routesOf(BodyPart.HIP to setOf(RoutingOutput.VRC_OSC))
		val config = BoneRoutingConfig(automatic = true, manualRoutes = built.toManualRoutesConfig(registry))

		val next = applyRoutingChange(config, automatic = false, routes = AUTO_ROUTES, registry = registry)

		assertEquals(built, manualRoutesOf(next.manualRoutes))
	}

	@Test
	fun `editing while manual stores the new table`() {
		val config = BoneRoutingConfig(automatic = false, manualRoutes = HAND_TABLE.toManualRoutesConfig(registry))
		val edited = routesOf(BodyPart.HIP to setOf(RoutingOutput.VRC_OSC))

		val next = applyRoutingChange(config, automatic = false, routes = edited, registry = registry)

		assertEquals(edited, manualRoutesOf(next.manualRoutes))
	}

	@Test
	fun `switching back to automatic keeps the manual table`() {
		val config = BoneRoutingConfig(automatic = false, manualRoutes = HAND_TABLE.toManualRoutesConfig(registry))

		val next = applyRoutingChange(config, automatic = true, routes = emptyMap(), registry = registry)

		assertEquals(true, next.automatic)
		assertEquals(HAND_TABLE, manualRoutesOf(next.manualRoutes))
	}

	@Test
	fun `staying automatic never creates a manual table`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = null)

		val next = applyRoutingChange(config, automatic = true, routes = AUTO_ROUTES, registry = registry)

		assertEquals(emptyMap(), next.manualRoutes)
	}

	@Test
	fun `a full round trip through automatic does not lose the manual table`() {
		var config = BoneRoutingConfig(automatic = true, manualRoutes = null)
		val built = AUTO_ROUTES + HAND_TABLE

		config = applyRoutingChange(config, automatic = false, routes = emptyMap(), registry = registry)
		config = applyRoutingChange(config, automatic = false, routes = built, registry = registry)
		config = applyRoutingChange(config, automatic = true, routes = emptyMap(), registry = registry)
		config = applyRoutingChange(config, automatic = false, routes = emptyMap(), registry = registry)

		assertEquals(built, manualRoutesOf(config.manualRoutes))
	}
}

class ForcedRoutesTest {
	@Test
	fun `required bones are never stored, the server owns them`() {
		val config = applyRoutingChange(
			BoneRoutingConfig(automatic = false, manualRoutes = emptyMap()),
			automatic = false,
			routes = routesOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER, RoutingOutput.VMC)),
			registry = registry,
		)

		assertEquals(routesOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER)), manualRoutesOf(config.manualRoutes))
	}

	@Test
	fun `a bone the user cannot edit is dropped entirely from storage`() {
		val config = applyRoutingChange(
			BoneRoutingConfig(automatic = false, manualRoutes = emptyMap()),
			automatic = false,
			routes = routesOf(BodyPart.NECK to setOf(RoutingOutput.VMC)),
			registry = registry,
		)

		assertEquals(emptyMap(), config.manualRoutes)
	}

	@Test
	fun `required bones come back when the routes are resolved`() {
		val config = BoneRoutingConfig(
			automatic = false,
			manualRoutes = routesOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER)).toManualRoutesConfig(registry),
		)

		val resolved = effectiveRoutes(
			manualRoutesOf(config.manualRoutes),
			active(RoutingOutput.DRIVER, RoutingOutput.VMC),
			registry,
		)

		assertEquals(setOf(RoutingOutput.DRIVER, RoutingOutput.VMC), resolved[BodyPart.HIP.boneId])
		assertEquals(setOf(RoutingOutput.VMC), resolved[BodyPart.NECK.boneId])
	}

	@Test
	fun `an inactive output does not force anything`() {
		val config = BoneRoutingConfig(automatic = false, manualRoutes = emptyMap())

		val resolved = effectiveRoutes(manualRoutesOf(config.manualRoutes), active(RoutingOutput.DRIVER), registry)

		assertEquals(emptyMap(), resolved)
	}

	@Test
	fun `the page shows required bones ticked whatever is stored`() {
		val shown = effectiveRoutes(emptyMap(), ALL_ON, registry)

		assertEquals(setOf(RoutingOutput.VMC), shown[BodyPart.NECK.boneId])
	}

	@Test
	fun `automatic gets required bones through the same step as manual`() {
		val states = active(RoutingOutput.DRIVER, RoutingOutput.VMC)

		val automatic = effectiveRoutes(computeAutomaticRoutes(setOf(BodyPart.HIP), states, registry), states, registry)
		val manual = effectiveRoutes(routesOf(BodyPart.HIP to setOf(RoutingOutput.DRIVER)), states, registry)

		assertEquals(automatic[BodyPart.NECK.boneId], manual[BodyPart.NECK.boneId])
		assertEquals(automatic[BodyPart.HIP.boneId], manual[BodyPart.HIP.boneId])
	}

	@Test
	fun `everything VMC accepts it also requires`() {
		assertEquals(acceptedBones(RoutingOutput.VMC), requiredBones(RoutingOutput.VMC))
		assertEquals(emptySet(), requiredBones(RoutingOutput.DRIVER))
	}
}

class OverrideRoutesTest {
	@Test
	fun `hands are the user's call on the driver only`() {
		assertEquals(OVERRIDABLE_BONES, overridableBones(RoutingOutput.DRIVER))
		// Never accepted there.
		assertEquals(emptySet(), overridableBones(RoutingOutput.VRC_OSC))
		// Accepted, but required, so it is always on.
		assertEquals(emptySet(), overridableBones(RoutingOutput.VMC))
	}

	@Test
	fun `automatic routes no hands on its own`() {
		val candidates = determineCandidateBones(setOf(BodyPart.LEFT_HAND, BodyPart.HIP))

		val routes = computeAutomaticRoutes(candidates, ALL_ON, registry)

		assertNull(routes[BodyPart.LEFT_HAND.boneId])
	}

	@Test
	fun `an override routes the hand while automatic`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = HAND_TABLE.toManualRoutesConfig(registry))

		val routes = computeAutomaticRoutes(determineCandidateBones(emptySet()), ALL_ON, registry) + overrideRoutes(config, registry)

		assertEquals(setOf(RoutingOutput.DRIVER), routes[BodyPart.LEFT_HAND.boneId])
	}

	@Test
	fun `only the overridable bones of a stored table count as overrides`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = (AUTO_ROUTES + HAND_TABLE).toManualRoutesConfig(registry))

		assertEquals(HAND_TABLE, overrideRoutes(config, registry))
	}

	@Test
	fun `an output that requires the hand cannot be overridden`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = null)

		val next = applyRoutingChange(
			config,
			automatic = true,
			routes = routesOf(BodyPart.LEFT_HAND to setOf(RoutingOutput.VMC)),
			registry = registry,
		)

		assertEquals(emptyMap(), next.manualRoutes)
	}

	@Test
	fun `editing while automatic stores the override and nothing else`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = null)

		val next = applyRoutingChange(config, automatic = true, routes = AUTO_ROUTES + HAND_TABLE, registry = registry)

		assertEquals(HAND_TABLE, manualRoutesOf(next.manualRoutes))
	}

	@Test
	fun `editing while automatic leaves a stored manual table alone`() {
		val stored = routesOf(BodyPart.HIP to setOf(RoutingOutput.VRC_OSC))
		val config = BoneRoutingConfig(automatic = true, manualRoutes = stored.toManualRoutesConfig(registry))

		val next = applyRoutingChange(config, automatic = true, routes = HAND_TABLE, registry = registry)

		assertEquals(stored + HAND_TABLE, manualRoutesOf(next.manualRoutes))
	}

	@Test
	fun `dropping the hand from the request clears the override`() {
		val config = BoneRoutingConfig(automatic = true, manualRoutes = HAND_TABLE.toManualRoutesConfig(registry))

		val next = applyRoutingChange(config, automatic = true, routes = AUTO_ROUTES, registry = registry)

		assertEquals(emptyMap(), next.manualRoutes)
	}

	@Test
	fun `the override survives a round trip through manual`() {
		var config = BoneRoutingConfig(automatic = true, manualRoutes = null)

		config = applyRoutingChange(config, automatic = true, routes = HAND_TABLE, registry = registry)
		config = applyRoutingChange(config, automatic = false, routes = emptyMap(), registry = registry)
		config = applyRoutingChange(config, automatic = true, routes = emptyMap(), registry = registry)

		assertEquals(setOf(RoutingOutput.DRIVER), overrideRoutes(config, registry)[BodyPart.LEFT_HAND.boneId])
	}
}
