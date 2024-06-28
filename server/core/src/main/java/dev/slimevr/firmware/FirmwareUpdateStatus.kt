package dev.slimevr.firmware

enum class FirmwareUpdateStatus(val id: Int) {
	DOWNLOADING(solarxr_protocol.rpc.FirmwareUpdateStatus.DOWNLOADING),
	AUTHENTICATING(solarxr_protocol.rpc.FirmwareUpdateStatus.AUTHENTICATING),
	UPLOADING(solarxr_protocol.rpc.FirmwareUpdateStatus.UPLOADING),
	SYNCING_WITH_MCU(solarxr_protocol.rpc.FirmwareUpdateStatus.SYNCING_WITH_MCU),
	REBOOTING(solarxr_protocol.rpc.FirmwareUpdateStatus.REBOOTING),
	NEED_MANUAL_REBOOT(solarxr_protocol.rpc.FirmwareUpdateStatus.NEED_MANUAL_REBOOT),
	PROVISIONING(solarxr_protocol.rpc.FirmwareUpdateStatus.PROVISIONING),
	DONE(solarxr_protocol.rpc.FirmwareUpdateStatus.DONE),
	ERROR_DEVICE_NOT_FOUND(solarxr_protocol.rpc.FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND),
	ERROR_TIMEOUT(solarxr_protocol.rpc.FirmwareUpdateStatus.ERROR_TIMEOUT),
	ERROR_DOWNLOAD_FAILED(solarxr_protocol.rpc.FirmwareUpdateStatus.ERROR_DOWNLOAD_FAILED),
	ERROR_AUTHENTICATION_FAILED(solarxr_protocol.rpc.FirmwareUpdateStatus.ERROR_AUTHENTICATION_FAILED),
	ERROR_UPLOAD_FAILED(solarxr_protocol.rpc.FirmwareUpdateStatus.ERROR_UPLOAD_FAILED),
	ERROR_PROVISIONING_FAILED(solarxr_protocol.rpc.FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED),
	ERROR_UNSUPPORTED_METHOD(solarxr_protocol.rpc.FirmwareUpdateStatus.ERROR_UNSUPPORTED_METHOD),
	ERROR_UNKNOWN(solarxr_protocol.rpc.FirmwareUpdateStatus.ERROR_UNKNOWN),
	;

	fun isError(): Boolean = id in ERROR_DEVICE_NOT_FOUND.id..ERROR_UNKNOWN.id

	companion object {
		fun getById(id: Int): FirmwareUpdateStatus? = byId[id]
	}
}

private val byId = FirmwareUpdateStatus.entries.associateBy { it.id }
