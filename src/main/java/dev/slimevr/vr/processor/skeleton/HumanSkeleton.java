package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.processor.ComputedHumanPoseTracker;
import dev.slimevr.vr.processor.ComputedHumanPoseTrackerPosition;
import dev.slimevr.vr.processor.TransformNode;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.collections.FastList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HumanSkeleton extends Skeleton implements SkeletonConfigCallback {

	public final SkeletonConfig skeletonConfig;
	public final List<BoneInfo> currentBoneInfo = new ArrayList<>();
	// #region Upper body nodes (torso)
	protected final TransformNode hmdNode = new TransformNode("HMD", false);
	protected final TransformNode headNode = new TransformNode("Head", false);
	protected final TransformNode neckNode = new TransformNode("Neck", false);
	protected final TransformNode chestNode = new TransformNode("Chest", false);
	protected final TransformNode trackerChestNode = new TransformNode("Chest-Tracker", false);
	protected final TransformNode waistNode = new TransformNode("Waist", false);
	protected final TransformNode hipNode = new TransformNode("Hip", false);
	// #endregion
	protected final TransformNode trackerWaistNode = new TransformNode("Waist-Tracker", false);
	// #region Lower body nodes (legs)
	protected final TransformNode leftHipNode = new TransformNode("Left-Hip", false);
	protected final TransformNode leftKneeNode = new TransformNode("Left-Knee", false);
	protected final TransformNode trackerLeftKneeNode = new TransformNode(
		"Left-Knee-Tracker",
		false
	);
	protected final TransformNode leftAnkleNode = new TransformNode("Left-Ankle", false);
	protected final TransformNode leftFootNode = new TransformNode("Left-Foot", false);
	protected final TransformNode trackerLeftFootNode = new TransformNode(
		"Left-Foot-Tracker",
		false
	);
	protected final TransformNode rightHipNode = new TransformNode("Right-Hip", false);
	protected final TransformNode rightKneeNode = new TransformNode("Right-Knee", false);
	protected final TransformNode trackerRightKneeNode = new TransformNode(
		"Right-Knee-Tracker",
		false
	);
	protected final TransformNode rightAnkleNode = new TransformNode("Right-Ankle", false);
	protected final TransformNode rightFootNode = new TransformNode("Right-Foot", false);
	protected final TransformNode trackerRightFootNode = new TransformNode(
		"Right-Foot-Tracker",
		false
	);
	// #region Arms (from controllers)
	protected final TransformNode leftControllerNodeContrl = new TransformNode(
		"Left-Controller-Contrl",
		false
	);
	protected final TransformNode rightControllerNodeContrl = new TransformNode(
		"Right-Controller-Contrl",
		false
	);
	// #endregion
	protected final TransformNode leftWristNodeContrl = new TransformNode(
		"Left-Wrist-Contrl",
		false
	);
	protected final TransformNode rightWristNodeContrl = new TransformNode(
		"Right-Wrist-Contrl",
		false
	);
	protected final TransformNode leftElbowNodeContrl = new TransformNode(
		"Left-Elbow-Contrl",
		false
	);
	protected final TransformNode rightElbowNodeContrl = new TransformNode(
		"Right-Elbow-Contrl",
		false
	);
	protected final TransformNode trackerLeftElbowNodeContrl = new TransformNode(
		"Left-Elbow-Tracker-Contrl",
		false
	);
	protected final TransformNode trackerRightElbowNodeContrl = new TransformNode(
		"Right-Elbow-Tracker-Contrl",
		false
	);
	// #region Arms (from HMD)
	protected final TransformNode leftShoulderNodeHmd = new TransformNode(
		"Left-Shoulder-Hmd",
		false
	);
	protected final TransformNode rightShoulderNodeHmd = new TransformNode(
		"Right-Shoulder-Hmd",
		false
	);
	// #endregion
	protected final TransformNode leftElbowNodeHmd = new TransformNode("Left-Elbow-Hmd", false);
	protected final TransformNode rightElbowNodeHmd = new TransformNode("Right-Elbow-Hmd", false);
	protected final TransformNode trackerLeftElbowNodeHmd = new TransformNode(
		"Left-Elbow-Tracker-Hmd",
		false
	);
	protected final TransformNode trackerRightElbowNodeHmd = new TransformNode(
		"Right-Elbow-Tracker-Hmd",
		false
	);
	protected final TransformNode leftWristNodeHmd = new TransformNode("Left-Wrist-Hmd", false);
	protected final TransformNode rightWristNodeHmd = new TransformNode("Right-Wrist-Hmd", false);
	protected final TransformNode leftHandNodeHmd = new TransformNode("Left-Hand-Hmd", false);
	protected final TransformNode rightHandNodeHmd = new TransformNode("Right-Hand-Hmd", false);
	protected final TransformNode trackerLeftHandNodeHmd = new TransformNode(
		"Left-Hand-Tracker-Hmd",
		false
	);
	protected final TransformNode trackerRightHandNodeHmd = new TransformNode(
		"Right-Hand-Tracker-Hmd",
		false
	);
	// #endregion
	protected final Quaternion kneeRotation = new Quaternion();
	// #region Buffers
	private final Vector3f posBuf = new Vector3f();
	private final Quaternion rotBuf1 = new Quaternion();
	private final Quaternion rotBuf2 = new Quaternion();
	private final Quaternion rotBuf3 = new Quaternion();
	private final Quaternion rotBuf4 = new Quaternion();
	protected boolean hasSpineTracker;
	protected boolean hasKneeTrackers;
	protected float minKneePitch = 0f * FastMath.DEG_TO_RAD;
	protected float maxKneePitch = 90f * FastMath.DEG_TO_RAD;
	static final Quaternion FORWARD_QUATERNION = new Quaternion()
		.fromAngles(FastMath.HALF_PI, 0, 0);
	static final float FLOOR_OFFSET = 0.05f;
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
	protected Tracker leftControllerTracker;
	protected Tracker rightControllerTracker;
	protected Tracker leftLowerArmTracker;
	protected Tracker rightLowerArmTracker;
	// #endregion
	protected Tracker leftUpperArmTracker;
	protected Tracker rightUpperArmTracker;
	protected Tracker leftHandTracker;
	protected Tracker rightHandTracker;
	// #region Tracker Output
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

	// #region FK Settings
	// Toggles for extended models
	protected boolean extendedSpineModel = true;
	protected boolean extendedPelvisModel = true;
	protected boolean extendedKneeModel = true;

	// Extended Spine Model
	protected float waistChestHipAveraging = 0.5f;
	protected float waistChestPelvisAveraging = 0.18f;
	protected float hipSpinePelvisAveraging = 0.25f;
	protected float pelvisHipAveraging = FastMath.ONE_THIRD;
	// Extended Pelvis Model
	protected float pelvisWaistTrackerAveraging = 0.75f;
	// Extended Knee Model
	protected float ankleKneeTrackerAveraging = 0.25f;

	protected boolean forceElbowsFromHMD = false;
	// #endregion

	// #region Constructors
	protected HumanSkeleton(List<? extends ComputedHumanPoseTracker> computedTrackers) {
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

		// #region Assemble skeleton arms from controllers
		leftControllerNodeContrl.attachChild(leftWristNodeContrl);
		rightControllerNodeContrl.attachChild(rightWristNodeContrl);
		leftWristNodeContrl.attachChild(leftElbowNodeContrl);
		rightWristNodeContrl.attachChild(rightElbowNodeContrl);
		// #endregion

		// #region Assemble skeleton arms from neck
		neckNode.attachChild(leftShoulderNodeHmd);
		neckNode.attachChild(rightShoulderNodeHmd);

		leftShoulderNodeHmd.attachChild(leftElbowNodeHmd);
		rightShoulderNodeHmd.attachChild(rightElbowNodeHmd);

		leftElbowNodeHmd.attachChild(leftWristNodeHmd);
		rightElbowNodeHmd.attachChild(rightWristNodeHmd);

		leftWristNodeHmd.attachChild(leftHandNodeHmd);
		rightWristNodeHmd.attachChild(rightHandNodeHmd);
		// #endregion

		// #region Attach tracker nodes for offsets
		chestNode.attachChild(trackerChestNode);
		hipNode.attachChild(trackerWaistNode);

		leftKneeNode.attachChild(trackerLeftKneeNode);
		rightKneeNode.attachChild(trackerRightKneeNode);

		leftFootNode.attachChild(trackerLeftFootNode);
		rightFootNode.attachChild(trackerRightFootNode);

		leftElbowNodeContrl.attachChild(trackerLeftElbowNodeContrl);
		rightElbowNodeContrl.attachChild(trackerRightElbowNodeContrl);

		leftElbowNodeHmd.attachChild(trackerLeftElbowNodeHmd);
		rightElbowNodeHmd.attachChild(trackerRightElbowNodeHmd);

		leftHandNodeHmd.attachChild(trackerLeftHandNodeHmd);
		rightHandNodeHmd.attachChild(trackerRightHandNodeHmd);
		// #endregion

		// Set default skeleton configuration (callback automatically sets
		// initial offsets)
		skeletonConfig = new SkeletonConfig(true, this);

		if (computedTrackers != null) {
			setComputedTrackers(computedTrackers);
		}
		fillNullComputedTrackers(true);
		resetBones();
	}

	public HumanSkeleton(
		VRServer server,
		List<? extends ComputedHumanPoseTracker> computedTrackers
	) {
		this(computedTrackers);
		setTrackersFromServer(server);
		skeletonConfig.loadFromConfig(server.config);
	}

	public HumanSkeleton(
		List<? extends Tracker> trackers,
		List<? extends ComputedHumanPoseTracker> computedTrackers
	) {
		this(computedTrackers);

		if (trackers != null) {
			setTrackersFromList(trackers);
		} else {
			setTrackersFromList(new FastList<Tracker>(0));
		}
	}

	public HumanSkeleton(
		List<? extends Tracker> trackers,
		List<? extends ComputedHumanPoseTracker> computedTrackers,
		Map<SkeletonConfigValue, Float> configs,
		Map<SkeletonConfigValue, Float> altConfigs
	) {
		// Initialize
		this(trackers, computedTrackers);

		// Set configs
		if (altConfigs != null) {
			// Set alts first, so if there's any overlap it doesn't affect the
			// values
			skeletonConfig.setConfigs(altConfigs, null);
		}
		skeletonConfig.setConfigs(configs, null);
	}

	public HumanSkeleton(
		List<? extends Tracker> trackers,
		List<? extends ComputedHumanPoseTracker> computedTrackers,
		Map<SkeletonConfigValue, Float> configs
	) {
		this(trackers, computedTrackers, configs, null);
	}
	// #endregion

	protected void resetBones() {
		currentBoneInfo.clear();

		// #region Assemble skeleton from hmd to hip
		currentBoneInfo.add(new BoneInfo(BoneType.HEAD, headNode));
		currentBoneInfo.add(new BoneInfo(BoneType.NECK, neckNode));
		currentBoneInfo.add(new BoneInfo(BoneType.CHEST, chestNode));
		currentBoneInfo.add(new BoneInfo(BoneType.WAIST, waistNode));
		currentBoneInfo.add(new BoneInfo(BoneType.HIP, hipNode));
		// #endregion

		// #region Assemble skeleton from hips to feet
		if (leftLowerLegTracker != null || leftUpperLegTracker != null || leftFootTracker != null) {
			currentBoneInfo.add(new BoneInfo(BoneType.LEFT_HIP, leftHipNode));
			currentBoneInfo.add(new BoneInfo(BoneType.LEFT_UPPER_LEG, leftKneeNode));
			currentBoneInfo.add(new BoneInfo(BoneType.LEFT_LOWER_LEG, leftKneeNode));
			currentBoneInfo.add(new BoneInfo(BoneType.LEFT_FOOT, leftKneeNode));
		}

		if (
			rightLowerLegTracker != null || rightUpperLegTracker != null || rightFootTracker != null
		) {
			currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_HIP, rightHipNode));
			currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_UPPER_LEG, rightKneeNode));
			currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_LOWER_LEG, rightKneeNode));
			currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_FOOT, rightKneeNode));
		}
		// #endregion

		// #region Assemble skeleton arms from controllers
		if (leftControllerTracker != null) {
			currentBoneInfo.add(new BoneInfo(BoneType.LEFT_CONTROLLER, leftWristNodeContrl));
			if (leftLowerArmTracker != null) {
				currentBoneInfo.add(new BoneInfo(BoneType.LEFT_LOWER_ARM, leftElbowNodeContrl));
			}
		}
		if (rightControllerTracker != null) {
			currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_CONTROLLER, rightWristNodeContrl));
			if (rightLowerArmTracker != null) {
				currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_LOWER_ARM, rightElbowNodeContrl));
			}
		}
		// #endregion

		// #region Assemble skeleton arms from chest
		if (rightUpperArmTracker != null) {
			currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_SHOULDER, rightShoulderNodeHmd));
			currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_UPPER_ARM, rightElbowNodeHmd));
			if (rightControllerTracker == null && rightLowerArmTracker != null) {
				currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_LOWER_ARM, rightWristNodeHmd));
				if (rightHandTracker != null) {
					currentBoneInfo.add(new BoneInfo(BoneType.RIGHT_HAND, rightHandNodeHmd));
				}
			}
		}
		if (leftUpperArmTracker != null) {
			currentBoneInfo.add(new BoneInfo(BoneType.LEFT_SHOULDER, leftShoulderNodeHmd));
			currentBoneInfo.add(new BoneInfo(BoneType.LEFT_UPPER_ARM, leftElbowNodeHmd));
			if (leftControllerTracker == null && leftLowerArmTracker != null) {
				currentBoneInfo.add(new BoneInfo(BoneType.LEFT_LOWER_ARM, leftWristNodeHmd));
				if (leftHandTracker != null) {
					currentBoneInfo.add(new BoneInfo(BoneType.LEFT_HAND, leftHandNodeHmd));
				}
			}
		}
		// #endregion

		// #region Attach tracker nodes for offsets
		if (true) { // Set to false to skip tracker bones
			currentBoneInfo.add(new BoneInfo(BoneType.CHEST_TRACKER, trackerChestNode));
			currentBoneInfo.add(new BoneInfo(BoneType.HIP_TRACKER, trackerWaistNode));

			if (
				leftLowerLegTracker != null
					|| leftUpperLegTracker != null
					|| leftFootTracker != null
			) {
				currentBoneInfo.add(new BoneInfo(BoneType.LEFT_KNEE_TRACKER, trackerLeftKneeNode));
				currentBoneInfo.add(new BoneInfo(BoneType.LEFT_FOOT_TRACKER, trackerLeftFootNode));
			}
			if (
				rightLowerLegTracker != null
					|| rightUpperLegTracker != null
					|| rightFootTracker != null
			) {
				currentBoneInfo
					.add(new BoneInfo(BoneType.RIGHT_KNEE_TRACKER, trackerRightKneeNode));
				currentBoneInfo
					.add(new BoneInfo(BoneType.RIGHT_FOOT_TRACKER, trackerRightFootNode));
			}
			if (leftControllerTracker != null && leftLowerArmTracker != null) {
				currentBoneInfo
					.add(new BoneInfo(BoneType.LEFT_ELBOW_TRACKER, trackerLeftElbowNodeContrl));
			}
			if (rightControllerTracker != null && rightLowerArmTracker != null) {
				currentBoneInfo
					.add(
						new BoneInfo(BoneType.RIGHT_ELBOW_TRACKER, trackerRightElbowNodeContrl)
					);
			}

			if (leftControllerTracker == null && leftLowerArmTracker != null) {
				currentBoneInfo
					.add(new BoneInfo(BoneType.LEFT_ELBOW_TRACKER, trackerLeftElbowNodeHmd));
			}
			if (rightControllerTracker != null && rightLowerArmTracker != null) {
				currentBoneInfo
					.add(new BoneInfo(BoneType.RIGHT_ELBOW_TRACKER, trackerLeftElbowNodeHmd));
			}

			if (leftHandTracker != null) {
				currentBoneInfo
					.add(new BoneInfo(BoneType.LEFT_HAND_TRACKER, trackerLeftHandNodeHmd));
			}
			if (rightHandTracker != null) {
				currentBoneInfo
					.add(new BoneInfo(BoneType.RIGHT_HAND_TRACKER, trackerLeftHandNodeHmd));
			}
		}
		// #endregion
	}

	// #region Set trackers inputs
	public void setTrackersFromList(List<? extends Tracker> trackers, boolean setHmd) {
		if (setHmd) {
			this.hmdTracker = TrackerUtils
				.findNonComputedHumanPoseTrackerForBodyPosition(
					trackers,
					TrackerPosition.HMD
				);
		}

		this.leftControllerTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_CONTROLLER
			);
		this.rightControllerTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_CONTROLLER
			);
		this.neckTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.NECK
			);
		this.chestTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.CHEST
			);
		this.waistTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.WAIST
			);
		this.hipTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.HIP
			);
		this.leftUpperLegTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_UPPER_LEG
			);
		this.leftLowerLegTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_LOWER_LEG
			);
		this.leftFootTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_FOOT
			);
		this.rightUpperLegTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_UPPER_LEG
			);
		this.rightLowerLegTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_LOWER_LEG
			);
		this.rightFootTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_FOOT
			);
		this.leftLowerArmTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_LOWER_ARM
			);
		this.rightLowerArmTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_LOWER_ARM
			);
		this.leftUpperArmTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_UPPER_ARM
			);
		this.rightUpperArmTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_UPPER_ARM
			);
		this.leftHandTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.LEFT_HAND
			);
		this.rightHandTracker = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(
				trackers,
				TrackerPosition.RIGHT_HAND
			);
		resetBones();
	}

	public void setTrackersFromList(List<? extends Tracker> trackers) {
		setTrackersFromList(trackers, true);
	}

	public void setTrackersFromServer(VRServer server) {
		this.hmdTracker = server.hmdTracker;
		setTrackersFromList(server.getAllTrackers(), false);
	}

	public void setComputedTracker(ComputedHumanPoseTracker tracker) {
		switch (tracker.getTrackerRole()) {
			case CHEST:
				computedChestTracker = tracker;
				break;
			case WAIST:
				computedWaistTracker = tracker;
				break;
			case LEFT_KNEE:
				computedLeftKneeTracker = tracker;
				break;
			case LEFT_FOOT:
				computedLeftFootTracker = tracker;
				break;
			case RIGHT_KNEE:
				computedRightKneeTracker = tracker;
				break;
			case RIGHT_FOOT:
				computedRightFootTracker = tracker;
				break;
			case LEFT_ELBOW:
				computedLeftElbowTracker = tracker;
				break;
			case RIGHT_ELBOW:
				computedRightElbowTracker = tracker;
				break;
			case LEFT_HAND:
				computedLeftHandTracker = tracker;
				break;
			case RIGHT_HAND:
				computedRightHandTracker = tracker;
				break;
			default:
				break;
		}
	}

	public void setComputedTrackers(List<? extends ComputedHumanPoseTracker> trackers) {
		for (ComputedHumanPoseTracker t : trackers) {
			setComputedTracker(t);
		}
	}
	// #endregion

	public void setComputedTrackersAndFillNull(
		List<? extends ComputedHumanPoseTracker> trackers,
		boolean onlyFillWaistAndFeet
	) {
		setComputedTrackers(trackers);
		fillNullComputedTrackers(onlyFillWaistAndFeet);
	}
	// #endregion

	// TODO What's onlyFillWaistAndFeet for? Needs refactoring.
	public void fillNullComputedTrackers(boolean onlyFillWaistAndFeet) {
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

		if (!onlyFillWaistAndFeet) {
			if (computedChestTracker == null) {
				computedChestTracker = new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.CHEST,
					TrackerRole.CHEST
				);
				computedChestTracker.setStatus(TrackerStatus.OK);
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
	}

	// #region Get Trackers
	public ComputedHumanPoseTracker getComputedTracker(TrackerRole trackerRole) {
		switch (trackerRole) {
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
	// Useful for sub-classes that need to return a sub-tracker (like
	// PoseFrameTracker -> TrackerFrame)
	protected Tracker trackerPreUpdate(Tracker tracker) {
		return tracker;
	}

	// Updates the pose from tracker positions
	@VRServerThread
	@Override
	public void updatePose() {
		updateLocalTransforms();
		updateRootTrackers();
		updateComputedTrackers();
	}
	// #endregion

	protected void updateRootTrackers() {
		hmdNode.update();
		leftControllerNodeContrl.update();
		rightControllerNodeContrl.update();
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

		Tracker leftControllerTracker = trackerPreUpdate(this.leftControllerTracker);
		Tracker rightControllerTracker = trackerPreUpdate(this.rightControllerTracker);
		Tracker rightLowerArmTracker = trackerPreUpdate(this.rightLowerArmTracker);
		Tracker leftLowerArmTracker = trackerPreUpdate(this.leftLowerArmTracker);
		Tracker rightUpperArmTracker = trackerPreUpdate(this.rightUpperArmTracker);
		Tracker leftUpperArmTracker = trackerPreUpdate(this.leftUpperArmTracker);
		Tracker leftHandTracker = trackerPreUpdate(this.leftHandTracker);
		Tracker rightHandTracker = trackerPreUpdate(this.rightHandTracker);
		// #endregion

		// hasSomething booleans
		hasSpineTracker = chestTracker != null || waistTracker != null || hipTracker != null;
		hasKneeTrackers = leftUpperLegTracker != null && rightUpperLegTracker != null;

		// HMD, head and neck
		if (hmdTracker != null) {
			hmdTracker.getPosition(posBuf);
			hmdNode.localTransform.setTranslation(posBuf);

			hmdTracker.getRotation(rotBuf1);
			hmdNode.localTransform.setRotation(rotBuf1);

			if (neckTracker != null)
				neckTracker.getRotation(rotBuf1);
			headNode.localTransform.setRotation(rotBuf1);
		} else {
			// Set to zero
			hmdNode.localTransform.setTranslation(Vector3f.ZERO);
			hmdNode.localTransform.setRotation(Quaternion.IDENTITY);
			headNode.localTransform.setRotation(Quaternion.IDENTITY);
		}

		// Spine
		if (hasSpineTracker) {
			TrackerUtils
				.getFirstAvailableTracker(chestTracker, waistTracker, hipTracker)
				.getRotation(rotBuf1);
			neckNode.localTransform.setRotation(rotBuf1);
			trackerChestNode.localTransform.setRotation(rotBuf1);

			TrackerUtils
				.getFirstAvailableTracker(waistTracker, hipTracker, chestTracker)
				.getRotation(rotBuf1);
			chestNode.localTransform.setRotation(rotBuf1);

			TrackerUtils
				.getFirstAvailableTracker(hipTracker, waistTracker, chestTracker)
				.getRotation(rotBuf1);
			waistNode.localTransform.setRotation(rotBuf1);
			hipNode.localTransform.setRotation(rotBuf1);
			trackerWaistNode.localTransform.setRotation(rotBuf1);
		} else if (hmdTracker != null) {
			// Align with last tracker's yaw (HMD or neck)
			rotBuf1.fromAngles(0, rotBuf1.getYaw(), 0);

			neckNode.localTransform.setRotation(rotBuf1);
			trackerChestNode.localTransform.setRotation(rotBuf1);
			chestNode.localTransform.setRotation(rotBuf1);
			waistNode.localTransform.setRotation(rotBuf1);
			hipNode.localTransform.setRotation(rotBuf1);
			trackerWaistNode.localTransform.setRotation(rotBuf1);
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
			// Align with the hip's yaw
			hipNode.localTransform.getRotation(rotBuf2);
			rotBuf2.fromAngles(0, rotBuf2.getYaw(), 0);
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
			leftKneeNode.localTransform.getRotation(rotBuf1);
			leftHipNode.localTransform.getRotation(rotBuf2);

			// Get the knee's inverse rotation.
			rotBuf3.set(rotBuf2);
			rotBuf3.inverseLocal();

			// Only rotate on local yaw and pitch
			//
			// R = InverseKnee * Ankle
			// C = Quaternion(-R.x, 0, 0, R.w)
			// Knee = Knee * R * C
			// normalize(Knee)
			rotBuf3.multLocal(rotBuf1);
			rotBuf1.set(-rotBuf3.getX(), 0, 0, rotBuf3.getW());
			rotBuf1.set(rotBuf2.mult(rotBuf3).mult(rotBuf1));
			rotBuf1.normalizeLocal();

			rotBuf1.slerpLocal(rotBuf2, ankleKneeTrackerAveraging);
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
			// Align with the hip's yaw
			hipNode.localTransform.getRotation(rotBuf2);
			rotBuf2.fromAngles(0, rotBuf2.getYaw(), 0);
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
			rightKneeNode.localTransform.getRotation(rotBuf1);
			rightHipNode.localTransform.getRotation(rotBuf2);

			// Get the knee's inverse rotation.
			rotBuf3.set(rotBuf2);
			rotBuf3.inverseLocal();

			// Only rotate on local yaw and pitch
			//
			// R = InverseKnee * Ankle
			// C = Quaternion(-R.x, 0, 0, R.w)
			// Knee = Knee * R * C
			// normalize(Knee)
			rotBuf3.multLocal(rotBuf1);
			rotBuf1.set(-rotBuf3.getX(), 0, 0, rotBuf3.getW());
			rotBuf1.set(rotBuf2.mult(rotBuf3).mult(rotBuf1));
			rotBuf1.normalizeLocal();

			rotBuf1.slerpLocal(rotBuf2, ankleKneeTrackerAveraging);
			trackerRightKneeNode.localTransform.setRotation(rotBuf1);
		}

		// Extended spine
		if (extendedSpineModel && hasSpineTracker) {
			if (
				(chestTracker != null && (waistTracker == null || hipTracker == null))
					|| (waistTracker != null && hipTracker == null)
			) {
				// Tries to guess missing lower spine trackers by interpolating
				// rotations
				if (waistTracker == null) {
					if (hipTracker != null) {
						// Calculates waist from chest + hip
						chestTracker.getRotation(rotBuf1);
						hipTracker.getRotation(rotBuf2);

						// Interpolate between the chest and the hip
						rotBuf1.slerpLocal(rotBuf2, waistChestHipAveraging);
						chestNode.localTransform.setRotation(rotBuf1);
					} else if (hasKneeTrackers) {
						// Calculates waist from chest + pelvis
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
						rotBuf3.pureSlerpLocal(rotBuf1, waistChestPelvisAveraging);

						chestNode.localTransform.setRotation(rotBuf3);
					}
				}
				if (hipTracker == null && hasKneeTrackers) {
					// Calculates hip from (chest or waist) + pelvis
					leftHipNode.localTransform.getRotation(rotBuf1);
					rightHipNode.localTransform.getRotation(rotBuf2);
					TrackerUtils
						.getFirstAvailableTracker(waistTracker, chestTracker, null)
						.getRotation(rotBuf3);

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
					rotBuf3.pureSlerpLocal(rotBuf1, hipSpinePelvisAveraging);

					waistNode.localTransform.setRotation(rotBuf3);
				}
			}
		}

		// Extended pelvis
		if (extendedPelvisModel && hasKneeTrackers) {
			// Average pelvis between two legs
			leftHipNode.localTransform.getRotation(rotBuf1);
			rightHipNode.localTransform.getRotation(rotBuf2);
			rotBuf2.nlerp(rotBuf1, 0.5f);
			waistNode.localTransform.getRotation(rotBuf1);

			rotBuf2.slerpLocal(rotBuf1, pelvisHipAveraging);
			hipNode.localTransform.setRotation(rotBuf2);

			// Averages the trackerWaistNode's rotation with the calculated
			// pelvis' on local yaw and roll. git blame AxisAngle :p
			leftHipNode.localTransform.getRotation(rotBuf1);
			rightHipNode.localTransform.getRotation(rotBuf2);
			waistNode.localTransform.getRotation(rotBuf3);

			// Get the rotation relative to where we expect the upper legs to be
			rotBuf3.mult(FORWARD_QUATERNION, rotBuf4);
			if (rotBuf4.dot(rotBuf1) < 0.0f) {
				rotBuf1.negateLocal();
			}
			if (rotBuf4.dot(rotBuf2) < 0.0f) {
				rotBuf2.negateLocal();
			}

			// Get waistNode's inverse rotation.
			rotBuf4.set(rotBuf3);
			rotBuf4.inverseLocal();

			// Only rotate on local yaw and pitch
			//
			// R = InverseWaist * (LeftLeft + RightLeg)
			// C = Quaternion(-R.x, 0, 0, R.w)
			// Pelvis = Waist * R * C
			// normalize(Pelvis)
			rotBuf4.multLocal(rotBuf1.add(rotBuf2));
			rotBuf2.set(-rotBuf4.getX(), 0, 0, rotBuf4.getW());
			rotBuf1.set(rotBuf3.mult(rotBuf4).mult(rotBuf2));
			rotBuf1.normalizeLocal();

			rotBuf1.slerpLocal(rotBuf3, pelvisWaistTrackerAveraging);
			trackerWaistNode.localTransform.setRotation(rotBuf1);
		}

		// Left arm from HMD
		if (leftUpperArmTracker != null || leftLowerArmTracker != null) {
			TrackerUtils
				.getFirstAvailableTracker(leftUpperArmTracker, leftLowerArmTracker, null)
				.getRotation(rotBuf1);

			leftShoulderNodeHmd.localTransform.setRotation(rotBuf1);
			trackerLeftElbowNodeHmd.localTransform.setRotation(rotBuf1);

			TrackerUtils
				.getFirstAvailableTracker(leftLowerArmTracker, leftUpperArmTracker, null)
				.getRotation(rotBuf1);

			leftElbowNodeHmd.localTransform.setRotation(rotBuf1);
		}
		if (leftHandTracker != null) {
			leftHandTracker.getRotation(rotBuf1);
			leftWristNodeHmd.localTransform.setRotation(rotBuf1);
			leftHandNodeHmd.localTransform.setRotation(rotBuf1);
			trackerLeftHandNodeHmd.localTransform.setRotation(rotBuf1);
		}

		// Right arm from HMD
		if (rightUpperArmTracker != null || rightLowerArmTracker != null) {
			TrackerUtils
				.getFirstAvailableTracker(rightUpperArmTracker, rightLowerArmTracker, null)
				.getRotation(rotBuf1);

			rightShoulderNodeHmd.localTransform.setRotation(rotBuf1);
			trackerRightElbowNodeHmd.localTransform.setRotation(rotBuf1);

			TrackerUtils
				.getFirstAvailableTracker(rightLowerArmTracker, rightUpperArmTracker, null)
				.getRotation(rotBuf1);

			rightElbowNodeHmd.localTransform.setRotation(rotBuf1);
		}
		if (rightHandTracker != null) {
			rightHandTracker.getRotation(rotBuf1);
			rightWristNodeHmd.localTransform.setRotation(rotBuf1);
			rightHandNodeHmd.localTransform.setRotation(rotBuf1);
			trackerRightHandNodeHmd.localTransform.setRotation(rotBuf1);
		}

		// Left elbow from SteamVR controller
		if (leftControllerTracker != null) {
			leftControllerTracker.getPosition(posBuf);
			leftControllerTracker.getRotation(rotBuf1);
			leftControllerNodeContrl.localTransform.setTranslation(posBuf);
			leftControllerNodeContrl.localTransform.setRotation(rotBuf1);

			if (leftLowerArmTracker != null || leftUpperArmTracker != null) {
				TrackerUtils
					.getFirstAvailableTracker(leftLowerArmTracker, leftUpperArmTracker, null)
					.getRotation(rotBuf1);

				leftWristNodeContrl.localTransform.setRotation(rotBuf1);

				TrackerUtils
					.getFirstAvailableTracker(leftUpperArmTracker, leftLowerArmTracker, null)
					.getRotation(rotBuf1);

				leftElbowNodeContrl.localTransform.setRotation(rotBuf1);
				trackerLeftElbowNodeContrl.localTransform.setRotation(rotBuf1);
			}
		}

		// Right elbow from SteamVR controller
		if (rightControllerTracker != null) {
			rightControllerTracker.getPosition(posBuf);
			rightControllerTracker.getRotation(rotBuf1);
			rightControllerNodeContrl.localTransform.setTranslation(posBuf);
			rightControllerNodeContrl.localTransform.setRotation(rotBuf1);

			if (rightLowerArmTracker != null || rightUpperArmTracker != null) {
				TrackerUtils
					.getFirstAvailableTracker(rightLowerArmTracker, rightUpperArmTracker, null)
					.getRotation(rotBuf1);

				rightWristNodeContrl.localTransform.setRotation(rotBuf1);

				TrackerUtils
					.getFirstAvailableTracker(rightUpperArmTracker, rightLowerArmTracker, null)
					.getRotation(rotBuf1);

				rightElbowNodeContrl.localTransform.setRotation(rotBuf1);
				trackerRightElbowNodeContrl.localTransform.setRotation(rotBuf1);
			}
		}
	}

	// #region Update the output trackers
	protected void updateComputedTrackers() {
		if (computedChestTracker != null) {
			computedChestTracker.position.set(trackerChestNode.worldTransform.getTranslation());
			computedChestTracker.rotation.set(trackerChestNode.worldTransform.getRotation());
			computedChestTracker.dataTick();
		}

		if (computedWaistTracker != null) {
			computedWaistTracker.position.set(trackerWaistNode.worldTransform.getTranslation());
			computedWaistTracker.rotation.set(trackerWaistNode.worldTransform.getRotation());
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
			if (leftControllerTracker != null && forceElbowsFromHMD == false) { // From
																				// controller
				computedLeftElbowTracker.position
					.set(trackerLeftElbowNodeContrl.worldTransform.getTranslation());
				computedLeftElbowTracker.rotation
					.set(trackerLeftElbowNodeContrl.worldTransform.getRotation());
			} else { // From HMD
				computedLeftElbowTracker.position
					.set(trackerLeftElbowNodeHmd.worldTransform.getTranslation());
				computedLeftElbowTracker.rotation
					.set(trackerLeftElbowNodeHmd.worldTransform.getRotation());
			}
			computedLeftElbowTracker.dataTick();
		}

		if (computedRightElbowTracker != null) {
			if (rightControllerTracker != null && forceElbowsFromHMD == false) { // From
																					// controller
				computedRightElbowTracker.position
					.set(trackerRightElbowNodeContrl.worldTransform.getTranslation());
				computedRightElbowTracker.rotation
					.set(trackerRightElbowNodeContrl.worldTransform.getRotation());
			} else { // From HMD
				computedRightElbowTracker.position
					.set(trackerRightElbowNodeHmd.worldTransform.getTranslation());
				computedRightElbowTracker.rotation
					.set(trackerRightElbowNodeHmd.worldTransform.getRotation());
			}
			computedRightElbowTracker.dataTick();
		}

		if (computedLeftHandTracker != null) {
			computedLeftHandTracker.position
				.set(trackerLeftHandNodeHmd.worldTransform.getTranslation());
			computedLeftHandTracker.rotation
				.set(trackerLeftHandNodeHmd.worldTransform.getRotation());
			computedLeftHandTracker.dataTick();
		}

		if (computedRightHandTracker != null) {
			computedRightHandTracker.position
				.set(trackerRightHandNodeHmd.worldTransform.getTranslation());
			computedRightHandTracker.rotation
				.set(trackerRightHandNodeHmd.worldTransform.getRotation());
			computedRightHandTracker.dataTick();
		}
	}
	// #endregion
	// #endregion

	// #region Skeleton Config
	@Override
	public void updateConfigState(SkeletonConfigValue config, float newValue) {
		// Do nothing, the node offset callback handles all that's needed
	}

	@Override
	public void updateToggleState(SkeletonConfigToggle configToggle, boolean newValue) {
		if (configToggle == null) {
			return;
		}

		// Cache the values of these configs
		switch (configToggle) {
			case EXTENDED_SPINE_MODEL:
				extendedSpineModel = newValue;
				break;
			case EXTENDED_PELVIS_MODEL:
				extendedPelvisModel = newValue;
				break;
			case EXTENDED_KNEE_MODEL:
				extendedKneeModel = newValue;
				break;
		}
	}

	@Override
	public void updateNodeOffset(BoneType nodeOffset, Vector3f offset) {
		if (nodeOffset == null) {
			return;
		}

		switch (nodeOffset) {
			case HEAD:
				headNode.localTransform.setTranslation(offset);
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
				trackerWaistNode.localTransform.setTranslation(offset);
				break;
			case LEFT_HIP:
				leftHipNode.localTransform.setTranslation(offset);
				break;
			case RIGHT_HIP:
				rightHipNode.localTransform.setTranslation(offset);
				break;
			case UPPER_LEG:
			case LEFT_UPPER_LEG:
			case RIGHT_UPPER_LEG:
				leftKneeNode.localTransform.setTranslation(offset);
				rightKneeNode.localTransform.setTranslation(offset);
				break;
			case KNEE_TRACKER:
			case LEFT_KNEE_TRACKER:
			case RIGHT_KNEE_TRACKER:
				trackerLeftKneeNode.localTransform.setTranslation(offset);
				trackerRightKneeNode.localTransform.setTranslation(offset);
				break;
			case LOWER_LEG:
			case LEFT_LOWER_LEG:
			case RIGHT_LOWER_LEG:
				leftAnkleNode.localTransform.setTranslation(offset);
				rightAnkleNode.localTransform.setTranslation(offset);
				break;
			case FOOT:
			case LEFT_FOOT:
			case RIGHT_FOOT:
				leftFootNode.localTransform.setTranslation(offset);
				rightFootNode.localTransform.setTranslation(offset);
				break;
			case FOOT_TRACKER:
			case LEFT_FOOT_TRACKER:
			case RIGHT_FOOT_TRACKER:
				trackerLeftFootNode.localTransform.setTranslation(offset);
				trackerRightFootNode.localTransform.setTranslation(offset);
				break;
			case CONTROLLER:
			case LEFT_CONTROLLER:
			case RIGHT_CONTROLLER:
				leftWristNodeContrl.localTransform.setTranslation(offset);
				rightWristNodeContrl.localTransform.setTranslation(offset);
				break;
			case LOWER_ARM:
			case LEFT_LOWER_ARM:
			case RIGHT_LOWER_ARM:
				leftElbowNodeContrl.localTransform.setTranslation(offset);
				rightElbowNodeContrl.localTransform.setTranslation(offset);
				break;
			case ELBOW_TRACKER:
			case LEFT_ELBOW_TRACKER:
			case RIGHT_ELBOW_TRACKER:
				trackerLeftElbowNodeContrl.localTransform.setTranslation(offset);
				trackerRightElbowNodeContrl.localTransform.setTranslation(offset);
				trackerLeftElbowNodeHmd.localTransform.setTranslation(offset);
				trackerRightElbowNodeHmd.localTransform.setTranslation(offset);
				break;
			case LEFT_SHOULDER:
				leftShoulderNodeHmd.localTransform.setTranslation(offset);
				break;
			case RIGHT_SHOULDER:
				rightShoulderNodeHmd.localTransform.setTranslation(offset);
				break;
			case HAND:
			case LEFT_HAND:
			case RIGHT_HAND:
				leftHandNodeHmd.localTransform.setTranslation(offset);
				rightHandNodeHmd.localTransform.setTranslation(offset);
				break;
			case UPPER_ARM:
			case LEFT_UPPER_ARM:
			case RIGHT_UPPER_ARM:
				leftElbowNodeHmd.localTransform.setTranslation(offset);
				rightElbowNodeHmd.localTransform.setTranslation(offset);
				break;
			case LOWER_ARM_HMD:
				leftWristNodeHmd.localTransform.setTranslation(offset);
				rightWristNodeHmd.localTransform.setTranslation(offset);
				break;
			default:
				break;
		}

		for (BoneInfo bone : currentBoneInfo) {
			bone.updateLength();
		}
	}

	public void updatePoseAffectedByConfig(SkeletonConfigValue config) {
		switch (config) {
			case HEAD:
				headNode.update();
				updateComputedTrackers();
				break;
			case NECK:
				neckNode.update();
				updateComputedTrackers();
				break;
			case TORSO:
				hipNode.update();
				updateComputedTrackers();
				break;
			case CHEST:
				chestNode.update();
				updateComputedTrackers();
				break;
			case WAIST:
				waistNode.update();
				updateComputedTrackers();
				break;
			case HIP_OFFSET:
				trackerWaistNode.update();
				updateComputedTrackers();
				break;
			case HIPS_WIDTH:
				leftHipNode.update();
				rightHipNode.update();
				updateComputedTrackers();
				break;
			case KNEE_HEIGHT:
				leftKneeNode.update();
				rightKneeNode.update();
				break;
			case LEGS_LENGTH:
				leftKneeNode.update();
				rightKneeNode.update();
				updateComputedTrackers();
				break;
			case FOOT_LENGTH:
				leftFootNode.update();
				rightFootNode.update();
				updateComputedTrackers();
				break;
			case FOOT_OFFSET:
				leftAnkleNode.update();
				rightAnkleNode.update();
				updateComputedTrackers();
				break;
			case SKELETON_OFFSET:
				trackerChestNode.update();
				trackerWaistNode.update();
				trackerLeftKneeNode.update();
				trackerRightKneeNode.update();
				trackerLeftFootNode.update();
				trackerRightFootNode.update();
				updateComputedTrackers();
				break;
			case CONTROLLER_DISTANCE_Z:
			case CONTROLLER_DISTANCE_Y:
				leftWristNodeContrl.update();
				rightWristNodeContrl.update();
				updateComputedTrackers();
				break;
			case LOWER_ARM_LENGTH:
				leftElbowNodeContrl.update();
				rightElbowNodeContrl.update();
				leftElbowNodeHmd.update();
				rightElbowNodeHmd.update();
				updateComputedTrackers();
				break;
			case ELBOW_OFFSET:
				trackerLeftElbowNodeContrl.update();
				trackerRightElbowNodeContrl.update();
				trackerLeftElbowNodeHmd.update();
				trackerRightElbowNodeHmd.update();
				updateComputedTrackers();
				break;
			case SHOULDERS_DISTANCE:
			case SHOULDERS_WIDTH:
				leftShoulderNodeHmd.update();
				rightShoulderNodeHmd.update();
				updateComputedTrackers();
				break;
			case UPPER_ARM_LENGTH:
				leftElbowNodeHmd.update();
				rightElbowNodeHmd.update();
				updateComputedTrackers();
				break;
		}
	}
	// #endregion

	@Override
	public TransformNode getRootNode() {
		return hmdNode;
	}

	@Override
	public TransformNode[] getAllNodes() {
		List<TransformNode> nodesList = new FastList<>();

		hmdNode.depthFirstTraversal((node) -> {
			nodesList.add(node);
		});
		leftControllerNodeContrl.depthFirstTraversal((node) -> {
			nodesList.add(node);
		});
		rightControllerNodeContrl.depthFirstTraversal((node) -> {
			nodesList.add(node);
		});

		return nodesList.toArray(new TransformNode[0]);
	}

	@Override
	public SkeletonConfig getSkeletonConfig() {
		return skeletonConfig;
	}

	@Override
	public void resetSkeletonConfig(SkeletonConfigValue config) {
		if (config == null) {
			return;
		}

		Vector3f vec;
		float height;
		switch (config) {
			case HEAD:
				skeletonConfig.setConfig(SkeletonConfigValue.HEAD, null);
				break;
			case NECK:
				skeletonConfig.setConfig(SkeletonConfigValue.NECK, null);
				break;
			case TORSO: // Distance from shoulders to hip (full torso length)
				vec = new Vector3f();
				hmdTracker.getPosition(vec);
				height = vec.y;
				if (height > 0.5f) { // Reset only if floor level seems right,
										// TODO: read floor level from SteamVR
					skeletonConfig
						.setConfig(
							SkeletonConfigValue.TORSO,
							((height) * 0.42f) - skeletonConfig.getConfig(SkeletonConfigValue.NECK)
						);
				} else// if floor level is incorrect
				{
					skeletonConfig.setConfig(SkeletonConfigValue.TORSO, null);
				}
				break;
			case CHEST: // Chest is 57% of the upper body by default (shoulders
						// to chest)
				skeletonConfig
					.setConfig(
						SkeletonConfigValue.CHEST,
						skeletonConfig.getConfig(SkeletonConfigValue.TORSO) * 0.57f
					);
				break;
			case WAIST: // Waist length is from hip to waist
				skeletonConfig.setConfig(SkeletonConfigValue.WAIST, null);
				break;
			case HIP_OFFSET:
				skeletonConfig.setConfig(SkeletonConfigValue.HIP_OFFSET, null);
				break;
			case HIPS_WIDTH:
				skeletonConfig.setConfig(SkeletonConfigValue.HIPS_WIDTH, null);
				break;
			case FOOT_LENGTH:
				skeletonConfig.setConfig(SkeletonConfigValue.FOOT_LENGTH, null);
				break;
			case FOOT_OFFSET:
				skeletonConfig.setConfig(SkeletonConfigValue.FOOT_OFFSET, null);
				break;
			case SKELETON_OFFSET:
				skeletonConfig.setConfig(SkeletonConfigValue.SKELETON_OFFSET, null);
				break;
			case LEGS_LENGTH: // Set legs length to be 5cm above floor level
				vec = new Vector3f();
				hmdTracker.getPosition(vec);
				height = vec.y;
				if (height > 0.5f) { // Reset only if floor level seems right,
										// TODO: read floor level from SteamVR
					skeletonConfig
						.setConfig(
							SkeletonConfigValue.LEGS_LENGTH,
							height
								- skeletonConfig.getConfig(SkeletonConfigValue.NECK)
								- skeletonConfig.getConfig(SkeletonConfigValue.TORSO)
								- FLOOR_OFFSET
						);
				} else // if floor level is incorrect
				{
					skeletonConfig.setConfig(SkeletonConfigValue.LEGS_LENGTH, null);
				}
				resetSkeletonConfig(SkeletonConfigValue.KNEE_HEIGHT);
				break;
			case KNEE_HEIGHT: // Knees are at 55% of the legs by default
				skeletonConfig
					.setConfig(
						SkeletonConfigValue.KNEE_HEIGHT,
						skeletonConfig.getConfig(SkeletonConfigValue.LEGS_LENGTH) * 0.55f
					);
				break;
			case CONTROLLER_DISTANCE_Z:
				skeletonConfig.setConfig(SkeletonConfigValue.CONTROLLER_DISTANCE_Z, null);
				break;
			case CONTROLLER_DISTANCE_Y:
				skeletonConfig.setConfig(SkeletonConfigValue.CONTROLLER_DISTANCE_Y, null);
				break;
			case LOWER_ARM_LENGTH:
				skeletonConfig.setConfig(SkeletonConfigValue.LOWER_ARM_LENGTH, null);
				break;
			case ELBOW_OFFSET:
				skeletonConfig.setConfig(SkeletonConfigValue.ELBOW_OFFSET, null);
				break;
			case SHOULDERS_DISTANCE:
				skeletonConfig.setConfig(SkeletonConfigValue.SHOULDERS_DISTANCE, null);
				break;
			case SHOULDERS_WIDTH:
				skeletonConfig.setConfig(SkeletonConfigValue.SHOULDERS_WIDTH, null);
				break;
			case UPPER_ARM_LENGTH:
				skeletonConfig.setConfig(SkeletonConfigValue.UPPER_ARM_LENGTH, null);
				break;
		}
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
			trackerPreUpdate(this.rightHandTracker) };
	}

	@Override
	public void resetTrackersFull() {
		// Pass all trackers through trackerPreUpdate
		Tracker hmdTracker = trackerPreUpdate(this.hmdTracker);
		Tracker[] trackersToReset = getTrackersToReset();

		// Resets all axis of the trackers with the HMD as reference.
		Quaternion referenceRotation = new Quaternion();
		hmdTracker.getRotation(referenceRotation);

		for (Tracker tracker : trackersToReset) {
			if (tracker != null) {
				tracker.resetFull(referenceRotation);
			}
		}
	}

	@Override
	@VRServerThread
	public void resetTrackersYaw() {
		// Pass all trackers through trackerPreUpdate
		Tracker hmdTracker = trackerPreUpdate(this.hmdTracker);
		Tracker[] trackersToReset = getTrackersToReset();

		// Resets the yaw of the trackers with the HMD as reference.
		Quaternion referenceRotation = new Quaternion();
		hmdTracker.getRotation(referenceRotation);

		for (Tracker tracker : trackersToReset) {
			if (tracker != null) {
				tracker.resetYaw(referenceRotation);
			}
		}
	}
}
