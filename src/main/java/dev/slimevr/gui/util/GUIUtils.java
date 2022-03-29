package dev.slimevr.gui.util;

import io.eiren.util.logging.LogManager;
import javafx.scene.image.Image;

public class GUIUtils {
	private GUIUtils() {
		throw new IllegalStateException("Utility class");
	}

	//#region Images
	public static final Image MISSING_IMAGE = getImage("/missing.png", null, false);

	public static Image getImage(String imageUrl) {
		return getImage(imageUrl, true);
	}

	public static Image getImage(String imageUrl, boolean backgroundLoading) {
		return getImage(imageUrl, MISSING_IMAGE, backgroundLoading);
	}

	public static Image getImage(String imageUrl, Image defaultImage) {
		return getImage(imageUrl, defaultImage, true);
	}

	public static Image getImage(String imageUrl, Image defaultImage, boolean backgroundLoading) {
		try {
			return new Image(imageUrl, backgroundLoading);
		} catch (Exception e) {
			LogManager.log.severe("Unable to load image \"" + (imageUrl != null ? imageUrl : "null") + "\"", e);
		}

		return defaultImage;
	}
	//#endregion
}
