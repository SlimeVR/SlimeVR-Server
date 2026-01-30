package dev.slimevr.firmware

enum class FirmwareUpdateMethod(val id: UByte) {
	NONE(solarxr_protocol.rpc.FirmwareUpdateMethod.NONE),
	OTA(solarxr_protocol.rpc.FirmwareUpdateMethod.OTAFirmwareUpdate),
	SERIAL(solarxr_protocol.rpc.FirmwareUpdateMethod.SerialFirmwareUpdate),
	;

	companion object {
		fun getById(id: UByte): FirmwareUpdateMethod? = byId[id]
	}
}

private val byId = FirmwareUpdateMethod.entries.associateBy { it.id }
