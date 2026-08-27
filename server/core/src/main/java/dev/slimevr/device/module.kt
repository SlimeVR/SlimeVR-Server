package dev.slimevr.device

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.context.debug.DiffStyle
import dev.slimevr.context.debug.LoggingMiddleware
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.McuType

data class DeviceState(
	val id: Int,
	val name: String,
	val manufacturer: String,
	val address: String,
	val macAddress: String?,
	val batteryLevel: Float?,
	val batteryVoltage: Float?,
	val batteryRemainingRuntime: Long?,
	val ping: Long?,
	val signalStrength: Int?,
	val firmwareVersion: String?,
	val firmwareDate: String?,
	val boardType: BoardType,
	val mcuType: McuType,
	val protocolVersion: Int,
	val status: TrackerStatus,
	val origin: DeviceOrigin,
	val driverName: String? = null,
	val packetsReceived: Long,
	val packetsLost: Long,
	val packetLossRecent: Float? = null,
	val samples: List<DevicePacketSample> = emptyList(),
)

sealed interface DeviceActions {
	data class Update(val transform: DeviceState.() -> DeviceState) : DeviceActions
	data class PacketStats(val packetsReceived: Long, val packetsLost: Long) : DeviceActions
}

typealias DeviceContext = Context<DeviceState, DeviceActions>
typealias DeviceBehaviour = Behaviour<Device>

class Device(
	val context: DeviceContext,
	val appContext: AppContextProvider,
) {
	fun getStatsForWindow(windowMs: Long, now: Long = System.currentTimeMillis()): WindowedDeviceStats = computeWindowedStats(context.state.value.samples, windowMs, now)

	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(
			scope: CoroutineScope,
			appContext: AppContextProvider,
			id: Int,
			name: String = "Device $id",
			manufacturer: String = "SlimeVR",
			address: String,
			macAddress: String? = null,
			origin: DeviceOrigin,
			driverName: String? = null,
			protocolVersion: Int,
		): Device {
			val deviceState = DeviceState(
				id = id,
				name = name,
				manufacturer = manufacturer,
				batteryLevel = null,
				batteryVoltage = null,
				batteryRemainingRuntime = null,
				origin = origin,
				driverName = driverName,
				address = address,
				macAddress = macAddress,
				protocolVersion = protocolVersion,
				ping = null,
				signalStrength = null,
				status = TrackerStatus.DISCONNECTED,
				mcuType = McuType.UNKNOWN,
				boardType = BoardType.UNKNOWN,
				firmwareVersion = null,
				firmwareDate = null,
				packetsReceived = 0L,
				packetsLost = 0L,
				packetLossRecent = null,
			)

			val context = Context.create(
				initialState = deviceState,
				scope = scope,
				reducer = ::reduce,
				debugMiddleware = LoggingMiddleware(
					block = setOf(DeviceActions.PacketStats::class),
					diffStyle = DiffStyle.MULTILINE,
				),
				name = "Device[$address]",
			)
			val device = Device(context = context, appContext = appContext)
			device.startObserving()
			return device
		}
	}
}
