package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion

class ShareableBone(val boneType: BoneType) {

	/**
	 * Returns the rotation of the bone relative to its parent
	 */
	var localRotation: Quaternion = Quaternion.IDENTITY
}
