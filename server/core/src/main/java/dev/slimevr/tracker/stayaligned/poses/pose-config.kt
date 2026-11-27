package dev.slimevr.tracker.stayaligned.poses

import dev.slimevr.config.StayAlignedPoseConfig
import dev.slimevr.config.UserConfigData
import solarxr_protocol.rpc.StayAlignedRelaxedPose

/** The poses the user can record and turn on, which is all of them but UNKNOWN. */
val STAY_ALIGNED_POSES: List<StayAlignedRelaxedPose> = StayAlignedRelaxedPose.entries - StayAlignedRelaxedPose.UNKNOWN

private val DEFAULT_STAY_ALIGNED_POSES = mapOf(
	StayAlignedRelaxedPose.KNEELING to StayAlignedPoseConfig(enabled = true),
)

fun hasStayAlignedPoseDefault(pose: StayAlignedRelaxedPose): Boolean = pose in DEFAULT_STAY_ALIGNED_POSES

fun stayAlignedPoseConfig(data: UserConfigData, pose: StayAlignedRelaxedPose): StayAlignedPoseConfig = data.stayAlignedPoses[pose] ?: DEFAULT_STAY_ALIGNED_POSES[pose] ?: StayAlignedPoseConfig()
