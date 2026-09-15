@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.slimevr.resourcepacks

import dev.slimevr.buildTestSkeleton
import dev.slimevr.buildTestUserConfig
import dev.slimevr.config.UserConfigActions
import dev.slimevr.resourcepacks.compiler.compileResourcePacks
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.skeleton.buildBones
import dev.slimevr.skeleton.defaultSkeletonState
import dev.slimevr.skeleton.inputprocessors.CopyRotationFallbackInputProcessor
import dev.slimevr.skeleton.reduce
import dev.slimevr.solarxr.rpc.buildSkeletonProportionsResponse
import dev.slimevr.testCoreResourcePack
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.SkeletonBone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResourcePackRuntimeTest {
	@Test
	fun `bones without tail offsets get state accept tracking and preserve hierarchy`() = runTest {
		val pack = testPack(
			"example:runtime",
			"data/example/bones/anchor.json" to """{"key":"example:anchor","nameKey":"example:anchor","parent":"slimevr:hip","headOffset":{"base":{"x":1,"y":0,"z":0}}}""",
			"data/example/bones/child.json" to """{"key":"example:child","nameKey":"example:child","parent":"example:anchor","rotationFallback":{"type":"copy","source":"parent"}}""",
		)
		val definition = compileResourcePacks(ResourcePackCatalog(testCoreResourcePack, listOf(pack), emptyList()))
		val initial = defaultSkeletonState(definition)
		assertEquals(definition.registry.maxId, initial.boneInputs.size)
		val anchor = definition.registry["example:anchor"]!!
		val child = definition.registry["example:child"]!!
		val rotation = Quaternion.rotationAroundYAxis(1f)
		val updated = reduce(initial, SkeletonActions.SetBoneRotation(anchor, rotation))
		assertEquals(rotation, updated.boneInputs[anchor]!!.rotation)
		assertEquals(Vector3.ZERO, updated.boneInputs[anchor]!!.offset)
		CopyRotationFallbackInputProcessor(definition.copyRotationFallbacks).process(updated.boneInputs, updated.skeletonHeight)
		val bones = buildBones(updated.boneInputs)
		assertEquals(rotation, bones[child]!!.rotation)
		assertEquals(bones[anchor]!!.tailPosition, bones[child]!!.headPosition)
		assertEquals(Vector3(1f, 0f, 0f), bones[anchor]!!.headOffset)
	}

	@Test
	fun `proportion responses preserve independent values with collinear offset terms`() = runTest {
		val pack = testPack(
			"example:collinear",
			"data/slimevr/bones/head.json" to """
			{"${'$'}extend":"data/slimevr/bones/head.json","tailOffset":{"terms":[
			{"proportion":"slimevr:head","direction":{"x":0,"y":0,"z":1}},
			{"proportion":"slimevr:neck","direction":{"x":0,"y":0,"z":1}}
			]}}
			""".trimIndent(),
		)
		val definition = compileResourcePacks(ResourcePackCatalog(testCoreResourcePack, listOf(pack), emptyList()))
		val config = buildTestUserConfig(backgroundScope)
		val skeleton = buildTestSkeleton(backgroundScope, definition, config)
		config.context.dispatch(UserConfigActions.Update { copy(proportions = mapOf("slimevr:head" to 0.2f, "slimevr:neck" to 0.3f)) })
		runCurrent()
		val state = skeleton.context.state.value
		assertEquals(0.5f, state.boneInputs[definition.registry["slimevr:head"]!!]!!.offset.z)
		val response = buildSkeletonProportionsResponse(state)
		assertEquals(0.2f, response.skeletonParts!!.single { it.bone == SkeletonBone.HEAD }.value)
		assertEquals(0.3f, response.skeletonParts!!.single { it.bone == SkeletonBone.NECK }.value)
		assertTrue(response.skeletonHeight > 1f)

		config.context.dispatch(UserConfigActions.Update { copy(proportions = emptyMap()) })
		runCurrent()
		assertEquals(definition.defaultProportionValues(), skeleton.context.state.value.proportionValues)
	}
}
