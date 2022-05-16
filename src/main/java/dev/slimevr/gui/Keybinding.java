package dev.slimevr.gui;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import dev.slimevr.VRServer;
import io.eiren.util.OperatingSystem;
import io.eiren.util.ann.AWTThread;
import io.eiren.util.logging.LogManager;


public class Keybinding implements HotkeyListener {
	private static final int RESET = 1;
	private static final int QUICK_RESET = 2;
	public final VRServer server;

	@AWTThread
	public Keybinding(VRServer server) {
		this.server = server;

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

				String resetBinding = this.server.config.getString("keybindings.reset");
				if (resetBinding == null) {
					resetBinding = "CTRL+ALT+SHIFT+Y";
					this.server.config.setProperty("keybindings.reset", resetBinding);
				}
				JIntellitype.getInstance().registerHotKey(RESET, resetBinding);
				LogManager.info("[Keybinding] Bound reset to " + resetBinding);

				String quickResetBinding = this.server.config.getString("keybindings.quickReset");
				if (quickResetBinding == null) {
					quickResetBinding = "CTRL+ALT+SHIFT+U";
					this.server.config.setProperty("keybindings.quickReset", quickResetBinding);
				}
				JIntellitype.getInstance().registerHotKey(QUICK_RESET, quickResetBinding);
				LogManager.info("[Keybinding] Bound quick reset to " + quickResetBinding);
			}
		} catch (Throwable e) {
			LogManager
				.info(
					"[Keybinding] JIntellitype initialization failed. Keybindings will be disabled. Try restarting your computer."
				);
		}
	}

	@AWTThread
	@Override
	public void onHotKey(int identifier) {
		switch (identifier) {
			case RESET:
				LogManager.info("[Keybinding] Reset pressed");
				server.resetTrackers();
				break;
			case QUICK_RESET:
				LogManager.info("[Keybinding] Quick reset pressed");
				server.resetTrackersYaw();
				break;
		}
	}
}
