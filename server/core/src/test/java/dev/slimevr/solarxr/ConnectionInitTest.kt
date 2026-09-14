package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.EventDispatcher
import dev.slimevr.TestAppContext
import dev.slimevr.bones.BoneRegistryManager
import dev.slimevr.buildTestVrServer
import dev.slimevr.context.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.MessageBundle
import solarxr_protocol.connection.BoneDefinition
import solarxr_protocol.connection.BoneRegistry
import solarxr_protocol.connection.BoneRegistryRequest
import solarxr_protocol.connection.ClientHello
import solarxr_protocol.connection.ConfigurationDone
import solarxr_protocol.connection.ConnectionError
import solarxr_protocol.connection.ConnectionMessage
import solarxr_protocol.connection.ConnectionMessageHeader
import solarxr_protocol.connection.HelloStatus
import solarxr_protocol.connection.InitializationRequiredError
import solarxr_protocol.connection.ServerHello
import solarxr_protocol.connection.UnsupportedRequestError
import solarxr_protocol.data_feed.DataFeedMessageHeader
import solarxr_protocol.data_feed.StartDataFeed
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.buildTestBridge(sent: MutableList<OutboundFrame>): SolarXRBridge {
	val appContext = object : TestAppContext() {
		override val server = buildTestVrServer(backgroundScope)
		override val bones = BoneRegistryManager.create(backgroundScope)
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
	bridge.outbound.on<OutboundFrame> { sent += it }.launchIn(backgroundScope)
	runCurrent()
	return bridge
}

private fun clientHello(version: UInt = SOLARXR_PROTOCOL_VERSION) = MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(ClientHello(protocolVersion = version))))
private fun OutboundFrame.connectionMessage(): ConnectionMessage? = bundle.connectionMsgs?.single()?.message

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectionInitTest {

	@Test
	fun `checkedSolarXRFrame rejects a truncated frame`() {
		assertFailsWith<IllegalArgumentException> {
			checkedSolarXRFrame(ByteBuffer.allocate(4))
		}
	}

	@Test
	fun `checkedSolarXRFrame rejects an oversized frame`() {
		assertFailsWith<IllegalArgumentException> {
			checkedSolarXRFrame(ByteBuffer.allocate(SOLARXR_MAX_FRAME_SIZE + 1))
		}
	}

	@Test
	fun `a MessageBundle round-trips through writeSolarXRBundle and checkedSolarXRFrame`() {
		val fbb = FlatBufferBuilder(64)
		writeSolarXRBundle(fbb, clientHello())
		val reader = checkedSolarXRFrame(fbb.dataBuffer())
		val decoded = MessageBundle.fromByteBuffer(reader).connectionMsgs?.single()?.message
		assertEquals(SOLARXR_PROTOCOL_VERSION, (decoded as? ClientHello)?.protocolVersion)
	}

	@Test
	fun `ClientHello with a matching version is accepted and moves to configuring`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
		val bridge = buildTestBridge(sent)

		onSolarXRMessage(clientHello(), bridge)
		runCurrent()

		val hello = sent.single()
		assertNull(hello.closeReason)
		assertEquals(ServerHello(HelloStatus.ACCEPTED, SOLARXR_PROTOCOL_VERSION), hello.connectionMessage())
		assertEquals(ConnectionPhase.CONFIGURING, bridge.context.state.value.phase)
	}

	@Test
	fun `ClientHello with a mismatched version is rejected and closes`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
		val bridge = buildTestBridge(sent)

		onSolarXRMessage(clientHello(version = SOLARXR_PROTOCOL_VERSION + 1u), bridge)
		runCurrent()

		val hello = sent.single()
		assertNotNull(hello.closeReason)
		assertEquals(ServerHello(HelloStatus.REJECTED_UNSUPPORTED_VERSION, SOLARXR_PROTOCOL_VERSION), hello.connectionMessage())
		assertEquals(ConnectionPhase.HELLO, bridge.context.state.value.phase)
	}

	@Test
	fun `anything but ClientHello during HELLO is rejected and closes`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
		val bridge = buildTestBridge(sent)

		onSolarXRMessage(MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(BoneRegistryRequest()))), bridge)
		runCurrent()

		val frame = sent.single()
		assertNotNull(frame.closeReason)
		assertIs<InitializationRequiredError>((frame.connectionMessage() as ConnectionError).data)
	}

	@Test
	fun `BoneRegistryRequest during configuration replies with the registry`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
		val bridge = buildTestBridge(sent)

		onSolarXRMessage(clientHello(), bridge)
		runCurrent()
		sent.clear()

		onSolarXRMessage(MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(BoneRegistryRequest()))), bridge)
		runCurrent()

		assertEquals(bridge.registry.value, sent.single().connectionMessage())
	}

	@Test
	fun `an undecodable connection message during configuration gets UnsupportedRequestError`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
		val bridge = buildTestBridge(sent)

		onSolarXRMessage(clientHello(), bridge)
		runCurrent()
		sent.clear()

		onSolarXRMessage(MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(message = null))), bridge)
		runCurrent()

		assertIs<UnsupportedRequestError>((sent.single().connectionMessage() as ConnectionError).data)
	}

	@Test
	fun `application bundles are rejected before configuration completes`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
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
		assertIs<InitializationRequiredError>((sent.single().connectionMessage() as ConnectionError).data)
	}

	@Test
	fun `application bundles dispatch once configuration completes`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
		val bridge = buildTestBridge(sent)
		var dispatched = false
		bridge.dataFeedDispatcher.on<StartDataFeed> { dispatched = true }.launchIn(backgroundScope)
		runCurrent()

		onSolarXRMessage(clientHello(), bridge)
		runCurrent()
		onSolarXRMessage(MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(ConfigurationDone()))), bridge)
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
	fun `a single bundle carrying ClientHello, BoneRegistryRequest and ConfigurationDone completes the handshake`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
		val bridge = buildTestBridge(sent)

		onSolarXRMessage(
			MessageBundle(
				connectionMsgs = listOf(
					ConnectionMessageHeader(ClientHello(protocolVersion = SOLARXR_PROTOCOL_VERSION)),
					ConnectionMessageHeader(BoneRegistryRequest()),
					ConnectionMessageHeader(ConfigurationDone()),
				),
			),
			bridge,
		)
		runCurrent()

		assertEquals(3, sent.size)
		assertEquals(ServerHello(HelloStatus.ACCEPTED, SOLARXR_PROTOCOL_VERSION), sent[0].connectionMessage())
		assertEquals(bridge.registry.value, sent[1].connectionMessage())
		assertIs<ConfigurationDone>(sent[2].connectionMessage())
		assertTrue(bridge.isReady)
	}

	@Test
	fun `a client-sent bone registry is silently ignored during configuration`() = runTest {
		val sent = mutableListOf<OutboundFrame>()
		val bridge = buildTestBridge(sent)

		onSolarXRMessage(clientHello(), bridge)
		runCurrent()
		sent.clear()

		val clientRegistry = BoneRegistry(bones = listOf(BoneDefinition(1u, "x", null, 0u)))
		onSolarXRMessage(MessageBundle(connectionMsgs = listOf(ConnectionMessageHeader(clientRegistry))), bridge)
		runCurrent()

		assertTrue(sent.isEmpty())
		// The registry stays the server-issued default; the client's attempt never lands.
		assertEquals(dev.slimevr.bones.BoneRegistry.standard().value, bridge.registry.value)
	}
}
