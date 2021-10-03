package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;

import io.eiren.yaml.YamlNode;

public class TrackerConfig {
	
	public final String trackerName;
	public String designation;
	public String description;
	public boolean hide;
	public Quaternion adjustment;
	public String mountingRotation;
	
	public TrackerConfig(Tracker tracker) {
		this.trackerName = tracker.getName();
		this.description = tracker.getDescriptiveName();
		this.designation = tracker.getBodyPosition() != null ? tracker.getBodyPosition().designation : null;
	}
	
	public TrackerConfig(YamlNode node) {
		this.trackerName = node.getString("name");
		this.description = node.getString("description");
		this.designation = node.getString("designation");
		this.hide = node.getBoolean("hide", false);
		this.mountingRotation = node.getString("rotation");
		YamlNode adjNode = node.getNode("adjustment");
		if(adjNode != null) {
			adjustment = new Quaternion(adjNode.getFloat("x", 0), adjNode.getFloat("y", 0), adjNode.getFloat("z", 0), adjNode.getFloat("w", 0));
		}
	}
	
	public void setDesignation(String newDesignation) {
		this.designation = newDesignation;
	}
	
	public void saveConfig(YamlNode configNode) {
		configNode.setProperty("name", trackerName);
		if(designation != null)
			configNode.setProperty("designation", designation);
		else
			configNode.removeProperty("designation");
		if(hide)
			configNode.setProperty("hide", hide);
		else
			configNode.removeProperty("hide");
		if(adjustment != null) {
			configNode.setProperty("adj.x", adjustment.getX());
			configNode.setProperty("adj.y", adjustment.getY());
			configNode.setProperty("adj.z", adjustment.getZ());
			configNode.setProperty("adj.w", adjustment.getW());
		} else {
			configNode.removeProperty("adj");
		}
		if(mountingRotation != null) {
			configNode.setProperty("rotation", mountingRotation);
		} else {
			configNode.removeProperty("rotation");
		}
		if(description != null) {
			configNode.setProperty("description", description);
		} else {
			configNode.removeProperty("description");
		}
	}
}
