package dev.slimevr.solarxr

import dev.slimevr.EventDispatcher
import dev.slimevr.TestAppContext
import dev.slimevr.buildTestHeightCalibration
import dev.slimevr.buildTestResetsManager
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestSkeleton
import dev.slimevr.buildTestUserConfig
import dev.slimevr.buildTestVrServer
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.solarxr.datafeed.DataFeedInitBehaviour
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.MessageBundle
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.PollDataFeed
import solarxr_protocol.data_feed.StartDataFeed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private class TestConn(val bridge: SolarXRBridge, val skeleton: Skeleton)

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.testConn(onSend: suspend (ByteArray) -> Unit): TestConn {
	val server = buildTestVrServer(backgroundScope)
	val skeleton = buildTestSkeleton(backgroundScope)
	val settings = buildTestSettings(backgroundScope)
	val userSettings = buildTestUserConfig(backgroundScope)
	val resetsManager = buildTestResetsManager(server, settings, backgroundScope)
	val heightCalibrationManager = buildTestHeightCalibration(server, userSettings, backgroundScope)
	val appContext = object : TestAppContext() {
		override val server = server
		override val skeleton = skeleton
		override val resetsManager = resetsManager
		override val heightCalibrationManager = heightCalibrationManager
	}
	val context = Context.create(
		initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
		scope = backgroundScope,
		reducer = ::reduce,
		behaviours = listOf(DataFeedInitBehaviour(server, skeleton, testScheduler.timeSource)),
		name = "SolarXRDataFeedTest",
	)
	val bridge = SolarXRBridge(
		id = 1,
		context = context,
		appContext = appContext,
		dataFeedDispatcher = EventDispatcher("test.datafeed", backgroundScope),
		rpcDispatcher = EventDispatcher("test.rpc", backgroundScope),
	)
	bridge.startObserving()
	bridge.outbound.on<MessageBundle> { onSend(ByteArray(0)) }.launchIn(backgroundScope)

	// launchIn registers the handler synchronously, but the dispatcher's own drain loop still has to
	// start before anything emitted here can reach it.
	runCurrent()
	return TestConn(bridge, skeleton)
}

private fun config(intervalMs: Int) = DataFeedConfig(minimumTimeSinceLast = intervalMs.toUShort())

/** Drives [Skeleton.computed] the way the 500Hz loop does, so the feed's gate has a clock to ride */
@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.tickSkeleton(conn: TestConn, duration: Duration, tick: Duration = 2.milliseconds) {
	// Let a freshly started feed subscribe and take the replayed frame before the clock moves
	runCurrent()
	val frame = conn.skeleton.currentComputed
	var elapsed = Duration.ZERO
	while (elapsed < duration) {
		advanceTimeBy(tick)
		conn.skeleton.computed.tryEmit(frame)
		runCurrent()
		elapsed += tick
	}
}

@OptIn(ExperimentalCoroutinesApi::class)
class DataFeedTest {

	@Test
	fun `StartDataFeed sends frames at the configured interval`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.bridge.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))

		// fires at t=0, t=100, t=200
		tickSkeleton(conn, 250.milliseconds)
		assertEquals(3, sendCount)
	}

	@Test
	fun `StartDataFeed with multiple configs runs each at its own frequency`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.bridge.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100), config(200))))

		// 100ms feed: t=0, t=100, t=200 -> 3 sends
		// 200ms feed: t=0, t=200 -> 2 sends
		tickSkeleton(conn, 250.milliseconds)
		assertEquals(5, sendCount)
	}

	@Test
	fun `PollDataFeed sends exactly one frame without starting a repeating timer`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.bridge.dataFeedDispatcher.emit(PollDataFeed(config = config(100)))

		tickSkeleton(conn, 500.milliseconds)
		assertEquals(1, sendCount)
	}

	@Test
	fun `StartDataFeed cancels old timers when called a second time`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.bridge.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))
		tickSkeleton(conn, 250.milliseconds)
		assertEquals(3, sendCount)

		conn.bridge.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))
		sendCount = 0

		tickSkeleton(conn, 250.milliseconds)
		assertEquals(3, sendCount)
	}

	@Test
	fun `StartDataFeed with empty list stops all existing timers`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.bridge.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))
		tickSkeleton(conn, 250.milliseconds)
		assertEquals(3, sendCount)

		conn.bridge.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = emptyList()))
		sendCount = 0

		tickSkeleton(conn, 500.milliseconds)
		assertEquals(0, sendCount)
	}

	// TODO: need more tests for the content of a datafeed + check if the masks work
}
