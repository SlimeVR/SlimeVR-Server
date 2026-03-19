package dev.slimevr.tracker

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import dev.slimevr.context.BasicModule
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

enum class DeviceOrigin {
	DRIVER,
	FEEDER,
	UDP,
	HID,
}

data class DeviceState(
	val id: Int,
	val name: String,
	val address: String,
	val batteryLevel: Float,
	val batteryVoltage: Float,
	val ping: Long?,
	val signalStrength: Int?,
	val origin: DeviceOrigin,
)

sealed interface DeviceActions {
	data class Update(val transform: DeviceState.() -> DeviceState) : DeviceActions
}



val DeviceStatsModule = DeviceModule(
	reducer = { s, a -> if (a is DeviceActions.Update) a.transform(s) else s },
	observer = {
		it.state.onEach { state ->
//			AppLogger.device.info("Device state changed", state)
		}.launchIn(it.scope)
	}
)

typealias DeviceContext = Context<DeviceState, DeviceActions>
typealias DeviceModule = BasicModule<DeviceState, DeviceActions>

data class Device(
	val context: DeviceContext,
)

fun createDevice(scope: CoroutineScope, id: Int, address: String, origin: DeviceOrigin, serverContext: VRServer): Device {
	val deviceState = DeviceState(
		id = id,
		name = "Device $id",
		batteryLevel = 0f,
		batteryVoltage = 0f,
		origin = origin,
		address = address,
		ping = null,
		signalStrength = null,
	)

	val modules = listOf(DeviceStatsModule)

	val context = createContext(
		initialState = deviceState,
		reducers = modules.map { it.reducer },
		scope = scope,
	)

	modules.map { it.observer }.forEach { it?.invoke(context) }

	return Device(
		context = context,
	)
}
