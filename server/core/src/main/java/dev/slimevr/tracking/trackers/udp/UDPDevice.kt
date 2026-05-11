package dev.slimevr.tracking.trackers.udp

import dev.slimevr.NetworkProtocol
import dev.slimevr.VRServer
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import java.net.InetAddress
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.TimeSource

class UDPDevice(
	var address: SocketAddress,
	var ipAddress: InetAddress,
	override val hardwareIdentifier: String,
	override val boardType: BoardType = BoardType.UNKNOWN,
	override val mcuType: MCUType = MCUType.UNKNOWN,
	val timeslot: Int,
) : Device(true) {

	override val id: Int = nextLocalDeviceId.incrementAndGet()

	@JvmField
	var lastPacket = System.currentTimeMillis()

	@JvmField
	var lastPingPacketId = -1

	@JvmField
	var lastPingPacketTime: Long = 0
	var lastPingPacketInstant = TimeSource.Monotonic.markNow()

	override var name: String? = null
		set(name) {
			super.name = name
			field = name
		}

	@JvmField
	var descriptiveName: String? = null

	@JvmField
	var serialBuffer = StringBuilder()

	@JvmField
	var lastSerialUpdate: Long = 0

	@JvmField
	var lastPacketNumber: Long = -1

	@JvmField
	var totalPacketsReceived: Int = 0

	@JvmField
	var acceptedPackets: Int = 0

	@JvmField
	var lastPacketCounterReset: Long = System.currentTimeMillis()

	val packetLossPercent: Float
		get() = if (totalPacketsReceived == 0) 0f else (1f - acceptedPackets.toFloat() / totalPacketsReceived.toFloat())

	@JvmField
	var protocol: NetworkProtocol? = null

	@JvmField
	var protocolVersion = 0

	@JvmField
	var timedOut = false
	override val trackers = ConcurrentHashMap<Int, Tracker>()

	override suspend fun setMag(state: Boolean, sensorId: Int) {
		if (sensorId == 255) {
			VRServer.instance.trackersServer.setConfigFlag(this, ConfigTypeId(1u), state)
			trackers.forEach { (_, t) -> t.setMagPrivate(state) }
		} else {
			require(trackers[sensorId] != null) { "There is no tracker $sensorId in device ${toString()}" }
			VRServer.instance.trackersServer.setConfigFlag(this, ConfigTypeId(1u), state, sensorId)
			trackers[sensorId]!!.setMagPrivate(state)
		}
	}

	var firmwareFeatures = FirmwareFeatures()

	fun isNextPacket(packetId: Long): Boolean {
		val now = System.currentTimeMillis()
		if (now - lastPacketCounterReset >= 10_000L) {
			totalPacketsReceived = 0
			acceptedPackets = 0
			lastPacketCounterReset = now
		}
		totalPacketsReceived++
		val accepted = packetId == 0L || packetId > lastPacketNumber
		if (accepted) {
			lastPacketNumber = packetId
			acceptedPackets++
		}
		val lost = totalPacketsReceived - acceptedPackets
		trackers.values.forEach {
			it.packetsReceived = totalPacketsReceived
			it.packetsLost = lost
			it.packetLoss = packetLossPercent
		}
		return accepted
	}

	override fun toString(): String = "udp:/$ipAddress"

	override var manufacturer: String?
		get() = "SlimeVR"
		set(manufacturer) {
			super.manufacturer = manufacturer
		}

	fun getTracker(id: Int): Tracker? = trackers[id]
}
