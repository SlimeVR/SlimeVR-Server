package dev.slimevr.firmware

interface FirmwareUpdateListener {
	fun onUpdateStatusChange(event: UpdateStatusEvent<*>)
}
