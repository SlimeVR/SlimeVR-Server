package dev.slimevr.config;


public class WindowConfig {

	public static final float INITAL_ZOOM = 1.5f;

	private float zoom = INITAL_ZOOM;

	private int width = 800;

	private int height = 800;

	private int posx = -1;
	private int posy = -1;

	public WindowConfig() {
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getPosx() {
		return posx;
	}

	public void setPosx(int posx) {
		this.posx = posx;
	}

	public int getPosy() {
		return posy;
	}

	public void setPosy(int posy) {
		this.posy = posy;
	}


}
