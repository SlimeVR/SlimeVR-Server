package dev.slimevr.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jme3.math.Quaternion;
import dev.slimevr.tracking.trackers.Tracker;


public class TrackerConfig {

	private String customName;
	private String designation;
	private boolean hide;

	private Quaternion adjustment;

	private Quaternion mountingRotation;

	public TrackerConfig() {
	}

	public TrackerConfig(Tracker tracker) {
		this.designation = tracker.getBodyPosition()
			!= null ? tracker.getBodyPosition().designation : null;
		this.customName = tracker.getCustomName();
	}

	static JsonNode toV2(JsonNode v1, JsonNodeFactory factory) {
		ObjectNode node = factory.objectNode();
		if (v1.has("customName"))
			node.set("customName", v1.get("customName"));
		if (v1.has("designation"))
			node.set("designation", v1.get("designation"));
		if (v1.has("hide"))
			node.set("hide", v1.get("hide"));
		if (v1.has("mountingRotation"))
			node.set("mountingRotation", v1.get("mountingRotation"));
		if (v1.has("adjustment"))
			node.set("adjustment", v1.get("adjustment"));
		return node;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public Quaternion getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(Quaternion adjustment) {
		this.adjustment = adjustment;
	}

	public Quaternion getMountingRotation() {
		return mountingRotation;
	}

	public void setMountingRotation(Quaternion mountingRotation) {
		this.mountingRotation = mountingRotation;
	}
}
