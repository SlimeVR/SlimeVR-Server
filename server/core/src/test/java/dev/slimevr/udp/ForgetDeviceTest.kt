package dev.slimevr.udp

import dev.slimevr.AppContextProvider
import dev.slimevr.TestAppContext
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestAppConfig
import dev.slimevr.buildTestSkeleton
import dev.slimevr.buildTestTracker
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.config.AppConfig
import dev.slimevr.config.DevicesConfigActions
import dev.slimevr.device.Device
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val MAC = "AA:BB:CC:DD:EE:FF"

private fun buildContext(scope: CoroutineScope): AppContextProvider {
	val server = buildTestVrServerStub(scope)
	val config = buildTestAppConfig(scope)
	val skeleton = buildTestSkeleton(scope)
	return object : TestAppContext() {
		override val server: VRServer = server
		override val config: AppConfig = config
		override val skeleton: Skeleton = skeleton
	}
}

private fun addDevice(appContext: AppContextProvider, origin: DeviceOrigin): Device {
	val id = appContext.server.nextHandle()
	val device = Device.create(
		scope = appContext.server.context.scope,
		appContext = appContext,
		id = id,
		address = "127.0.0.$id",
		macAddress = MAC,
		hardwareId = MAC,
		origin = origin,
		protocolVersion = 21,
	)
	appContext.server.context.dispatch(VRServerActions.NewDevice(id, device))
	return device
}

private fun addTracker(
	appContext: AppContextProvider,
	device: Device,
	sensorId: Int,
	bodyPart: BodyPart? = null,
	status: TrackerStatus = TrackerStatus.DISCONNECTED,
): Tracker {
	val id = appContext.server.nextHandle()
	val tracker = buildTestTracker(
		scope = appContext.server.context.scope,
		appContext = appContext,
		settings = appContext.config.settings,
		id = id,
		bodyPart = bodyPart,
		status = status,
		origin = device.context.state.value.origin,
	)
	val hardwareId = "$MAC:$sensorId"
	tracker.context.dispatch(TrackerActions.Update { copy(deviceId = device.context.state.value.id, hardwareId = hardwareId) })
	appContext.server.context.dispatch(VRServerActions.NewTracker(id, tracker))
	appContext.config.devices.context.dispatch(DevicesConfigActions.UpdateTracker(hardwareId) { copy(bodyPart = bodyPart) })
	return tracker
}

@OptIn(ExperimentalCoroutinesApi::class)
class ForgetDeviceTest {
	@Test
	fun `forget removes the udp device, its trackers, the allowed mac and saved config, and nothing else`() = runTest {
		val appContext = buildContext(backgroundScope)
		val devices = appContext.config.devices
		devices.context.dispatch(DevicesConfigActions.AddAllowedUdpDevice(MAC))
		val device = addDevice(appContext, DeviceOrigin.UDP)
		val trackers = listOf(
			addTracker(appContext, device, 0, BodyPart.UPPER_CHEST, TrackerStatus.OK),
			addTracker(appContext, device, 1),
		)
		// Same MAC over HID must survive a UDP forget
		val hidDevice = addDevice(appContext, DeviceOrigin.HID)
		val hidTracker = addTracker(appContext, hidDevice, 5)
		appContext.skeleton.context.dispatch(SkeletonActions.SetBonePose(BodyPart.UPPER_CHEST, null, null, Quaternion.IDENTITY, null, null))
		runCurrent()

		forgetUdpDevice(appContext, MAC)
		runCurrent()

		val serverState = appContext.server.context.state.value
		assertEquals(setOf(hidDevice.context.state.value.id), serverState.devices.keys)
		assertEquals(setOf(hidTracker.context.state.value.id), serverState.trackers.keys)
		assertFalse(device.context.scope.isActive)
		trackers.forEach { assertFalse(it.context.scope.isActive) }
		assertTrue(hidDevice.context.scope.isActive)
		assertFalse(MAC in devices.context.state.value.allowedUdpDevices)
		assertEquals(setOf("$MAC:5"), devices.context.state.value.trackers.keys)
		assertEquals(false, appContext.skeleton.context.state.value.boneInputs[BodyPart.UPPER_CHEST]?.isRotationActive)
	}
}
