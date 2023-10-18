package dev.slimevr.firmware

data class UpdateStatusEvent<T>(
	val deviceId: UpdateDeviceId<T>,
	val status: FirmwareUpdateStatus,
	val progress: Int = 0,
	val time: Long = System.currentTimeMillis(),
)
