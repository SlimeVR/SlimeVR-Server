package io.eiren.gui;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class AssetsHandler {

	public static void downloadAssets(){
		File logoFile = new File("./assets/logo.png");
		File assetsDir = new File("./assets");
		if(!assetsDir.isFile()){
			assetsDir.mkdir();
		}
		if(!logoFile.isFile()) {
			try{
				BufferedImage image = ImageIO.read(new URL(
						"https://www.dropbox.com/s/tidq5gdng1egoew/logo.png?dl=1"));
				ImageIO.write(image, "png", logoFile);
			}catch(IIOException | MalformedURLException e){
				System.out.println("[AssetHandler] Could not download asset(s), bad internet connection? \n" +
						"[AssetHandler] Continuing without assets (the logo)...");
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
