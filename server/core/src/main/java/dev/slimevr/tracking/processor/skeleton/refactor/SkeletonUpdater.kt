package dev.slimevr.tracking.processor.skeleton.refactor

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.snapshots.TrackerSnapshot
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

class SkeletonUpdater(
	val skeleton: Skeleton,
	val trackers: TrackersData,
	val config: HumanSkeletonConfig,
	val skeletonOffsets: Map<SkeletonConfigOffsets, Float>,
) {

	class TrackerData(
		val rotation: Quaternion,
		val position: Vector3?,
	) {
		companion object {
			fun fromSnapshot(snapshot: TrackerSnapshot) = TrackerData(
				snapshot.adjustedTrackerToWorld.toFloat(),
				snapshot.trackerOriginInWorld?.toFloat(),
			)
		}
	}

	// TODO: Just use this instead of TrackersSnapshot
	class TrackersData(
		val head: TrackerData?,
		val neck: TrackerData?,
		val upperChest: TrackerData?,
		val chest: TrackerData?,
		val waist: TrackerData?,
		val hip: TrackerData?,
		val leftUpperLeg: TrackerData?,
		val leftLowerLeg: TrackerData?,
		val leftFoot: TrackerData?,
		val rightUpperLeg: TrackerData?,
		val rightLowerLeg: TrackerData?,
		val rightFoot: TrackerData?,
		val leftShoulder: TrackerData?,
		val leftUpperArm: TrackerData?,
		val leftLowerArm: TrackerData?,
		val leftHand: TrackerData?,
		val rightShoulder: TrackerData?,
		val rightUpperArm: TrackerData?,
		val rightLowerArm: TrackerData?,
		val rightHand: TrackerData?,
	) {
		companion object {
			fun fromSnapshot(trackers: Map<TrackerPosition, TrackerSnapshot>) = TrackersData(
				head = trackers[TrackerPosition.HEAD]?.let(TrackerData::fromSnapshot),
				neck = trackers[TrackerPosition.NECK]?.let(TrackerData::fromSnapshot),
				upperChest = trackers[TrackerPosition.UPPER_CHEST]?.let(TrackerData::fromSnapshot),
				chest = trackers[TrackerPosition.CHEST]?.let(TrackerData::fromSnapshot),
				waist = trackers[TrackerPosition.WAIST]?.let(TrackerData::fromSnapshot),
				hip = trackers[TrackerPosition.HIP]?.let(TrackerData::fromSnapshot),
				leftUpperLeg = trackers[TrackerPosition.LEFT_UPPER_LEG]?.let(TrackerData::fromSnapshot),
				leftLowerLeg = trackers[TrackerPosition.LEFT_LOWER_LEG]?.let(TrackerData::fromSnapshot),
				leftFoot = trackers[TrackerPosition.LEFT_FOOT]?.let(TrackerData::fromSnapshot),
				rightUpperLeg = trackers[TrackerPosition.RIGHT_UPPER_LEG]?.let(TrackerData::fromSnapshot),
				rightLowerLeg = trackers[TrackerPosition.RIGHT_LOWER_LEG]?.let(TrackerData::fromSnapshot),
				rightFoot = trackers[TrackerPosition.RIGHT_FOOT]?.let(TrackerData::fromSnapshot),
				leftShoulder = trackers[TrackerPosition.LEFT_SHOULDER]?.let(TrackerData::fromSnapshot),
				leftUpperArm = trackers[TrackerPosition.LEFT_UPPER_ARM]?.let(TrackerData::fromSnapshot),
				leftLowerArm = trackers[TrackerPosition.LEFT_LOWER_ARM]?.let(TrackerData::fromSnapshot),
				leftHand = trackers[TrackerPosition.LEFT_HAND]?.let(TrackerData::fromSnapshot),
				rightShoulder = trackers[TrackerPosition.RIGHT_SHOULDER]?.let(TrackerData::fromSnapshot),
				rightUpperArm = trackers[TrackerPosition.RIGHT_UPPER_ARM]?.let(TrackerData::fromSnapshot),
				rightLowerArm = trackers[TrackerPosition.RIGHT_LOWER_ARM]?.let(TrackerData::fromSnapshot),
				rightHand = trackers[TrackerPosition.RIGHT_HAND]?.let(TrackerData::fromSnapshot),
			)
		}
	}

	class HumanSkeletonConfig(
		val extendedSpineModel: Boolean = true,
		val extendedPelvisModel: Boolean = true,
		val extendedKneeModel: Boolean = true,
		val waistFromChestHipAveraging: Float = 0.3f,
		val waistFromChestLegsAveraging: Float = 0.3f,
		val hipFromChestLegsAveraging: Float = 0.5f,
		val hipFromWaistLegsAveraging: Float = 0.4f,
		val hipLegsAveraging: Float = 0.25f,
		val kneeTrackerAnkleAveraging: Float = 0.85f,
		val kneeAnkleAveraging: Float = 0.0f,
	)

	fun update() {
		updateBoneLengths()
		updateBoneRotations()
		updateBonePositions()
	}

	/**
	 * Update all the bones' transforms from trackers
	 */
	private fun updateBoneRotations() {
		// Head
		updateHeadTransforms()

		// Spine
		updateSpineTransforms()

		// Left leg
		updateLegTransforms(
			skeleton.leftUpperLegBone,
			skeleton.leftKneeTrackerBone,
			skeleton.leftLowerLegBone,
			skeleton.leftFootBone,
			skeleton.leftFootTrackerBone,
			trackers.leftUpperLeg,
			trackers.leftLowerLeg,
			trackers.leftFoot,
		)

		// Right leg
		updateLegTransforms(
			skeleton.rightUpperLegBone,
			skeleton.rightKneeTrackerBone,
			skeleton.rightLowerLegBone,
			skeleton.rightFootBone,
			skeleton.rightFootTrackerBone,
			trackers.rightUpperLeg,
			trackers.rightLowerLeg,
			trackers.rightFoot,
		)

		// Left arm
		updateArmTransforms(
			skeleton.isTrackingLeftArmFromController,
			skeleton.leftUpperShoulderBone,
			skeleton.leftShoulderBone,
			skeleton.leftUpperArmBone,
			skeleton.leftElbowTrackerBone,
			skeleton.leftLowerArmBone,
			skeleton.leftHandBone,
			skeleton.leftHandTrackerBone,
			trackers.leftShoulder,
			trackers.leftUpperArm,
			trackers.leftLowerArm,
			trackers.leftHand,
		)

		// Right arm
		updateArmTransforms(
			skeleton.isTrackingRightArmFromController,
			skeleton.rightUpperShoulderBone,
			skeleton.rightShoulderBone,
			skeleton.rightUpperArmBone,
			skeleton.rightElbowTrackerBone,
			skeleton.rightLowerArmBone,
			skeleton.rightHandBone,
			skeleton.rightHandTrackerBone,
			trackers.rightShoulder,
			trackers.rightUpperArm,
			trackers.rightLowerArm,
			trackers.rightHand,
		)
	}

	/**
	 * Update the head and neck bone transforms
	 */
	private fun updateHeadTransforms() {
		trackers.head?.let { head ->
			// Set head position
			head.position?.let { skeleton.headBone.setPosition(it) }

			// Get head rotation
			var headRot = head.rotation

			// Set head rotation
			skeleton.headBone.setRotation(headRot)
			skeleton.headTrackerBone.setRotation(headRot)

			// Get neck rotation
			trackers.neck?.let { headRot = it.rotation }

			// Set neck rotation
			skeleton.neckBone.setRotation(headRot)
		}
	}

	/**
	 * Update the spine transforms, from the upper chest to the hip
	 */
	private fun updateSpineTransforms() {
		val hasSpineTracker = getFirstAvailableTracker(trackers.upperChest, trackers.chest, trackers.waist, trackers.hip) != null
		val hasKneeTrackers = getFirstAvailableTracker(trackers.leftUpperLeg, trackers.rightUpperLeg) != null

		if (hasSpineTracker) {
			// Upper chest and chest tracker
			getFirstAvailableTracker(trackers.upperChest, trackers.chest, trackers.waist, trackers.hip)?.let {
				skeleton.upperChestBone.setRotation(it.rotation)
				skeleton.chestTrackerBone.setRotation(it.rotation)
			}

			// Chest
			getFirstAvailableTracker(trackers.chest, trackers.upperChest, trackers.waist, trackers.hip)?.let {
				skeleton.chestBone.setRotation(it.rotation)
			}

			// Waist
			getFirstAvailableTracker(trackers.waist, trackers.chest, trackers.hip, trackers.upperChest)?.let {
				skeleton.waistBone.setRotation(it.rotation)
			}

			// Hip and hip tracker
			getFirstAvailableTracker(trackers.hip, trackers.waist, trackers.chest, trackers.upperChest)?.let {
				skeleton.hipBone.setRotation(it.rotation)
				skeleton.hipTrackerBone.setRotation(it.rotation)
			}
		} else if (trackers.head != null) {
			// Align with neck's yaw
			val yawRot = skeleton.neckBone.getGlobalRotation().project(Vector3.Companion.POS_Y).unit()
			skeleton.upperChestBone.setRotation(yawRot)
			skeleton.chestTrackerBone.setRotation(yawRot)
			skeleton.chestBone.setRotation(yawRot)
			skeleton.waistBone.setRotation(yawRot)
			skeleton.hipBone.setRotation(yawRot)
			skeleton.hipTrackerBone.setRotation(yawRot)
		}

		// Extended spine model
		if (config.extendedSpineModel && hasSpineTracker) {
			// Tries to guess missing lower spine trackers by interpolating rotations
			if (trackers.waist == null) {
				getFirstAvailableTracker(trackers.chest, trackers.upperChest)?.let { chest ->
					trackers.hip?.let {
						// Calculates waist from chest + hip
						var hipRot = it.rotation
						var chestRot = chest.rotation

						// Interpolate between the chest and the hip
						chestRot = chestRot.interpQ(hipRot, config.waistFromChestHipAveraging)

						// Set waist's rotation
						skeleton.waistBone.setRotation(chestRot)
					} ?: run {
						if (hasKneeTrackers) {
							// Calculates waist from chest + legs
							var leftLegRot = trackers.leftUpperLeg?.rotation ?: Quaternion.Companion.IDENTITY
							var rightLegRot = trackers.rightUpperLeg?.rotation ?: Quaternion.Companion.IDENTITY
							var chestRot = chest.rotation

							// Interpolate between the pelvis, averaged from the legs, and the chest
							chestRot = chestRot.interpQ(leftLegRot.lerpQ(rightLegRot, 0.5f), config.waistFromChestLegsAveraging).unit()

							// Set waist's rotation
							skeleton.waistBone.setRotation(chestRot)
						}
					}
				}
			}
			if (trackers.hip == null && hasKneeTrackers) {
				trackers.waist?.let {
					// Calculates hip from waist + legs
					var leftLegRot = trackers.leftUpperLeg?.rotation ?: Quaternion.Companion.IDENTITY
					var rightLegRot = trackers.rightUpperLeg?.rotation ?: Quaternion.Companion.IDENTITY
					var waistRot = it.rotation

					// Interpolate between the pelvis, averaged from the legs, and the chest
					waistRot = waistRot.interpQ(leftLegRot.lerpQ(rightLegRot, 0.5f), config.hipFromWaistLegsAveraging).unit()

					// Set hip rotation
					skeleton.hipBone.setRotation(waistRot)
					skeleton.hipTrackerBone.setRotation(waistRot)
				} ?: run {
					getFirstAvailableTracker(trackers.chest, trackers.upperChest)?.let {
						// Calculates hip from chest + legs
						var leftLegRot = trackers.leftUpperLeg?.rotation ?: Quaternion.Companion.IDENTITY
						var rightLegRot = trackers.rightUpperLeg?.rotation ?: Quaternion.Companion.IDENTITY
						var chestRot = it.rotation

						// Interpolate between the pelvis, averaged from the legs, and the chest
						chestRot = chestRot.interpQ(leftLegRot.lerpQ(rightLegRot, 0.5f), config.hipFromChestLegsAveraging).unit()

						// Set hip rotation
						skeleton.hipBone.setRotation(chestRot)
						skeleton.hipTrackerBone.setRotation(chestRot)
					}
				}
			}
		}

		// Extended pelvis model
		if (config.extendedPelvisModel && hasKneeTrackers && trackers.hip == null) {
			val leftLegRot = trackers.leftUpperLeg?.rotation ?: Quaternion.Companion.IDENTITY
			val rightLegRot = trackers.rightUpperLeg?.rotation ?: Quaternion.Companion.IDENTITY
			val hipRot = skeleton.hipBone.getLocalRotation()

			val extendedPelvisRot = extendedPelvisYawRoll(leftLegRot, rightLegRot, hipRot)

			// Interpolate between the hipRot and extendedPelvisRot
			val newHipRot = hipRot.interpR(
				if (extendedPelvisRot.lenSq() != 0.0f) extendedPelvisRot else Quaternion.Companion.IDENTITY,
				config.hipLegsAveraging,
			)

			// Set new hip rotation
			skeleton.hipBone.setRotation(newHipRot)
			skeleton.hipTrackerBone.setRotation(newHipRot)
		}

		// Set left and right hip rotations to the hip's
		skeleton.leftHipBone.setRotation(skeleton.hipBone.getLocalRotation())
		skeleton.rightHipBone.setRotation(skeleton.hipBone.getLocalRotation())
	}

	/**
	 * Update a leg's transforms, from its hip to its foot
	 */
	private fun updateLegTransforms(
		upperLegBone: Bone,
		kneeTrackerBone: Bone,
		lowerLegBone: Bone,
		footBone: Bone,
		footTrackerBone: Bone,
		upperLegTracker: TrackerData?,
		lowerLegTracker: TrackerData?,
		footTracker: TrackerData?,
	) {
		var legRot = Quaternion.Companion.IDENTITY

		upperLegTracker?.let {
			// Get upper leg rotation
			legRot = it.rotation
		} ?: run {
			// Use hip's yaw
			legRot = skeleton.hipBone.getLocalRotation().project(Vector3.Companion.POS_Y).unit()
		}
		// Set upper leg rotation
		upperLegBone.setRotation(legRot)
		kneeTrackerBone.setRotation(legRot)

		lowerLegTracker?.let {
			// Get lower leg rotation
			legRot = it.rotation
		} ?: run {
			// Use lower leg or hip's yaw
			legRot = legRot.project(Vector3.Companion.POS_Y).unit()
		}
		// Set lower leg rotation
		lowerLegBone.setRotation(legRot)

		// Get foot rotation
		footTracker?.let { legRot = it.rotation }
		// Set foot rotation
		footBone.setRotation(legRot)
		footTrackerBone.setRotation(legRot)

		// Extended knee model
		if (config.extendedKneeModel) {
			upperLegTracker?.let { upper ->
				lowerLegTracker?.let { lower ->
					// Averages the upper leg's rotation with the local lower leg's
					// pitch and roll and apply to the tracker node.
					val upperRot = upper.rotation
					val lowerRot = lower.rotation
					val extendedRot = extendedKneeYawRoll(upperRot, lowerRot)

					upperLegBone.setRotation(upperRot.interpR(extendedRot, config.kneeAnkleAveraging))
					kneeTrackerBone.setRotation(upperRot.interpR(extendedRot, config.kneeTrackerAnkleAveraging))
				}
			}
		}
	}

	/**
	 * Update an arm's transforms, from its shoulder to its hand
	 */
	private fun updateArmTransforms(
		isTrackingFromController: Boolean,
		upperShoulderBone: Bone,
		shoulderBone: Bone,
		upperArmBone: Bone,
		elbowTrackerBone: Bone,
		lowerArmBone: Bone,
		handBone: Bone,
		handTrackerBone: Bone,
		shoulderTracker: TrackerData?,
		upperArmTracker: TrackerData?,
		lowerArmTracker: TrackerData?,
		handTracker: TrackerData?,
	) {
		if (isTrackingFromController) { // From controller
			// Set hand rotation and position from tracker
			handTracker?.let {
				it.position?.let { handTrackerBone.setPosition(it) }
				handTrackerBone.setRotation(it.rotation)
				handBone.setRotation(it.rotation)
			}

			// Get lower arm rotation
			var armRot = getFirstAvailableTracker(lowerArmTracker, upperArmTracker)
				?.rotation ?: Quaternion.Companion.IDENTITY
			// Set lower arm rotation
			lowerArmBone.setRotation(armRot)

			// Get upper arm rotation
			armRot = getFirstAvailableTracker(upperArmTracker, lowerArmTracker)
				?.rotation ?: Quaternion.Companion.IDENTITY
			// Set elbow tracker rotation
			elbowTrackerBone.setRotation(armRot)
		} else { // From HMD
			// Get shoulder rotation
			var armRot = shoulderTracker?.rotation ?: skeleton.upperChestBone.getLocalRotation()
			// Set shoulder rotation
			upperShoulderBone.setRotation(skeleton.upperChestBone.getLocalRotation())
			shoulderBone.setRotation(armRot)

			if (upperArmTracker != null || lowerArmTracker != null) {
				// Get upper arm rotation
				getFirstAvailableTracker(upperArmTracker, lowerArmTracker)?.let { armRot = it.rotation }
				// Set upper arm and elbow tracker rotation
				upperArmBone.setRotation(armRot)
				elbowTrackerBone.setRotation(armRot)

				// Get lower arm rotation
				getFirstAvailableTracker(lowerArmTracker, upperArmTracker)?.let { armRot = it.rotation }
				// Set lower arm rotation
				lowerArmBone.setRotation(armRot)
			} else {
				// Fallback arm rotation as upper chest
				armRot = skeleton.upperChestBone.getLocalRotation()
				upperArmBone.setRotation(armRot)
				elbowTrackerBone.setRotation(armRot)
				lowerArmBone.setRotation(armRot)
			}

			// Get hand rotation
			handTracker?.let { armRot = it.rotation }
			// Set hand, and hand tracker rotation
			handBone.setRotation(armRot)
			handTrackerBone.setRotation(armRot)
		}
	}

	fun updateBonePositions() {
		skeleton.headBone.update()
		if (skeleton.isTrackingLeftArmFromController) skeleton.leftHandTrackerBone.update()
		if (skeleton.isTrackingRightArmFromController) skeleton.rightHandTrackerBone.update()
	}

	/**
	 * Rotates the first Quaternion to match its yaw and roll to the rotation of
	 * the second Quaternion
	 *
	 * @param knee the first Quaternion
	 * @param ankle the second Quaternion
	 * @return the rotated Quaternion
	 */
	private fun extendedKneeYawRoll(knee: Quaternion, ankle: Quaternion): Quaternion {
		val r = knee.inv() * ankle
		val c = Quaternion(r.w, -r.x, 0f, 0f)
		return (knee * r * c).unit()
	}

	/**
	 * Rotates the third Quaternion to match its yaw and roll to the rotation of
	 * the average of the first and second quaternions.
	 *
	 * @param leftKnee the first Quaternion
	 * @param rightKnee the second Quaternion
	 * @param hip the third Quaternion
	 * @return the rotated Quaternion
	 */
	private fun extendedPelvisYawRoll(
		leftKnee: Quaternion,
		rightKnee: Quaternion,
		hip: Quaternion,
	): Quaternion {
		// R = InverseHip * (LeftLeft + RightLeg)
		// C = Quaternion(R.w, -R.x, 0, 0)
		// Pelvis = Hip * R * C
		// normalize(Pelvis)
		val r = hip.inv() * (leftKnee + rightKnee)
		val c = Quaternion(r.w, -r.x, 0f, 0f)
		return (hip * r * c).unit()
	}

	private fun getFirstAvailableTracker(
		vararg trackers: TrackerData?,
	): TrackerData? = trackers.firstOrNull { it != null }

	private fun updateBoneLengths() {
		for (boneType in BoneType.values) {
			computeNodeOffset(boneType)
		}
	}

	private fun computeNodeOffset(nodeOffset: BoneType) {
		when (nodeOffset) {
			BoneType.HEAD -> setNodeOffset(nodeOffset, 0f, 0f, getOffset(SkeletonConfigOffsets.HEAD))

			BoneType.NECK -> setNodeOffset(nodeOffset, 0f, -getOffset(SkeletonConfigOffsets.NECK), 0f)

			BoneType.UPPER_CHEST -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.UPPER_CHEST),
				0f,
			)

			BoneType.CHEST_TRACKER -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.CHEST_OFFSET) -
					getOffset(SkeletonConfigOffsets.CHEST),
				-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET),
			)

			BoneType.CHEST -> setNodeOffset(nodeOffset, 0f, -getOffset(SkeletonConfigOffsets.CHEST), 0f)

			BoneType.WAIST -> setNodeOffset(nodeOffset, 0f, -getOffset(SkeletonConfigOffsets.WAIST), 0f)

			BoneType.HIP -> setNodeOffset(nodeOffset, 0f, -getOffset(SkeletonConfigOffsets.HIP), 0f)

			BoneType.HIP_TRACKER -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.HIP_OFFSET),
				-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET),
			)

			BoneType.LEFT_HIP -> setNodeOffset(
				nodeOffset,
				-getOffset(SkeletonConfigOffsets.HIPS_WIDTH) / 2f,
				0f,
				0f,
			)

			BoneType.RIGHT_HIP -> setNodeOffset(
				nodeOffset,
				getOffset(SkeletonConfigOffsets.HIPS_WIDTH) / 2f,
				0f,
				0f,
			)

			BoneType.LEFT_UPPER_LEG, BoneType.RIGHT_UPPER_LEG -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.UPPER_LEG),
				0f,
			)

			BoneType.LEFT_KNEE_TRACKER, BoneType.RIGHT_KNEE_TRACKER, BoneType.LEFT_FOOT_TRACKER, BoneType.RIGHT_FOOT_TRACKER -> setNodeOffset(
				nodeOffset,
				0f,
				0f,
				-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET),
			)

			BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.LOWER_LEG),
				-getOffset(SkeletonConfigOffsets.FOOT_SHIFT),
			)

			BoneType.LEFT_FOOT, BoneType.RIGHT_FOOT -> setNodeOffset(
				nodeOffset,
				0f,
				0f,
				-getOffset(SkeletonConfigOffsets.FOOT_LENGTH),
			)

			BoneType.LEFT_UPPER_SHOULDER -> setNodeOffset(
				nodeOffset,
				0f,
				0f,
				0f,
			)

			BoneType.RIGHT_UPPER_SHOULDER -> setNodeOffset(
				nodeOffset,
				0f,
				0f,
				0f,
			)

			BoneType.LEFT_SHOULDER -> setNodeOffset(
				nodeOffset,
				-getOffset(SkeletonConfigOffsets.SHOULDERS_WIDTH) / 2f,
				-getOffset(SkeletonConfigOffsets.SHOULDERS_DISTANCE),
				0f,
			)

			BoneType.RIGHT_SHOULDER -> setNodeOffset(
				nodeOffset,
				getOffset(SkeletonConfigOffsets.SHOULDERS_WIDTH) / 2f,
				-getOffset(SkeletonConfigOffsets.SHOULDERS_DISTANCE),
				0f,
			)

			BoneType.LEFT_UPPER_ARM, BoneType.RIGHT_UPPER_ARM -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.UPPER_ARM),
				0f,
			)

			BoneType.LEFT_LOWER_ARM, BoneType.RIGHT_LOWER_ARM -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.LOWER_ARM),
				0f,
			)

			BoneType.LEFT_HAND, BoneType.RIGHT_HAND -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.HAND_Y),
				-getOffset(SkeletonConfigOffsets.HAND_Z),
			)

			BoneType.LEFT_ELBOW_TRACKER, BoneType.RIGHT_ELBOW_TRACKER -> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.ELBOW_OFFSET),
				0f,
			)

			BoneType.LEFT_THUMB_METACARPAL, BoneType.LEFT_THUMB_PROXIMAL, BoneType.LEFT_THUMB_DISTAL,
			BoneType.RIGHT_THUMB_METACARPAL, BoneType.RIGHT_THUMB_PROXIMAL, BoneType.RIGHT_THUMB_DISTAL,
			-> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.HAND_Y) * 0.2f,
				-getOffset(SkeletonConfigOffsets.HAND_Y) * 0.1f,
			)

			BoneType.LEFT_INDEX_PROXIMAL, BoneType.LEFT_INDEX_INTERMEDIATE, BoneType.LEFT_INDEX_DISTAL,
			BoneType.RIGHT_INDEX_PROXIMAL, BoneType.RIGHT_INDEX_INTERMEDIATE, BoneType.RIGHT_INDEX_DISTAL,
			-> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.HAND_Y) * 0.25f,
				0f,
			)

			BoneType.LEFT_MIDDLE_PROXIMAL, BoneType.LEFT_MIDDLE_INTERMEDIATE, BoneType.LEFT_MIDDLE_DISTAL,
			BoneType.RIGHT_MIDDLE_PROXIMAL, BoneType.RIGHT_MIDDLE_INTERMEDIATE, BoneType.RIGHT_MIDDLE_DISTAL,
			-> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.HAND_Y) * 0.3f,
				0f,
			)

			BoneType.LEFT_RING_PROXIMAL, BoneType.LEFT_RING_INTERMEDIATE, BoneType.LEFT_RING_DISTAL,
			BoneType.RIGHT_RING_PROXIMAL, BoneType.RIGHT_RING_INTERMEDIATE, BoneType.RIGHT_RING_DISTAL,
			-> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.HAND_Y) * 0.28f,
				0f,
			)

			BoneType.LEFT_LITTLE_PROXIMAL, BoneType.LEFT_LITTLE_INTERMEDIATE, BoneType.LEFT_LITTLE_DISTAL,
			BoneType.RIGHT_LITTLE_PROXIMAL, BoneType.RIGHT_LITTLE_INTERMEDIATE, BoneType.RIGHT_LITTLE_DISTAL,
			-> setNodeOffset(
				nodeOffset,
				0f,
				-getOffset(SkeletonConfigOffsets.HAND_Y) * 0.2f,
				0f,
			)

			else -> {}
		}
	}

	private fun getOffset(config: SkeletonConfigOffsets): Float {
		val configOffset = skeletonOffsets[config]
		return configOffset ?: config.defaultValue
	}

	private fun setNodeOffset(boneType: BoneType, x: Float, y: Float, z: Float) {
		val bone = skeleton.getBone(boneType) ?: return

		var transOffset = Vector3(x, y, z)

		// If no head position, headShift and neckLength = 0
		if ((boneType == BoneType.HEAD || boneType == BoneType.NECK) && (trackers.head == null || trackers.head!!.position == null)) {
			transOffset = Vector3.Companion.NULL
		}
		// If trackingArmFromController, reverse
		if (((boneType == BoneType.LEFT_LOWER_ARM || boneType == BoneType.LEFT_HAND) && skeleton.isTrackingLeftArmFromController) ||
			(
				(boneType == BoneType.RIGHT_LOWER_ARM || boneType == BoneType.RIGHT_HAND) &&
					skeleton.isTrackingRightArmFromController
				)
		) {
			transOffset = -transOffset
		}

		// Compute bone rotation
		val rotOffset = if (transOffset.len() > 0f) {
			if (transOffset.unit().y == 1f) {
				Quaternion.Companion.I
			} else {
				Quaternion.Companion.fromTo(Vector3.Companion.NEG_Y, transOffset)
			}
		} else {
			Quaternion.Companion.IDENTITY
		}

		// Update bone length
		bone.length = transOffset.len()

		// Set bone rotation offset
		bone.rotationOffset = rotOffset
	}
}
