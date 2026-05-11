package dev.slimevr.udp

import dev.slimevr.AppLogger
import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerIdNum
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.UnknownDeviceHandshakeNotification
import kotlin.random.Random

internal const val CONNECTION_TIMEOUT_MS = 5000L
internal const val CONNECTION_REMOVAL_MS = 30_000L

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
				receiver.context.dispatch(UDPConnectionActions.LastPacket(packetNum = packet.packetNumber, time = now))
			}
		}
	}
}

object PacketLossBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		var totalPacketsReceived = 0L
		var acceptedPackets = 0L
		var lastPacketCounterReset = System.currentTimeMillis()
		var lastPacketNumber = 0L

		receiver.packetEvents.onAny { packet ->
			val num = packet.packetNumber ?: return@onAny
			val now = System.currentTimeMillis()

			if (now - lastPacketCounterReset >= 10_000L) {
				totalPacketsReceived = 0L
				acceptedPackets = 0L
				lastPacketCounterReset = now
			}

			totalPacketsReceived++
			val accepted = num == 0L || num > lastPacketNumber
			if (accepted) {
				lastPacketNumber = num
				acceptedPackets++
			}

			receiver.getDevice()?.context?.dispatch(
				DeviceActions.PacketStats(packetsReceived = totalPacketsReceived, packetsLost = totalPacketsReceived - acceptedPackets),
			)
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
		receiver.context.scope.safeLaunch {
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
		}
	}
}

object HandshakeBehaviour : UDPConnectionBehaviour {
	override fun reduce(state: UDPConnectionState, action: UDPConnectionActions) = when (action) {
		is UDPConnectionActions.Handshake -> state.copy(didHandshake = true, deviceId = action.deviceId)
		is UDPConnectionActions.TimedOut -> state.copy(didHandshake = false)
		else -> state
	}

	private fun findOrCreateDevice(receiver: UDPConnection, state: UDPConnectionState, data: Handshake): Device {
		val devices = receiver.appContext.server.context.state.value.devices.values
		val existing = data.macString?.let { mac ->
			devices.find { device ->
				val ds = device.context.state.value
				ds.macAddress == mac && ds.origin == DeviceOrigin.UDP
			}
		}
		if (existing != null) {
			receiver.context.dispatch(UDPConnectionActions.Handshake(existing.context.state.value.id))
			return existing
		}
		val deviceId = receiver.appContext.server.nextHandle()
		val newDevice = Device.create(
			id = deviceId,
			scope = receiver.appContext.server.context.scope,
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
					receiver.appContext.server.context.scope.safeLaunch {
						receiver.appContext.server.context.state.value.solarxr.values.forEach { bridge ->
							bridge.sendRpc(UnknownDeviceHandshakeNotification(macAddress = mac))
						}
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

			// Apply handshake fields to device, always, for both first connect and reconnect
			device.context.dispatch(
				DeviceActions.Update {
					copy(
						macAddress = packet.data.macString ?: macAddress,
						boardType = packet.data.boardType,
						mcuType = packet.data.mcuType,
						firmware = packet.data.firmware ?: firmware,
						protocolVersion = packet.data.protocolVersion,
						status = TrackerStatus.OK,
					)
				},
			)

			receiver.send(Handshake())
		}
	}
}

object TimeoutBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.context.scope.safeLaunch {
			while (isActive) {
				val state = receiver.context.state.value
				if (!state.didHandshake) {
					delay(500)
					continue
				}
				val timeUntilTimeout = CONNECTION_TIMEOUT_MS - (System.currentTimeMillis() - state.lastPacket)
				if (timeUntilTimeout <= 0) {
					AppLogger.udp.info("[${state.address}] Connection timed out for ${state.id}")
					receiver.context.dispatch(UDPConnectionActions.TimedOut)
					receiver.getDevice()?.context?.dispatch(
						DeviceActions.Update { copy(status = TrackerStatus.TIMED_OUT) },
					)
					state.trackerIds.mapNotNull { receiver.appContext.server.getTracker(it.id) }.forEach { tracker ->
						tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.TIMED_OUT))
					}
				} else {
					delay(timeUntilTimeout + 1)
				}
			}
		}
	}
}

object DisconnectBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		var removalJob: Job? = null
		receiver.context.state
			.distinctUntilChangedBy { it.didHandshake }
			.onEach { state ->
				if (!state.didHandshake) {
					removalJob = receiver.context.scope.safeLaunch {
						delay(CONNECTION_REMOVAL_MS)
						val currentState = receiver.context.state.value
						AppLogger.udp.info("[${currentState.address}] Connection removed after extended timeout")
						receiver.appContext.udpServer.context.dispatch(UdpServerActions.ConnectionRemoved(currentState.address))
						receiver.getDevice()?.context?.dispatch(
							DeviceActions.Update { copy(status = TrackerStatus.DISCONNECTED) },
						)
						currentState.trackerIds.mapNotNull { receiver.appContext.server.getTracker(it.id) }.forEach { tracker ->
							tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.DISCONNECTED))
						}
					}
				} else {
					removalJob?.cancel()
					removalJob = null
				}
			}
			.launchIn(receiver.context.scope)
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

