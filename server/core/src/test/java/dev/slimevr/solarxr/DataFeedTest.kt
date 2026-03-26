package dev.slimevr.solarxr

import dev.slimevr.buildTestVrServer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.PollDataFeed
import solarxr_protocol.data_feed.StartDataFeed
import kotlin.test.Test
import kotlin.test.assertEquals

private fun config(intervalMs: Int) = DataFeedConfig(minimumTimeSinceLast = intervalMs.toUShort())

@OptIn(ExperimentalCoroutinesApi::class)
class DataFeedTest {

	@Test
	fun `StartDataFeed sends frames at the configured interval`() = runTest {
		var sendCount = 0
		val conn = createSolarXRConnection(buildTestVrServer(backgroundScope), onSend = { sendCount++ }, scope = backgroundScope)

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))

		// fires at t=0, t=100, t=200
		advanceTimeBy(250)
		assertEquals(3, sendCount)
	}

	@Test
	fun `StartDataFeed with multiple configs runs each at its own frequency`() = runTest {
		var sendCount = 0
		val conn = createSolarXRConnection(buildTestVrServer(backgroundScope), onSend = { sendCount++ }, scope = backgroundScope)

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100), config(200))))

		// 100ms feed: t=0, t=100, t=200 -> 3 sends
		// 200ms feed: t=0, t=200 -> 2 sends
		advanceTimeBy(250)
		assertEquals(5, sendCount)
	}

	@Test
	fun `PollDataFeed sends exactly one frame without starting a repeating timer`() = runTest {
		var sendCount = 0
		val conn = createSolarXRConnection(buildTestVrServer(backgroundScope), onSend = { sendCount++ }, scope = backgroundScope)

		conn.dataFeedDispatcher.emit(PollDataFeed(config = config(100)))

		advanceTimeBy(500)
		assertEquals(1, sendCount)
	}

	@Test
	fun `StartDataFeed cancels old timers when called a second time`() = runTest {
		var sendCount = 0
		val conn = createSolarXRConnection(buildTestVrServer(backgroundScope), onSend = { sendCount++ }, scope = backgroundScope)

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
		val conn = createSolarXRConnection(buildTestVrServer(backgroundScope), onSend = { sendCount++ }, scope = backgroundScope)

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = listOf(config(100))))
		advanceTimeBy(250)
		assertEquals(3, sendCount)

		conn.dataFeedDispatcher.emit(StartDataFeed(dataFeeds = emptyList()))
		sendCount = 0

		advanceTimeBy(500)
		assertEquals(0, sendCount)
	}

	//TODO: need more tests for the content of a datafeed + check if the masks work
}
