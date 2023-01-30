package dev.slimevr.serial;

public enum ProvisioningStatus {

	NONE(0),
	SERIAL_INIT(1),
	PROVISIONING(2),
	CONNECTING(3),
	CONNECTION_ERROR(4),
	LOOKING_FOR_SERVER(5),
	COULD_NOT_FIND_SERVER(6),
	DONE(7);

	public int id;

	ProvisioningStatus(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
