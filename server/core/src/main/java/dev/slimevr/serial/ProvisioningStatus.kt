package dev.slimevr.serial

import solarxr_protocol.rpc.WifiProvisioningStatus

enum class ProvisioningStatus(@JvmField val id: Int, val isError: Boolean, val timeout: Int = 10_000) {
	NONE(WifiProvisioningStatus.NONE, false, 3_000),
	SERIAL_INIT(WifiProvisioningStatus.SERIAL_INIT, false, 3_000),
	PROVISIONING(WifiProvisioningStatus.PROVISIONING, false),
	OBTAINING_MAC_ADDRESS(WifiProvisioningStatus.OBTAINING_MAC_ADDRESS, false),
	CONNECTING(WifiProvisioningStatus.CONNECTING, false, 30_000),
	CONNECTION_ERROR(WifiProvisioningStatus.CONNECTION_ERROR, true),
	LOOKING_FOR_SERVER(WifiProvisioningStatus.LOOKING_FOR_SERVER, false),
	COULD_NOT_FIND_SERVER(WifiProvisioningStatus.COULD_NOT_FIND_SERVER, true),
	NO_SERIAL_LOGS_ERROR(WifiProvisioningStatus.NO_SERIAL_LOGS_ERROR, true),
	NO_SERIAL_DEVICE_FOUND(WifiProvisioningStatus.NO_SERIAL_DEVICE_FOUND, true),
	DONE(WifiProvisioningStatus.DONE, false),
}
