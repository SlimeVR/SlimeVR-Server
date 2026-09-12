package dev.slimevr.device

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlin.math.roundToInt

private const val MAX_SAMPLE_RETENTION_MS = 12_000L
private const val MAX_SAMPLES = 4096

private const val MIN_PACKETS_FOR_LOSS = 20

private const val MAX_LOSS_LOOKBACK_MS = 5_000L

/** Fewer dropped packets than this is jitter, not an outage anyone can see in the pose */
private const val MIN_PACKETS_FOR_GAP = 3

/**
 * Share of a sample that has to go missing before it reads as an outage.
 *
 * Sources deliver very different batches per sample: a datagram carries one packet, a dongle status
 * report carries a second's worth. A flat count would mark a run on the first and ordinary radio
 * jitter on the second. As a share, a run of three lost datagrams is most of its sample and still
 * counts, while a handful lost out of two hundred does not.
 */
private const val MIN_SHARE_FOR_GAP = 0.25f

data class DevicePacketSample(
	val time: Long,
	val received: Int,
	val lost: Int,
)

data class DeviceRssiSample(
	val time: Long,
	val rssi: Int,
)

data class WindowedDeviceStats(
	val rssiAvg: Int?,
	val rssiMin: Int?,
	val rssiMax: Int?,
	val packetLoss: Float?,
	val packetsReceived: Int?,
	val packetsLost: Int?,
)

/**
 * Rolling packet telemetry for one device.
 *
 * This stays outside the device state, because it is too much data and changes to handle.
 * Since nothing really need to watch for all these changes it is easier to have it outside the device.
 */
class DevicePacketHistory {
	// Both series share one lock. Taking them separately would only add an ordering to get wrong
	private val lock = Any()

	private val samples = ArrayDeque<DevicePacketSample>()

	private val rssiSamples = ArrayDeque<DeviceRssiSample>()

	private val mutableLatest = MutableSharedFlow<DevicePacketSample>(
		replay = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)

	/** One emission per packet, for consumers measuring the gap between two of them */
	val latest: SharedFlow<DevicePacketSample> = mutableLatest

	fun record(sample: DevicePacketSample) {
		synchronized(lock) {
			samples.addLast(sample)
			// Age bounds the window, the count bounds a device reporting faster than expected
			val cutoff = sample.time - MAX_SAMPLE_RETENTION_MS
			while (samples.size > 1 && (samples.first().time < cutoff || samples.size > MAX_SAMPLES)) {
				samples.removeFirst()
			}
		}
		mutableLatest.tryEmit(sample)
	}

	fun recordRssi(sample: DeviceRssiSample) {
		synchronized(lock) {
			if (rssiSamples.lastOrNull()?.rssi == sample.rssi) return
			rssiSamples.addLast(sample)
			val cutoff = sample.time - MAX_SAMPLE_RETENTION_MS
			while (rssiSamples.size > 1 && (rssiSamples.first().time < cutoff || rssiSamples.size > MAX_SAMPLES)) {
				rssiSamples.removeFirst()
			}
		}
	}

	fun statsForWindow(windowMs: Long, now: Long): WindowedDeviceStats = synchronized(lock) {
		computeWindowedStats(samples, rssiSamples, windowMs, now)
	}
}

/**
 * Sums the packet increments falling in [windowMs] and reduces the signal strength readings over
 * the same span to an average and a range.
 */
fun computeWindowedStats(
	packetSamples: List<DevicePacketSample>,
	rssiSamples: List<DeviceRssiSample>,
	windowMs: Long,
	now: Long = System.currentTimeMillis(),
): WindowedDeviceStats {
	val cutoff = now - windowMs
	val lookbackCutoff = now - maxOf(windowMs, MAX_LOSS_LOOKBACK_MS)
	var received = 0L
	var lost = 0L

	for (index in packetSamples.indices.reversed()) {
		val sample = packetSamples[index]
		if (sample.time < lookbackCutoff) break
		if (sample.time < cutoff && received + lost >= MIN_PACKETS_FOR_LOSS) break
		received += sample.received
		lost += sample.lost
	}

	val total = received + lost
	val packetLoss = if (total >= MIN_PACKETS_FOR_LOSS) lost.toFloat() / total.toFloat() else null

	var rssiMin = Int.MAX_VALUE
	var rssiMax = Int.MIN_VALUE
	var weightedSum = 0.0
	var weightTotal = 0L
	val latestRssi = rssiSamples.lastOrNull()?.rssi

	var nextTime = now
	for (index in rssiSamples.indices.reversed()) {
		val sample = rssiSamples[index]
		val to = minOf(nextTime, now)
		nextTime = sample.time

		if (to <= cutoff) break

		val from = maxOf(sample.time, cutoff)
		if (to <= from) continue

		if (sample.rssi < rssiMin) rssiMin = sample.rssi
		if (sample.rssi > rssiMax) rssiMax = sample.rssi
		weightedSum += sample.rssi.toDouble() * (to - from)
		weightTotal += to - from
	}

	val measured = weightTotal > 0
	return WindowedDeviceStats(
		rssiAvg = if (measured) (weightedSum / weightTotal).roundToInt() else latestRssi,
		rssiMin = if (measured) rssiMin else latestRssi,
		rssiMax = if (measured) rssiMax else latestRssi,
		packetLoss = packetLoss,
		packetsReceived = if (packetLoss != null) received.toInt() else null,
		packetsLost = if (packetLoss != null) lost.toInt() else null,
	)
}

/**
 * Whether a sample lost enough, and a large enough share of itself, to read as an outage rather
 * than as the ordinary jitter of a radio link.
 */
fun isOutage(sample: DevicePacketSample): Boolean {
	if (sample.lost < MIN_PACKETS_FOR_GAP) return false
	val observed = sample.received + sample.lost
	return observed > 0 && sample.lost.toFloat() / observed.toFloat() >= MIN_SHARE_FOR_GAP
}
