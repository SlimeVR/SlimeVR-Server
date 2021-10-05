package io.eiren.vr;

import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.HotkeyListener;
import io.eiren.util.ann.AWTThread;
import io.eiren.util.logging.LogManager;

public class Keybinding implements HotkeyListener {
	public final VRServer server;
	private static final int RESET = 1;
	private static final int QUICK_RESET = 2;

	@AWTThread
	public Keybinding(VRServer server) {
		this.server = server;

		if(JIntellitype.isJIntellitypeSupported()) {
			JIntellitype.getInstance().addHotKeyListener(this);

			String resetBinding = this.server.config.getString("keybindings.reset");
			if(resetBinding == null) {
				resetBinding = "CTRL+ALT+SHIFT+Y";
				this.server.config.setProperty("keybindings.reset", resetBinding);
			}
			JIntellitype.getInstance().registerHotKey(RESET, resetBinding);
			LogManager.log.info("[Keybinding] Bound reset to " + resetBinding);

			String quickResetBinding = this.server.config.getString("keybindings.quickReset");
			if(quickResetBinding == null) {
				quickResetBinding = "CTRL+ALT+SHIFT+U";
				this.server.config.setProperty("keybindings.quickReset", quickResetBinding);
			}
			JIntellitype.getInstance().registerHotKey(QUICK_RESET, quickResetBinding);
			LogManager.log.info("[Keybinding] Bound quick reset to " + quickResetBinding);
		}
	}

	@AWTThread
	@Override
	public void onHotKey(int identifier) {
		switch(identifier) {
		case RESET:
			LogManager.log.info("[Keybinding] Reset pressed");
			server.resetTrackers();
			break;
		case QUICK_RESET:
			LogManager.log.info("[Keybinding] Quick reset pressed");
			server.resetTrackersYaw();
			break;
		}
	}
}
