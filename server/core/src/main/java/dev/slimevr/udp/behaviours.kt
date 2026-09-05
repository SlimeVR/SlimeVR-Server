package dev.slimevr.udp

import com.jme3.math.FastMath
import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.UnknownDeviceHandshakeNotification
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Change between IMU axes and OpenGL/SteamVR axes
 */
internal val AXES_OFFSET = Quaternion.fromRotationVector(-FastMath.HALF_PI, 0f, 0f)
internal const val CONNECTION_TIMEOUT_MS = 5000L

class PacketBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.on<PacketEvent<UDPPacket>> { packet ->
			val state = receiver.context.state.value
			val now = System.currentTimeMillis()
			val num = packet.packetNumber
			if (num == 0L && now - state.lastPacket > CONNECTION_TIMEOUT_MS) {
				AppLogger.udp.info("[${state.address}] Reconnecting")
			} else if (num != null && num != 0L && num <= state.lastPacketNum) {
				AppLogger.udp.warn("[${state.address}] Received packet with wrong packet number")
				return@on
			}
			receiver.context.dispatch(UDPConnectionActions.LastPacket(packetNum = num, time = now))
		}.launchIn(receiver.context.scope)
	}
}

/**
 * Packets missing between [last] and [num]. A number at or below the mark arrived, so it is not
 * loss, whether it is a duplicate or one that overtook its neighbours.
 */
internal fun packetsLostBetween(last: Long?, num: Long): Int = when {
	last == null || num <= last -> 0
	else -> (num - last - 1).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}

class PacketLossBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		var lastPacketNumber: Long? = null

		// A tracker restarts its counter when it reconnects
		receiver.context.state
			.distinctUntilChangedBy { it.lastHandshake }
			.onEach { lastPacketNumber = null }
			.launchIn(receiver.context.scope)

		receiver.packetEvents.on<PacketEvent<UDPPacket>> { packet ->
			val num = packet.packetNumber ?: return@on
			val last = lastPacketNumber
			val lost = packetsLostBetween(last, num)
			// Highest number seen wins, so a late arrival cannot drag the mark backwards
			lastPacketNumber = if (last == null) num else maxOf(last, num)

			receiver.getDevice()?.recordPacketStats(received = 1, lost = lost, at = System.currentTimeMillis())
		}.launchIn(receiver.context.scope)
	}
}

class PingBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		// Send the ping every 1s
		receiver.context.scope.launch {
			while (isActive) {
				val state = receiver.context.state.value
				if (state.didHandshake) {
					val pingId = Random.nextInt()
					receiver.context.dispatch(UDPConnectionActions.StartPing(startTime = System.currentTimeMillis(), pingId = pingId))
					receiver.send(PingPong(pingId))
				}
				delay(1000)
			}
		}

		// listen for the pong
		receiver.packetEvents.onPacket<PingPong> { packet ->
			val state = receiver.context.state.value
			val deviceId = state.deviceId ?: return@onPacket

			if (packet.data.pingId != state.lastPing.id) {
				AppLogger.udp.warn("[${state.address}] Ping ID does not match, ignoring ${packet.data.pingId} != ${state.lastPing.id}")
				return@onPacket
			}

			val ping = (System.currentTimeMillis() - state.lastPing.startTime) / 2
			val device = receiver.appContext.server.getDevice(deviceId) ?: return@onPacket
			device.context.dispatch(DeviceActions.Update { copy(ping = ping) })
		}.launchIn(receiver.context.scope)
	}
}

