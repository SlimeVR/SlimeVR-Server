package dev.slimevr.device

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.context.debug.DiffStyle
import dev.slimevr.context.debug.LoggingMiddleware
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.McuType

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
	val macAddress: String?,
	val batteryLevel: Float,
	val batteryVoltage: Float,
	val ping: Long?,
	val signalStrength: Int?,
	val firmware: String?,
	val boardType: BoardType,
	val mcuType: McuType,
	val protocolVersion: Int,
	val status: TrackerStatus,
	val origin: DeviceOrigin,
	val packetsReceived: Long,
	val packetsLost: Long,
)

sealed interface DeviceActions {
	data class Update(val transform: DeviceState.() -> DeviceState) : DeviceActions
	data class PacketStats(val packetsReceived: Long, val packetsLost: Long) : DeviceActions
}

typealias DeviceContext = Context<DeviceState, DeviceActions>
typealias DeviceBehaviour = Behaviour<DeviceState, DeviceActions, DeviceContext>

class Device(
	val context: DeviceContext,
) {
	companion object {
		fun create(
			scope: CoroutineScope,
			id: Int,
			address: String,
			macAddress: String? = null,
			origin: DeviceOrigin,
			protocolVersion: Int,
		): Device {
			val deviceState = DeviceState(
				id = id,
				name = "Device $id",
				batteryLevel = 0f,
				batteryVoltage = 0f,
				origin = origin,
				address = address,
				macAddress = macAddress,
				protocolVersion = protocolVersion,
				ping = null,
				signalStrength = null,
				status = TrackerStatus.DISCONNECTED,
				mcuType = McuType.Other,
				boardType = BoardType.UNKNOWN,
				firmware = null,
				packetsReceived = 0L,
				packetsLost = 0L,
			)

			val behaviours = listOf(DeviceStatsBehaviour)
			val context = Context.create(
				initialState = deviceState,
				scope = scope,
				behaviours = behaviours,
				debugMiddleware = LoggingMiddleware(
					block = setOf(DeviceActions.PacketStats::class),
					diffStyle = DiffStyle.MULTILINE,
				),
				name = "Device[$address]",
			)
			behaviours.forEach { it.observe(context) }
			return Device(context = context)
		}
	}
}
