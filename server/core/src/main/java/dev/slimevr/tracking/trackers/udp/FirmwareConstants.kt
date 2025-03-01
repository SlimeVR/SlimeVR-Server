package dev.slimevr.tracking.trackers.udp

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
	ICM42688(10u),
	BMI270(11u),
	LSM6DS3TRC(12u),
	LSM6DSV(13u),
	LSM6DSO(14u),
	LSM6DSR(15u),
	ICM45686(16u),
	ICM45605(17u),
	ADC_RESISTANCE(18u),
	DEV_RESERVED(250u),
	;

	fun getSolarType(): Int = this.id.toInt()

	companion object {
		private val byId = entries.associateBy { it.id }

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
	BEETLE32C3(11u),
	ESP32C3DEVKITM1(12u),
	OWOTRACK(13u),
	WRANGLER(14u),
	MOCOPI(15u),
	WEMOSWROOM02(16u),
	XIAO_ESP32C3(17u),
	HARITORA(18u),
	ESP32C6DEVKITC1(19u),
	GLOVE_IMU_SLIMEVR_DEV(20u),
	DEV_RESERVED(250u),
	;

	fun getSolarType(): Int = this.id.toInt()

	override fun toString(): String = when (this) {
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
		BEETLE32C3 -> "Beetle ESP32-C3"
		ESP32C3DEVKITM1 -> "Espressif ESP32-C3 DevKitM-1"
		OWOTRACK -> "owoTrack"
		WRANGLER -> "Wrangler Joycons"
		MOCOPI -> "Sony Mocopi"
		WEMOSWROOM02 -> "Wemos Wroom-02 D1 Mini"
		XIAO_ESP32C3 -> "Seeed Studio XIAO ESP32C3"
		HARITORA -> "Haritora"
		ESP32C6DEVKITC1 -> "Espressif ESP32-C6 DevKitC-1"
		GLOVE_IMU_SLIMEVR_DEV -> "SlimeVR Dev IMU Glove"
		DEV_RESERVED -> "Prototype"
	}

	companion object {
		private val byId = entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: UInt): BoardType? = byId[id]
	}
}

enum class MCUType(val id: UInt) {
	UNKNOWN(0u),
	ESP8266(1u),
	ESP32(2u),
	OWOTRACK_ANDROID(3u),
	WRANGLER(4u),
	OWOTRACK_IOS(5u),
	ESP32_C3(6u),
	MOCOPI(7u),
	HARITORA(8u),
	DEV_RESERVED(250u),
	;

	fun getSolarType(): Int = this.id.toInt()

	companion object {
		private val byId = entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: UInt): MCUType? = byId[id]
	}
}

enum class TrackerDataType(val id: UInt) {
	ROTATION(0u),
	FLEX_RESISTANCE(1u),
	FLEX_ANGLE(2u),
	;

	fun getSolarType(): Int = this.id.toInt()

	companion object {
		private val byId = entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: UInt): TrackerDataType? = byId[id]
	}
}

@JvmInline
value class ConfigTypeId(val v: UShort)

enum class MagnetometerStatus {
	NOT_SUPPORTED,
	DISABLED,
	ENABLED,
	;

	fun getSolarType(): Int = this.ordinal

	companion object {
		private val byId = entries.associateBy { it.ordinal.toUByte() }

		@JvmStatic
		fun getById(id: UByte): MagnetometerStatus? = byId[id]
	}
}

@JvmInline
value class SensorConfig(val v: UShort) {
	val magStatus
		get(): MagnetometerStatus {
			if ((v.toUInt() shr 1) and 1u == 0u) return MagnetometerStatus.NOT_SUPPORTED
			return if ((v and 1u) == 1u.toUShort()) {
				MagnetometerStatus.ENABLED
			} else {
				MagnetometerStatus.DISABLED
			}
		}
}
