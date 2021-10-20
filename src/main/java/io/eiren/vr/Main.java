package io.eiren.vr;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

import dev.slimevr.gui.VRServerGUI;
import io.eiren.util.logging.LogManager;

public class Main {
	
	public static String VERSION = "0.1.0 Test 1";
	
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

		if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
			LogManager.log.severe("SlimeVR start-up error! A minimum of Java 11 is required.");
			JOptionPane.showMessageDialog(null, "SlimeVR start-up error! A minimum of Java 11 is required.", "SlimeVR: Java Runtime Mismatch", JOptionPane.ERROR_MESSAGE);
			return;
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
