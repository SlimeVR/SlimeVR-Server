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

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.testConn(onSend: suspend (ByteArray) -> Unit): SolarXRBridge {
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
	return bridge
}

private fun config(intervalMs: Int) = DataFeedConfig(minimumTimeSinceLast = intervalMs.toUShort())

@OptIn(ExperimentalCoroutinesApi::class)
class DataFeedTest {

	@Test
	fun `StartDataFeed sends frames at the configured interval`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))

		// fires at t=0, t=100, t=200
		advanceTimeBy(250)
		assertEquals(3, sendCount)
	}

	@Test
	fun `StartDataFeed with multiple configs runs each at its own frequency`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100), config(200))))

		// 100ms feed: t=0, t=100, t=200 -> 3 sends
		// 200ms feed: t=0, t=200 -> 2 sends
		advanceTimeBy(250)
		assertEquals(5, sendCount)
	}

	@Test
	fun `PollDataFeed sends exactly one frame without starting a repeating timer`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.dataFeedDispatcher.emit(PollDataFeed(config = config(100)))

		advanceTimeBy(500)
		assertEquals(1, sendCount)
	}

	@Test
	fun `StartDataFeed cancels old timers when called a second time`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))
		advanceTimeBy(250)
		assertEquals(3, sendCount)

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))
		sendCount = 0

		advanceTimeBy(250)
		assertEquals(3, sendCount)
	}

	@Test
	fun `StartDataFeed with empty list stops all existing timers`() = runTest {
		var sendCount = 0
		val conn = testConn { sendCount++ }

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))
		advanceTimeBy(250)
		assertEquals(3, sendCount)

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = emptyList()))
		sendCount = 0

		advanceTimeBy(500)
		assertEquals(0, sendCount)
	}

	// TODO: need more tests for the content of a datafeed + check if the masks work
}
