package dev.slimevr.tracker

import dev.slimevr.VRServer
import dev.slimevr.context.BasicModule
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
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
	val batteryLevel: Int,
	val batteryVoltage: Int,
	val ping: Long?,
	val signalStrength: Int?,
	val origin: DeviceOrigin,
)

sealed interface DeviceActions {
	data class SetBattery(val level: Int, val voltage: Int) : DeviceActions
	data class SetPing(val ping: Long) : DeviceActions
	data class SetSignalStrength(val signalStrength: Int) : DeviceActions
}

typealias DeviceContext = Context<DeviceState, DeviceActions>
typealias DeviceModule = BasicModule<DeviceState, DeviceActions>

data class Device(
	val context: DeviceContext,
)

val PingModule = DeviceModule(
	reducer = { s, a ->
		when (a) {
			is DeviceActions.SetPing -> s.copy(ping = a.ping)
			else -> s
		}
	},
	observer = {
		it.state
			.distinctUntilChangedBy { device -> device.ping }
			.filter { device -> device.ping != null }
			.onEach { device ->
				println("[${device.name}] ping change to ${device.ping}")
			}.launchIn(it.scope)
	},
)

fun createDevice(scope: CoroutineScope, id: Int, address: String, origin: DeviceOrigin, serverContext: VRServer): Device {
	val deviceState = DeviceState(
		id = id,
		name = "Device $id",
		batteryLevel = 0,
		batteryVoltage = 0,
		origin = origin,
		address = address,
		ping = null,
		signalStrength = null,
	)

	val modules = listOf(PingModule)

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
