package io.eiren.vr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import io.eiren.gui.AssetsHandler;
import io.eiren.gui.VRServerGUI;
import io.eiren.util.logging.LogManager;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class Main {

	public static String VERSION = "0.0.17";

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
			AssetsHandler.downloadAssets();
			new VRServerGUI(vrServer);
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
