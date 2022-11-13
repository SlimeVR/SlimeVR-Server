package dev.slimevr.serial;

import com.fazecast.jSerialComm.SerialPort;


public interface SerialListener {

	void onSerialConnected(SerialPort port);

	void onSerialDisconnected();

	void onSerialLog(String str);
}
