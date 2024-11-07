package dev.slimevr.serial;

import solarxr_protocol.rpc.WifiProvisioningStatus;


public enum ProvisioningStatus {

	NONE(WifiProvisioningStatus.NONE),
	SERIAL_INIT(WifiProvisioningStatus.SERIAL_INIT),
	PROVISIONING(WifiProvisioningStatus.PROVISIONING),
	OBTAINING_MAC_ADDRESS(WifiProvisioningStatus.OBTAINING_MAC_ADDRESS),
	CONNECTING(WifiProvisioningStatus.CONNECTING),
	CONNECTION_ERROR(WifiProvisioningStatus.CONNECTION_ERROR),
	LOOKING_FOR_SERVER(WifiProvisioningStatus.LOOKING_FOR_SERVER),
	COULD_NOT_FIND_SERVER(WifiProvisioningStatus.COULD_NOT_FIND_SERVER),
	DONE(WifiProvisioningStatus.DONE);

	public final int id;

	ProvisioningStatus(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
