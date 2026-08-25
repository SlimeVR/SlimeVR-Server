package dev.slimevr.device

import kotlin.math.roundToInt

data class DevicePacketSample(
	val time: Long,
	val rssi: Int?,
	val packetsReceived: Long?,
	val packetsLost: Long?,
)

data class WindowedDeviceStats(
	val rssiAvg: Int?,
	val rssiMin: Int?,
	val rssiMax: Int?,
	val packetLoss: Float?,
)

private const val MAX_SAMPLE_RETENTION_MS = 5_000L
private const val MIN_SAMPLES_FOR_LOSS = 20

fun trimSamples(samples: List<DevicePacketSample>, now: Long = System.currentTimeMillis()): List<DevicePacketSample> {
	val cutoff = now - MAX_SAMPLE_RETENTION_MS
	val idx = samples.indexOfFirst { it.time >= cutoff }
	return if (idx <= 0) samples else samples.subList(idx, samples.size)
}

fun computeWindowedStats(
	samples: List<DevicePacketSample>,
	windowMs: Long = 1000L,
	now: Long = System.currentTimeMillis(),
): WindowedDeviceStats {
	if (samples.isEmpty()) {
		return WindowedDeviceStats(null, null, null, null)
	}

	val cutoff = now - windowMs
	var windowSamples = samples.filter { it.time >= cutoff }

	if (windowSamples.isEmpty()) {
		windowSamples = listOf(samples.last())
	}

	val rssiValues = windowSamples.mapNotNull { it.rssi }
	val rssiAvg: Int?
	val rssiMin: Int?
	val rssiMax: Int?

	if (rssiValues.isNotEmpty()) {
		rssiAvg = rssiValues.average().roundToInt()
		rssiMin = rssiValues.minOrNull()
		rssiMax = rssiValues.maxOrNull()
	} else {
		val latestRssi = samples.lastOrNull { it.rssi != null }?.rssi
		rssiAvg = latestRssi
		rssiMin = latestRssi
		rssiMax = latestRssi
	}

	var lossSamples = samples.filter { it.packetsReceived != null && it.packetsLost != null }
	val recentLossSamples = lossSamples.filter { it.time >= cutoff }
	if (recentLossSamples.size >= MIN_SAMPLES_FOR_LOSS) {
		lossSamples = recentLossSamples
	} else if (lossSamples.size > MIN_SAMPLES_FOR_LOSS) {
		lossSamples = lossSamples.takeLast(MIN_SAMPLES_FOR_LOSS)
	}

	val packetLoss: Float? = if (lossSamples.size >= 2) {
		val first = lossSamples.first()
		val last = lossSamples.last()
		val dRec = last.packetsReceived!! - first.packetsReceived!!
		val dLost = last.packetsLost!! - first.packetsLost!!
		val total = dRec + dLost
		if (total > 0) {
			(dLost.toFloat() / total.toFloat()).coerceIn(0f, 1f)
		} else {
			0f
		}
	} else {
		null
	}

	return WindowedDeviceStats(
		rssiAvg = rssiAvg,
		rssiMin = rssiMin,
		rssiMax = rssiMax,
		packetLoss = packetLoss,
	)
}
