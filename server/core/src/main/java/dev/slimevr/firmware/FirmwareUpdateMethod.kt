package dev.slimevr.firmware

enum class FirmwareUpdateMethod(val id: Byte) {
	NONE(solarxr_protocol.rpc.FirmwareUpdateMethod.NONE),
	OTA(solarxr_protocol.rpc.FirmwareUpdateMethod.OTAFirmwareUpdate),
	SERIAL(solarxr_protocol.rpc.FirmwareUpdateMethod.SerialFirmwareUpdate),
	;

	companion object {
		fun getById(id: Byte): FirmwareUpdateMethod? = byId[id]
	}
}

private val byId = FirmwareUpdateMethod.entries.associateBy { it.id }
