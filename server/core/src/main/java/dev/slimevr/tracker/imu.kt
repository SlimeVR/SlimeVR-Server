package dev.slimevr.tracker

enum class IMUType(val id: UByte) {
	UNKNOWN(solarxr_protocol.datatypes.hardware_info.ImuType.Other.toUByte()),
	MPU9250(solarxr_protocol.datatypes.hardware_info.ImuType.MPU9250.toUByte()),
	MPU6500(solarxr_protocol.datatypes.hardware_info.ImuType.MPU6500.toUByte()),
	BNO080(solarxr_protocol.datatypes.hardware_info.ImuType.BNO080.toUByte()),
	BNO085(solarxr_protocol.datatypes.hardware_info.ImuType.BNO085.toUByte()),
	BNO055(solarxr_protocol.datatypes.hardware_info.ImuType.BNO055.toUByte()),
	MPU6050(solarxr_protocol.datatypes.hardware_info.ImuType.MPU6050.toUByte()),
	BNO086(solarxr_protocol.datatypes.hardware_info.ImuType.BNO086.toUByte()),
	BMI160(solarxr_protocol.datatypes.hardware_info.ImuType.BMI160.toUByte()),
	ICM20948(solarxr_protocol.datatypes.hardware_info.ImuType.ICM20948.toUByte()),
	ICM42688(solarxr_protocol.datatypes.hardware_info.ImuType.ICM42688.toUByte()),
	BMI270(solarxr_protocol.datatypes.hardware_info.ImuType.BMI270.toUByte()),
	LSM6DS3TRC(solarxr_protocol.datatypes.hardware_info.ImuType.LSM6DS3TRC.toUByte()),
	LSM6DSV(solarxr_protocol.datatypes.hardware_info.ImuType.LSM6DSV.toUByte()),
	LSM6DSO(solarxr_protocol.datatypes.hardware_info.ImuType.LSM6DSO.toUByte()),
	LSM6DSR(solarxr_protocol.datatypes.hardware_info.ImuType.LSM6DSR.toUByte()),
	ICM45686(solarxr_protocol.datatypes.hardware_info.ImuType.ICM45686.toUByte()),
	ICM45605(solarxr_protocol.datatypes.hardware_info.ImuType.ICM45605.toUByte()),
	ADC_RESISTANCE(solarxr_protocol.datatypes.hardware_info.ImuType.ADCRESISTANCE.toUByte()),
	DEV_RESERVED(solarxr_protocol.datatypes.hardware_info.ImuType.DEVRESERVED.toUByte()),
	;

	companion object {
		private val map = entries.associateBy { it.id }
		fun fromId(id: UByte) = map[id]
	}
}
