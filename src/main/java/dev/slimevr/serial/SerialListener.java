package dev.slimevr.serial;

import com.fazecast.jSerialComm.SerialPort;

public interface SerialListener {

	public void onSerialConnected(SerialPort port);

	public void onSerialDisconnected();

	public void onSerialLog(String str);

}
