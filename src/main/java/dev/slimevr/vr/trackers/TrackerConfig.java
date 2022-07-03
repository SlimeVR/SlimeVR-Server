package dev.slimevr.vr.trackers;

import com.jme3.math.Quaternion;
import io.eiren.yaml.YamlNode;

import java.util.Objects;


public class TrackerConfig {

	public final String trackerName;
	public final String customName;
	public String designation;
	public boolean hide;
	public Quaternion adjustment;
	public String oldMountingRotation;
	public Quaternion mountingRotation;

	public TrackerConfig(Tracker tracker) {
		this.trackerName = tracker.getName();
		this.designation = tracker.getBodyPosition()
			!= null ? tracker.getBodyPosition().designation : null;
		this.customName = tracker.getCustomName();
	}

	public TrackerConfig(YamlNode node) {
		this.trackerName = node.getString("name");
		this.customName = node.getString("customName");
		this.designation = node.getString("designation");
		this.hide = node.getBoolean("hide", false);
		this.oldMountingRotation = node.getString("rotation");
		YamlNode mountingRotationNode = node.getNode("mountingRotation");
		if (mountingRotationNode != null) {
			mountingRotation = new Quaternion(
				mountingRotationNode.getFloat("x", 0),
				mountingRotationNode.getFloat("y", 0),
				mountingRotationNode.getFloat("z", 0),
				mountingRotationNode.getFloat("w", 1)
			);
		}

		if (oldMountingRotation != null) {
			TrackerMountingRotation rot = TrackerMountingRotation.fromName(oldMountingRotation);
			mountingRotation = Objects
				.requireNonNullElse(rot, TrackerMountingRotation.FRONT).quaternion;
		}

		YamlNode adjNode = node.getNode("adjustment");
		if (adjNode != null) {
			adjustment = new Quaternion(
				adjNode.getFloat("x", 0),
				adjNode.getFloat("y", 0),
				adjNode.getFloat("z", 0),
				adjNode.getFloat("w", 0)
			);
		}
	}

	public void setDesignation(String newDesignation) {
		this.designation = newDesignation;
	}

	public void saveConfig(YamlNode configNode) {
		configNode.setProperty("name", trackerName);
		if (customName != null) {
			configNode.setProperty("customName", customName);
		}
		if (designation != null)
			configNode.setProperty("designation", designation);
		else
			configNode.removeProperty("designation");
		if (hide)
			configNode.setProperty("hide", hide);
		else
			configNode.removeProperty("hide");
		if (adjustment != null) {
			configNode.setProperty("adj.x", adjustment.getX());
			configNode.setProperty("adj.y", adjustment.getY());
			configNode.setProperty("adj.z", adjustment.getZ());
			configNode.setProperty("adj.w", adjustment.getW());
		} else {
			configNode.removeProperty("adj");
		}
		if (oldMountingRotation != null) {
			configNode.removeProperty("rotation");
		}

		if (mountingRotation != null) {
			configNode.setProperty("mountingRotation.x", mountingRotation.getX());
			configNode.setProperty("mountingRotation.y", mountingRotation.getY());
			configNode.setProperty("mountingRotation.z", mountingRotation.getZ());
			configNode.setProperty("mountingRotation.w", mountingRotation.getW());
		} else {
			configNode.removeProperty("mountingRotation");
		}
	}
}
