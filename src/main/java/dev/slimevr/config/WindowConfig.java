package dev.slimevr.config;

import io.eiren.yaml.YamlNode;

import java.util.HashMap;


public class WindowConfig {

	public static String CONFIG_ROOT = "window";

	private final ConfigManager configManager;

	private YamlNode rootNode;

	private float zoom = 1.5f;

	private float initialZoom = zoom;
	private int width = 800;

	private int height = 800;

	private int posx = -1;
	private int posy = -1;


	public WindowConfig(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void loadConfig() {
		this.rootNode = this.configManager.getConfig().getNode(CONFIG_ROOT);
		if (rootNode == null)
			this.rootNode = new YamlNode(new HashMap<>());
		configManager.getConfig().setProperty(CONFIG_ROOT, this.rootNode);

		// Backward compatibility with the old zoom setting
		float oldZoom = configManager.getConfig().getFloat("zoom", -1);
		if (oldZoom != -1) {
			configManager.getConfig().removeProperty("zoom");
			this.zoom = oldZoom;
		}

		this.zoom = rootNode.getFloat("zoom", this.zoom);
		this.width = rootNode.getInt("width", this.width);
		this.height = rootNode.getInt("height", this.height);
		this.posx = rootNode.getInt("posx", this.posx);
		this.posy = rootNode.getInt("posy", this.posy);
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
		this.rootNode.setProperty("zoom", this.zoom);
	}


	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.rootNode.setProperty("width", this.width);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		this.rootNode.setProperty("height", this.height);
	}

	public int getPosx() {
		return posx;
	}

	public void setPosx(int posx) {
		this.posx = posx;
		this.rootNode.setProperty("posx", this.posx);
	}

	public int getPosy() {
		return posy;
	}

	public void setPosy(int posy) {
		this.posy = posy;
		this.rootNode.setProperty("posy", this.posy);
	}

	public float getInitialZoom() {
		return initialZoom;
	}
}
