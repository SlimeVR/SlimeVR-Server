package dev.slimevr.solarxr.rpc

import dev.slimevr.AppContextProvider
import dev.slimevr.config.SettingsActions
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.udp.UdpServerActions
import dev.slimevr.udp.UnknownDeviceState
import dev.slimevr.udp.forgetUdpDevice
import dev.slimevr.util.ipv4AddressFromString
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.AddUnknownDeviceRequest
import solarxr_protocol.rpc.ForgetDeviceRequest
import solarxr_protocol.rpc.IgnoreUnknownDeviceRequest
import solarxr_protocol.rpc.UnknownDevice
import solarxr_protocol.rpc.UnknownDeviceStatus
import solarxr_protocol.rpc.UnknownDevicesResponse

internal fun createUnknownDevice(device: UnknownDeviceState, allowedMacs: Set<String>) = UnknownDevice(
	macAddress = device.macAddress,
	ipAddress = ipv4AddressFromString(device.address),
	boardType = device.boardType,
	mcuType = device.mcuType,
	firmwareVersion = device.firmwareVersion,
	status = if (device.macAddress in allowedMacs) UnknownDeviceStatus.ADDING else UnknownDeviceStatus.WAITING,
)

class KnownTrackersBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		val udpServer = appContext.udpServer

		receiver.rpcDispatcher.on<AddUnknownDeviceRequest> { req ->
			val mac = req.macAddress ?: return@on
			appContext.config.settings.context.dispatch(SettingsActions.AddAllowedUdpDevice(mac))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<IgnoreUnknownDeviceRequest> { req ->
			val mac = req.macAddress ?: return@on
			udpServer.context.dispatch(UdpServerActions.IgnoreUnknownDevice(mac))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ForgetDeviceRequest> { req ->
			val mac = req.macAddress ?: return@on
			forgetUdpDevice(appContext, mac)
		}.launchIn(receiver.context.scope)

		combine(
			udpServer.context.state.map { it.unknownDevices },
			appContext.config.settings.context.state.map { it.data.allowedUdpDevices },
		) { unknownDevices, allowedMacs ->
			unknownDevices.values.map { createUnknownDevice(it, allowedMacs) }
		}
			.distinctUntilChanged()
			.onEach { devices -> receiver.sendRpc(UnknownDevicesResponse(devices = devices)) }
			.launchIn(receiver.context.scope)
	}
}
