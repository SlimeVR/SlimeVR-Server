@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.slimevr.skeleton

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.boneId
import dev.slimevr.buildTestSkeleton
import dev.slimevr.buildTestUserConfig
import dev.slimevr.config.UserConfigActions
import dev.slimevr.resourcepacks.ResourcePackCatalog
import dev.slimevr.resourcepacks.compiler.compileResourcePacks
import dev.slimevr.resourcepacks.testPack
import dev.slimevr.skeleton.inputprocessors.CopyRotationFallbackInputProcessor
import dev.slimevr.solarxr.rpc.buildSkeletonProportionsResponse
import dev.slimevr.testCompiledSkeleton
import dev.slimevr.testCoreResourcePack
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.SkeletonBone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProportionsBehaviourTest {
	@Test
	fun `pack bones get state, hierarchy and head offsets`() = runTest {
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
	fun `proportion updates only move the bones that use them`() = runTest {
		val heightConfig = buildTestUserConfig(backgroundScope)
		val heightSkeleton = buildTestSkeleton(backgroundScope, testCompiledSkeleton, heightConfig)
		val headId = BodyPart.HEAD.boneId
		val defaultHeadOffset = heightSkeleton.context.state.value.boneInputs[headId]!!.offset
		assertTrue(defaultHeadOffset.lenSq() > 0f)

		heightConfig.context.dispatch(
			UserConfigActions.Update {
				copy(userHeight = 1.7f, proportions = testCompiledSkeleton.heightScaledProportionValues(1.7f))
			},
		)
		runCurrent()

		val newHeadOffset = heightSkeleton.context.state.value.boneInputs[headId]!!.offset
		assertEquals(defaultHeadOffset, newHeadOffset)
		assertEquals(0.05f, heightSkeleton.context.state.value.boneInputs[BodyPart.LEFT_LOWER_LEG.boneId]!!.offset.z)

		val singleBoneConfig = buildTestUserConfig(backgroundScope)
		val singleBoneSkeleton = buildTestSkeleton(backgroundScope, testCompiledSkeleton, singleBoneConfig)
		val hipId = BodyPart.HIP.boneId
		val defaultHipOffset = singleBoneSkeleton.context.state.value.boneInputs[hipId]!!.offset

		singleBoneConfig.context.dispatch(UserConfigActions.Update { copy(proportions = mapOf("slimevr:upper_leg" to 0.5f)) })
		runCurrent()

		val newHipOffset = singleBoneSkeleton.context.state.value.boneInputs[hipId]!!.offset
		assertEquals(defaultHipOffset, newHipOffset)

		val upperLegId = BodyPart.LEFT_UPPER_LEG.boneId
		val newUpperLegOffset = singleBoneSkeleton.context.state.value.boneInputs[upperLegId]!!.offset
		assertEquals(0.5f, -newUpperLegOffset.y)
		assertTrue(singleBoneSkeleton.context.state.value.skeletonHeight > 1f)
	}

	@Test
	fun `collinear offset terms keep independent proportion values in the RPC response`() = runTest {
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
