package dev.slimevr.config;

import dev.slimevr.Main;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;


public class DriftConfig {

	// Is drift compensation enabled
	private boolean enabled = true; // TODO set to false

	// Amount of drift compensation applied
	private float amount = 0.7f;

	public DriftConfig() {
	}

	public void updateTrackersDrift() {
		for (Tracker t : Main.vrServer.getAllTrackers()) {
			Tracker tracker = t.get();
			if (tracker instanceof IMUTracker) {
				((IMUTracker) tracker)
					.setDriftSettings(
						getEnabled(),
						getAmount()
					);
			}
		}
	}


	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}
}
