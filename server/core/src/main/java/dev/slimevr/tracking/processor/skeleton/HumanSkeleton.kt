package dev.slimevr.tracking.processor.skeleton

import com.jme3.math.FastMath
import dev.slimevr.VRServer
import dev.slimevr.tracking.processor.BoneInfo
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.TransformNode
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles
import dev.slimevr.tracking.processor.config.SkeletonConfigValues
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerRole
import dev.slimevr.tracking.trackers.TrackerUtils.getFirstAvailableTracker
import dev.slimevr.tracking.trackers.TrackerUtils.getTrackerForSkeleton
import dev.slimevr.util.ann.VRServerThread
import io.eiren.util.ann.ThreadSafe
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.IDENTITY
import io.github.axisangles.ktmath.Vector3
import io.github.axisangles.ktmath.Vector3.Companion.NULL
import io.github.axisangles.ktmath.Vector3.Companion.POS_Y

class HumanSkeleton(
	val humanPoseManager: HumanPoseManager,
) {
	// Upper body nodes (torso)
	val hmdNode = TransformNode(BoneType.HMD, false)
	val headNode = TransformNode(BoneType.HEAD, false)
	val trackerHeadNode = TransformNode(BoneType.HEAD_TRACKER, false)
	val neckNode = TransformNode(BoneType.NECK, false)
	val upperChestNode = TransformNode(BoneType.CHEST, false)
	val trackerChestNode = TransformNode(BoneType.CHEST_TRACKER, false)
	val chestNode = TransformNode(BoneType.CHEST, false)
	val waistNode = TransformNode(BoneType.WAIST, false)
	val hipNode = TransformNode(BoneType.HIP, false)
	val trackerHipNode = TransformNode(BoneType.HIP_TRACKER, false)

	// Lower body nodes (legs)
	val leftHipNode = TransformNode(BoneType.LEFT_HIP, false)
	val leftKneeNode = TransformNode(BoneType.LEFT_UPPER_LEG, false)
	val trackerLeftKneeNode = TransformNode(BoneType.LEFT_KNEE_TRACKER, false)
	val leftAnkleNode = TransformNode(BoneType.LEFT_LOWER_LEG, false)
	val leftFootNode = TransformNode(BoneType.LEFT_FOOT, false)
	val trackerLeftFootNode = TransformNode(BoneType.LEFT_FOOT_TRACKER, false)
	val rightHipNode = TransformNode(BoneType.RIGHT_HIP, false)
	val rightKneeNode = TransformNode(BoneType.RIGHT_UPPER_LEG, false)
	val trackerRightKneeNode = TransformNode(BoneType.RIGHT_KNEE_TRACKER, false)
	val rightAnkleNode = TransformNode(BoneType.RIGHT_LOWER_LEG, false)
	val rightFootNode = TransformNode(BoneType.RIGHT_FOOT, false)
	val trackerRightFootNode = TransformNode(BoneType.RIGHT_FOOT_TRACKER, false)

	// Arms
	val leftShoulderHeadNode = TransformNode(BoneType.LEFT_SHOULDER, false)
	val rightShoulderHeadNode = TransformNode(BoneType.RIGHT_SHOULDER, false)
	val leftShoulderTailNode = TransformNode(BoneType.LEFT_UPPER_ARM, false)
	val rightShoulderTailNode = TransformNode(BoneType.RIGHT_UPPER_ARM, false)
	val leftElbowNode = TransformNode(BoneType.LEFT_LOWER_ARM, false)
	val rightElbowNode = TransformNode(BoneType.RIGHT_LOWER_ARM, false)
	val trackerLeftElbowNode = TransformNode(BoneType.LEFT_ELBOW_TRACKER, false)
	val trackerRightElbowNode = TransformNode(BoneType.RIGHT_ELBOW_TRACKER, false)
	val leftWristNode = TransformNode(BoneType.LEFT_HAND, false)
	val rightWristNode = TransformNode(BoneType.RIGHT_HAND, false)
	val leftHandNode = TransformNode(BoneType.LEFT_HAND, false)
	val rightHandNode = TransformNode(BoneType.RIGHT_HAND, false)
	val trackerLeftHandNode = TransformNode(BoneType.LEFT_HAND_TRACKER, false)
	val trackerRightHandNode = TransformNode(BoneType.RIGHT_HAND_TRACKER, false)

	val allBoneInfo: MutableList<BoneInfo> = ArrayList()

	val shareableBoneInfo: MutableList<BoneInfo?> = ArrayList()

	// Buffers
	var hasSpineTracker = false
	var hasKneeTrackers = false
	var hasLeftLegTracker = false
	var hasRightLegTracker = false
	var hasLeftFootTracker = false
	var hasRightFootTracker = false
	var hasLeftArmTracker = false
	var hasRightArmTracker = false

	// Input trackers
	var headTracker: Tracker? = null
	var neckTracker: Tracker? = null
	var upperChestTracker: Tracker? = null
	var chestTracker: Tracker? = null
	var waistTracker: Tracker? = null
	var hipTracker: Tracker? = null
	var leftUpperLegTracker: Tracker? = null
	var leftLowerLegTracker: Tracker? = null
	var leftFootTracker: Tracker? = null
	var rightUpperLegTracker: Tracker? = null
	var rightLowerLegTracker: Tracker? = null
	var rightFootTracker: Tracker? = null
	var leftLowerArmTracker: Tracker? = null
	var rightLowerArmTracker: Tracker? = null
	var leftUpperArmTracker: Tracker? = null
	var rightUpperArmTracker: Tracker? = null
	var leftHandTracker: Tracker? = null
	var rightHandTracker: Tracker? = null
	var leftShoulderTracker: Tracker? = null
	var rightShoulderTracker: Tracker? = null

	// Output trackers
	var computedHeadTracker: Tracker? = null
	var computedChestTracker: Tracker? = null
	var computedHipTracker: Tracker? = null
	var computedLeftKneeTracker: Tracker? = null
	var computedLeftFootTracker: Tracker? = null
	var computedRightKneeTracker: Tracker? = null
	var computedRightFootTracker: Tracker? = null
	var computedLeftElbowTracker: Tracker? = null
	var computedRightElbowTracker: Tracker? = null
	var computedLeftHandTracker: Tracker? = null
	var computedRightHandTracker: Tracker? = null

	// Toggles
	private var extendedSpineModel = false
	private var extendedPelvisModel = false
	private var extendedKneeModel = false
	private var forceArmsFromHMD = true

	// Ratios
	private var waistFromChestHipAveraging = 0f
	private var waistFromChestLegsAveraging = 0f
	private var hipFromChestLegsAveraging = 0f
	private var hipFromWaistLegsAveraging = 0f
	private var hipLegsAveraging = 0f
	private var kneeTrackerAnkleAveraging = 0f
	private var kneeAnkleAveraging = 0f

	// Others
	private var sendAllBones = false
	private var pauseTracking = false // Pauses skeleton tracking if true, resumes skeleton tracking if false

	// Modules
	var legTweaks = LegTweaks(this)
	var tapDetectionManager = TapDetectionManager(this)
	var viveEmulation = ViveEmulation(this)
	var localizer = Localizer(this)

	// Constructors
	init {
		assembleSkeleton()
		if (humanPoseManager.computedTrackers != null) {
			setComputedTrackers(humanPoseManager.computedTrackers)
		}
		resetBones()
	}

	constructor(
		humanPoseManager: HumanPoseManager,
		server: VRServer,
	) : this(humanPoseManager) {
		setTrackersFromList(server.allTrackers)
		tapDetectionManager = TapDetectionManager(
			this,
			humanPoseManager,
			server.configManager.vrConfig.tapDetection,
			server.resetHandler,
			server.tapSetupHandler,
			server.allTrackers
		)
		legTweaks.setConfig(server.configManager.vrConfig.legTweaks)
		localizer.setEnabled(humanPoseManager.getToggle(SkeletonConfigToggles.SELF_LOCALIZATION))
	}

	constructor(
		humanPoseManager: HumanPoseManager,
		trackers: List<Tracker>?,
	) : this(humanPoseManager) {
		var trackersList = trackers
		if (trackersList == null) {
			trackersList = FastList(0)
		}
		setTrackersFromList(trackersList)
	}

	/**
	 * Assembles the whole skeleton
	 */
	@ThreadSafe
	fun assembleSkeleton() {
		// Assemble skeleton from head to hip
		hmdNode.attachChild(headNode)
		headNode.attachChild(neckNode)
		neckNode.attachChild(upperChestNode)
		upperChestNode.attachChild(chestNode)
		chestNode.attachChild(waistNode)
		waistNode.attachChild(hipNode)

		// Assemble skeleton from hips to feet
		hipNode.attachChild(leftHipNode)
		hipNode.attachChild(rightHipNode)
		leftHipNode.attachChild(leftKneeNode)
		rightHipNode.attachChild(rightKneeNode)
		leftKneeNode.attachChild(leftAnkleNode)
		rightKneeNode.attachChild(rightAnkleNode)
		leftAnkleNode.attachChild(leftFootNode)
		rightAnkleNode.attachChild(rightFootNode)

		// Attach tracker nodes for tracker offsets
		neckNode.attachChild(trackerHeadNode)
		neckNode.attachChild(trackerChestNode)
		hipNode.attachChild(trackerHipNode)
		leftKneeNode.attachChild(trackerLeftKneeNode)
		rightKneeNode.attachChild(trackerRightKneeNode)
		leftFootNode.attachChild(trackerLeftFootNode)
		rightFootNode.attachChild(trackerRightFootNode)

		// Attach arms
		assembleSkeletonArms(false)
	}

	/**
	 * Dynamically assembles the arms of the skeleton
	 *
	 * @param reset disassemble before reassembling
	 */
	@ThreadSafe
	fun assembleSkeletonArms(reset: Boolean) {
		if (reset) {
			for (node in armNodes) {
				node.detachWithChildren()
			}
		}

		// Assemble skeleton arms
		neckNode.attachChild(leftShoulderHeadNode)
		neckNode.attachChild(rightShoulderHeadNode)
		leftShoulderHeadNode.attachChild(leftShoulderTailNode)
		rightShoulderHeadNode.attachChild(rightShoulderTailNode)
		if (isTrackingLeftArmFromController) {
			leftWristNode.attachChild(leftElbowNode)
			leftHandNode.attachChild(leftWristNode)
		} else {
			leftShoulderTailNode.attachChild(leftElbowNode)
			leftElbowNode.attachChild(leftWristNode)
			leftWristNode.attachChild(leftHandNode)
		}
		if (isTrackingRightArmFromController) {
			rightWristNode.attachChild(rightElbowNode)
			rightHandNode.attachChild(rightWristNode)
		} else {
			rightShoulderTailNode.attachChild(rightElbowNode)
			rightElbowNode.attachChild(rightWristNode)
			rightWristNode.attachChild(rightHandNode)
		}

		// Attach tracker nodes for tracker offsets
		leftElbowNode.attachChild(trackerLeftElbowNode)
		rightElbowNode.attachChild(trackerRightElbowNode)
		leftHandNode.attachChild(trackerLeftHandNode)
		rightHandNode.attachChild(trackerRightHandNode)
	}

	/**
	 * Rebuilds allBoneInfo and shareableBoneInfo
	 */
	private fun resetBones() {
		allBoneInfo.clear()
		shareableBoneInfo.clear()

		// Create all bones and add to allBoneInfo
		for (boneType in BoneType.values) allBoneInfo.add(BoneInfo(boneType, getTailNodeOfBone(boneType)))

		// Add shareable bones to shareableBoneInfo
		// Head
		shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.HEAD))
		shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.NECK))

		// Spine and legs
		if (hasSpineTracker || hasLeftLegTracker || hasRightLegTracker || sendAllBones) {
			// Spine
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.CHEST))
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.WAIST))
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.HIP))

			// Left leg
			if (hasLeftLegTracker || sendAllBones) {
				if (sendAllBones) {
					// don't send currently
					shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_HIP))
				}
				shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_UPPER_LEG))
				shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_LOWER_LEG))
				if (leftFootTracker != null || sendAllBones) {
					shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_FOOT))
				}
			}

			// Right leg
			if (hasRightLegTracker || sendAllBones) {
				if (sendAllBones) {
					// don't send currently
					shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_HIP))
				}
				shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_UPPER_LEG))
				shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_LOWER_LEG))
				if (rightFootTracker != null || sendAllBones) {
					shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_FOOT))
				}
			}
		}

		// TODO: Expose more bones
		// TODO: Handle arms fromHmd and fromControllers

		// Left arm
		if ((hasLeftArmTracker || leftShoulderTracker != null) && sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_SHOULDER))
		}
		if (hasLeftArmTracker || sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_UPPER_ARM))
		}
		if (hasLeftArmTracker && sendAllBones) {
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_LOWER_ARM))
		}
		if ((hasLeftArmTracker || leftHandTracker != null) && sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_HAND))
		}

		// Right arm
		if ((hasRightArmTracker || rightShoulderTracker != null) && sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_SHOULDER))
		}
		if (hasRightArmTracker || sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_UPPER_ARM))
		}
		if (hasRightArmTracker && sendAllBones) {
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_LOWER_ARM))
		}
		if (hasRightArmTracker && rightHandTracker != null && sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_HAND))
		}
	}

	/**
	 * Set input trackers from a list
	 */
	fun setTrackersFromList(trackers: List<Tracker>) {
		headTracker = getTrackerForSkeleton(trackers, TrackerPosition.HEAD)
		neckTracker = getTrackerForSkeleton(trackers, TrackerPosition.NECK)
		upperChestTracker = getTrackerForSkeleton(trackers, TrackerPosition.UPPER_CHEST)
		chestTracker = getTrackerForSkeleton(trackers, TrackerPosition.CHEST)
		waistTracker = getTrackerForSkeleton(trackers, TrackerPosition.WAIST)
		hipTracker = getTrackerForSkeleton(trackers, TrackerPosition.HIP)
		leftUpperLegTracker = getTrackerForSkeleton(trackers, TrackerPosition.LEFT_UPPER_LEG)
		leftLowerLegTracker = getTrackerForSkeleton(trackers, TrackerPosition.LEFT_LOWER_LEG)
		leftFootTracker = getTrackerForSkeleton(trackers, TrackerPosition.LEFT_FOOT)
		rightUpperLegTracker = getTrackerForSkeleton(trackers, TrackerPosition.RIGHT_UPPER_LEG)
		rightLowerLegTracker = getTrackerForSkeleton(trackers, TrackerPosition.RIGHT_LOWER_LEG)
		rightFootTracker = getTrackerForSkeleton(trackers, TrackerPosition.RIGHT_FOOT)
		leftLowerArmTracker = getTrackerForSkeleton(trackers, TrackerPosition.LEFT_LOWER_ARM)
		rightLowerArmTracker = getTrackerForSkeleton(trackers, TrackerPosition.RIGHT_LOWER_ARM)
		leftUpperArmTracker = getTrackerForSkeleton(trackers, TrackerPosition.LEFT_UPPER_ARM)
		rightUpperArmTracker = getTrackerForSkeleton(trackers, TrackerPosition.RIGHT_UPPER_ARM)
		leftHandTracker = getTrackerForSkeleton(trackers, TrackerPosition.LEFT_HAND)
		rightHandTracker = getTrackerForSkeleton(trackers, TrackerPosition.RIGHT_HAND)
		leftShoulderTracker = getTrackerForSkeleton(trackers, TrackerPosition.LEFT_SHOULDER)
		rightShoulderTracker = getTrackerForSkeleton(trackers, TrackerPosition.RIGHT_SHOULDER)

		// Check for specific conditions and store them in booleans.
		hasSpineTracker = upperChestTracker != null || chestTracker != null || waistTracker != null || hipTracker != null
		hasKneeTrackers = leftUpperLegTracker != null && rightUpperLegTracker != null
		hasLeftLegTracker = leftUpperLegTracker != null || leftLowerLegTracker != null || leftFootTracker != null
		hasRightLegTracker = rightUpperLegTracker != null || rightLowerLegTracker != null || rightFootTracker != null
		hasLeftFootTracker = leftFootTracker != null
		hasRightFootTracker = rightFootTracker != null
		hasLeftArmTracker = leftLowerArmTracker != null || leftUpperArmTracker != null
		hasRightArmTracker = rightLowerArmTracker != null || rightUpperArmTracker != null

		// Rebuilds the arm skeleton nodes attachments
		assembleSkeletonArms(true)

		// Refresh headShift
		humanPoseManager.computeNodeOffset(BoneType.HEAD)

		// Refresh node offsets for arms
		computeDependentArmOffsets()

		// Rebuild the bone list
		resetBones()

		// Update tap detection's trackers
		tapDetectionManager.updateConfig(trackers)
	}

	/**
	 * Set output trackers from list
	 */
	private fun setComputedTrackers(trackers: List<Tracker>) {
		for (t in trackers) {
			setComputedTracker(t)
		}
	}

	/**
	 * Set output tracker
	 */
	private fun setComputedTracker(tracker: Tracker) {
		when (tracker.trackerPosition) {
			TrackerPosition.HEAD -> computedHeadTracker = tracker
			TrackerPosition.UPPER_CHEST -> computedChestTracker = tracker
			TrackerPosition.HIP -> computedHipTracker = tracker
			TrackerPosition.LEFT_UPPER_LEG -> computedLeftKneeTracker = tracker
			TrackerPosition.LEFT_FOOT -> computedLeftFootTracker = tracker
			TrackerPosition.RIGHT_UPPER_LEG -> computedRightKneeTracker = tracker
			TrackerPosition.RIGHT_FOOT -> computedRightFootTracker = tracker
			TrackerPosition.LEFT_UPPER_ARM -> computedLeftElbowTracker = tracker
			TrackerPosition.RIGHT_UPPER_ARM -> computedRightElbowTracker = tracker
			TrackerPosition.LEFT_HAND -> computedLeftHandTracker = tracker
			TrackerPosition.RIGHT_HAND -> computedRightHandTracker = tracker
			else -> {}
		}
	}

	/**
	 * Get output tracker from TrackerRole
	 */
	fun getComputedTracker(trackerRole: TrackerRole): Tracker {
		return when (trackerRole) {
			TrackerRole.HEAD -> computedHeadTracker!!
			TrackerRole.CHEST -> computedChestTracker!!
			TrackerRole.WAIST -> computedHipTracker!!
			TrackerRole.LEFT_KNEE -> computedLeftKneeTracker!!
			TrackerRole.LEFT_FOOT -> computedLeftFootTracker!!
			TrackerRole.RIGHT_KNEE -> computedRightKneeTracker!!
			TrackerRole.RIGHT_FOOT -> computedRightFootTracker!!
			TrackerRole.LEFT_ELBOW -> computedLeftElbowTracker!!
			TrackerRole.RIGHT_ELBOW -> computedRightElbowTracker!!
			TrackerRole.LEFT_HAND -> computedLeftHandTracker!!
			TrackerRole.RIGHT_HAND -> computedRightHandTracker!!
			else -> throw IllegalArgumentException()
		}
	}

	/**
	 * Updates the pose from tracker positions
	 */
	@VRServerThread
	fun updatePose() {
		tapDetectionManager.update()
		updateLocalTransforms()
		updateNodes()
		updateComputedTrackers()
		// Don't run legtweaks if the tracking is paused
		if (!pauseTracking) tweakLegPos()
		localizer.update()
		viveEmulation.update()
	}

	/**
	 * Update all the nodes by updating the roots
	 */
	@ThreadSafe
	fun updateNodes() {
		hmdNode.update()
		if (isTrackingLeftArmFromController) {
			leftHandNode.update()
		}
		if (isTrackingRightArmFromController) {
			rightHandNode.update()
		}
	}

	/**
	 * Run legtweaks
	 */
	private fun tweakLegPos() {
		legTweaks.tweakLegs()
	}

	/**
	 * Update the nodes transforms from the trackers
	 */
	private fun updateLocalTransforms() {
		// HMD, head and neck
		var headRot = IDENTITY
		if (headTracker != null) {
			if (headTracker!!.hasPosition) hmdNode.localTransform.translation = headTracker!!.position
			headRot = headTracker!!.getRotation()
			hmdNode.localTransform.rotation = headRot
			trackerHeadNode.localTransform.rotation = headRot
			if (neckTracker != null) headRot = neckTracker!!.getRotation()
			headNode.localTransform.rotation = headRot
		} else {
			if (!localizer.getEnabled()) hmdNode.localTransform.translation = NULL
			if (neckTracker != null) {
				headRot = neckTracker!!.getRotation()
			} else if (hasSpineTracker) {
				headRot = getFirstAvailableTracker(
					upperChestTracker,
					chestTracker,
					waistTracker,
					hipTracker
				)!!.getRotation()
			}
			hmdNode.localTransform.rotation = headRot
			trackerHeadNode.localTransform.rotation = headRot
			headNode.localTransform.rotation = headRot
		}

		// Only update the head and neck as they are relevant to the position
		// of the computed trackers for VR, the rest should be frozen
		if (pauseTracking) return

		// Spine
		if (hasSpineTracker) {
			// Upper chest
			var torsoRot = getFirstAvailableTracker(upperChestTracker, chestTracker, waistTracker, hipTracker)!!.getRotation()
			neckNode.localTransform.rotation = torsoRot
			trackerChestNode.localTransform.rotation = torsoRot

			// Chest
			torsoRot = getFirstAvailableTracker(chestTracker, upperChestTracker, waistTracker, hipTracker)!!.getRotation()
			upperChestNode.localTransform.rotation = torsoRot

			// Waist
			torsoRot = getFirstAvailableTracker(waistTracker, chestTracker, upperChestTracker, hipTracker)!!.getRotation()
			chestNode.localTransform.rotation = torsoRot

			// Hip
			torsoRot = getFirstAvailableTracker(hipTracker, waistTracker, chestTracker, upperChestTracker)!!.getRotation()
			waistNode.localTransform.rotation = torsoRot
			hipNode.localTransform.rotation = torsoRot
			trackerHipNode.localTransform.rotation = torsoRot
		} else if (headTracker != null) {
			// Align with last tracker's yaw (HMD or neck)
			val yawQuat = headRot.project(POS_Y).unit()
			neckNode.localTransform.rotation = yawQuat
			trackerChestNode.localTransform.rotation = yawQuat
			upperChestNode.localTransform.rotation = yawQuat
			chestNode.localTransform.rotation = yawQuat
			waistNode.localTransform.rotation = yawQuat
			hipNode.localTransform.rotation = yawQuat
			trackerHipNode.localTransform.rotation = yawQuat
		}

		// Left Leg
		// Get rotation
		val leftUpperLeg = if (leftUpperLegTracker != null) {
			leftUpperLegTracker!!.getRotation()
		} else {
			// Align with the hip's yaw
			hipNode.localTransform.rotation.project(POS_Y).unit()
		}
		var leftLowerLeg: Quaternion
		leftLowerLeg = if (leftLowerLegTracker != null) {
			leftLowerLegTracker!!.getRotation()
		} else {
			// Align with the upper leg's yaw
			leftUpperLeg.project(POS_Y).unit()
		}
		leftHipNode.localTransform.rotation = leftUpperLeg
		trackerLeftKneeNode.localTransform.rotation = leftUpperLeg
		leftKneeNode.localTransform.rotation = leftLowerLeg
		if (leftFootTracker != null) {
			leftLowerLeg = leftFootTracker!!.getRotation()
		}
		leftAnkleNode.localTransform.rotation = leftLowerLeg
		leftFootNode.localTransform.rotation = leftLowerLeg
		trackerLeftFootNode.localTransform.rotation = leftLowerLeg

		// Extended left knee
		if (leftUpperLegTracker != null && leftLowerLegTracker != null && extendedKneeModel) {
			// Averages the knee's rotation with the local ankle's
			// pitch and roll and apply to the tracker node.
			val leftHipRot = leftHipNode.localTransform.rotation
			val leftKneeRot = leftKneeNode.localTransform.rotation
			val extendedRot = extendedKneeYawRoll(leftHipRot, leftKneeRot)

			leftHipNode
				.localTransform
				.rotation = leftHipRot.interpR(extendedRot, kneeAnkleAveraging)
			trackerLeftKneeNode
				.localTransform
				.rotation = leftHipRot.interpR(extendedRot, kneeTrackerAnkleAveraging)
		}

		// Right Leg
		// Get rotations
		val rightUpperLeg = if (rightUpperLegTracker != null) {
			rightUpperLegTracker!!.getRotation()
		} else {
			// Align with the hip's yaw
			hipNode.localTransform.rotation.project(POS_Y).unit()
		}
		var rightLowerLeg: Quaternion
		rightLowerLeg = if (rightLowerLegTracker != null) {
			rightLowerLegTracker!!.getRotation()
		} else {
			// Align with the upper leg's yaw
			rightUpperLeg.project(POS_Y).unit()
		}
		rightHipNode.localTransform.rotation = rightUpperLeg
		trackerRightKneeNode.localTransform.rotation = rightUpperLeg
		rightKneeNode.localTransform.rotation = rightLowerLeg
		if (rightFootTracker != null) rightLowerLeg = rightFootTracker!!.getRotation()
		rightAnkleNode.localTransform.rotation = rightLowerLeg
		rightFootNode.localTransform.rotation = rightLowerLeg
		trackerRightFootNode.localTransform.rotation = rightLowerLeg

		// Extended right knee
		if (rightUpperLegTracker != null && rightLowerLegTracker != null && extendedKneeModel) {
			// Averages the knee's rotation with the local ankle's
			// pitch and roll and apply to the tracker node.
			val rightHipRot = rightHipNode.localTransform.rotation
			val rightKneeRot = rightKneeNode.localTransform.rotation
			val extendedRot = extendedKneeYawRoll(rightHipRot, rightKneeRot)

			rightHipNode
				.localTransform
				.rotation = rightHipRot.interpR(extendedRot, kneeAnkleAveraging)
			trackerRightKneeNode
				.localTransform
				.rotation = rightHipRot.interpR(extendedRot, kneeTrackerAnkleAveraging)
		}

		// Extended spine
		if (extendedSpineModel && hasSpineTracker) {
			// Tries to guess missing lower spine trackers by interpolating
			// rotations
			if (waistTracker == null) {
				if ((upperChestTracker != null || chestTracker != null) && hipTracker != null) {
					// Calculates waist from chest + hip
					var hipRot = hipTracker!!.getRotation()
					var chestRot = getFirstAvailableTracker(upperChestTracker, chestTracker)!!.getRotation()

					// Get the rotation relative to where we expect the hip to be
					if (chestRot.times(FORWARD_QUATERNION).dot(hipRot) < 0.0f) {
						hipRot = hipRot.unaryMinus()
					}

					// Interpolate between the chest and the hip
					chestRot = chestRot.interpQ(hipRot, waistFromChestHipAveraging)
					chestNode.localTransform.rotation = chestRot
				} else if ((upperChestTracker != null || chestTracker != null) && hasKneeTrackers) {
					// Calculates waist from chest + legs
					var leftHipRot = leftHipNode.localTransform.rotation
					var rightHipRot = rightHipNode.localTransform.rotation
					var chestRot = getFirstAvailableTracker(upperChestTracker, chestTracker)!!.getRotation()

					// Get the rotation relative to where we expect the
					// upper legs to be
					val expectedUpperLegsRot = chestRot.times(FORWARD_QUATERNION)
					if (expectedUpperLegsRot.dot(leftHipRot) < 0.0f) {
						leftHipRot = leftHipRot.unaryMinus()
					}
					if (expectedUpperLegsRot.dot(rightHipRot) < 0.0f) {
						rightHipRot = rightHipRot.unaryMinus()
					}

					// Interpolate between the pelvis, averaged from the legs, and the chest
					chestRot = chestRot.interpQ(leftHipRot.lerpQ(rightHipRot, 0.5f), waistFromChestLegsAveraging).unit()
					chestNode.localTransform.rotation = chestRot
				}
			}
			if (hipTracker == null && hasKneeTrackers) {
				if (waistTracker != null) {
					// Calculates hip from waist + legs
					var leftHipRot = leftHipNode.localTransform.rotation
					var rightHipRot = rightHipNode.localTransform.rotation
					var waistRot = waistTracker!!.getRotation()

					// Get the rotation relative to where we expect the
					// upper legs to be
					val expectedUpperLegsRot = waistRot.times(FORWARD_QUATERNION)
					if (expectedUpperLegsRot.dot(leftHipRot) < 0.0f) {
						leftHipRot = leftHipRot.unaryMinus()
					}
					if (expectedUpperLegsRot.dot(rightHipRot) < 0.0f) {
						rightHipRot = rightHipRot.unaryMinus()
					}

					// Interpolate between the pelvis, averaged from the legs,
					// and the chest
					waistRot = waistRot
						.interpQ(leftHipRot.lerpQ(rightHipRot, 0.5f), hipFromWaistLegsAveraging)
						.unit()
					waistNode.localTransform.rotation = waistRot
					hipNode.localTransform.rotation = waistRot
					trackerHipNode.localTransform.rotation = waistRot
				} else if (upperChestTracker != null || chestTracker != null) {
					// Calculates hip from chest + legs
					var leftHipRot = leftHipNode.localTransform.rotation
					var rightHipRot = rightHipNode.localTransform.rotation
					var chestRot = getFirstAvailableTracker(upperChestTracker, chestTracker)!!.getRotation()

					// Get the rotation relative to where we expect the upper legs to be
					val expectedUpperLegsRot = chestRot.times(FORWARD_QUATERNION)
					if (expectedUpperLegsRot.dot(leftHipRot) < 0.0f) {
						leftHipRot = leftHipRot.unaryMinus()
					}
					if (expectedUpperLegsRot.dot(rightHipRot) < 0.0f) {
						rightHipRot = rightHipRot.unaryMinus()
					}

					// Interpolate between the pelvis, averaged from the legs, and the chest
					chestRot = chestRot.interpQ(leftHipRot.lerpQ(rightHipRot, 0.5f), hipFromChestLegsAveraging).unit()
					waistNode.localTransform.rotation = chestRot
					hipNode.localTransform.rotation = chestRot
					trackerHipNode.localTransform.rotation = chestRot
				}
			}
		}

		// Extended pelvis
		if (extendedPelvisModel && hasKneeTrackers && hipTracker == null) {
			val leftHipRot = leftHipNode.localTransform.rotation
			val rightHipRot = rightHipNode.localTransform.rotation
			val hipRot = hipNode.localTransform.rotation
			val extendedPelvisRot = extendedPelvisYawRoll(leftHipRot, rightHipRot, hipRot)
			val slerp = hipRot.interpR(extendedPelvisRot, hipLegsAveraging)

			hipNode.localTransform.rotation = slerp
			trackerHipNode.localTransform.rotation = slerp
		}

		// Left arm
		if (isTrackingLeftArmFromController) { // From controller
			leftHandNode.localTransform.translation = leftHandTracker!!.position
			leftHandNode.localTransform.rotation = leftHandTracker!!.getRotation()
			val lowerArm = getFirstAvailableTracker(leftLowerArmTracker, leftUpperArmTracker)
			if (lowerArm != null) {
				leftWristNode.localTransform.rotation = lowerArm.getRotation()
				val leftArmRot = getFirstAvailableTracker(leftUpperArmTracker, leftLowerArmTracker)!!.getRotation()

				leftElbowNode.localTransform.rotation = leftArmRot
				trackerLeftElbowNode.localTransform.rotation = leftArmRot
			}
		} else { // From HMD
			val leftShoulderRot: Quaternion
			leftShoulderRot =
				if (leftShoulderTracker != null) leftShoulderTracker!!.getRotation() else neckNode.localTransform.rotation
			leftShoulderHeadNode.localTransform.rotation = leftShoulderRot
			var leftArmRot: Quaternion
			if (leftUpperArmTracker != null || leftLowerArmTracker != null) {
				leftArmRot = getFirstAvailableTracker(leftUpperArmTracker, leftLowerArmTracker)!!.getRotation()
				leftShoulderTailNode.localTransform.rotation = leftArmRot
				trackerLeftElbowNode.localTransform.rotation = leftArmRot

				leftArmRot = getFirstAvailableTracker(leftLowerArmTracker, leftUpperArmTracker)!!.getRotation()
				leftElbowNode.localTransform.rotation = leftArmRot
			} else {
				leftArmRot = neckNode.localTransform.rotation
				leftShoulderTailNode.localTransform.rotation = leftArmRot
				trackerLeftElbowNode.localTransform.rotation = leftArmRot
				leftElbowNode.localTransform.rotation = leftArmRot
			}
			if (leftHandTracker != null) leftArmRot = leftHandTracker!!.getRotation()
			leftWristNode.localTransform.rotation = leftArmRot
			leftHandNode.localTransform.rotation = leftArmRot
			trackerLeftHandNode.localTransform.rotation = leftArmRot
		}

		// Right arm
		if (isTrackingRightArmFromController) { // From controller
			rightHandNode.localTransform.translation = rightHandTracker!!.position
			rightHandNode.localTransform.rotation = rightHandTracker!!.getRotation()
			val lowerArm = getFirstAvailableTracker(rightLowerArmTracker, rightUpperArmTracker)
			if (lowerArm != null) {
				rightWristNode.localTransform.rotation = lowerArm.getRotation()
				val rightArmRot = getFirstAvailableTracker(rightUpperArmTracker, rightLowerArmTracker)!!.getRotation()

				rightElbowNode.localTransform.rotation = rightArmRot
				trackerRightElbowNode.localTransform.rotation = rightArmRot
			}
		} else { // From HMD
			val rightShoulderRot: Quaternion
			rightShoulderRot =
				if (rightShoulderTracker != null) rightShoulderTracker!!.getRotation() else neckNode.localTransform.rotation
			rightShoulderHeadNode.localTransform.rotation = rightShoulderRot
			var rightArmRot: Quaternion
			if (rightUpperArmTracker != null || rightLowerArmTracker != null) {
				rightArmRot = getFirstAvailableTracker(rightUpperArmTracker, rightLowerArmTracker)!!.getRotation()
				rightShoulderTailNode.localTransform.rotation = rightArmRot
				trackerRightElbowNode.localTransform.rotation = rightArmRot

				rightArmRot = getFirstAvailableTracker(rightLowerArmTracker, rightUpperArmTracker)!!.getRotation()
				rightElbowNode.localTransform.rotation = rightArmRot
			} else {
				rightArmRot = neckNode.localTransform.rotation

				rightShoulderTailNode.localTransform.rotation = rightArmRot
				trackerRightElbowNode.localTransform.rotation = rightArmRot
				rightElbowNode.localTransform.rotation = rightArmRot
			}
			if (rightHandTracker != null) rightArmRot = rightHandTracker!!.getRotation()
			rightWristNode.localTransform.rotation = rightArmRot
			rightHandNode.localTransform.rotation = rightArmRot
			trackerRightHandNode.localTransform.rotation = rightArmRot
		}
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
		// R = InverseKnee * Ankle
		// C = Quaternion(R.w, -R.x, 0, 0)
		// Knee = Knee * R * C
		// normalize(Knee)
		val r = knee.inv().times(ankle)
		val c = Quaternion(r.w, -r.x, 0f, 0f)
		return knee.times(r).times(c).unit()
	}

	/**
	 * Rotates the first Quaternion to match its yaw and roll to the rotation of
	 * the average of the second and third quaternions.
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
		// Get the knees' rotation relative to where we expect them to be.
		// The angle between your knees and hip can be over 180 degrees...
		var leftKneeRot = leftKnee
		var rightKneeRot = rightKnee

		val kneeRot = hip.times(FORWARD_QUATERNION)
		if (kneeRot.dot(leftKneeRot) < 0.0f) {
			leftKneeRot = leftKneeRot.unaryMinus()
		}
		if (kneeRot.dot(rightKneeRot) < 0.0f) {
			rightKneeRot = rightKneeRot.unaryMinus()
		}

		// R = InverseHip * (LeftLeft + RightLeg)
		// C = Quaternion(R.w, -R.x, 0, 0)
		// Pelvis = Hip * R * C
		// normalize(Pelvis)
		val r = hip.inv().times(leftKneeRot.plus(rightKneeRot))
		val c = Quaternion(r.w, -r.x, 0f, 0f)
		return hip.times(r).times(c).unit()
	}

	// Update the output trackers
	private fun updateComputedTrackers() {
		computedHeadTracker!!.position = trackerHeadNode.worldTransform.translation
		computedHeadTracker!!.setRotation(trackerHeadNode.worldTransform.rotation)
		computedHeadTracker!!.dataTick()

		computedChestTracker!!.position = trackerChestNode.worldTransform.translation
		computedChestTracker!!.setRotation(trackerChestNode.worldTransform.rotation)
		computedChestTracker!!.dataTick()

		computedHipTracker!!.position = trackerHipNode.worldTransform.translation
		computedHipTracker!!.setRotation(trackerHipNode.worldTransform.rotation)
		computedHipTracker!!.dataTick()

		computedLeftKneeTracker!!.position = trackerLeftKneeNode.worldTransform.translation
		computedLeftKneeTracker!!.setRotation(trackerLeftKneeNode.worldTransform.rotation)
		computedLeftKneeTracker!!.dataTick()

		computedLeftFootTracker!!.position = trackerLeftFootNode.worldTransform.translation
		computedLeftFootTracker!!.setRotation(trackerLeftFootNode.worldTransform.rotation)
		computedLeftFootTracker!!.dataTick()

		computedRightKneeTracker!!.position = trackerRightKneeNode.worldTransform.translation
		computedRightKneeTracker!!.setRotation(trackerRightKneeNode.worldTransform.rotation)
		computedRightKneeTracker!!.dataTick()

		computedRightFootTracker!!.position = trackerRightFootNode.worldTransform.translation
		computedRightFootTracker!!.setRotation(trackerRightFootNode.worldTransform.rotation)
		computedRightFootTracker!!.dataTick()

		computedLeftElbowTracker!!.position = trackerLeftElbowNode.worldTransform.translation
		computedLeftElbowTracker!!.setRotation(trackerLeftElbowNode.worldTransform.rotation)
		computedLeftElbowTracker!!.dataTick()

		computedRightElbowTracker!!.position = trackerRightElbowNode.worldTransform.translation
		computedRightElbowTracker!!.setRotation(trackerRightElbowNode.worldTransform.rotation)
		computedRightElbowTracker!!.dataTick()

		computedLeftHandTracker!!.position = trackerLeftHandNode.worldTransform.translation
		computedLeftHandTracker!!.setRotation(trackerLeftHandNode.worldTransform.rotation)
		computedLeftHandTracker!!.dataTick()

		computedRightHandTracker!!.position = trackerRightHandNode.worldTransform.translation
		computedRightHandTracker!!.setRotation(trackerRightHandNode.worldTransform.rotation)
		computedRightHandTracker!!.dataTick()
	}

	// Skeleton Config toggles
	fun updateToggleState(configToggle: SkeletonConfigToggles?, newValue: Boolean) {
		if (configToggle == null) {
			return
		}
		when (configToggle) {
			SkeletonConfigToggles.EXTENDED_SPINE_MODEL -> extendedSpineModel = newValue
			SkeletonConfigToggles.EXTENDED_PELVIS_MODEL -> extendedPelvisModel = newValue
			SkeletonConfigToggles.EXTENDED_KNEE_MODEL -> extendedKneeModel = newValue
			SkeletonConfigToggles.FORCE_ARMS_FROM_HMD -> {
				forceArmsFromHMD = newValue
				assembleSkeletonArms(true) // Rebuilds the arm skeleton nodes attachments
				computeDependentArmOffsets() // Refresh node offsets for arms
			}
			SkeletonConfigToggles.SKATING_CORRECTION -> legTweaks.setSkatingReductionEnabled(newValue)
			SkeletonConfigToggles.FLOOR_CLIP -> legTweaks.setFloorclipEnabled(newValue)
			SkeletonConfigToggles.VIVE_EMULATION -> viveEmulation.enabled = newValue
			SkeletonConfigToggles.TOE_SNAP -> legTweaks.toeSnapEnabled = newValue
			SkeletonConfigToggles.FOOT_PLANT -> legTweaks.footPlantEnabled = newValue
			SkeletonConfigToggles.SELF_LOCALIZATION -> localizer.setEnabled(newValue)
		}
	}

	// Skeleton Config ratios
	fun updateValueState(configValue: SkeletonConfigValues?, newValue: Float) {
		if (configValue == null) {
			return
		}
		when (configValue) {
			SkeletonConfigValues.WAIST_FROM_CHEST_HIP_AVERAGING -> waistFromChestHipAveraging = newValue
			SkeletonConfigValues.WAIST_FROM_CHEST_LEGS_AVERAGING -> waistFromChestLegsAveraging = newValue
			SkeletonConfigValues.HIP_FROM_CHEST_LEGS_AVERAGING -> hipFromChestLegsAveraging = newValue
			SkeletonConfigValues.HIP_FROM_WAIST_LEGS_AVERAGING -> hipFromWaistLegsAveraging = newValue
			SkeletonConfigValues.HIP_LEGS_AVERAGING -> hipLegsAveraging = newValue
			SkeletonConfigValues.KNEE_TRACKER_ANKLE_AVERAGING -> kneeTrackerAnkleAveraging = newValue
			SkeletonConfigValues.KNEE_ANKLE_AVERAGING -> kneeAnkleAveraging = newValue
		}
	}

	// Skeleton Config bone lengths
	fun updateNodeOffset(nodeOffset: BoneType, offset: Vector3) {
		when (nodeOffset) {
			BoneType.HEAD -> {
				if (headTracker != null && headTracker!!.hasPosition) {
					headNode.localTransform.translation = offset
				} else {
					headNode.localTransform.translation = NULL
				}
			}
			BoneType.NECK -> neckNode.localTransform.translation = offset
			BoneType.UPPER_CHEST -> upperChestNode.localTransform.translation = offset
			BoneType.CHEST_TRACKER -> trackerChestNode.localTransform.translation = offset
			BoneType.CHEST -> chestNode.localTransform.translation = offset
			BoneType.WAIST -> waistNode.localTransform.translation = offset
			BoneType.HIP -> hipNode.localTransform.translation = offset
			BoneType.HIP_TRACKER -> trackerHipNode.localTransform.translation = offset
			BoneType.LEFT_HIP -> leftHipNode.localTransform.translation = offset
			BoneType.RIGHT_HIP -> rightHipNode.localTransform.translation = offset
			BoneType.LEFT_UPPER_LEG -> leftKneeNode.localTransform.translation = offset
			BoneType.RIGHT_UPPER_LEG -> rightKneeNode.localTransform.translation = offset
			BoneType.LEFT_KNEE_TRACKER -> trackerLeftKneeNode.localTransform.translation = offset
			BoneType.RIGHT_KNEE_TRACKER -> trackerRightKneeNode.localTransform.translation = offset
			BoneType.LEFT_LOWER_LEG -> leftAnkleNode.localTransform.translation = offset
			BoneType.RIGHT_LOWER_LEG -> rightAnkleNode.localTransform.translation = offset
			BoneType.LEFT_FOOT -> leftFootNode.localTransform.translation = offset
			BoneType.RIGHT_FOOT -> rightFootNode.localTransform.translation = offset
			BoneType.LEFT_FOOT_TRACKER -> trackerLeftFootNode.localTransform.translation = offset
			BoneType.RIGHT_FOOT_TRACKER -> trackerRightFootNode.localTransform.translation = offset
			BoneType.LEFT_SHOULDER -> leftShoulderTailNode.localTransform.translation = offset
			BoneType.RIGHT_SHOULDER -> rightShoulderTailNode.localTransform.translation = offset
			BoneType.LEFT_UPPER_ARM -> {
				if (isTrackingLeftArmFromController) {
					leftElbowNode.localTransform.translation = NULL
				} else {
					leftElbowNode.localTransform.translation = offset
				}
			}
			BoneType.RIGHT_UPPER_ARM -> {
				if (isTrackingRightArmFromController) {
					rightElbowNode.localTransform.translation = NULL
				} else {
					rightElbowNode.localTransform.translation = offset
				}
			}
			BoneType.LEFT_LOWER_ARM -> {
				if (isTrackingLeftArmFromController) {
					leftElbowNode.localTransform.translation = offset
				} else {
					leftWristNode.localTransform.translation = offset.unaryMinus()
				}
			}
			BoneType.RIGHT_LOWER_ARM -> {
				if (isTrackingRightArmFromController) {
					rightElbowNode.localTransform.translation = offset
				} else {
					rightWristNode.localTransform.translation = offset.unaryMinus()
				}
			}
			BoneType.LEFT_ELBOW_TRACKER ->
				trackerLeftElbowNode
					.localTransform
					.translation = offset

			BoneType.RIGHT_ELBOW_TRACKER ->
				trackerRightElbowNode
					.localTransform
					.translation = offset
			BoneType.LEFT_HAND -> {
				if (isTrackingLeftArmFromController) {
					leftWristNode.localTransform.translation = offset.unaryMinus()
				} else {
					leftHandNode.localTransform.translation = offset
				}
			}
			BoneType.RIGHT_HAND -> {
				if (isTrackingRightArmFromController) {
					rightWristNode.localTransform.translation = offset.unaryMinus()
				} else {
					rightHandNode.localTransform.translation = offset
				}
			}
			else -> throw IllegalArgumentException("Used unsupported offset in HumanSkeleton")
		}

		for (bone in allBoneInfo) {
			bone.updateLength()
		}
	}

	private fun computeDependentArmOffsets() {
		humanPoseManager.computeNodeOffset(BoneType.LEFT_UPPER_ARM)
		humanPoseManager.computeNodeOffset(BoneType.RIGHT_UPPER_ARM)
		humanPoseManager.computeNodeOffset(BoneType.LEFT_LOWER_ARM)
		humanPoseManager.computeNodeOffset(BoneType.RIGHT_LOWER_ARM)
		humanPoseManager.computeNodeOffset(BoneType.LEFT_HAND)
		humanPoseManager.computeNodeOffset(BoneType.RIGHT_HAND)
	}

	fun getTailNodeOfBone(bone: BoneType?): TransformNode? {
		return if (bone == null) {
			null
		} else {
			when (bone) {
				BoneType.HMD, BoneType.HEAD -> headNode
				BoneType.HEAD_TRACKER -> trackerHeadNode
				BoneType.NECK -> neckNode
				BoneType.UPPER_CHEST -> upperChestNode
				BoneType.CHEST_TRACKER -> trackerChestNode
				BoneType.CHEST -> chestNode
				BoneType.WAIST -> waistNode
				BoneType.HIP -> hipNode
				BoneType.HIP_TRACKER -> trackerHipNode
				BoneType.LEFT_HIP -> leftHipNode
				BoneType.RIGHT_HIP -> rightHipNode
				BoneType.LEFT_UPPER_LEG -> leftKneeNode
				BoneType.RIGHT_UPPER_LEG -> rightKneeNode
				BoneType.RIGHT_KNEE_TRACKER -> trackerRightKneeNode
				BoneType.LEFT_KNEE_TRACKER -> trackerLeftKneeNode
				BoneType.LEFT_LOWER_LEG -> leftAnkleNode
				BoneType.RIGHT_LOWER_LEG -> rightAnkleNode
				BoneType.LEFT_FOOT -> leftFootNode
				BoneType.RIGHT_FOOT -> rightFootNode
				BoneType.LEFT_FOOT_TRACKER -> trackerLeftFootNode
				BoneType.RIGHT_FOOT_TRACKER -> trackerRightFootNode
				BoneType.LEFT_SHOULDER -> leftShoulderTailNode
				BoneType.RIGHT_SHOULDER -> rightShoulderTailNode
				BoneType.LEFT_UPPER_ARM -> leftElbowNode
				BoneType.RIGHT_UPPER_ARM -> rightElbowNode
				BoneType.LEFT_ELBOW_TRACKER -> trackerLeftElbowNode
				BoneType.RIGHT_ELBOW_TRACKER -> trackerRightElbowNode
				BoneType.LEFT_LOWER_ARM -> if (isTrackingLeftArmFromController) leftElbowNode else leftWristNode
				BoneType.RIGHT_LOWER_ARM -> if (isTrackingRightArmFromController) rightElbowNode else rightWristNode
				BoneType.LEFT_HAND -> if (isTrackingLeftArmFromController) leftWristNode else leftHandNode
				BoneType.RIGHT_HAND -> if (isTrackingRightArmFromController) rightWristNode else rightHandNode
				BoneType.LEFT_HAND_TRACKER -> trackerLeftHandNode
				BoneType.RIGHT_HAND_TRACKER -> trackerRightHandNode
			}
		}
	}

	fun getBoneInfoForBoneType(boneType: BoneType): BoneInfo? {
		for (bone in allBoneInfo) {
			if (bone.boneType == boneType) return bone
		}
		return null
	}

	val allNodes: Array<TransformNode>
		get() = arrayOf(
			hmdNode,
			headNode,
			trackerHeadNode,
			neckNode,
			upperChestNode,
			trackerChestNode,
			chestNode,
			waistNode,
			hipNode,
			trackerHipNode,
			leftHipNode,
			leftKneeNode,
			trackerLeftKneeNode,
			leftAnkleNode,
			leftFootNode,
			trackerLeftFootNode,
			rightHipNode,
			rightKneeNode,
			trackerRightKneeNode,
			rightAnkleNode,
			rightFootNode,
			trackerRightFootNode

		).copyInto(armNodes)
	private val armNodes: Array<TransformNode>
		get() = arrayOf(
			leftShoulderHeadNode,
			rightShoulderHeadNode,
			leftShoulderTailNode,
			rightShoulderTailNode,
			leftElbowNode,
			rightElbowNode,
			trackerLeftElbowNode,
			trackerRightElbowNode,
			leftWristNode,
			rightWristNode,
			leftHandNode,
			rightHandNode,
			trackerLeftHandNode,
			trackerRightHandNode
		)
	val hmdHeight: Float
		get() = if (headTracker != null && headTracker!!.hasPosition) headTracker!!.position.y else 0f
	val isTrackingLeftArmFromController: Boolean
		/**
		 * Runs checks to know if we should (and are) performing the tracking of the
		 * left arm from the controller.
		 *
		 * @return a bool telling us if we are tracking the left arm from the
		 * controller or not.
		 */
		get() = leftHandTracker != null && leftHandTracker!!.hasPosition && !forceArmsFromHMD
	val isTrackingRightArmFromController: Boolean
		/**
		 * Runs checks to know if we should (and are) performing the tracking of the
		 * right arm from the controller.
		 *
		 * @return a bool telling us if we are tracking the right arm from the
		 * controller or not.
		 */
		get() = rightHandTracker != null && rightHandTracker!!.hasPosition && !forceArmsFromHMD
	val localTrackers: List<Tracker?>
		get() = java.util.List
			.of(
				neckTracker,
				chestTracker,
				waistTracker,
				hipTracker,
				leftUpperLegTracker,
				leftLowerLegTracker,
				leftFootTracker,
				rightUpperLegTracker,
				rightLowerLegTracker,
				rightFootTracker,
				leftLowerArmTracker,
				rightLowerArmTracker,
				leftUpperArmTracker,
				rightUpperArmTracker,
				leftHandTracker,
				rightHandTracker,
				leftShoulderTracker,
				rightShoulderTracker
			)

	fun resetTrackersFull(resetSourceName: String?) {
		val trackersToReset = humanPoseManager.getTrackersToReset()

		// Resets all axis of the trackers with the HMD as reference.
		var referenceRotation = IDENTITY
		if (headTracker != null) {
			if (headTracker!!.needsReset) {
				headTracker!!.resetsHandler.resetFull(referenceRotation)
			} else {
				referenceRotation =
					headTracker!!.getRotation()
			}
		}
		for (tracker in trackersToReset) {
			if (tracker != null && tracker.needsReset) {
				tracker.resetsHandler.resetFull(referenceRotation)
			}
		}

		// Tell floorclip to reset its floor level on the next update
		// of the computed trackers
		if (!localizer.getEnabled()) {
			legTweaks.resetFloorLevel()
		}
		legTweaks.resetBuffer()
		localizer.reset()
		LogManager.info(String.format("[HumanSkeleton] Reset: full (%s)", resetSourceName))
	}

	@VRServerThread
	fun resetTrackersYaw(resetSourceName: String?) {
		val trackersToReset = humanPoseManager.getTrackersToReset()

		// Resets the yaw of the trackers with the head as reference.
		var referenceRotation = IDENTITY
		if (headTracker != null) {
			if (headTracker!!.needsReset) {
				headTracker!!.resetsHandler.resetYaw(referenceRotation)
			} else {
				referenceRotation =
					headTracker!!.getRotation()
			}
		}
		for (tracker in trackersToReset) {
			if (tracker != null && tracker.needsReset) {
				tracker.resetsHandler.resetYaw(referenceRotation)
			}
		}
		legTweaks.resetBuffer()
		LogManager.info(String.format("[HumanSkeleton] Reset: yaw (%s)", resetSourceName))
	}

	@VRServerThread
	fun resetTrackersMounting(resetSourceName: String?) {
		val trackersToReset = humanPoseManager.getTrackersToReset()

		// Resets the mounting rotation of the trackers with the HMD as
		// reference.
		var referenceRotation = IDENTITY
		if (headTracker != null) {
			if (headTracker!!.needsMounting) {
				headTracker!!.resetsHandler.resetMounting(referenceRotation)
			} else {
				referenceRotation =
					headTracker!!.getRotation()
			}
		}
		for (tracker in trackersToReset) {
			if (tracker != null && tracker.needsMounting) {
				tracker.resetsHandler.resetMounting(referenceRotation)
			}
		}
		legTweaks.resetBuffer()
		localizer.reset()
		LogManager.info(String.format("[HumanSkeleton] Reset: mounting (%s)", resetSourceName))
	}

	@VRServerThread
	fun clearTrackersMounting(resetSourceName: String?) {
		val trackersToReset = humanPoseManager.getTrackersToReset()
		if (headTracker != null && headTracker!!.needsMounting) {
			headTracker!!
				.resetsHandler
				.clearMounting()
		}
		for (tracker in trackersToReset) {
			if (tracker != null &&
				tracker.needsMounting
			) {
				tracker.resetsHandler.clearMounting()
			}
		}
		legTweaks.resetBuffer()
		LogManager.info(String.format("[HumanSkeleton] Clear: mounting (%s)", resetSourceName))
	}

	fun updateTapDetectionConfig() {
		tapDetectionManager.updateConfig(null)
	}

	fun updateLegTweaksConfig() {
		legTweaks.updateConfig()
	}

	// Does not save to config
	fun setLegTweaksStateTemp(
		skatingCorrection: Boolean,
		floorClip: Boolean,
		toeSnap: Boolean,
		footPlant: Boolean,
	) {
		legTweaks.setSkatingReductionEnabled(skatingCorrection)
		legTweaks.setFloorclipEnabled(floorClip)
		legTweaks.toeSnapEnabled = toeSnap
		legTweaks.footPlantEnabled = footPlant
	}

	// Resets to config values
	fun clearLegTweaksStateTemp(
		skatingCorrection: Boolean,
		floorClip: Boolean,
		toeSnap: Boolean,
		footPlant: Boolean,
	) {
		// only reset the true values as they are a mask for what to reset
		if (skatingCorrection) {
			legTweaks
				.setSkatingReductionEnabled(
					humanPoseManager.getToggle(SkeletonConfigToggles.SKATING_CORRECTION)
				)
		}
		if (floorClip) {
			legTweaks
				.setFloorclipEnabled(humanPoseManager.getToggle(SkeletonConfigToggles.FLOOR_CLIP))
		}
		if (toeSnap) legTweaks.toeSnapEnabled = humanPoseManager.getToggle(SkeletonConfigToggles.TOE_SNAP)
		if (footPlant) legTweaks.footPlantEnabled = humanPoseManager.getToggle(SkeletonConfigToggles.FOOT_PLANT)
	}

	val legTweaksState: BooleanArray
		get() {
			val state = BooleanArray(4)
			state[0] = legTweaks.floorclipEnabled
			state[1] = legTweaks.skatingReductionEnabled
			state[2] = legTweaks.toeSnapEnabled
			state[3] = legTweaks.footPlantEnabled
			return state
		}

	// master enable/disable of all leg tweaks (for Autobone)
	@VRServerThread
	fun setLegTweaksEnabled(value: Boolean) {
		legTweaks.enabled = value
	}

	@VRServerThread
	fun setFloorclipEnabled(value: Boolean) {
		humanPoseManager.setToggle(SkeletonConfigToggles.FLOOR_CLIP, value)
	}

	@VRServerThread
	fun setSkatingCorrectionEnabled(value: Boolean) {
		humanPoseManager.setToggle(SkeletonConfigToggles.SKATING_CORRECTION, value)
	}

	fun getPauseTracking(): Boolean {
		return pauseTracking
	}

	fun setPauseTracking(pauseTracking: Boolean) {
		if (!pauseTracking && this.pauseTracking) {
			// If unpausing tracking, clear the legtweaks buffer
			legTweaks.resetBuffer()
		}
		this.pauseTracking = pauseTracking
	}

	companion object {
		val FORWARD_QUATERNION = EulerAngles(
			EulerOrder.YZX,
			FastMath.HALF_PI,
			0f,
			0f
		).toQuaternion()
	}
}
