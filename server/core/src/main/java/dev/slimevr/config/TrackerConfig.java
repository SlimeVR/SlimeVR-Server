package dev.slimevr.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.slimevr.tracking.trackers.Tracker;
import io.github.axisangles.ktmath.Quaternion;


public class TrackerConfig {

	private String customName;
	private String designation;
	private boolean hide;
	private Quaternion adjustment;
	private Quaternion mountingOrientation;
	private Boolean allowDriftCompensation;


	public TrackerConfig() {
	}

	public TrackerConfig(Tracker tracker) {
		this.designation = tracker.getTrackerPosition()
			!= null ? tracker.getTrackerPosition().getDesignation() : null;
		this.customName = tracker.getCustomName();
		allowDriftCompensation = tracker.isImu();
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

	public Quaternion getMountingOrientation() {
		return mountingOrientation;
	}

	public void setMountingOrientation(Quaternion mountingOrientation) {
		this.mountingOrientation = mountingOrientation;
	}

	public Boolean getAllowDriftCompensation() {
		return allowDriftCompensation;
	}

	public void setAllowDriftCompensation(Boolean allowDriftCompensation) {
		this.allowDriftCompensation = allowDriftCompensation;
	}
}
