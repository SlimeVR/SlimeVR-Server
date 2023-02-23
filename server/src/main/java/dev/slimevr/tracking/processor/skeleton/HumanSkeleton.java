package dev.slimevr.tracking.processor.skeleton;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.tracking.processor.BoneInfo;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.processor.TransformNode;
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles;
import dev.slimevr.tracking.processor.config.SkeletonConfigValues;
import dev.slimevr.tracking.trackers.*;
import dev.slimevr.util.ann.VRServerThread;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HumanSkeleton {
	protected final HumanPoseManager humanPoseManager;
	// #region Upper body nodes (torso)
	// @formatter:off
	protected final TransformNode hmdNode = new TransformNode(BoneType.HMD, false);
	protected final TransformNode headNode = new TransformNode(BoneType.HEAD, false);
	protected final TransformNode trackerHeadNode = new TransformNode(BoneType.HEAD_TRACKER, false);
	protected final TransformNode neckNode = new TransformNode(BoneType.NECK, false);
	protected final TransformNode chestNode = new TransformNode(BoneType.CHEST, false);
	protected final TransformNode trackerChestNode = new TransformNode(BoneType.CHEST_TRACKER, false);
	protected final TransformNode waistNode = new TransformNode(BoneType.WAIST, false);
	protected final TransformNode hipNode = new TransformNode(BoneType.HIP, false);
	protected final TransformNode trackerHipNode = new TransformNode(BoneType.HIP_TRACKER, false);
	// #endregion
	// #region Lower body nodes (legs)
	protected final TransformNode leftHipNode = new TransformNode(BoneType.LEFT_HIP, false);
	protected final TransformNode leftKneeNode = new TransformNode(BoneType.LEFT_UPPER_LEG, false);
	protected final TransformNode trackerLeftKneeNode = new TransformNode(BoneType.LEFT_KNEE_TRACKER, false);
	protected final TransformNode leftAnkleNode = new TransformNode(BoneType.LEFT_LOWER_LEG, false);
	protected final TransformNode leftFootNode = new TransformNode(BoneType.LEFT_FOOT, false);
	protected final TransformNode trackerLeftFootNode = new TransformNode(BoneType.LEFT_FOOT_TRACKER, false);
	protected final TransformNode rightHipNode = new TransformNode(BoneType.RIGHT_HIP, false);
	protected final TransformNode rightKneeNode = new TransformNode(BoneType.RIGHT_UPPER_LEG, false);
	protected final TransformNode trackerRightKneeNode = new TransformNode(BoneType.RIGHT_KNEE_TRACKER, false);
	protected final TransformNode rightAnkleNode = new TransformNode(BoneType.RIGHT_LOWER_LEG, false);
	protected final TransformNode rightFootNode = new TransformNode(BoneType.RIGHT_FOOT, false);
	protected final TransformNode trackerRightFootNode = new TransformNode(BoneType.RIGHT_FOOT_TRACKER, false);
	// #endregion
	// #region Arms
	protected final TransformNode leftShoulderHeadNode = new TransformNode(BoneType.LEFT_SHOULDER, false);
	protected final TransformNode rightShoulderHeadNode = new TransformNode(BoneType.RIGHT_SHOULDER, false);
	protected final TransformNode leftShoulderTailNode = new TransformNode(BoneType.LEFT_UPPER_ARM, false);
	protected final TransformNode rightShoulderTailNode = new TransformNode(BoneType.RIGHT_UPPER_ARM, false);
	protected final TransformNode leftElbowNode = new TransformNode(BoneType.LEFT_LOWER_ARM, false);
	protected final TransformNode rightElbowNode = new TransformNode(BoneType.RIGHT_LOWER_ARM, false);
	protected final TransformNode trackerLeftElbowNode = new TransformNode(BoneType.LEFT_ELBOW_TRACKER, false);
	protected final TransformNode trackerRightElbowNode = new TransformNode(BoneType.RIGHT_ELBOW_TRACKER, false);
	protected final TransformNode leftWristNode = new TransformNode(BoneType.LEFT_HAND, false);
	protected final TransformNode rightWristNode = new TransformNode(BoneType.RIGHT_HAND, false);
	protected final TransformNode leftHandNode = new TransformNode(BoneType.LEFT_HAND, false);
	protected final TransformNode rightHandNode = new TransformNode(BoneType.RIGHT_HAND, false);
	protected final TransformNode trackerLeftHandNode = new TransformNode(BoneType.LEFT_HAND_TRACKER, false);
	protected final TransformNode trackerRightHandNode = new TransformNode(BoneType.RIGHT_HAND_TRACKER, false);
	// @formatter:on
	public final List<BoneInfo> allBoneInfo = new ArrayList<>();
	public final List<BoneInfo> shareableBoneInfo = new ArrayList<>();
	// #endregion
	// #region Buffers
	private final Vector3f posBuf = new Vector3f();
	private final Quaternion rotBuf1 = new Quaternion();
	private final Quaternion rotBuf2 = new Quaternion();
	private final Quaternion rotBuf3 = new Quaternion();
	private final Quaternion rotBuf4 = new Quaternion();
	protected boolean hasSpineTracker;
	protected boolean hasKneeTrackers;
	protected boolean hasLeftLegTracker;
	protected boolean hasRightLegTracker;
	protected boolean hasLeftArmTracker;
	protected boolean hasRightArmTracker;
	static final Quaternion FORWARD_QUATERNION = new Quaternion()
		.fromAngles(FastMath.HALF_PI, 0, 0);
	// #region Tracker Input
	protected Tracker hmdTracker;
	protected Tracker neckTracker;
	protected Tracker chestTracker;
	protected Tracker waistTracker;
	protected Tracker hipTracker;
	protected Tracker leftUpperLegTracker;
	protected Tracker leftLowerLegTracker;
	protected Tracker leftFootTracker;
	protected Tracker rightUpperLegTracker;
	protected Tracker rightLowerLegTracker;
	protected Tracker rightFootTracker;
	protected Tracker leftLowerArmTracker;
	protected Tracker rightLowerArmTracker;
	protected Tracker leftUpperArmTracker;
	protected Tracker rightUpperArmTracker;
	protected Tracker leftHandTracker;
	protected Tracker rightHandTracker;
	protected Tracker leftShoulderTracker;
	protected Tracker rightShoulderTracker;
	// #endregion
	// #region Tracker Output
	protected ComputedHumanPoseTracker computedHeadTracker;
	protected ComputedHumanPoseTracker computedChestTracker;
	protected ComputedHumanPoseTracker computedWaistTracker;
	protected ComputedHumanPoseTracker computedLeftKneeTracker;
	protected ComputedHumanPoseTracker computedLeftFootTracker;
	protected ComputedHumanPoseTracker computedRightKneeTracker;
	protected ComputedHumanPoseTracker computedRightFootTracker;
	// #endregion
	protected ComputedHumanPoseTracker computedLeftElbowTracker;
	protected ComputedHumanPoseTracker computedRightElbowTracker;
	protected ComputedHumanPoseTracker computedLeftHandTracker;
	protected ComputedHumanPoseTracker computedRightHandTracker;
	// #endregion

	// #region Settings
	// Toggles
	protected boolean extendedSpineModel;
	protected boolean extendedPelvisModel;
	protected boolean extendedKneeModel;
	protected boolean forceArmsFromHMD = true;
	// Values
	protected float waistFromChestHipAveraging;
	protected float waistFromChestLegsAveraging;
	protected float hipFromChestLegsAveraging;
	protected float hipFromWaistLegsAveraging;
	protected float hipLegsAveraging;
	protected float kneeTrackerAnkleAveraging;
	// Others
	protected boolean sendAllBones = false;
	// #endregion

	// #region Clip Correction
	protected LegTweaks legTweaks = new LegTweaks(this);
	// #endregion

	// #region tap detection
	protected TapDetectionManager tapDetectionManager = new TapDetectionManager(this);
	// #endregion

	// #region Vive emulation
	protected ViveEmulation viveEmulation = new ViveEmulation(this);
	// #endregion

	// #region Constructors
	protected HumanSkeleton(
		HumanPoseManager humanPoseManager
	) {
		this.humanPoseManager = humanPoseManager;

		assembleSkeleton();

		if (humanPoseManager.getComputedTracker() != null) {
			setComputedTrackers(humanPoseManager.getComputedTracker());
		}
		fillNullComputedTrackers();
		resetBones();
	}

	public HumanSkeleton(
		HumanPoseManager humanPoseManager,
		VRServer server
	) {
		this(humanPoseManager);

		setTrackersFromList(server.getAllTrackers());

		tapDetectionManager = new TapDetectionManager(
			this,
			humanPoseManager,
			server.getConfigManager().getVrConfig().getTapDetection()
		);
		legTweaks.setConfig(server.getConfigManager().getVrConfig().getLegTweaks());
	}

	public HumanSkeleton(
		HumanPoseManager humanPoseManager,
		List<? extends Tracker> trackers
	) {
		this(humanPoseManager);

		setTrackersFromList(Objects.requireNonNullElseGet(trackers, () -> new FastList<>(0)));
	}
	// #endregion

	@ThreadSafe
	protected void assembleSkeleton() {
		// #region Assemble skeleton from hmd to hip
		hmdNode.attachChild(headNode);
		headNode.attachChild(neckNode);
		neckNode.attachChild(chestNode);
		chestNode.attachChild(waistNode);
		waistNode.attachChild(hipNode);
		// #endregion

		// #region Assemble skeleton from hips to feet
		hipNode.attachChild(leftHipNode);
		hipNode.attachChild(rightHipNode);

		leftHipNode.attachChild(leftKneeNode);
		rightHipNode.attachChild(rightKneeNode);

		leftKneeNode.attachChild(leftAnkleNode);
		rightKneeNode.attachChild(rightAnkleNode);

		leftAnkleNode.attachChild(leftFootNode);
		rightAnkleNode.attachChild(rightFootNode);
		// #endregion

		// #region Attach tracker nodes for tracker offsets
		neckNode.attachChild(trackerHeadNode);
		neckNode.attachChild(trackerChestNode);
		hipNode.attachChild(trackerHipNode);

		leftKneeNode.attachChild(trackerLeftKneeNode);
		rightKneeNode.attachChild(trackerRightKneeNode);

		leftFootNode.attachChild(trackerLeftFootNode);
		rightFootNode.attachChild(trackerRightFootNode);
		// #endregion

		// Attach arms
		assembleSkeletonArms(false);
	}

	@ThreadSafe
	protected void assembleSkeletonArms(boolean reset) {
		if (reset) {
			for (TransformNode node : getArmNodes()) {
				node.detachWithChildren();
			}
		}

		// #region Assemble skeleton arms
		neckNode.attachChild(leftShoulderHeadNode);
		neckNode.attachChild(rightShoulderHeadNode);

		leftShoulderHeadNode.attachChild(leftShoulderTailNode);
		rightShoulderHeadNode.attachChild(rightShoulderTailNode);

		if (isTrackingLeftArmFromController()) {
			leftWristNode.attachChild(leftElbowNode);
			leftHandNode.attachChild(leftWristNode);

		} else {
			leftShoulderTailNode.attachChild(leftElbowNode);
			leftElbowNode.attachChild(leftWristNode);
			leftWristNode.attachChild(leftHandNode);

		}
		if (isTrackingRightArmFromController()) {
			rightWristNode.attachChild(rightElbowNode);
			rightHandNode.attachChild(rightWristNode);
		} else {
			rightShoulderTailNode.attachChild(rightElbowNode);
			rightElbowNode.attachChild(rightWristNode);
			rightWristNode.attachChild(rightHandNode);

		}
		// #endregion

		// #region Attach tracker nodes for tracker offsets
		leftElbowNode.attachChild(trackerLeftElbowNode);
		rightElbowNode.attachChild(trackerRightElbowNode);

		leftHandNode.attachChild(trackerLeftHandNode);
		rightHandNode.attachChild(trackerRightHandNode);
		// #endregion
	}

	protected void resetBones() {
		allBoneInfo.clear();
		shareableBoneInfo.clear();

		// Create all bones and add to allBoneInfo
		for (BoneType boneType : BoneType.values)
			allBoneInfo.add(new BoneInfo(boneType, getTailNodeOfBone(boneType)));

		// Add shareable bones to shareableBoneInfo
		// Head
		shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.HEAD));
		shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.NECK));

		// Spine and legs
		if (hasSpineTracker || hasLeftLegTracker || hasRightLegTracker || sendAllBones) {
			// Spine
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.CHEST));
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.WAIST));
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.HIP));

			// Left leg
			if (hasLeftLegTracker || sendAllBones) {
				if (sendAllBones) {
					// don't send currently
					shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_HIP));
				}

				shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_UPPER_LEG));
				shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_LOWER_LEG));

				if (leftFootTracker != null || sendAllBones) {
					shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_FOOT));
				}
			}

			// Right leg
			if (hasRightLegTracker || sendAllBones) {
				if (sendAllBones) {
					// don't send currently
					shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_HIP));
				}

				shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_UPPER_LEG));
				shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_LOWER_LEG));

				if (rightFootTracker != null || sendAllBones) {
					shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_FOOT));
				}
			}
		}

		// TODO: Expose the hand/controller bones while accounting for the z and
		// y offsets.
		// TODO: Handle going from HMD and handle shoulder/upper arm. RN we only
		// support controller based bones in the overlay.

		// Left arm
		if ((hasLeftArmTracker || leftShoulderTracker != null) && sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_SHOULDER));
		}
		if (hasLeftArmTracker || sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_UPPER_ARM));
		}
		if (hasLeftArmTracker && sendAllBones) {
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_LOWER_ARM));
		}
		if ((hasLeftArmTracker || leftHandTracker != null) && sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.LEFT_HAND));
		}

		// Right arm
		if ((hasRightArmTracker || rightShoulderTracker != null) && sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_SHOULDER));
		}
		if (hasRightArmTracker || sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_UPPER_ARM));
		}
		if (hasRightArmTracker && sendAllBones) {
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_LOWER_ARM));
		}
		if ((hasRightArmTracker && rightHandTracker != null) && sendAllBones) {
			// don't send currently
			shareableBoneInfo.add(getBoneInfoForBoneType(BoneType.RIGHT_HAND));
		}
	}


	// #region Set trackers inputs
	protected void setTrackersFromList(List<? extends Tracker> trackers) {
		hmdTracker = TrackerUtils.getHMDTracker(trackers);
		if (hmdTracker == null) {
			hmdTracker = TrackerUtils
				.findNonComputedHumanPoseTrackerForBodyPosition(
					trackers,
					TrackerPosition.HMD
				);
		}
		neckTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.NECK
			);
		chestTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.CHEST
			);
		waistTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.WAIST
			);
		hipTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.HIP
			);
		leftUpperLegTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_UPPER_LEG
			);
		leftLowerLegTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_LOWER_LEG
			);
		leftFootTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_FOOT
			);
		rightUpperLegTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_UPPER_LEG
			);
		rightLowerLegTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_LOWER_LEG
			);
		rightFootTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_FOOT
			);
		leftLowerArmTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_LOWER_ARM
			);
		rightLowerArmTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_LOWER_ARM
			);
		leftUpperArmTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_UPPER_ARM
			);
		rightUpperArmTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_UPPER_ARM
			);
		leftHandTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_HAND
			);
		rightHandTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_HAND
			);
		leftShoulderTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_SHOULDER
			);
		rightShoulderTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_SHOULDER
			);

		// Check for specific conditions and store them in booleans.
		hasSpineTracker = chestTracker != null || waistTracker != null || hipTracker != null;
		hasKneeTrackers = leftUpperLegTracker != null && rightUpperLegTracker != null;
		hasLeftLegTracker = leftUpperLegTracker != null
			|| leftLowerLegTracker != null
			|| leftFootTracker != null;
		hasRightLegTracker = rightUpperLegTracker != null
			|| rightLowerLegTracker != null
			|| rightFootTracker != null;
		hasLeftArmTracker = leftLowerArmTracker != null || leftUpperArmTracker != null;
		hasRightArmTracker = rightLowerArmTracker != null || rightUpperArmTracker != null;

		// Rebuilds the arm skeleton nodes attachments
		assembleSkeletonArms(true);

		// Refresh node offsets for arms
		computeDependentArmOffsets();

		// Rebuild the bone list
		resetBones();
	}

	protected void setComputedTracker(ComputedHumanPoseTracker tracker) {
		switch (tracker.getTrackerRole()) {
			case HEAD -> computedHeadTracker = tracker;
			case CHEST -> computedChestTracker = tracker;
			case WAIST -> computedWaistTracker = tracker;
			case LEFT_KNEE -> computedLeftKneeTracker = tracker;
			case LEFT_FOOT -> computedLeftFootTracker = tracker;
			case RIGHT_KNEE -> computedRightKneeTracker = tracker;
			case RIGHT_FOOT -> computedRightFootTracker = tracker;
			case LEFT_ELBOW -> computedLeftElbowTracker = tracker;
			case RIGHT_ELBOW -> computedRightElbowTracker = tracker;
			case LEFT_HAND -> computedLeftHandTracker = tracker;
			case RIGHT_HAND -> computedRightHandTracker = tracker;
			default -> {}
		}
	}

	protected void setComputedTrackers(List<? extends ComputedHumanPoseTracker> trackers) {
		for (ComputedHumanPoseTracker t : trackers) {
			setComputedTracker(t);
		}
	}
	// #endregion
	// #endregion

	protected void fillNullComputedTrackers() {
		if (computedHeadTracker == null) {
			computedHeadTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.HEAD,
				TrackerRole.HEAD
			);
			computedHeadTracker.setStatus(TrackerStatus.OK);
		}
		if (computedChestTracker == null) {
			computedChestTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.CHEST,
				TrackerRole.CHEST
			);
			computedChestTracker.setStatus(TrackerStatus.OK);
		}
		if (computedWaistTracker == null) {
			computedWaistTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.WAIST,
				TrackerRole.WAIST
			);
			computedWaistTracker.setStatus(TrackerStatus.OK);
		}
		if (computedLeftFootTracker == null) {
			computedLeftFootTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.LEFT_FOOT,
				TrackerRole.LEFT_FOOT
			);
			computedLeftFootTracker.setStatus(TrackerStatus.OK);
		}
		if (computedRightFootTracker == null) {
			computedRightFootTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.RIGHT_FOOT,
				TrackerRole.RIGHT_FOOT
			);
			computedRightFootTracker.setStatus(TrackerStatus.OK);
		}
		if (computedLeftKneeTracker == null) {
			computedLeftKneeTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.LEFT_KNEE,
				TrackerRole.LEFT_KNEE
			);
			computedLeftKneeTracker.setStatus(TrackerStatus.OK);
		}
		if (computedRightKneeTracker == null) {
			computedRightKneeTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.RIGHT_KNEE,
				TrackerRole.RIGHT_KNEE
			);
			computedRightKneeTracker.setStatus(TrackerStatus.OK);
		}
		if (computedLeftElbowTracker == null) {
			computedLeftElbowTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.LEFT_ELBOW,
				TrackerRole.LEFT_ELBOW
			);
			computedLeftElbowTracker.setStatus(TrackerStatus.OK);
		}
		if (computedRightElbowTracker == null) {
			computedRightElbowTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.RIGHT_ELBOW,
				TrackerRole.RIGHT_ELBOW
			);
			computedRightElbowTracker.setStatus(TrackerStatus.OK);
		}
		if (computedLeftHandTracker == null) {
			computedLeftHandTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.LEFT_HAND,
				TrackerRole.LEFT_HAND
			);
			computedLeftHandTracker.setStatus(TrackerStatus.OK);
		}
		if (computedRightHandTracker == null) {
			computedRightHandTracker = new ComputedHumanPoseTracker(
				Tracker.getNextLocalTrackerId(),
				ComputedHumanPoseTrackerPosition.RIGHT_HAND,
				TrackerRole.RIGHT_HAND
			);
			computedRightHandTracker.setStatus(TrackerStatus.OK);
		}
	}

	// #region Get trackers
	public ComputedHumanPoseTracker getComputedTracker(TrackerRole trackerRole) {
		switch (trackerRole) {
			case HEAD:
				return computedHeadTracker;
			case CHEST:
				return computedChestTracker;
			case WAIST:
				return computedWaistTracker;
			case LEFT_KNEE:
				return computedLeftKneeTracker;
			case LEFT_FOOT:
				return computedLeftFootTracker;
			case RIGHT_KNEE:
				return computedRightKneeTracker;
			case RIGHT_FOOT:
				return computedRightFootTracker;
			case LEFT_ELBOW:
				return computedLeftElbowTracker;
			case RIGHT_ELBOW:
				return computedRightElbowTracker;
			case LEFT_HAND:
				return computedLeftHandTracker;
			case RIGHT_HAND:
				return computedRightHandTracker;
			default:
				break;
		}

		return null;
	}

	// #region Processing
	// Useful for subclasses that need to return a sub-tracker (like
	// PoseFrameTracker -> TrackerFrame)
	protected Tracker trackerPreUpdate(Tracker tracker) {
		return tracker;
	}

	// Updates the pose from tracker positions
	@VRServerThread
	public void updatePose() {
		tapDetectionManager.update();
		updateLocalTransforms();
		updateRootTrackers();
		updateComputedTrackers();
		tweakLegPos();
		viveEmulation.update();
	}
	// #endregion

	@ThreadSafe
	protected void updateRootTrackers() {
		hmdNode.update();
		if (isTrackingLeftArmFromController()) {
			leftHandNode.update();
		}
		if (isTrackingRightArmFromController()) {
			rightHandNode.update();
		}
	}

	// correct any clipping that is happening to the feet trackers
	private void tweakLegPos() {
		// correct the foot positions
		legTweaks.tweakLegs();
	}

	// #region Update the node transforms from the trackers
	protected void updateLocalTransforms() {
		// #region Pass all trackers through trackerPreUpdate for Autobone
		Tracker hmdTracker = trackerPreUpdate(this.hmdTracker);

		Tracker neckTracker = trackerPreUpdate(this.neckTracker);
		Tracker chestTracker = trackerPreUpdate(this.chestTracker);
		Tracker waistTracker = trackerPreUpdate(this.waistTracker);
		Tracker hipTracker = trackerPreUpdate(this.hipTracker);

		Tracker leftUpperLegTracker = trackerPreUpdate(this.leftUpperLegTracker);
		Tracker leftLowerLegTracker = trackerPreUpdate(this.leftLowerLegTracker);
		Tracker leftFootTracker = trackerPreUpdate(this.leftFootTracker);

		Tracker rightUpperLegTracker = trackerPreUpdate(this.rightUpperLegTracker);
		Tracker rightLowerLegTracker = trackerPreUpdate(this.rightLowerLegTracker);
		Tracker rightFootTracker = trackerPreUpdate(this.rightFootTracker);

		Tracker leftLowerArmTracker = trackerPreUpdate(this.leftLowerArmTracker);
		Tracker rightLowerArmTracker = trackerPreUpdate(this.rightLowerArmTracker);
		Tracker leftUpperArmTracker = trackerPreUpdate(this.leftUpperArmTracker);
		Tracker rightUpperArmTracker = trackerPreUpdate(this.rightUpperArmTracker);
		Tracker leftHandTracker = trackerPreUpdate(this.leftHandTracker);
		Tracker rightHandTracker = trackerPreUpdate(this.rightHandTracker);
		Tracker leftShoulderTracker = trackerPreUpdate(this.leftShoulderTracker);
		Tracker rightShoulderTracker = trackerPreUpdate(this.rightShoulderTracker);
		// #endregion

		// HMD, head and neck
		if (hmdTracker != null) {
			hmdTracker.getPosition(posBuf);
			hmdNode.localTransform.setTranslation(posBuf);

			hmdTracker.getRotation(rotBuf1);
			hmdNode.localTransform.setRotation(rotBuf1);
			trackerHeadNode.localTransform.setRotation(rotBuf1);

			if (neckTracker != null)
				neckTracker.getRotation(rotBuf1);
			headNode.localTransform.setRotation(rotBuf1);
		} else {
			hmdNode.localTransform.setTranslation(Vector3f.ZERO);

			rotBuf1.loadIdentity();
			if (neckTracker != null)
				neckTracker.getRotation(rotBuf1);
			else if (hasSpineTracker)
				TrackerUtils
					.getFirstAvailableTracker(chestTracker, waistTracker, hipTracker)
					.getRotation(rotBuf1);

			hmdNode.localTransform.setRotation(rotBuf1);
			trackerHeadNode.localTransform.setRotation(rotBuf1);
			headNode.localTransform.setRotation(rotBuf1);
		}

		// Spine
		if (hasSpineTracker) {
			TrackerUtils
				.getFirstAvailableTracker(chestTracker, waistTracker, hipTracker)
				.getRotation(rotBuf1);
			neckNode.localTransform.setRotation(rotBuf1);
			trackerChestNode.localTransform.setRotation(rotBuf1);

			TrackerUtils
				.getFirstAvailableTracker(waistTracker, chestTracker, hipTracker)
				.getRotation(rotBuf1);
			chestNode.localTransform.setRotation(rotBuf1);

			TrackerUtils
				.getFirstAvailableTracker(hipTracker, waistTracker, chestTracker)
				.getRotation(rotBuf1);
			waistNode.localTransform.setRotation(rotBuf1);
			hipNode.localTransform.setRotation(rotBuf1);
			trackerHipNode.localTransform.setRotation(rotBuf1);
		} else if (hmdTracker != null) {
			// Align with last tracker's yaw (HMD or neck)
			rotBuf1.fromAngles(0, rotBuf1.getYaw(), 0);

			neckNode.localTransform.setRotation(rotBuf1);
			trackerChestNode.localTransform.setRotation(rotBuf1);
			chestNode.localTransform.setRotation(rotBuf1);
			waistNode.localTransform.setRotation(rotBuf1);
			hipNode.localTransform.setRotation(rotBuf1);
			trackerHipNode.localTransform.setRotation(rotBuf1);
		}

		// Left Leg
		// Get rotations
		if (leftUpperLegTracker != null) {
			leftUpperLegTracker.getRotation(rotBuf1);
		} else {
			// Align with the hip's yaw
			hipNode.localTransform.getRotation(rotBuf1);
			rotBuf1.fromAngles(0, rotBuf1.getYaw(), 0);
		}
		if (leftLowerLegTracker != null) {
			leftLowerLegTracker.getRotation(rotBuf2);
		} else {
			// Align with the upper leg's yaw
			rotBuf2.fromAngles(0, rotBuf1.getYaw(), 0);
		}

		leftHipNode.localTransform.setRotation(rotBuf1);
		trackerLeftKneeNode.localTransform.setRotation(rotBuf1);
		leftKneeNode.localTransform.setRotation(rotBuf2);

		if (leftFootTracker != null)
			leftFootTracker.getRotation(rotBuf2);

		leftAnkleNode.localTransform.setRotation(rotBuf2);
		leftFootNode.localTransform.setRotation(rotBuf2);
		trackerLeftFootNode.localTransform.setRotation(rotBuf2);

		// Extended left knee
		if (leftUpperLegTracker != null && leftLowerLegTracker != null && extendedKneeModel) {
			// Averages the knee's rotation with the local ankle's
			// pitch and roll and apply to the tracker node.
			leftHipNode.localTransform.getRotation(rotBuf1);
			leftKneeNode.localTransform.getRotation(rotBuf2);

			rotBuf2.set(extendedKneeYawRoll(rotBuf1, rotBuf2));

			rotBuf1.slerpLocal(rotBuf2, kneeTrackerAnkleAveraging);
			trackerLeftKneeNode.localTransform.setRotation(rotBuf1);
		}

		// Right Leg
		// Get rotations
		if (rightUpperLegTracker != null) {
			rightUpperLegTracker.getRotation(rotBuf1);
		} else {
			// Align with the hip's yaw
			hipNode.localTransform.getRotation(rotBuf1);
			rotBuf1.fromAngles(0, rotBuf1.getYaw(), 0);
		}
		if (rightLowerLegTracker != null) {
			rightLowerLegTracker.getRotation(rotBuf2);
		} else {
			// Align with the upper leg's yaw
			rotBuf2.fromAngles(0, rotBuf1.getYaw(), 0);
		}

		rightHipNode.localTransform.setRotation(rotBuf1);
		trackerRightKneeNode.localTransform.setRotation(rotBuf1);
		rightKneeNode.localTransform.setRotation(rotBuf2);

		if (rightFootTracker != null)
			rightFootTracker.getRotation(rotBuf2);

		rightAnkleNode.localTransform.setRotation(rotBuf2);
		rightFootNode.localTransform.setRotation(rotBuf2);
		trackerRightFootNode.localTransform.setRotation(rotBuf2);

		// Extended right knee
		if (rightUpperLegTracker != null && rightLowerLegTracker != null && extendedKneeModel) {
			// Averages the knee's rotation with the local ankle's
			// pitch and roll and apply to the tracker node.
			rightHipNode.localTransform.getRotation(rotBuf1);
			rightKneeNode.localTransform.getRotation(rotBuf2);

			rotBuf2.set(extendedKneeYawRoll(rotBuf1, rotBuf2));

			rotBuf1.slerpLocal(rotBuf2, kneeTrackerAnkleAveraging);
			trackerRightKneeNode.localTransform.setRotation(rotBuf1);
		}

		// Extended spine
		if (extendedSpineModel && hasSpineTracker) {
			// Tries to guess missing lower spine trackers by interpolating
			// rotations
			if (waistTracker == null) {
				if (chestTracker != null && hipTracker != null) {
					// Calculates waist from chest + hip
					hipTracker.getRotation(rotBuf1);
					chestTracker.getRotation(rotBuf2);

					// Get the rotation relative to where we expect the
					// hip to be
					rotBuf2.mult(FORWARD_QUATERNION, rotBuf4);
					if (rotBuf4.dot(rotBuf1) < 0.0f) {
						rotBuf1.negateLocal();
					}

					// Interpolate between the chest and the hip
					rotBuf2.pureSlerpLocal(rotBuf1, waistFromChestHipAveraging);

					chestNode.localTransform.setRotation(rotBuf2);
				} else if (chestTracker != null && hasKneeTrackers) {
					// Calculates waist from chest + legs
					leftHipNode.localTransform.getRotation(rotBuf1);
					rightHipNode.localTransform.getRotation(rotBuf2);
					chestTracker.getRotation(rotBuf3);

					// Get the rotation relative to where we expect the
					// upper legs to be
					rotBuf3.mult(FORWARD_QUATERNION, rotBuf4);
					if (rotBuf4.dot(rotBuf1) < 0.0f) {
						rotBuf1.negateLocal();
					}
					if (rotBuf4.dot(rotBuf2) < 0.0f) {
						rotBuf2.negateLocal();
					}

					// Average the legs to calculate the pelvis
					rotBuf1.nlerp(rotBuf2, 0.5f);

					// Interpolate between the pelvis and the chest
					rotBuf3.pureSlerpLocal(rotBuf1, waistFromChestLegsAveraging);

					chestNode.localTransform.setRotation(rotBuf3);
				}
			}
			if (hipTracker == null && hasKneeTrackers) {
				if (waistTracker != null) {
					// Calculates hip from waist + legs
					leftHipNode.localTransform.getRotation(rotBuf1);
					rightHipNode.localTransform.getRotation(rotBuf2);
					waistTracker.getRotation(rotBuf3);

					// Get the rotation relative to where we expect the
					// upper legs to be
					rotBuf3.mult(FORWARD_QUATERNION, rotBuf4);
					if (rotBuf4.dot(rotBuf1) < 0.0f) {
						rotBuf1.negateLocal();
					}
					if (rotBuf4.dot(rotBuf2) < 0.0f) {
						rotBuf2.negateLocal();
					}

					// Average the legs to calculate the pelvis
					rotBuf1.nlerp(rotBuf2, 0.5f);

					// Interpolate between the pelvis and the chest
					rotBuf3.pureSlerpLocal(rotBuf1, hipFromWaistLegsAveraging);

					waistNode.localTransform.setRotation(rotBuf3);
					hipNode.localTransform.setRotation(rotBuf3);
					trackerHipNode.localTransform.setRotation(rotBuf3);
				} else if (chestTracker != null) {
					// Calculates hip from chest + legs
					leftHipNode.localTransform.getRotation(rotBuf1);
					rightHipNode.localTransform.getRotation(rotBuf2);
					chestTracker.getRotation(rotBuf3);

					// Get the rotation relative to where we expect the
					// upper legs to be
					rotBuf3.mult(FORWARD_QUATERNION, rotBuf4);
					if (rotBuf4.dot(rotBuf1) < 0.0f) {
						rotBuf1.negateLocal();
					}
					if (rotBuf4.dot(rotBuf2) < 0.0f) {
						rotBuf2.negateLocal();
					}

					// Average the legs to calculate the pelvis
					rotBuf1.nlerp(rotBuf2, 0.5f);

					// Interpolate between the pelvis and the chest
					rotBuf3.pureSlerpLocal(rotBuf1, hipFromChestLegsAveraging);

					waistNode.localTransform.setRotation(rotBuf3);
					hipNode.localTransform.setRotation(rotBuf3);
					trackerHipNode.localTransform.setRotation(rotBuf3);
				}
			}
		}

		// Extended pelvis
		if (extendedPelvisModel && hasKneeTrackers && hipTracker == null) {
			leftHipNode.localTransform.getRotation(rotBuf1);
			rightHipNode.localTransform.getRotation(rotBuf2);
			hipNode.localTransform.getRotation(rotBuf3);

			rotBuf1.set(extendedPelvisYawRoll(rotBuf1, rotBuf2, rotBuf3));

			rotBuf3.slerpLocal(rotBuf1, hipLegsAveraging);
			hipNode.localTransform.setRotation(rotBuf3);
			trackerHipNode.localTransform.setRotation(rotBuf3);
		}

		// Left arm
		if (isTrackingLeftArmFromController()) { // From controller
			leftHandTracker.getPosition(posBuf);
			leftHandTracker.getRotation(rotBuf1);
			leftHandNode.localTransform.setTranslation(posBuf);
			leftHandNode.localTransform.setRotation(rotBuf1);

			Tracker lowerArm = TrackerUtils
				.getFirstAvailableTracker(leftLowerArmTracker, leftUpperArmTracker);
			if (lowerArm != null) {
				lowerArm.getRotation(rotBuf1);

				leftWristNode.localTransform.setRotation(rotBuf1);

				TrackerUtils
					.getFirstAvailableTracker(leftUpperArmTracker, leftLowerArmTracker)
					.getRotation(rotBuf1);

				leftElbowNode.localTransform.setRotation(rotBuf1);
				trackerLeftElbowNode.localTransform.setRotation(rotBuf1);
			}
		} else { // From HMD
			if (leftShoulderTracker != null)
				leftShoulderTracker.getRotation(rotBuf1);
			else
				neckNode.localTransform.getRotation(rotBuf1);
			leftShoulderHeadNode.localTransform.setRotation(rotBuf1);

			if (leftUpperArmTracker != null || leftLowerArmTracker != null) {
				TrackerUtils
					.getFirstAvailableTracker(leftUpperArmTracker, leftLowerArmTracker)
					.getRotation(rotBuf1);
				leftShoulderTailNode.localTransform.setRotation(rotBuf1);
				trackerLeftElbowNode.localTransform.setRotation(rotBuf1);

				TrackerUtils
					.getFirstAvailableTracker(leftLowerArmTracker, leftUpperArmTracker)
					.getRotation(rotBuf1);
				leftElbowNode.localTransform.setRotation(rotBuf1);
			} else {
				neckNode.localTransform.getRotation(rotBuf1);
				leftShoulderTailNode.localTransform.setRotation(rotBuf1);
				trackerLeftElbowNode.localTransform.setRotation(rotBuf1);
				leftElbowNode.localTransform.setRotation(rotBuf1);
			}

			if (leftHandTracker != null)
				leftHandTracker.getRotation(rotBuf1);

			leftWristNode.localTransform.setRotation(rotBuf1);
			leftHandNode.localTransform.setRotation(rotBuf1);
			trackerLeftHandNode.localTransform.setRotation(rotBuf1);
		}

		// Right arm
		if (isTrackingRightArmFromController()) { // From controller
			rightHandTracker.getPosition(posBuf);
			rightHandTracker.getRotation(rotBuf1);
			rightHandNode.localTransform.setTranslation(posBuf);
			rightHandNode.localTransform.setRotation(rotBuf1);

			Tracker lowerArm = TrackerUtils
				.getFirstAvailableTracker(rightLowerArmTracker, rightUpperArmTracker);
			if (lowerArm != null) {
				lowerArm.getRotation(rotBuf1);

				rightWristNode.localTransform.setRotation(rotBuf1);

				TrackerUtils
					.getFirstAvailableTracker(rightUpperArmTracker, rightLowerArmTracker)
					.getRotation(rotBuf1);

				rightElbowNode.localTransform.setRotation(rotBuf1);
				trackerRightElbowNode.localTransform.setRotation(rotBuf1);
			}
		} else { // From HMD
			if (rightShoulderTracker != null)
				rightShoulderTracker.getRotation(rotBuf1);
			else
				neckNode.localTransform.getRotation(rotBuf1);
			rightShoulderHeadNode.localTransform.setRotation(rotBuf1);

			if (rightUpperArmTracker != null || rightLowerArmTracker != null) {
				TrackerUtils
					.getFirstAvailableTracker(rightUpperArmTracker, rightLowerArmTracker)
					.getRotation(rotBuf1);
				rightShoulderTailNode.localTransform.setRotation(rotBuf1);
				trackerRightElbowNode.localTransform.setRotation(rotBuf1);

				TrackerUtils
					.getFirstAvailableTracker(rightLowerArmTracker, rightUpperArmTracker)
					.getRotation(rotBuf1);
				rightElbowNode.localTransform.setRotation(rotBuf1);
			} else {
				neckNode.localTransform.getRotation(rotBuf1);
				rightShoulderTailNode.localTransform.setRotation(rotBuf1);
				trackerRightElbowNode.localTransform.setRotation(rotBuf1);
				rightElbowNode.localTransform.setRotation(rotBuf1);
			}

			if (rightHandTracker != null)
				rightHandTracker.getRotation(rotBuf1);

			rightWristNode.localTransform.setRotation(rotBuf1);
			rightHandNode.localTransform.setRotation(rotBuf1);
			trackerRightHandNode.localTransform.setRotation(rotBuf1);
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
	private Quaternion extendedKneeYawRoll(Quaternion knee, Quaternion ankle) {
		// Clone the knee since we're modifying it and returning it
		knee = knee.clone();

		// Get the inverse rotation of the knee
		rotBuf3.set(knee).inverseLocal();

		// R = InverseKnee * Ankle
		// C = Quaternion(-R.x, 0, 0, R.w)
		// Knee = Knee * R * C
		// normalize(Knee)
		rotBuf3.multLocal(ankle);
		rotBuf4.set(-rotBuf3.getX(), 0, 0, rotBuf3.getW());
		knee.multLocal(rotBuf3).multLocal(rotBuf4);
		return knee.normalizeLocal();
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
	private Quaternion extendedPelvisYawRoll(
		Quaternion leftKnee,
		Quaternion rightKnee,
		Quaternion hip
	) {
		// Clone the hip since we're modifying it and returning it
		hip = hip.clone();
		// Clone the knees here because otherwise it doesn't work because Java
		leftKnee = leftKnee.clone();
		rightKnee = rightKnee.clone();


		// Get the knees' rotation relative to where we expect them to be.
		// The angle between your knees and hip can be over 180 degrees...
		hip.mult(FORWARD_QUATERNION, rotBuf1);
		if (rotBuf1.dot(leftKnee) < 0.0f) {
			leftKnee.negateLocal();
		}
		if (rotBuf1.dot(rightKnee) < 0.0f) {
			rightKnee.negateLocal();
		}

		// Get the inverse rotation of the hip.
		rotBuf1.set(hip).inverseLocal();

		// R = InverseHip * (LeftLeft + RightLeg)
		// C = Quaternion(-R.x, 0, 0, R.w)
		// Pelvis = Hip * R * C
		// normalize(Pelvis)
		rotBuf1.multLocal(leftKnee.add(rightKnee));
		rotBuf2.set(-rotBuf1.getX(), 0, 0, rotBuf1.getW());
		hip.multLocal(rotBuf1).multLocal(rotBuf2);
		return hip.normalizeLocal();
	}

	// #region Update the output trackers
	protected void updateComputedTrackers() {
		if (computedHeadTracker != null) {
			computedHeadTracker.position.set(trackerHeadNode.worldTransform.getTranslation());
			computedHeadTracker.rotation.set(trackerHeadNode.worldTransform.getRotation());
			computedHeadTracker.dataTick();
		}

		if (computedChestTracker != null) {
			computedChestTracker.position.set(trackerChestNode.worldTransform.getTranslation());
			computedChestTracker.rotation.set(trackerChestNode.worldTransform.getRotation());
			computedChestTracker.dataTick();
		}

		if (computedWaistTracker != null) {
			computedWaistTracker.position.set(trackerHipNode.worldTransform.getTranslation());
			computedWaistTracker.rotation.set(trackerHipNode.worldTransform.getRotation());
			computedWaistTracker.dataTick();
		}

		if (computedLeftKneeTracker != null) {
			computedLeftKneeTracker.position
				.set(trackerLeftKneeNode.worldTransform.getTranslation());
			computedLeftKneeTracker.rotation.set(trackerLeftKneeNode.worldTransform.getRotation());
			computedLeftKneeTracker.dataTick();
		}

		if (computedLeftFootTracker != null) {
			computedLeftFootTracker.position
				.set(trackerLeftFootNode.worldTransform.getTranslation());
			computedLeftFootTracker.rotation.set(trackerLeftFootNode.worldTransform.getRotation());
			computedLeftFootTracker.dataTick();
		}

		if (computedRightKneeTracker != null) {
			computedRightKneeTracker.position
				.set(trackerRightKneeNode.worldTransform.getTranslation());
			computedRightKneeTracker.rotation
				.set(trackerRightKneeNode.worldTransform.getRotation());
			computedRightKneeTracker.dataTick();
		}

		if (computedRightFootTracker != null) {
			computedRightFootTracker.position
				.set(trackerRightFootNode.worldTransform.getTranslation());
			computedRightFootTracker.rotation
				.set(trackerRightFootNode.worldTransform.getRotation());
			computedRightFootTracker.dataTick();
		}

		if (computedLeftElbowTracker != null) {
			computedLeftElbowTracker.position
				.set(trackerLeftElbowNode.worldTransform.getTranslation());
			computedLeftElbowTracker.rotation
				.set(trackerLeftElbowNode.worldTransform.getRotation());
			computedLeftElbowTracker.dataTick();
		}

		if (computedRightElbowTracker != null) {
			computedRightElbowTracker.position
				.set(trackerRightElbowNode.worldTransform.getTranslation());
			computedRightElbowTracker.rotation
				.set(trackerRightElbowNode.worldTransform.getRotation());
			computedRightElbowTracker.dataTick();
		}

		if (computedLeftHandTracker != null) {
			computedLeftHandTracker.position
				.set(trackerLeftHandNode.worldTransform.getTranslation());
			computedLeftHandTracker.rotation
				.set(trackerLeftHandNode.worldTransform.getRotation());
			computedLeftHandTracker.dataTick();
		}

		if (computedRightHandTracker != null) {
			computedRightHandTracker.position
				.set(trackerRightHandNode.worldTransform.getTranslation());
			computedRightHandTracker.rotation
				.set(trackerRightHandNode.worldTransform.getRotation());
			computedRightHandTracker.dataTick();
		}
	}
	// #endregion
	// #endregion

	// #region Skeleton Config
	public void updateToggleState(SkeletonConfigToggles configToggle, boolean newValue) {
		if (configToggle == null) {
			return;
		}

		// Cache the values of these configs
		switch (configToggle) {
			case EXTENDED_SPINE_MODEL -> extendedSpineModel = newValue;
			case EXTENDED_PELVIS_MODEL -> extendedPelvisModel = newValue;
			case EXTENDED_KNEE_MODEL -> extendedKneeModel = newValue;
			case FORCE_ARMS_FROM_HMD -> {
				forceArmsFromHMD = newValue;

				// Rebuilds the arm skeleton nodes attachments
				assembleSkeletonArms(true);

				// Refresh node offsets for arms
				computeDependentArmOffsets();
			}
			case SKATING_CORRECTION -> legTweaks.setSkatingReductionEnabled(newValue);
			case FLOOR_CLIP -> legTweaks.setFloorclipEnabled(newValue);
			case VIVE_EMULATION -> viveEmulation.setEnabled(newValue);
		}
	}

	public void updateValueState(SkeletonConfigValues configValue, float newValue) {
		if (configValue == null) {
			return;
		}

		// Cache the values of these configs
		switch (configValue) {
			case WAIST_FROM_CHEST_HIP_AVERAGING -> waistFromChestHipAveraging = newValue;
			case WAIST_FROM_CHEST_LEGS_AVERAGING -> waistFromChestLegsAveraging = newValue;
			case HIP_FROM_CHEST_LEGS_AVERAGING -> hipFromChestLegsAveraging = newValue;
			case HIP_FROM_WAIST_LEGS_AVERAGING -> hipFromWaistLegsAveraging = newValue;
			case HIP_LEGS_AVERAGING -> hipLegsAveraging = newValue;
			case KNEE_TRACKER_ANKLE_AVERAGING -> kneeTrackerAnkleAveraging = newValue;
		}
	}

	public void updateNodeOffset(BoneType nodeOffset, Vector3f offset) {
		if (nodeOffset == null) {
			return;
		}

		switch (nodeOffset) {
			case HEAD:
				if (hmdTracker != null && hmdTracker.hasPosition()) {
					headNode.localTransform.setTranslation(offset);
				} else {
					headNode.localTransform.setTranslation(Vector3f.ZERO);
				}
				break;
			case NECK:
				neckNode.localTransform.setTranslation(offset);
				break;
			case CHEST:
				chestNode.localTransform.setTranslation(offset);
				break;
			case CHEST_TRACKER:
				trackerChestNode.localTransform.setTranslation(offset);
				break;
			case WAIST:
				waistNode.localTransform.setTranslation(offset);
				break;
			case HIP:
				hipNode.localTransform.setTranslation(offset);
				break;
			case HIP_TRACKER:
				trackerHipNode.localTransform.setTranslation(offset);
				break;
			case LEFT_HIP:
				leftHipNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_HIP:
				rightHipNode.localTransform.setTranslation(offset);
				break;
			case LEFT_UPPER_LEG:
				leftKneeNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_UPPER_LEG:
				rightKneeNode.localTransform.setTranslation(offset);
				break;
			case LEFT_KNEE_TRACKER:
				trackerLeftKneeNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_KNEE_TRACKER:
				trackerRightKneeNode.localTransform.setTranslation(offset);
				break;
			case LEFT_LOWER_LEG:
				leftAnkleNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_LOWER_LEG:
				rightAnkleNode.localTransform.setTranslation(offset);
				break;
			case LEFT_FOOT:
				leftFootNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_FOOT:
				rightFootNode.localTransform.setTranslation(offset);
				break;
			case LEFT_FOOT_TRACKER:
				trackerLeftFootNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_FOOT_TRACKER:
				trackerRightFootNode.localTransform.setTranslation(offset);
				break;
			case LEFT_SHOULDER:
				leftShoulderTailNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_SHOULDER:
				rightShoulderTailNode.localTransform.setTranslation(offset);
				break;
			case LEFT_UPPER_ARM:
				if (!isTrackingLeftArmFromController()) {
					leftElbowNode.localTransform.setTranslation(offset);
				}
			case RIGHT_UPPER_ARM:
				if (!isTrackingRightArmFromController()) {
					rightElbowNode.localTransform.setTranslation(offset);
				}
				break;
			case LEFT_LOWER_ARM:
				if (isTrackingLeftArmFromController()) {
					leftElbowNode.localTransform.setTranslation(offset);
				} else {
					leftWristNode.localTransform.setTranslation(offset.negate());
				}
			case RIGHT_LOWER_ARM:
				if (isTrackingRightArmFromController()) {
					rightElbowNode.localTransform.setTranslation(offset);
				} else {
					rightWristNode.localTransform.setTranslation(offset.negate());
				}
				break;
			case LEFT_ELBOW_TRACKER:
				trackerLeftElbowNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_ELBOW_TRACKER:
				trackerRightElbowNode.localTransform.setTranslation(offset);
				break;
			case LEFT_HAND:
				if (isTrackingLeftArmFromController()) {
					leftWristNode.localTransform.setTranslation(offset.negate());
				} else {
					leftHandNode.localTransform.setTranslation(offset);
				}
				break;
			case RIGHT_HAND:
				if (isTrackingRightArmFromController()) {
					rightWristNode.localTransform.setTranslation(offset.negate());
				} else {
					rightHandNode.localTransform.setTranslation(offset);
				}
				break;
			default:
				break;
		}

		for (BoneInfo bone : allBoneInfo) {
			bone.updateLength();
		}
	}

	private void computeDependentArmOffsets() {
		humanPoseManager.computeNodeOffset(BoneType.LEFT_UPPER_ARM);
		humanPoseManager.computeNodeOffset(BoneType.RIGHT_UPPER_ARM);
		humanPoseManager.computeNodeOffset(BoneType.LEFT_LOWER_ARM);
		humanPoseManager.computeNodeOffset(BoneType.RIGHT_LOWER_ARM);
	}

	public TransformNode getTailNodeOfBone(BoneType bone) {
		if (bone == null) {
			return null;
		}

		switch (bone) {
			case HMD:
			case HEAD:
				return headNode;
			case HEAD_TRACKER:
				return trackerHeadNode;
			case NECK:
				return neckNode;
			case CHEST:
				return chestNode;
			case CHEST_TRACKER:
				return trackerChestNode;
			case WAIST:
				return waistNode;
			case HIP:
				return hipNode;
			case HIP_TRACKER:
				return trackerHipNode;
			case LEFT_HIP:
				return leftHipNode;
			case RIGHT_HIP:
				return rightHipNode;
			case LEFT_UPPER_LEG:
				return leftKneeNode;
			case RIGHT_UPPER_LEG:
				return rightKneeNode;
			case RIGHT_KNEE_TRACKER:
				return trackerRightKneeNode;
			case LEFT_KNEE_TRACKER:
				return trackerLeftKneeNode;
			case LEFT_LOWER_LEG:
				return leftAnkleNode;
			case RIGHT_LOWER_LEG:
				return rightAnkleNode;
			case LEFT_FOOT:
				return leftFootNode;
			case RIGHT_FOOT:
				return rightFootNode;
			case LEFT_FOOT_TRACKER:
				return trackerLeftFootNode;
			case RIGHT_FOOT_TRACKER:
				return trackerRightFootNode;
			case LEFT_SHOULDER:
				return leftShoulderTailNode;
			case RIGHT_SHOULDER:
				return rightShoulderTailNode;
			case LEFT_UPPER_ARM:
				return leftElbowNode;
			case RIGHT_UPPER_ARM:
				return rightElbowNode;
			case LEFT_ELBOW_TRACKER:
				return trackerLeftElbowNode;
			case RIGHT_ELBOW_TRACKER:
				return trackerRightElbowNode;
			case LEFT_LOWER_ARM:
				if (isTrackingLeftArmFromController()) {
					return leftElbowNode;
				} else {
					return leftWristNode;
				}
			case RIGHT_LOWER_ARM:
				if (isTrackingRightArmFromController()) {
					return rightElbowNode;
				} else {
					return rightWristNode;
				}
			case LEFT_HAND:
				if (isTrackingLeftArmFromController()) {
					return leftWristNode;
				} else {
					return leftHandNode;
				}
			case RIGHT_HAND:
				if (isTrackingRightArmFromController()) {
					return rightWristNode;
				} else {
					return rightHandNode;
				}
			case LEFT_HAND_TRACKER:
				return trackerLeftHandNode;
			case RIGHT_HAND_TRACKER:
				return trackerRightHandNode;
			default:
				return null;
		}
	}

	public BoneInfo getBoneInfoForBoneType(BoneType boneType) {
		for (BoneInfo bone : allBoneInfo) {
			if (bone.boneType == boneType)
				return bone;
		}

		return null;
	}
	// #endregion

	public TransformNode getRootNode() {
		return hmdNode;
	}

	protected TransformNode[] getAllNodes() {
		return new TransformNode[] {
			hmdNode,
			headNode,
			trackerHeadNode,
			neckNode,
			chestNode,
			trackerChestNode,
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
			trackerRightFootNode,
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
			trackerRightHandNode,
		};
	}

	protected TransformNode[] getArmNodes() {
		return new TransformNode[] {
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
			trackerRightHandNode,
		};
	}

	public float getHmdHeight() {
		Vector3f hmdVec = new Vector3f();
		if (hmdTracker != null)
			hmdTracker.getPosition(hmdVec);
		return hmdVec.y;
	}

	/**
	 * Runs checks to know if we should (and are) performing the tracking of the
	 * left arm from the controller.
	 *
	 * @return a bool telling us if we are tracking the left arm from the
	 * controller or not.
	 */
	public boolean isTrackingLeftArmFromController() {
		return leftHandTracker != null && leftHandTracker.hasPosition() && !forceArmsFromHMD;
	}

	/**
	 * Runs checks to know if we should (and are) performing the tracking of the
	 * right arm from the controller.
	 *
	 * @return a bool telling us if we are tracking the right arm from the
	 * controller or not.
	 */
	public boolean isTrackingRightArmFromController() {
		return rightHandTracker != null && rightHandTracker.hasPosition() && !forceArmsFromHMD;
	}

	protected Tracker[] getTrackersToReset() {
		return new Tracker[] {
			trackerPreUpdate(this.neckTracker),
			trackerPreUpdate(this.chestTracker),
			trackerPreUpdate(this.waistTracker),
			trackerPreUpdate(this.hipTracker),
			trackerPreUpdate(this.leftUpperLegTracker),
			trackerPreUpdate(this.leftLowerLegTracker),
			trackerPreUpdate(this.leftFootTracker),
			trackerPreUpdate(this.rightUpperLegTracker),
			trackerPreUpdate(this.rightLowerLegTracker),
			trackerPreUpdate(this.rightFootTracker),
			trackerPreUpdate(this.leftLowerArmTracker),
			trackerPreUpdate(this.rightLowerArmTracker),
			trackerPreUpdate(this.leftUpperArmTracker),
			trackerPreUpdate(this.rightUpperArmTracker),
			trackerPreUpdate(this.leftHandTracker),
			trackerPreUpdate(this.rightHandTracker),
			trackerPreUpdate(this.leftShoulderTracker),
			trackerPreUpdate(this.rightShoulderTracker),
		};
	}

	public void resetTrackersFull(String resetSourceName) {
		// Pass all trackers through trackerPreUpdate
		Tracker hmdTracker = trackerPreUpdate(this.hmdTracker);
		Tracker[] trackersToReset = getTrackersToReset();

		// Resets all axis of the trackers with the HMD as reference.
		Quaternion referenceRotation = new Quaternion();
		if (hmdTracker != null) {
			if (hmdTracker instanceof IMUTracker)
				hmdTracker.resetFull(referenceRotation);
			hmdTracker.getRotation(referenceRotation);
		}

		for (Tracker tracker : trackersToReset) {
			if (tracker != null) {
				tracker.resetFull(referenceRotation);
			}
		}

		// tell the clip corrector to reset its floor level on the next update
		// of the computed trackers
		this.legTweaks.resetFloorLevel();
		this.legTweaks.resetBuffer();

		LogManager.info("Reset: full (%s)".formatted(resetSourceName));
	}

	private boolean shouldResetMounting(TrackerPosition position) {
		return position != null
			// TODO: Feet can't currently be reset using this method, maybe
			// they'll need a separate step just for them?
			&& position != TrackerPosition.LEFT_FOOT
			&& position != TrackerPosition.RIGHT_FOOT;
	}

	private boolean shouldReverseYaw(TrackerPosition position) {
		switch (position) {
			case LEFT_UPPER_LEG:
			case RIGHT_UPPER_LEG:
			case LEFT_LOWER_ARM:
			case LEFT_HAND:
			case RIGHT_LOWER_ARM:
			case RIGHT_HAND:
				return true;
			default:
				return false;
		}
	}

	@VRServerThread
	public void resetTrackersMounting(String resetSourceName) {
		// Pass all trackers through trackerPreUpdate
		Tracker[] trackersToReset = getTrackersToReset();

		if (hmdTracker != null) {
			if (hmdTracker instanceof IMUTracker)
				hmdTracker.resetMounting(shouldReverseYaw(hmdTracker.getBodyPosition()));
		}

		for (Tracker tracker : trackersToReset) {
			if (tracker != null && shouldResetMounting(tracker.getBodyPosition())) {
				tracker.resetMounting(shouldReverseYaw(tracker.getBodyPosition()));
			}
		}
		this.legTweaks.resetBuffer();

		LogManager.info("Reset: mounting (%s)".formatted(resetSourceName));
	}

	@VRServerThread
	public void resetTrackersYaw(String resetSourceName) {
		// Pass all trackers through trackerPreUpdate
		Tracker hmdTracker = trackerPreUpdate(this.hmdTracker);
		Tracker[] trackersToReset = getTrackersToReset();

		// Resets the yaw of the trackers with the HMD as reference.
		Quaternion referenceRotation = new Quaternion();
		if (hmdTracker != null) {
			if (hmdTracker instanceof IMUTracker)
				hmdTracker.resetYaw(referenceRotation);
			hmdTracker.getRotation(referenceRotation);
		}

		for (Tracker tracker : trackersToReset) {
			if (tracker != null) {
				tracker.resetYaw(referenceRotation);
			}
		}
		this.legTweaks.resetBuffer();

		LogManager.info("Reset: yaw (%s)".formatted(resetSourceName));
	}

	public void updateTapDetectionConfig() {
		tapDetectionManager.updateConfig();
	}

	public void updateLegTweaksConfig() {
		legTweaks.updateConfig();
	}

	public boolean[] getLegTweaksState() {
		boolean[] state = new boolean[2];
		state[0] = this.legTweaks.getFloorclipEnabled();
		state[1] = this.legTweaks.getSkatingReductionEnabled();
		return state;
	}

	// master enable/disable of all leg tweaks (for autobone)
	@VRServerThread
	public void setLegTweaksEnabled(boolean value) {
		this.legTweaks.setEnabled(value);
	}

	@VRServerThread
	public void setFloorclipEnabled(boolean value) {
		humanPoseManager.setToggle(SkeletonConfigToggles.FLOOR_CLIP, value);
	}

	@VRServerThread
	public void setSkatingCorrectionEnabled(boolean value) {
		humanPoseManager.setToggle(SkeletonConfigToggles.SKATING_CORRECTION, value);
	}
}
