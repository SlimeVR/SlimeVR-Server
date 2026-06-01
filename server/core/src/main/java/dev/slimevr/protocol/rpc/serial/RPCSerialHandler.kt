package dev.slimevr.protocol.rpc.serial

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.serial.SerialListener
import dev.slimevr.serial.SerialPort
import dev.slimevr.tracking.trackers.hid.HIDCommon
import io.eiren.util.logging.LogManager
import solarxr_protocol.rpc.*
import java.util.*
import java.util.function.Consumer
import kotlin.collections.emptyList

fun createSerialDevice(fbb: FlatBufferBuilder, port: SerialPort): Int {
	val portOffset = fbb.createString(port.portLocation)
	val nameOffset = fbb.createString(port.descriptivePortName)
	val portType = when {
		HIDCommon.matchesReceiver(port.vendorId, port.productId) -> SerialDeviceType.HID_RECEIVER
		HIDCommon.matchesTracker(port.vendorId, port.productId) -> SerialDeviceType.HID_TRACKER
		else -> SerialDeviceType.ESP_TRACKER
	}

	return SerialDevice.createSerialDevice(fbb, portOffset, nameOffset, portType)
}

class RPCSerialHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI) : SerialListener {
	init {
		rpcHandler.registerPacketListener(RpcMessage.SerialTrackerRebootRequest, ::onSerialTrackerRebootRequest)
		rpcHandler.registerPacketListener(RpcMessage.SerialTrackerGetInfoRequest, ::onSerialTrackerGetInfoRequest)
		rpcHandler.registerPacketListener(RpcMessage.SerialTrackerFactoryResetRequest, ::onSerialTrackerFactoryResetRequest)
		rpcHandler.registerPacketListener(RpcMessage.SerialTrackerGetWifiScanRequest, ::onSerialTrackerGetWifiScanRequest)
		rpcHandler.registerPacketListener(RpcMessage.SerialTrackerCustomCommandRequest, ::onSerialTrackerCustomCommandRequest)
		rpcHandler.registerPacketListener(RpcMessage.SetWifiRequest, ::onSetWifiRequest)
		rpcHandler.registerPacketListener(RpcMessage.OpenSerialRequest, ::onOpenSerialRequest)
		rpcHandler.registerPacketListener(RpcMessage.CloseSerialRequest, ::onCloseSerialRequest)
		rpcHandler.registerPacketListener(RpcMessage.SerialDevicesRequest, ::onRequestSerialDevices)
		this.api.server.serialHandler.addListener(this)
	}

	override fun onSerialDisconnected() {
		val fbb = FlatBufferBuilder(32)

		SerialUpdateResponse.startSerialUpdateResponse(fbb)
		SerialUpdateResponse.addClosed(fbb, true)
		val update = SerialUpdateResponse.endSerialUpdateResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update)
		fbb.finish(outbound)

