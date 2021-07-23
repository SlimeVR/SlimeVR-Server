package io.eiren.vr;

import java.io.File;

import io.eiren.gui.jfx.SlimeVRGUIJFX;
import io.eiren.util.logging.LogManager;
import javafx.application.Application;

public class Main {
	
	public static String VERSION = "0.0.10";
	
	public static VRServer vrServer;
	
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
			Application.launch(SlimeVRGUIJFX.class, args);
		} catch(Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				Thread.sleep(2000L);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
