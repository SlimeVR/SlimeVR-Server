package dev.slimevr.vr.trackers;

import com.jme3.math.Quaternion;

public interface TrackerWithFix {

	Quaternion getYawFix();
	Quaternion getGyroFix();
	Quaternion getAttachmentFix();

}
