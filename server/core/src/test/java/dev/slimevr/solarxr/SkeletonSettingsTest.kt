package dev.slimevr.solarxr

import dev.slimevr.TestAppContext
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestSkeleton
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.solarxr.rpc.SkeletonSettingsBehaviour
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.LegTweaksTmpChange
import solarxr_protocol.rpc.LegTweaksTmpClear
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.testConn(): Pair<SolarXRBridge, Skeleton> {
	val skeleton = buildTestSkeleton(backgroundScope)
	val settings = buildTestSettings(backgroundScope)
	val appContext = object : TestAppContext() {
		override val skeleton = skeleton
	}
	val context = Context.create(
		initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
		scope = backgroundScope,
		reducer = ::reduce,
		behaviours = listOf(SkeletonSettingsBehaviour(settings)),
		name = "SolarXRSkeletonSettingsTest",
	)
	val bridge = SolarXRBridge(
		id = 1,
		context = context,
		appContext = appContext,
	)
	bridge.startObserving()
	runCurrent()
	return bridge to skeleton
}

@OptIn(ExperimentalCoroutinesApi::class)
class SkeletonSettingsTest {

	@Test
	fun `LegTweaksTmpChange disables the requested toggles`() = runTest {
		val (conn, skeleton) = testConn()

		conn.rpcDispatcher.emit(LegTweaksTmpChange(floorClip = false, footPlant = false))
		runCurrent()

		assertFalse(skeleton.effectiveFloorClip)
		assertFalse(skeleton.effectiveFootPlant)
		// Untouched (null) fields keep the config default
		assertTrue(skeleton.effectiveSkatingCorrection)
		assertTrue(skeleton.effectiveToeSnap)
	}

	@Test
	fun `LegTweaksTmpChange with null fields does not clobber an existing override`() = runTest {
		val (conn, skeleton) = testConn()

		conn.rpcDispatcher.emit(LegTweaksTmpChange(floorClip = false))
		runCurrent()
		conn.rpcDispatcher.emit(LegTweaksTmpChange(footPlant = false))
		runCurrent()

		assertFalse(skeleton.effectiveFloorClip)
		assertFalse(skeleton.effectiveFootPlant)
	}

	@Test
	fun `LegTweaksTmpClear resets only the flagged toggles`() = runTest {
		val (conn, skeleton) = testConn()

		conn.rpcDispatcher.emit(LegTweaksTmpChange(floorClip = false, skatingCorrection = false))
		runCurrent()

		conn.rpcDispatcher.emit(LegTweaksTmpClear(floorClip = true))
		runCurrent()

		assertTrue(skeleton.effectiveFloorClip)
		assertFalse(skeleton.effectiveSkatingCorrection)
	}

	@Test
	fun `LegTweaksTmpClear with all fields true resets everything to config`() = runTest {
		val (conn, skeleton) = testConn()

		conn.rpcDispatcher.emit(
			LegTweaksTmpChange(floorClip = false, skatingCorrection = false, toeSnap = false, footPlant = false),
		)
		runCurrent()
		assertFalse(skeleton.effectiveFloorClip)
		assertFalse(skeleton.effectiveSkatingCorrection)
		assertFalse(skeleton.effectiveToeSnap)
		assertFalse(skeleton.effectiveFootPlant)

		conn.rpcDispatcher.emit(
			LegTweaksTmpClear(floorClip = true, skatingCorrection = true, toeSnap = true, footPlant = true),
		)
		runCurrent()

		assertTrue(skeleton.effectiveFloorClip)
		assertTrue(skeleton.effectiveSkatingCorrection)
		assertTrue(skeleton.effectiveToeSnap)
		assertTrue(skeleton.effectiveFootPlant)
	}
}
