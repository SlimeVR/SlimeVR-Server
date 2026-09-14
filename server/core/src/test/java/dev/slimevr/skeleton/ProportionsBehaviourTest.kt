@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.slimevr.skeleton

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.boneId
import dev.slimevr.buildTestSkeleton
import dev.slimevr.buildTestUserConfig
import dev.slimevr.config.UserConfigActions
import dev.slimevr.testCompiledSkeleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProportionsBehaviourTest {
	@Test
	fun `a height-calibration proportions update does not zero the bones it doesn't mention`() = runTest {
		val userConfig = buildTestUserConfig(backgroundScope)
		val skeleton = buildTestSkeleton(backgroundScope, testCompiledSkeleton, userConfig)
		val headId = BodyPart.HEAD.boneId
		val defaultHeadOffset = skeleton.context.state.value.boneInputs[headId]!!.offset
		assertTrue(defaultHeadOffset.lenSq() > 0f)

		userConfig.context.dispatch(
			UserConfigActions.Update {
				copy(userHeight = 1.7f, proportions = testCompiledSkeleton.heightScaledProportionValues(1.7f))
			},
		)
		runCurrent()

		val newHeadOffset = skeleton.context.state.value.boneInputs[headId]!!.offset
		assertEquals(defaultHeadOffset, newHeadOffset)
	}

	@Test
	fun `a single-bone proportions change does not zero every other bone`() = runTest {
		val userConfig = buildTestUserConfig(backgroundScope)
		val skeleton = buildTestSkeleton(backgroundScope, testCompiledSkeleton, userConfig)
		val hipId = BodyPart.HIP.boneId
		val defaultHipOffset = skeleton.context.state.value.boneInputs[hipId]!!.offset

		userConfig.context.dispatch(UserConfigActions.Update { copy(proportions = mapOf("slimevr:upper_leg" to 0.5f)) })
		runCurrent()

		val newHipOffset = skeleton.context.state.value.boneInputs[hipId]!!.offset
		assertEquals(defaultHipOffset, newHipOffset)

		val upperLegId = BodyPart.LEFT_UPPER_LEG.boneId
		val newUpperLegOffset = skeleton.context.state.value.boneInputs[upperLegId]!!.offset
		assertEquals(0.5f, -newUpperLegOffset.y)
	}
}
