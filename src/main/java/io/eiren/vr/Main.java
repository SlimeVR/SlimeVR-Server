package io.eiren.vr;

import java.io.File;

import io.eiren.gui.VRServerGUI;
import io.eiren.util.logging.LogManager;

public class Main {
	
	public static String VERSION = "0.0.19 Test 7";
	
	public static VRServer vrServer;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
		
		File dir = new File("").getAbsoluteFile();
		try {
			LogManager.initialize(new File(dir, "logs/"), dir);
		} catch(Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			vrServer = new VRServer();
			vrServer.start();
			new Keybinding(vrServer);
			new VRServerGUI(vrServer);
		} catch(Throwable e) {
			e.printStackTrace();
			try {
				Thread.sleep(2000L);
			} catch(InterruptedException e2) {
				e.printStackTrace();
			}
			System.exit(1); // Exit in case error happened on init and window not appeared, but some thread started
		} finally {
			try {
				Thread.sleep(2000L);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
