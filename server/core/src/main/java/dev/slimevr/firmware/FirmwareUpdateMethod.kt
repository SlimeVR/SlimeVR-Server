package dev.slimevr.firmware

import solarxr_protocol.rpc.FlashingMethod

enum class FirmwareUpdateMethod(val id: Int) {
	NONE(FlashingMethod.NONE), OTA(FlashingMethod.OTA), SERIAL(FlashingMethod.SERIAL);

	companion object {
		fun getById(id: Int): FirmwareUpdateMethod? = byId[id]
	}
}

private val byId = FirmwareUpdateMethod.entries.associateBy { it.id }
