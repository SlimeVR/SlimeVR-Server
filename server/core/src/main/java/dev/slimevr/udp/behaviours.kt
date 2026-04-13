package dev.slimevr.udp

import dev.slimevr.AppLogger
import dev.slimevr.VRServerActions
import dev.slimevr.config.Settings
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerIdNum
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.random.Random

internal const val CONNECTION_TIMEOUT_MS = 5000L

object PacketBehaviour : UDPConnectionBehaviour {
	override fun reduce(state: UDPConnectionState, action: UDPConnectionActions) = when (action) {
		is UDPConnectionActions.LastPacket -> {
			var newState = state.copy(lastPacket = action.time)
			if (action.packetNum != null) newState = newState.copy(lastPacketNum = action.packetNum)
			newState
		}

		else -> state
	}

	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onAny { packet ->
			val state = receiver.context.state.value
			val now = System.currentTimeMillis()
			if (now - state.lastPacket > CONNECTION_TIMEOUT_MS && packet.packetNumber == 0L) {
				receiver.context.dispatch(UDPConnectionActions.LastPacket(packetNum = 0, time = now))
				AppLogger.udp.info("[${state.address}] Reconnecting")
			} else if (packet.packetNumber != null && packet.packetNumber < state.lastPacketNum) {
				// Note: Packet number is nullable for bundled packets, as only the bundle packet itself has the number
				// the packets inside of it do not
				AppLogger.udp.warn("[${state.address}] WARN: Received packet with wrong packet number")
				return@onAny
			} else {
				receiver.context.dispatch(UDPConnectionActions.LastPacket(time = now))
			}
		}
	}
}

object PingBehaviour : UDPConnectionBehaviour {
	override fun reduce(state: UDPConnectionState, action: UDPConnectionActions) = when (action) {
		is UDPConnectionActions.StartPing -> state.copy(lastPing = state.lastPing.copy(startTime = action.startTime, id = action.pingId))
		else -> state
	}

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
			val device = receiver.serverContext.getDevice(deviceId) ?: return@onPacket
			device.context.dispatch(DeviceActions.Update { copy(ping = ping) })
		}
	}
}

object HandshakeBehaviour : UDPConnectionBehaviour {
	override fun reduce(state: UDPConnectionState, action: UDPConnectionActions) = when (action) {
		is UDPConnectionActions.Handshake -> state.copy(didHandshake = true, deviceId = action.deviceId)
		is UDPConnectionActions.Disconnected -> state.copy(didHandshake = false)
		else -> state
	}

	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<Handshake> { packet ->
			val state = receiver.context.state.value

			val device = if (state.deviceId == null) {
				val deviceId = receiver.serverContext.nextHandle()
				val newDevice = Device.create(
					id = deviceId,
					scope = receiver.serverContext.context.scope,
					address = state.address,
					macAddress = packet.data.macString,
					origin = DeviceOrigin.UDP,
					protocolVersion = packet.data.protocolVersion,
				)
				receiver.serverContext.context.dispatch(VRServerActions.NewDevice(deviceId = deviceId, context = newDevice))
				receiver.context.dispatch(UDPConnectionActions.Handshake(deviceId))
				newDevice
			} else {
				receiver.context.dispatch(UDPConnectionActions.Handshake(state.deviceId))
				receiver.getDevice() ?: run {
					AppLogger.udp.warn("[${state.address}] Reconnect handshake but device ${state.deviceId} not found")
					receiver.send(Handshake())
					return@onPacket
				}
			}

			// Apply handshake fields to device, always, for both first connect and reconnect
			device.context.dispatch(
				DeviceActions.Update {
					copy(
						macAddress = packet.data.macString ?: macAddress,
						boardType = packet.data.boardType,
						mcuType = packet.data.mcuType,
						firmware = packet.data.firmware ?: firmware,
						protocolVersion = packet.data.protocolVersion,
					)
				},
			)

			receiver.send(Handshake())
		}
	}
}

object TimeoutBehaviour : UDPConnectionBehaviour {
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
					AppLogger.udp.info("[${state.address}] Connection timed out for ${state.id}")
					receiver.context.dispatch(UDPConnectionActions.Disconnected)
					receiver.getDevice()?.context?.dispatch(
						DeviceActions.Update { copy(status = TrackerStatus.DISCONNECTED) },
					)
					state.trackerIds.mapNotNull { receiver.serverContext.getTracker(it.id) }.forEach { tracker ->
						tracker.context.dispatch(TrackerActions.Update { copy(status = TrackerStatus.DISCONNECTED) })
					}
				} else {
					delay(timeUntilTimeout + 1)
				}
			}
		}
	}
}

object DeviceStatsBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<BatteryLevel> { event ->
			val device = receiver.getDevice() ?: return@onPacket
			device.context.dispatch(
				DeviceActions.Update {
					copy(batteryLevel = event.data.level, batteryVoltage = event.data.voltage)
				},
			)
		}

		receiver.packetEvents.onPacket<SignalStrength> { event ->
			val device = receiver.getDevice() ?: return@onPacket
			device.context.dispatch(DeviceActions.Update { copy(signalStrength = event.data.signal) })
		}
	}
}

class SensorInfoBehaviour(private val settings: Settings) : UDPConnectionBehaviour {
	override fun reduce(state: UDPConnectionState, action: UDPConnectionActions) = when (action) {
		is UDPConnectionActions.AssignTracker -> state.copy(trackerIds = state.trackerIds + action.trackerId)
		else -> state
	}

	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<SensorInfo> { event ->
			val device = receiver.getDevice()
				?: error("invalid state - a device should exist at this point")

			val tracker = receiver.getTracker(event.data.sensorId)
			val action = TrackerActions.Update {
				copy(
					sensorType = event.data.imuType,
					status = event.data.status,
					completedRestCalibration = event.data.hasCompletedRestCalibration ?: tracker?.context?.state?.value?.completedRestCalibration ?: false
				)
			}

			if (tracker != null) {
				tracker.context.dispatch(action)
			} else {
				val deviceState = device.context.state.value
				val mac = deviceState.macAddress ?: run {
						AppLogger.udp.warn("[${deviceState.address}] No MAC address available, falling back to IP for hardware ID")
						deviceState.address
					}
					val hardwareId = "$mac:${event.data.sensorId}"
				val trackerId = receiver.serverContext.nextHandle()
				val newTracker = Tracker.create(
					id = trackerId,
					hardwareId = hardwareId,
					sensorType = event.data.imuType,
					deviceId = deviceState.id,
					origin = DeviceOrigin.UDP,
					scope = receiver.serverContext.context.scope,
					server = receiver.serverContext,
					settings = settings,
				)

				receiver.serverContext.context.dispatch(
					VRServerActions.NewTracker(trackerId = trackerId, context = newTracker),
				)
				receiver.context.dispatch(
					UDPConnectionActions.AssignTracker(
						trackerId = TrackerIdNum(id = trackerId, trackerNum = event.data.sensorId),
					),
				)
				newTracker.context.dispatch(action)
			}
		}
	}
}

object SensorRotationBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<RotationData> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(rawRotation = event.data.rotation) })
		}

		receiver.packetEvents.onPacket<RotationAndAccel> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(rawRotation = event.data.rotation, acceleration = event.data.acceleration) })
		}

		receiver.packetEvents.onPacket<Accel> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(acceleration = event.data.acceleration) })
		}

		receiver.packetEvents.onPacket<Rotation2> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(rawRotation = event.data.rotation) })
		}
	}
}

object BundledPacketBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<PacketBundle> { event ->
			for (packet in event.data.packets) {
				if (!receiver.context.state.value.didHandshake && packet !is PreHandshakePacket) continue
				// we set the packetNumber to null so we ignore the check
				// it should be done by the parent packet
				receiver.packetEvents.emit(PacketEvent(packet, packetNumber = null))
			}
		}

		receiver.packetEvents.onPacket<PacketBundleCompact> { event ->
			for (packet in event.data.packets) {
				if (!receiver.context.state.value.didHandshake && packet !is PreHandshakePacket) continue
				// we set the packetNumber to null so we ignore the check
				// it should be done by the parent packet
				receiver.packetEvents.emit(PacketEvent(packet, packetNumber = null))
			}
		}
	}
}

object FlagsBehaviour : UDPConnectionBehaviour {
	override fun reduce(
		state: UDPConnectionState,
		action: UDPConnectionActions
	): UDPConnectionState {
		return when (action) {
			is UDPConnectionActions.FirmwareFeatures -> state.copy(features = action.features)
			else -> state
		}
	}

	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<FeatureFlags> { event ->
			receiver.context.dispatch(UDPConnectionActions.FirmwareFeatures(event.data.firmwareFeatures))
			// send back the server features
			receiver.send(FeatureFlags())
		}
	}
}

object TemperatureBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<Temperature> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(imuTemp = event.data.temp) })
		}
	}
}
