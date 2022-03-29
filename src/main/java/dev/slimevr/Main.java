package dev.slimevr;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

import dev.slimevr.gui.MainStage;
import dev.slimevr.gui.Keybinding;
import io.eiren.util.logging.LogManager;
import javafx.application.Application;

public class Main {
	
	public static String VERSION = "0.1.6";
	
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
			new ServerSocket(6969).close();
			new ServerSocket(35903).close();
			new ServerSocket(21110).close();
		} catch (IOException e) {
			LogManager.log.severe("SlimeVR start-up error! Required ports are busy. Make sure there is no other instance of SlimeVR Server running.");
			JOptionPane.showMessageDialog(null, "SlimeVR start-up error! Required ports are busy. Make sure there is no other instance of SlimeVR Server running.", "SlimeVR: Ports are busy", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			vrServer = new VRServer();
			vrServer.start(); 
			new Keybinding(vrServer);

			MainStage.launch(MainStage.class);

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
