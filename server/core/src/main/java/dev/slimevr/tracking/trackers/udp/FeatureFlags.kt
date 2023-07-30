package dev.slimevr.tracking.trackers.udp

import java.nio.ByteBuffer

/**
 * Bit packed flags, enum values start with 0 and indicate which bit it is.
 *
 * Change the enums and `flagsEnabled` inside to extend.
 */
class FirmwareFeatures {
	enum class FirmwareFeatureFlags {
		// EXAMPLE_FEATURE,

		// Add new flags here

		BITS_TOTAL, ;
	}

	fun has(flag: FirmwareFeatureFlags): Boolean {
		val bit = flag.ordinal
		return (flags[bit / 8].toInt() and (1 shl (bit % 8))) != 0
	}

	/**
	 * Whether the firmware supports the "feature flags" feature,
	 * set to true when we've received flags packet from the firmware.
	 */
	fun isAvailable(): Boolean = available

	companion object {
		@JvmStatic
		fun from(received: ByteBuffer, length: Int): FirmwareFeatures {
			val res = FirmwareFeatures()
			res.available = true
			received.get(res.flags, 0, Math.min(res.flags.size, length))
			return res
		}
	}

	private var available = false
	private val flags = ByteArray(FirmwareFeatureFlags.BITS_TOTAL.ordinal / 8 + 1)
}

enum class ServerFeatureFlags {
	/** Server can parse bundle packets: `PACKET_BUNDLE` = 100 (0x64). */
	PROTOCOL_BUNDLE_SUPPORT,

	// Add new flags here

	BITS_TOTAL, ;

	companion object {
		private var flagsEnabled: List<ServerFeatureFlags> = listOf(
			PROTOCOL_BUNDLE_SUPPORT

			// Add enabled flags here
		)

		private val flagsLength = BITS_TOTAL.ordinal / 8 + 1
		private val flags = run {
			val tempPacked = ByteArray(flagsLength)

			for (flag in flagsEnabled) {
				val bit = flag.ordinal
				tempPacked[bit / 8] =
					(tempPacked[bit / 8].toInt() or (1 shl (bit % 8))).toByte()
			}

			tempPacked
		}

		fun getPacked() = flags
	}
}
