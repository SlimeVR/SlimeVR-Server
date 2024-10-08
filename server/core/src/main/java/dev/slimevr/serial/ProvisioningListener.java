package dev.slimevr.serial;

public interface ProvisioningListener {

	void onProvisioningStatusChange(ProvisioningStatus status, SerialPort port);
}
