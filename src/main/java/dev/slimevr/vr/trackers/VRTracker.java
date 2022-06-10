package dev.slimevr.vr.trackers;

import dev.slimevr.vr.IDevice;
import io.eiren.util.BufferedTimer;


public class VRTracker extends ComputedTracker
	implements TrackerWithTPS, TrackerWithDevice, TrackerWithConfig {

	protected BufferedTimer timer = new BufferedTimer(1f);

	private final IDevice device;

	public VRTracker(
		int trackerId,
		String name,
		boolean hasRotation,
		boolean hasPosition,
		IDevice device
	) {
		super(trackerId, name, hasRotation, hasPosition);
		this.device = device;
	}

	@Override
	public boolean userEditable() {
		return true;
	}

	@Override
	public boolean isComputed() {
		return true;
	}

	public IDevice getDevice() {
		return this.device;
	}

	public float getTPS() {
		return timer.getAverageFPS();
	}

	public void dataTick() {
		timer.update();
	}

	public void saveConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
	}

	public void loadConfig(TrackerConfig config) {
		// Loading a config is an act of user editing, therefore it shouldn't
		// not be
		// allowed if editing is not allowed
		if (userEditable()) {
			TrackerPosition
				.getByDesignation(config.designation)
				.ifPresent(trackerPosition -> bodyPosition = trackerPosition);
		}
	}
}
