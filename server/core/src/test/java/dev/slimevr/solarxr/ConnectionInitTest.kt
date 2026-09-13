package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.EventDispatcher
import dev.slimevr.TestAppContext
import dev.slimevr.buildTestSkeleton
import dev.slimevr.buildTestVrServer
import dev.slimevr.context.Context
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferWriter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.ClientHello
import solarxr_protocol.HelloStatus
import solarxr_protocol.MessageBundle
import solarxr_protocol.ServerHello
import solarxr_protocol.connection.BoneDefinition
import solarxr_protocol.connection.BoneRegistry
import solarxr_protocol.connection.ConfigurationAcknowledged
import solarxr_protocol.connection.ConnectionError
import solarxr_protocol.connection.ConnectionErrorCode
import solarxr_protocol.connection.ConnectionMessageHeader
import solarxr_protocol.data_feed.DataFeedMessageHeader
import solarxr_protocol.data_feed.StartDataFeed
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.buildTestBridge(sent: MutableList<MessageBundle>): SolarXRBridge {
	val appContext = object : TestAppContext() {
		override val server = buildTestVrServer(backgroundScope)
		override val skeleton = buildTestSkeleton(backgroundScope)
	}
	val context = Context.create(
		initialState = SolarXRBridgeState(),
		scope = backgroundScope,
		reducer = ::reduce,
		name = "ConnectionInitTest",
	)
	val bridge = SolarXRBridge(
		id = 1,
		context = context,
		appContext = appContext,
		dataFeedDispatcher = EventDispatcher("test.datafeed", backgroundScope),
		rpcDispatcher = EventDispatcher("test.rpc", backgroundScope),
		driverDispatcher = EventDispatcher("test.driver", backgroundScope),
	)
	bridge.outbound.on<MessageBundle> { sent += it }.launchIn(backgroundScope)
	runCurrent()
	return bridge
}

private fun MessageBundle.singleConnectionError(): ConnectionError = connectionMsgs?.single()?.message as? ConnectionError ?: error("Expected a single ConnectionError")

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectionInitTest {

	@Test
	fun `checkedSolarXRFrame rejects a wrong identifier`() {
		val fbb = FlatBufferBuilder(64)
		ClientHello(protocolVersion = SOLARXR_PROTOCOL_VERSION).finish(JvmFlatBufferWriter(fbb))
		assertFailsWith<IllegalArgumentException> {
			checkedSolarXRFrame(fbb.dataBuffer(), ServerHello.FILE_IDENTIFIER)
		}
	}

	@Test
	fun `checkedSolarXRFrame rejects a truncated frame`() {
		assertFailsWith<IllegalArgumentException> {
			checkedSolarXRFrame(ByteBuffer.allocate(4), ClientHello.FILE_IDENTIFIER)
		}
	}

	@Test
	fun `checkedSolarXRFrame rejects an oversized frame`() {
		assertFailsWith<IllegalArgumentException> {
			checkedSolarXRFrame(ByteBuffer.allocate(SOLARXR_MAX_FRAME_SIZE + 1), ClientHello.FILE_IDENTIFIER)
		}
	}

	@Test
	fun `ClientHello, ServerHello and MessageBundle round-trip with their identifiers`() {
		val helloFbb = FlatBufferBuilder(64)
		ClientHello(protocolVersion = SOLARXR_PROTOCOL_VERSION).finish(JvmFlatBufferWriter(helloFbb))
		val helloReader = checkedSolarXRFrame(helloFbb.dataBuffer(), ClientHello.FILE_IDENTIFIER)
		assertTrue(ClientHello.hasIdentifier(helloReader))
		assertEquals(SOLARXR_PROTOCOL_VERSION, ClientHello.fromByteBuffer(helloReader).protocolVersion)

		val replyFbb = FlatBufferBuilder(64)
		ServerHello(HelloStatus.ACCEPTED, SOLARXR_PROTOCOL_VERSION).finish(JvmFlatBufferWriter(replyFbb))
		val replyReader = checkedSolarXRFrame(replyFbb.dataBuffer(), ServerHello.FILE_IDENTIFIER)
		assertTrue(ServerHello.hasIdentifier(replyReader))
		assertEquals(HelloStatus.ACCEPTED, ServerHello.fromByteBuffer(replyReader).status)

		val bundleFbb = FlatBufferBuilder(64)
		MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(ConfigurationAcknowledged())))
			.finish(JvmFlatBufferWriter(bundleFbb))
		val bundleReader = checkedSolarXRFrame(bundleFbb.dataBuffer(), MessageBundle.FILE_IDENTIFIER)
		assertTrue(MessageBundle.hasIdentifier(bundleReader))
		assertTrue(MessageBundle.fromByteBuffer(bundleReader).connectionMsgs?.single()?.message is ConfigurationAcknowledged)
	}

	@Test
	fun `application bundles are rejected before configuration completes`() = runTest {
		val sent = mutableListOf<MessageBundle>()
		val bridge = buildTestBridge(sent)
		var dispatched = false
		bridge.dataFeedDispatcher.on<StartDataFeed> { dispatched = true }.launchIn(backgroundScope)
		runCurrent()

		onSolarXRMessage(
			MessageBundle(dataFeedMsgs = listOf(DataFeedMessageHeader(message = StartDataFeed(dataFeeds = emptyList())))),
			bridge,
		)
		runCurrent()

		assertFalse(dispatched)
		assertEquals(ConnectionErrorCode.INITIALIZATION_REQUIRED, sent.single().singleConnectionError().code)
	}

	@Test
	fun `application bundles dispatch once configuration completes`() = runTest {
		val sent = mutableListOf<MessageBundle>()
		val bridge = buildTestBridge(sent)
		var dispatched = false
		bridge.dataFeedDispatcher.on<StartDataFeed> { dispatched = true }.launchIn(backgroundScope)
		runCurrent()

		onSolarXRMessage(MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(ConfigurationAcknowledged()))), bridge)
		runCurrent()
		assertTrue(bridge.isReady)

		onSolarXRMessage(
			MessageBundle(dataFeedMsgs = listOf(DataFeedMessageHeader(message = StartDataFeed(dataFeeds = emptyList())))),
			bridge,
		)
		runCurrent()

		assertTrue(dispatched)
	}

	@Test
	fun `a client-sent bone registry is silently ignored`() = runTest {
		val sent = mutableListOf<MessageBundle>()
		val bridge = buildTestBridge(sent)

		val clientRegistry = BoneRegistry(
			bones = listOf(BoneDefinition(1u, "x", null, 0u, null)),
		)
		onSolarXRMessage(MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(clientRegistry))), bridge)
		runCurrent()

		assertTrue(sent.isEmpty())
		// The registry stays the server-issued default; the client's attempt never lands.
		assertEquals(dev.slimevr.skeleton.BoneRegistry.standard().value, bridge.registry.value)
	}
}
