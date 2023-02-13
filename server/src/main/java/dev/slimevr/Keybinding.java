package dev.slimevr;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import dev.slimevr.config.KeybindingsConfig;
import io.eiren.util.OperatingSystem;
import io.eiren.util.ann.AWTThread;
import io.eiren.util.logging.LogManager;


public class Keybinding implements HotkeyListener {
	private static final int RESET = 1;
	private static final int QUICK_RESET = 2;
	private static final int RESET_MOUNTING = 3;
	public final VRServer server;
	public final KeybindingsConfig config;

	@AWTThread
	public Keybinding(VRServer server) {
		this.server = server;

		this.config = server.getConfigManager().getVrConfig().getKeybindings();

		if (OperatingSystem.getCurrentPlatform() != OperatingSystem.WINDOWS) {
			LogManager
				.info(
					"[Keybinding] Currently only supported on Windows. Keybindings will be disabled."
				);
			return;
		}

		try {
			if (JIntellitype.getInstance() instanceof JIntellitype) {
				JIntellitype.getInstance().addHotKeyListener(this);

				String resetBinding = this.config.getResetBinding();
				JIntellitype.getInstance().registerHotKey(RESET, resetBinding);
				LogManager.info("[Keybinding] Bound reset to " + resetBinding);

				String quickResetBinding = this.config.getQuickResetBinding();
				JIntellitype.getInstance().registerHotKey(QUICK_RESET, quickResetBinding);
				LogManager.info("[Keybinding] Bound quick reset to " + quickResetBinding);

				String resetMountingBinding = this.config.getResetMountingBinding();
				JIntellitype.getInstance().registerHotKey(RESET_MOUNTING, resetMountingBinding);
				LogManager.info("[Keybinding] Bound reset mounting to " + resetMountingBinding);
			}
		} catch (Throwable e) {
			LogManager
				.warning(
					"[Keybinding] JIntellitype initialization failed. Keybindings will be disabled. Try restarting your computer."
				);
		}
	}

	@AWTThread
	@Override
	public void onHotKey(int identifier) {
		switch (identifier) {
			case RESET -> {
				LogManager.info("[Keybinding] Reset pressed");
				server.scheduleResetTrackers(this.config.getResetDelay());
			}
			case QUICK_RESET -> {
				LogManager.info("[Keybinding] Quick reset pressed");
				server.scheduleResetTrackersYaw(this.config.getQuickResetDelay());
			}
			case RESET_MOUNTING -> {
				LogManager.info("[Keybinding] Reset mounting pressed");
				server.scheduleResetTrackersMounting(this.config.getResetMountingDelay());
			}
		}
	}
}
