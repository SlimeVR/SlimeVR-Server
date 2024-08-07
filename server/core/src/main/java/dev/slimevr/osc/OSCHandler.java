package dev.slimevr.osc;

import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;

import java.net.InetAddress;


public interface OSCHandler {

	public void refreshSettings(boolean refreshRouterSettings);

	public void updateOscReceiver(int portIn, String[] args);

	public void updateOscSender(int portOut, String address);

	public void update();

	public OSCPortOut getOscSender();

	public int getPortOut();

	public InetAddress getAddress();

	public OSCPortIn getOscReceiver();

	public int getPortIn();

}
