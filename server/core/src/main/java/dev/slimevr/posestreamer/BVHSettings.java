package dev.slimevr.posestreamer;

public class BVHSettings {
	private float offsetScale = 100f;
	private float positionScale = 100f;
	private boolean writeEndNodes = false;

	public static final BVHSettings DEFAULT = new BVHSettings();
	public static final BVHSettings BLENDER = new BVHSettings(DEFAULT)
		.setOffsetScale(1f)
		.setPositionScale(1f);

	public BVHSettings() {
	}

	public BVHSettings(BVHSettings source) {
		this.offsetScale = source.offsetScale;
		this.positionScale = source.positionScale;
		this.writeEndNodes = source.writeEndNodes;
	}

	public float getOffsetScale() {
		return offsetScale;
	}

	public BVHSettings setOffsetScale(float offsetScale) {
		this.offsetScale = offsetScale;
		return this;
	}

	public float getPositionScale() {
		return positionScale;
	}

	public BVHSettings setPositionScale(float positionScale) {
		this.positionScale = positionScale;
		return this;
	}

	public boolean shouldWriteEndNodes() {
		return writeEndNodes;
	}

	public BVHSettings setWriteEndNodes(boolean writeEndNodes) {
		this.writeEndNodes = writeEndNodes;
		return this;
	}
}
