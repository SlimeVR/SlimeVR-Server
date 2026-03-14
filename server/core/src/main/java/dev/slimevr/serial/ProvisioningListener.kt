package dev.slimevr.serial

interface ProvisioningListener {
	fun onProvisioningStatusChange(status: ProvisioningStatus, port: SerialPort?)
}