		this.forAllListeners { conn: GenericConnection ->
			conn.send(fbb.dataBuffer())
			conn.context.useSerial = false
		}
	}

	override fun onSerialLog(str: String, server: Boolean) {
		val fbb = FlatBufferBuilder(32)

		val logOffset = fbb.createString(str)
		val portOffset = api.server.serialHandler.getCurrentPort()?.let { port ->
			createSerialDevice(fbb, port)
		} ?: 0

		SerialUpdateResponse.startSerialUpdateResponse(fbb)
		SerialUpdateResponse.addLog(fbb, logOffset)
		SerialUpdateResponse.addDevice(fbb, portOffset)
		val update = SerialUpdateResponse.endSerialUpdateResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update)
		fbb.finish(outbound)

		this.forAllListeners { conn ->
			conn.send(fbb.dataBuffer())
		}
	}

	override fun onNewSerialDevice(port: SerialPort) {
		val fbb = FlatBufferBuilder(32)

		val deviceOffset = createSerialDevice(fbb, port)
		val newSerialOffset = NewSerialDeviceResponse
			.createNewSerialDeviceResponse(fbb, deviceOffset)
		val outbound = rpcHandler
			.createRPCMessage(fbb, RpcMessage.NewSerialDeviceResponse, newSerialOffset)
		fbb.finish(outbound)

		this.api
			.apiServers
			.forEach(
				Consumer { server: ProtocolAPIServer ->
					server
						.apiConnections
						.forEach { conn: GenericConnection ->
							conn.send(fbb.dataBuffer())
						}
				},
			)
	}

	override fun onSerialConnected(port: SerialPort) {
		val fbb = FlatBufferBuilder(32)

		val deviceOffset = createSerialDevice(fbb, port)
		SerialUpdateResponse.startSerialUpdateResponse(fbb)
		SerialUpdateResponse.addClosed(fbb, false)
		SerialUpdateResponse.addDevice(fbb, deviceOffset)
		val update = SerialUpdateResponse.endSerialUpdateResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update)
		fbb.finish(outbound)

		this.forAllListeners { conn ->
			conn.send(fbb.dataBuffer())
		}
	}

	fun onSerialTrackerRebootRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(SerialTrackerRebootRequest()) as SerialTrackerRebootRequest?
		if (req == null) return

		this.api.server.serialHandler.rebootRequest()
	}

	fun onSerialTrackerGetInfoRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(SerialTrackerGetInfoRequest()) as SerialTrackerGetInfoRequest?
		if (req == null) return

		this.api.server.serialHandler.infoRequest()
	}

	fun onSerialTrackerFactoryResetRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(SerialTrackerFactoryResetRequest()) as SerialTrackerFactoryResetRequest?
		if (req == null) return

		this.api.server.serialHandler.factoryResetRequest()
	}

	fun onSerialTrackerGetWifiScanRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(SerialTrackerGetWifiScanRequest()) as SerialTrackerGetWifiScanRequest?
		if (req == null) return

		this.api.server.serialHandler.wifiScanRequest()
	}

	fun onSerialTrackerCustomCommandRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(SerialTrackerCustomCommandRequest()) as SerialTrackerCustomCommandRequest?

		if (req == null || req.command() == null) return

		this.api.server.serialHandler.customCommandRequest(Objects.requireNonNull(req.command()))
	}

	private fun onRequestSerialDevices(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(SerialDevicesRequest()) as SerialDevicesRequest?
		if (req == null) return

		val fbb = FlatBufferBuilder(32)

		val devicesOffsets = try {
			api.server.serialHandler.knownPorts.map { port ->
				createSerialDevice(fbb, port)
			}.toList()
		} catch (e: Throwable) {
			LogManager.severe("Using serial ports is not supported on this platform", e)
			emptyList()
		}

		SerialDevicesResponse.startDevicesVector(fbb, devicesOffsets.size)
		devicesOffsets.forEach { offset: Int -> SerialDevicesResponse.addDevices(fbb, offset) }
		val devices = fbb.endVector()
		val serialDeviceOffsets = SerialDevicesResponse.createSerialDevicesResponse(fbb, devices)
		val outbound = rpcHandler
			.createRPCMessage(fbb, RpcMessage.SerialDevicesResponse, serialDeviceOffsets)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun onSetWifiRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(SetWifiRequest()) as? SetWifiRequest? ?: return

		if (req.password() == null || req.ssid() == null || !this.api.server.serialHandler.isConnected) {
			return
		}
		this.api.server.serialHandler.setWifi(req.ssid(), req.password())
	}

	fun onOpenSerialRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req =
			messageHeader.message(OpenSerialRequest()) as? OpenSerialRequest? ?: return

		conn.context.useSerial = true

		this.api.server.queueTask {
			try {
				this.api.server.serialHandler.openSerial(req.port(), req.auto(), true)
			} catch (e: Exception) {
				LogManager.severe("Unable to open serial port", e)
			} catch (e: Throwable) {
				LogManager.severe(
					"Using serial ports is not supported on this platform",
					e,
				)
			}
			val fbb = FlatBufferBuilder(32)
			SerialUpdateResponse.startSerialUpdateResponse(fbb)
			SerialUpdateResponse.addClosed(
				fbb,
				!this.api.server.serialHandler.isConnected,
			)
			val update = SerialUpdateResponse.endSerialUpdateResponse(fbb)
			val outbound = rpcHandler
				.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update)
			fbb.finish(outbound)
			conn.send(fbb.dataBuffer())
		}
	}

	fun onCloseSerialRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(CloseSerialRequest()) as CloseSerialRequest?
		if (req == null) return

		conn.context.useSerial = false

		this.api.server.serialHandler.closeSerial()

		val fbb = FlatBufferBuilder(32)
		SerialUpdateResponse.startSerialUpdateResponse(fbb)
		SerialUpdateResponse.addClosed(fbb, !this.api.server.serialHandler.isConnected)
		val update = SerialUpdateResponse.endSerialUpdateResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun forAllListeners(action: (GenericConnection) -> Unit) {
		this.api.apiServers.forEach { server ->
			server.apiConnections.filter { it.context.useSerial }.forEach(action)
		}
	}

	override fun onSerialDeviceDeleted(port: SerialPort) {
	}
}