object SensorInfoBehaviour : UDPConnectionBehaviour {
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
					trackerId = TrackerIdNum(id = existingTracker.context.state.value.id, trackerNum = event.data.sensorId),
				),
			)
			return existingTracker to false
		}

		val trackerId = receiver.appContext.server.nextHandle()
		val newTracker = Tracker.create(
			id = trackerId,
			hardwareId = hardwareId,
			sensorType = event.data.imuType,
			deviceId = deviceState.id,
			origin = DeviceOrigin.UDP,
			scope = receiver.appContext.server.context.scope,
			server = receiver.appContext.server,
			settings = receiver.appContext.config.settings,
		)
		receiver.appContext.server.context.dispatch(VRServerActions.NewTracker(trackerId = trackerId, context = newTracker))
		receiver.context.dispatch(
			UDPConnectionActions.AssignTracker(trackerId = TrackerIdNum(id = trackerId, trackerNum = event.data.sensorId)),
		)
		return newTracker to true
	}

	override fun reduce(state: UDPConnectionState, action: UDPConnectionActions) = when (action) {
		is UDPConnectionActions.AssignTracker -> state.copy(trackerIds = state.trackerIds + action.trackerId)
		else -> state
	}

	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<SensorInfo> { event ->
			val device = receiver.getDevice() ?: error("invalid state - a device should exist at this point")

			val existingTracker = receiver.getTracker(event.data.sensorId)
			if (existingTracker != null) {
				existingTracker.context.dispatchAll(
					listOf(
						TrackerActions.Update { copy(sensorType = event.data.imuType, completedRestCalibration = event.data.hasCompletedRestCalibration) },
						TrackerActions.SetStatus(event.data.status),
					),
				)
				return@onPacket
			}

			val (tracker, isNew) = assignTracker(receiver, device, event)
			tracker.context.dispatchAll(
				listOf(
					TrackerActions.Update { copy(sensorType = event.data.imuType, completedRestCalibration = event.data.hasCompletedRestCalibration) },
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
			val globalMagEnabled = receiver.appContext.config.settings.context.state.value.data.globalMagEnabled
			if (remoteMagStatus != desiredMagStatus) {
				if (globalMagEnabled && remoteMagStatus != MagnetometerStatus.ENABLED && desiredMagStatus != MagnetometerStatus.NOT_SUPPORTED) {
					desiredMagStatus = MagnetometerStatus.ENABLED
				}
				receiver.context.dispatch(
					UDPConnectionActions.SetSensorConfig(sensorId = event.data.sensorId, flags = SensorConfigFlags(magStatus = desiredMagStatus)),
				)
			}
		}
	}
}

object SensorRotationBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<RotationData> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = event.data.rotation))
		}

		receiver.packetEvents.onPacket<RotationAndAccel> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = event.data.rotation, acceleration = event.data.acceleration))
		}

		receiver.packetEvents.onPacket<Accel> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.SetRotation(acceleration = event.data.acceleration))
		}

		receiver.packetEvents.onPacket<Rotation2> { event ->
			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = event.data.rotation))
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
		action: UDPConnectionActions,
	): UDPConnectionState = when (action) {
		is UDPConnectionActions.FirmwareFeatures -> state.copy(features = action.features)
		else -> state
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

object SensorConfigBehaviour : UDPConnectionBehaviour {
	override fun reduce(state: UDPConnectionState, action: UDPConnectionActions) = when (action) {
		is UDPConnectionActions.SetSensorConfig -> state.copy(
			sensorConfigFlags = state.sensorConfigFlags + (action.sensorId to action.flags),
		)

		else -> state
	}

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

object AckConfigBehaviour : UDPConnectionBehaviour {
	override fun observe(receiver: UDPConnection) {
		receiver.packetEvents.onPacket<AckConfigChange> { event ->
			val configType = SensorConfigType.fromId(event.data.configType) ?: return@onPacket
			val flags = receiver.context.state.value.sensorConfigFlags[event.data.sensorId] ?: return@onPacket

			val tracker = receiver.getTracker(event.data.sensorId) ?: return@onPacket
			if (configType == SensorConfigType.MAGNETOMETER) {
				tracker.context.dispatch(TrackerActions.SetMagStatus(flags.magStatus))
			}
		}
	}
}