class HandshakeBehaviour : UDPConnectionBehaviour {
	private fun findOrCreateDevice(receiver: UDPConnection, state: UDPConnectionState, data: Handshake): Device {
		val devices = receiver.appContext.server.context.state.value.devices.values
		val existing = data.macString?.let { mac ->
			devices.find { device ->
				val ds = device.context.state.value
				ds.macAddress == mac && ds.origin == DeviceOrigin.UDP
			}
		}
		if (existing != null) {
			val existingId = existing.context.state.value.id
			receiver.appContext.udpServer.context.state.value.connections.values
				.filter { c -> c.context.state.value.address != state.address && c.context.state.value.deviceId == existingId }
				.forEach { oldConn ->
					receiver.appContext.udpServer.removeConnection(oldConn.context.state.value.address)
				}
			receiver.context.dispatch(UDPConnectionActions.Handshake(existingId))
			return existing
		}
		val deviceId = receiver.appContext.server.nextHandle()
		val newDevice = Device.create(
			scope = receiver.appContext.server.context.scope,
			appContext = receiver.appContext,
			id = deviceId,
			address = state.address,
			macAddress = data.macString,
			origin = DeviceOrigin.UDP,
			protocolVersion = data.protocolVersion,
		)
		receiver.appContext.server.context.dispatch(VRServerActions.NewDevice(deviceId = deviceId, context = newDevice))
		receiver.context.dispatch(UDPConnectionActions.Handshake(deviceId))
		return newDevice
	}

	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<Handshake> { packet ->
			val state = receiver.context.state.value
			val mac = packet.data.macString

			if (mac != null) {
				val settings = receiver.appContext.config.settings.context.state.value.data
				if (mac !in settings.allowedUdpDevices) {
					AppLogger.udp.info("[${state.address}] Unknown MAC $mac, notifying solarxr")
					receiver.appContext.server.context.scope.launch {
						receiver.appContext.server.sendSolarxrRpc(
							UnknownDeviceHandshakeNotification(macAddress = mac),
						)
					}
					return@onPacket
				}
			}

			val device = if (state.deviceId == null) {
				findOrCreateDevice(receiver, state, packet.data)
			} else {
				receiver.context.dispatch(UDPConnectionActions.Handshake(state.deviceId))
				receiver.getDevice() ?: run {
					AppLogger.udp.warn("[${state.address}] Reconnect handshake but device ${state.deviceId} not found")
					receiver.send(Handshake())
					return@onPacket
				}
			}

			val previousStatus = device.context.state.value.status
			if (previousStatus != TrackerStatus.OK) {
				AppLogger.udp.info("[${state.address}] Handshake from ${device.context.state.value.macAddress}, was $previousStatus")
			}

			// Apply handshake fields to device, always, for both first connect and reconnect
			device.context.dispatch(
				DeviceActions.Update {
					copy(
						macAddress = packet.data.macString ?: macAddress,
						boardType = packet.data.boardType,
						mcuType = packet.data.mcuType,
						firmwareVersion = packet.data.firmware ?: firmwareVersion,
						protocolVersion = packet.data.protocolVersion,
						status = TrackerStatus.OK,
					)
				},
			)

			receiver.send(Handshake())
		}.launchIn(receiver.context.scope)
	}
}

private fun updateConnectionStatus(receiver: UDPConnection, status: TrackerStatus): Boolean {
	val state = receiver.context.state.value
	val device = receiver.getDevice() ?: return false
	if (device.context.state.value.status == status) return false

	device.context.dispatch(DeviceActions.Update { copy(status = status) })
	state.trackerIds
		.mapNotNull { receiver.appContext.server.getTracker(it.trackerId) }
		.forEach { tracker -> tracker.context.dispatch(TrackerActions.SetStatus(status)) }
	return true
}

class TimeoutBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.context.scope.launch {
			while (isActive) {
				val state = receiver.context.state.value
				if (!state.didHandshake) {
					delay(500)
					continue
				}
				val timeUntilTimeout = CONNECTION_TIMEOUT_MS - (System.currentTimeMillis() - state.lastPacket)
				if (timeUntilTimeout <= 0) {
					if (updateConnectionStatus(receiver, TrackerStatus.TIMED_OUT)) {
						AppLogger.udp.info("[${state.address}] Connection timed out")
					}
					delay(500)
				} else {
					delay(timeUntilTimeout + 1)
				}
			}
		}
	}
}

class DisconnectBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.context.scope.launch {
			while (isActive) {
				val state = receiver.context.state.value
				if (!state.didHandshake) {
					delay(500)
					continue
				}
				val timeUntilRemoval = receiver.appContext.config.settings.context.state.value.data.trackersConfig.timeoutDelay.toDouble().seconds - (System.currentTimeMillis() - state.lastPacket).milliseconds
				if (timeUntilRemoval <= 0.milliseconds) {
					AppLogger.udp.info("[${state.address}] Connection removed after extended timeout")
					receiver.appContext.udpServer.removeConnection(state.address)
					updateConnectionStatus(receiver, TrackerStatus.DISCONNECTED)
					break
				} else {
					delay(timeUntilRemoval + 1.milliseconds)
				}
			}
		}
	}
}

class DeviceStatsBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<BatteryLevel> { event ->
			val device = receiver.getDevice() ?: return@onPacket
			val voltage = event.data.voltage
			val level = event.data.level
			val batteryLevel = if (voltage != null) {
				// Gate on voltage validity: too low/high means no battery or measurement error
				if (voltage > 2f && voltage < 6f) if (level < 0.01f) null else level else null
			} else {
				level
			}
			device.context.dispatch(
				DeviceActions.Update { copy(batteryLevel = batteryLevel, batteryVoltage = voltage) },
			)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.onPacket<SignalStrength> { event ->
			val device = receiver.getDevice() ?: return@onPacket
			device.context.dispatch(DeviceActions.Update { copy(signalStrength = event.data.signal) })
			device.recordRssi(event.data.signal)
		}.launchIn(receiver.context.scope)
	}
}

class SensorInfoBehaviour : UDPConnectionBehaviour {
	private suspend fun assignTracker(receiver: UDPConnection, device: Device, event: PacketEvent<SensorInfo>): Pair<Tracker, Boolean> {
		val deviceState = device.context.state.value
		val mac = deviceState.macAddress ?: run {
			AppLogger.udp.warn("[${deviceState.address}] No MAC address available, falling back to IP for hardware ID")
			deviceState.address
		}
		val hardwareId = "$mac:${event.data.sensorId}"

		val existingTracker = receiver.appContext.server.context.state.value.trackers.values
			.find { t -> t.context.state.value.deviceId == deviceState.id && t.context.state.value.hardwareId == hardwareId }

		if (existingTracker != null) {
			receiver.context.dispatch(
				UDPConnectionActions.AssignTracker(
					trackerId = TrackerSensorIds(trackerId = existingTracker.context.state.value.id, sensorId = event.data.sensorId),
				),
			)
			return existingTracker to false
		}

		val trackerId = receiver.appContext.server.nextHandle()
		val newTracker = Tracker.create(
			id = trackerId,
			hardwareId = hardwareId,
			imuType = event.data.imuType,
			deviceId = deviceState.id,
			origin = DeviceOrigin.UDP,
			scope = receiver.appContext.server.context.scope,
			appContext = receiver.appContext,
		)
		receiver.appContext.server.context.dispatch(VRServerActions.NewTracker(trackerId = trackerId, context = newTracker))
		receiver.context.dispatch(
			UDPConnectionActions.AssignTracker(trackerId = TrackerSensorIds(trackerId = trackerId, sensorId = event.data.sensorId)),
		)
		return newTracker to true
	}

	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<SensorInfo> { event ->
			val device = receiver.getDevice() ?: error("invalid state - a device should exist at this point")

			val existingTracker = receiver.getTracker(event.data.sensorId)
			if (existingTracker != null) {
				existingTracker.context.dispatchAll(
					listOf(
						TrackerActions.Update { copy(imuType = event.data.imuType, completedRestCalibration = event.data.hasCompletedRestCalibration) },
						TrackerActions.SetStatus(event.data.status),
					),
				)
				return@onPacket
			}

			val (tracker, isNew) = assignTracker(receiver, device, event)
			tracker.context.dispatchAll(
				listOf(
					TrackerActions.Update { copy(imuType = event.data.imuType, completedRestCalibration = event.data.hasCompletedRestCalibration) },
					TrackerActions.SetStatus(event.data.status),
				),
			)
			if (isNew && tracker.context.state.value.magStatus == MagnetometerStatus.NOT_SUPPORTED) {
				tracker.context.dispatch(
					TrackerActions.SetMagStatus(
						if (event.data.sensorConfig?.magSupported == true) MagnetometerStatus.DISABLED else MagnetometerStatus.NOT_SUPPORTED,
					),
				)
			}

			val remoteMagStatus = event.data.sensorConfig?.let {
				if (it.magSupported) {
					if (it.magEnabled) MagnetometerStatus.ENABLED else MagnetometerStatus.DISABLED
				} else {
					MagnetometerStatus.NOT_SUPPORTED
				}
			} ?: MagnetometerStatus.NOT_SUPPORTED

			var desiredMagStatus = tracker.context.state.value.magStatus
			val globalMagEnabled = receiver.appContext.config.settings.context.state.value.data.trackersConfig.globalMagEnabled
			if (remoteMagStatus != desiredMagStatus) {
				if (globalMagEnabled && remoteMagStatus != MagnetometerStatus.ENABLED && desiredMagStatus != MagnetometerStatus.NOT_SUPPORTED) {
					desiredMagStatus = MagnetometerStatus.ENABLED
				}
				receiver.context.dispatch(
					UDPConnectionActions.SetSensorConfig(sensorId = event.data.sensorId, flags = SensorConfigFlags(magStatus = desiredMagStatus)),
				)
			}
		}.launchIn(receiver.context.scope)
	}
}

class SensorRotationBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<RotationData> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.setRotation(rotation = AXES_OFFSET * event.data.rotation)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.onPacket<RotationAndAccel> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.setRotation(rotation = AXES_OFFSET * event.data.rotation, acceleration = event.data.acceleration)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.onPacket<Accel> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.setRotation(acceleration = event.data.acceleration)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.onPacket<Rotation2> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.setRotation(rotation = AXES_OFFSET * event.data.rotation)
		}.launchIn(receiver.context.scope)
	}
}

class BundledPacketBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<PacketBundle> { event ->
			for (packet in event.data.packets) {
				if (!receiver.context.state.value.didHandshake && packet !is PreHandshakePacket) continue
				// we set the packetNumber to null so we ignore the check
				// it should be done by the parent packet
				receiver.packetEvents.emit(PacketEvent(packet, packetNumber = null))
			}
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.onPacket<PacketBundleCompact> { event ->
			for (packet in event.data.packets) {
				if (!receiver.context.state.value.didHandshake && packet !is PreHandshakePacket) continue
				// we set the packetNumber to null so we ignore the check
				// it should be done by the parent packet
				receiver.packetEvents.emit(PacketEvent(packet, packetNumber = null))
			}
		}.launchIn(receiver.context.scope)
	}
}

class FlagsBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<FeatureFlags> { event ->
			receiver.context.dispatch(UDPConnectionActions.FirmwareFeatures(event.data.firmwareFeatures))
			// send back the server features
			receiver.send(FeatureFlags())
		}.launchIn(receiver.context.scope)
	}
}

class TemperatureBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<Temperature> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(imuTemp = event.data.temp) })
		}.launchIn(receiver.context.scope)
	}
}

class SensorConfigBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.context.state
			.distinctUntilChangedBy { it.sensorConfigFlags }
			.onEach { state ->
				for ((sensorId, flags) in state.sensorConfigFlags) {
					receiver.send(
						SetConfigFlag(
							sensorId = sensorId,
							configType = SensorConfigType.MAGNETOMETER,
							state = flags.magStatus == MagnetometerStatus.ENABLED,
						),
					)
				}
			}
			.launchIn(receiver.context.scope)
	}
}

class AckConfigBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<AckConfigChange> { event ->
			val configType = SensorConfigType.fromId(event.data.configType) ?: return@onPacket
			val flags = receiver.context.state.value.sensorConfigFlags[event.data.sensorId] ?: return@onPacket

			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			if (configType == SensorConfigType.MAGNETOMETER) {
				tracker.context.dispatch(TrackerActions.SetMagStatus(flags.magStatus))
			}
		}.launchIn(receiver.context.scope)
	}
}
