package dev.slimevr.tracking.trackers.udp

import solarxr_protocol.datatypes.hardware_info.ImuType

enum class IMUType(val id: UInt) {
	UNKNOWN(0u),
	MPU9250(1u),
	MPU6500(2u),
	BNO080(3u),
	BNO085(4u),
	BNO055(5u),
	MPU6050(6u),
	BNO086(7u),
	BMI160(8u),
	ICM20948(9u),
	;

	fun getSolarType(): Int = this.id.toInt()

	companion object {
		private val byId = IMUType.values().associateBy { it.id }

		@JvmStatic
		fun getById(id: UInt): IMUType? = byId[id]
	}
}

enum class BoardType(val id: UInt) {
	UNKNOWN(0u),
	SLIMEVR_LEGACY(1u),
	SLIMEVR_DEV(2u),
	NODEMCU(3u),
	CUSTOM(4u),
	WROOM32(5u),
	WEMOSD1MINI(6u),
	TTGO_TBASE(7u),
	ESP01(8u),
	SLIMEVR(9u),
	LOLIN_C3_MINI(10u),
	BEETLE32C32(11u),
	ES32C3DEVKITM1(12u),
	;

	override fun toString(): String {
		return when (this) {
			UNKNOWN -> "Unknown"
			SLIMEVR_LEGACY -> "SlimeVR Legacy"
			SLIMEVR_DEV -> "SlimeVR Dev"
			NODEMCU -> "NodeMCU"
			CUSTOM -> "Custom Board"
			WROOM32 -> "WROOM32"
			WEMOSD1MINI -> "Wemos D1 Mini"
			TTGO_TBASE -> "TTGO T-Base"
			ESP01 -> "ESP-01"
			SLIMEVR -> "SlimeVR"
			LOLIN_C3_MINI -> "Lolin C3 Mini"
			BEETLE32C32 -> "Beetle ESP32-C3"
			ES32C3DEVKITM1 -> "Espressif ESP32-C3 DevKitM-1"
		}
	}

	companion object {
		private val byId = BoardType.values().associateBy { it.id }

		@JvmStatic
		fun getById(id: UInt): BoardType? = byId[id]
	}
}

enum class MCUType(val id: UInt) {
	UNKNOWN(0u),
	ESP8266(1u),
	ESP32(2u),
	;

	fun getSolarType(): Int = this.id.toInt()

	companion object {
		private val byId = MCUType.values().associateBy { it.id }

		@JvmStatic
		fun getById(id: UInt): MCUType? = byId[id]
	}
}
