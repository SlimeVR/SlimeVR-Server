package dev.slimevr.firmware

enum class FirmwareUpdateMethod(val id: Int) {
	NONE(0), OTA(1), SERIAL(2);

	companion object {
		fun getById(id: Int): FirmwareUpdateMethod? = byId[id]
	}
}

private val byId = FirmwareUpdateMethod.entries.associateBy { it.id }
