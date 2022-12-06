package dev.slimevr.config;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import dev.slimevr.config.serializers.BooleanMapDeserializer;
import dev.slimevr.vr.trackers.TrackerRole;

import java.util.HashMap;
import java.util.Map;


public class OSCConfig {

	// Are the OSC receiver and sender enabled?
	private boolean enabled = false;

	// Port to receive OSC messages from
	private int portIn;

	// Port to send out OSC messages at
	private int portOut;

	// Address to send out OSC messages at
	private String address = "127.0.0.1";

	// Which trackers' data to send
	@JsonDeserialize(using = BooleanMapDeserializer.class)
	@JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer.class)
	public Map<String, Boolean> trackers = new HashMap<>();

	public OSCConfig() {
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}

	public int getPortIn() {
		return portIn;
	}

	public void setPortIn(int portIn) {
		this.portIn = portIn;
	}

	public int getPortOut() {
		return portOut;
	}

	public void setPortOut(int portOut) {
		this.portOut = portOut;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean getOSCTrackerRole(TrackerRole role, boolean def) {
		return trackers.getOrDefault(role.name().toLowerCase(), def);
	}

	public void setOSCTrackerRole(TrackerRole role, boolean val) {
		this.trackers.put(role.name().toLowerCase(), val);
	}
}
