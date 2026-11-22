package dev.slimevr.solarxr

import dev.slimevr.TestAppContext
import dev.slimevr.config.AppConfig
import dev.slimevr.config.MemoryStorage
import dev.slimevr.context.Context
import dev.slimevr.solarxr.rpc.ProfilesBehaviour
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.MessageBundle
import solarxr_protocol.rpc.CreateProfileRequest
import solarxr_protocol.rpc.DeleteProfileRequest
import solarxr_protocol.rpc.ProfileKind
import solarxr_protocol.rpc.ProfileOperationResponse
import solarxr_protocol.rpc.ProfileOperationStatus
import solarxr_protocol.rpc.ProfilesRequest
import solarxr_protocol.rpc.ProfilesResponse
import solarxr_protocol.rpc.RenameProfileRequest
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.SwitchProfileRequest
import kotlin.test.Test
import kotlin.test.assertEquals

private class Harness(val bridge: SolarXRBridge, val config: AppConfig, val sent: List<RpcMessage>) {
	fun profiles() = sent.filterIsInstance<ProfilesResponse>()
	fun statuses() = sent.filterIsInstance<ProfileOperationResponse>().map { it.status }
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun buildHarness(scope: TestScope): Harness {
	val config = AppConfig.create(scope.backgroundScope, MemoryStorage())
	val sent = mutableListOf<RpcMessage>()
	val context = Context.create(
		initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
		scope = scope.backgroundScope,
		reducer = ::reduce,
		behaviours = listOf(ProfilesBehaviour(config)),
		name = "SolarXRProfilesTest",
	)
	val bridge = SolarXRBridge(
		id = 1,
		context = context,
		appContext = object : TestAppContext() {
			override val config: AppConfig = config
		},
	)
	bridge.outbound.on<MessageBundle> { bundle ->
		bundle.rpcMsgs.orEmpty().mapNotNull { it.message }.forEach(sent::add)
	}.launchIn(scope.backgroundScope)
	bridge.startObserving()
	scope.runCurrent()
	return Harness(bridge, config, sent)
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun awaitStatuses(scope: TestScope, h: Harness, count: Int) {
	val deadline = System.nanoTime() + 5_000_000_000L
	while (h.statuses().size < count && System.nanoTime() < deadline) {
		scope.runCurrent()
		Thread.sleep(5)
	}
	scope.runCurrent()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilesBehaviourTest {

	@Test
	fun `a request answers with the profiles and the selected ones`() = runTest {
		val h = buildHarness(this)

		h.bridge.rpcDispatcher.emit(ProfilesRequest())
		runCurrent()

		val response = h.profiles().single()
		assertEquals("default", response.settings?.active)
		assertEquals(listOf("default"), response.settings?.names)
		assertEquals("default", response.user?.active)
		assertEquals(listOf("default"), response.user?.names)
	}

	@Test
	fun `creating a profile answers ok and pushes the new list without selecting it`() = runTest {
		val h = buildHarness(this)

		h.bridge.rpcDispatcher.emit(CreateProfileRequest(kind = ProfileKind.SETTINGS, name = "second", copyFrom = ""))
		runCurrent()

		assertEquals(listOf(ProfileOperationStatus.OK), h.statuses())
		val pushed = h.profiles().single()
		assertEquals("default", pushed.settings?.active)
		assertEquals(listOf("default", "second"), pushed.settings?.names)
		assertEquals(listOf("default"), pushed.user?.names)
	}

	@Test
	fun `switching pushes the new selection`() = runTest {
		val h = buildHarness(this)
		h.bridge.rpcDispatcher.emit(CreateProfileRequest(kind = ProfileKind.USER, name = "me", copyFrom = null))
		runCurrent()

		h.bridge.rpcDispatcher.emit(SwitchProfileRequest(kind = ProfileKind.USER, name = "me"))
		awaitStatuses(this, h, count = 2)

		assertEquals(listOf(ProfileOperationStatus.OK, ProfileOperationStatus.OK), h.statuses())
		assertEquals("me", h.profiles().last().user?.active)
		assertEquals("default", h.profiles().last().settings?.active)
		assertEquals("me", h.config.userConfig.context.state.value.name)
	}

	@Test
	fun `renaming and deleting go through the same answer`() = runTest {
		val h = buildHarness(this)
		h.bridge.rpcDispatcher.emit(CreateProfileRequest(kind = ProfileKind.SETTINGS, name = "old", copyFrom = null))
		runCurrent()

		h.bridge.rpcDispatcher.emit(RenameProfileRequest(kind = ProfileKind.SETTINGS, name = "old", newName = "new"))
		runCurrent()
		assertEquals(listOf("default", "new"), h.profiles().last().settings?.names)

		h.bridge.rpcDispatcher.emit(DeleteProfileRequest(kind = ProfileKind.SETTINGS, name = "new"))
		runCurrent()
		assertEquals(listOf("default"), h.profiles().last().settings?.names)
		assertEquals(List(3) { ProfileOperationStatus.OK }, h.statuses())
	}

	@Test
	fun `a request that cannot be done answers why and pushes nothing`() = runTest {
		val h = buildHarness(this)

		h.bridge.rpcDispatcher.emit(SwitchProfileRequest(name = "default"))
		h.bridge.rpcDispatcher.emit(CreateProfileRequest(kind = ProfileKind.SETTINGS, name = "../x", copyFrom = null))
		h.bridge.rpcDispatcher.emit(CreateProfileRequest(kind = ProfileKind.SETTINGS, name = "default", copyFrom = null))
		h.bridge.rpcDispatcher.emit(SwitchProfileRequest(kind = ProfileKind.SETTINGS, name = "missing"))
		h.bridge.rpcDispatcher.emit(DeleteProfileRequest(kind = ProfileKind.SETTINGS, name = "default"))
		runCurrent()

		assertEquals(
			listOf(
				ProfileOperationStatus.INVALID_KIND,
				ProfileOperationStatus.INVALID_NAME,
				ProfileOperationStatus.ALREADY_EXISTS,
				ProfileOperationStatus.NOT_FOUND,
				ProfileOperationStatus.IS_ACTIVE,
			),
			h.statuses(),
		)
		assertEquals(emptyList(), h.profiles())
	}
}
