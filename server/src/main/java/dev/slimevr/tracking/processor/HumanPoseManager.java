package dev.slimevr.tracking.processor;

import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.tracking.processor.config.SkeletonConfigManager;
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets;
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles;
import dev.slimevr.tracking.processor.config.SkeletonConfigValues;
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton;
import dev.slimevr.tracking.trackers.*;
import dev.slimevr.util.ann.VRServerThread;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Class to handle communicate between classes in "skeleton" package and outside
 */
public class HumanPoseManager {

	private VRServer server;
	private final List<ComputedHumanPoseTracker> computedTrackers = new FastList<>();
	private final List<Consumer<HumanSkeleton>> onSkeletonUpdated = new FastList<>();
	private final SkeletonConfigManager skeletonConfigManager;
	private HumanSkeleton skeleton;

	// #region Constructors
	private HumanPoseManager() {
		skeletonConfigManager = new SkeletonConfigManager(true, this);
		initializeComputedHumanPoseTracker();
	}

	/**
	 * Creates a new HumanPoseManager that uses the VRServer
	 *
	 * @param server the used VRServer
	 */
	public HumanPoseManager(VRServer server) {
		this();
		this.server = server;
	}

	/**
	 * Creates a new HumanPoseManager that uses the given trackers for the
	 * HumanSkeleton and the default config
	 *
	 * @param trackers a list of all trackers
	 */
	public HumanPoseManager(List<? extends Tracker> trackers) {
		this();
		skeleton = new HumanSkeleton(this, trackers);
	}

	/**
	 * Creates a new HumanPoseManager that uses the given trackers for the
	 * HumanSkeleton and the given offsets for config
	 *
	 * @param trackers a list of all trackers
	 * @param offsetConfigs a map of the SkeletonConfigOffsets and values for
	 * them
	 */
	public HumanPoseManager(
		List<? extends Tracker> trackers,
		Map<SkeletonConfigOffsets, Float> offsetConfigs
	) {
		this();
		skeleton = new HumanSkeleton(this, trackers);
		// Set offsetConfigs from given offsetConfigs on creation
		skeletonConfigManager.setOffsets(offsetConfigs);
	}

	/**
	 * Creates a new HumanPoseManager that uses the given trackers for the
	 * HumanSkeleton and the given offsets for config
	 *
	 * @param trackers a list of all trackers
	 * @param offsetConfigs a map of the SkeletonConfigOffsets and values for
	 * them
	 * @param altOffsetConfigs an alternative map of the SkeletonConfigOffsets
	 * and values for them
	 */
	public HumanPoseManager(
		List<? extends Tracker> trackers,
		Map<SkeletonConfigOffsets, Float> offsetConfigs,
		Map<SkeletonConfigOffsets, Float> altOffsetConfigs
	) {
		this();
		skeleton = new HumanSkeleton(this, trackers);
		// Set offsetConfigs from given offsetConfigs on creation
		if (altOffsetConfigs != null) {
			// Set alts first, so if there's any overlap it doesn't affect
			// the values
			skeletonConfigManager.setOffsets(altOffsetConfigs);
		}
		skeletonConfigManager.setOffsets(offsetConfigs);
	}

	// #endregion
	// #region private methods
	private void initializeComputedHumanPoseTracker() {
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.HEAD,
					TrackerRole.HEAD
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.CHEST,
					TrackerRole.CHEST
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.WAIST,
					TrackerRole.WAIST
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.LEFT_FOOT,
					TrackerRole.LEFT_FOOT
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.RIGHT_FOOT,
					TrackerRole.RIGHT_FOOT
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.LEFT_KNEE,
					TrackerRole.LEFT_KNEE
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.RIGHT_KNEE,
					TrackerRole.RIGHT_KNEE
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.LEFT_ELBOW,
					TrackerRole.LEFT_ELBOW
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.RIGHT_ELBOW,
					TrackerRole.RIGHT_ELBOW
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.LEFT_HAND,
					TrackerRole.LEFT_HAND
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.RIGHT_HAND,
					TrackerRole.RIGHT_HAND
				)
			);
	}

	@VRServerThread
	private void updateSkeletonModelFromServer() {
		disconnectAllTrackers();
		skeleton = new HumanSkeleton(this, server);
		skeletonConfigManager.loadFromConfig(server.getConfigManager());
		for (Consumer<HumanSkeleton> sc : onSkeletonUpdated)
			sc.accept(skeleton);
	}

	@VRServerThread
	private void disconnectAllTrackers() {
		for (ComputedHumanPoseTracker t : computedTrackers) {
			t.setStatus(TrackerStatus.DISCONNECTED);
		}
	}

	// #endregion
	// #region public methods
	// #region skeleton methods
	@VRServerThread
	public void trackerAdded(Tracker tracker) {
		updateSkeletonModelFromServer();
	}

	@VRServerThread
	public void trackerUpdated(Tracker tracker) {
		updateSkeletonModelFromServer();
	}

	@VRServerThread
	public void addSkeletonUpdatedCallback(Consumer<HumanSkeleton> consumer) {
		onSkeletonUpdated.add(consumer);
		if (isSkeletonPresent())
			consumer.accept(skeleton);
	}

	/**
	 * @return True if the skeleton isn't null, or false if it's null
	 */
	@ThreadSafe
	public boolean isSkeletonPresent() {
		return skeleton != null;
	}

	/**
	 * Updates the pose of the skeleton from trackers rotations
	 */
	@VRServerThread
	public void update() {
		if (isSkeletonPresent())
			skeleton.updatePose();
	}

	@ThreadSafe
	public HumanSkeleton getSkeleton() {
		return skeleton;
	}

	// #endregion
	// #region tracker/nodes/bones methods
	/**
	 * @return a list of the computed trackers as ShareableTrackers
	 */
	@ThreadSafe
	public List<? extends ShareableTracker> getShareableTracker() {
		return computedTrackers;
	}

	/**
	 * @return a list of the computed trackers as ComputedHumanPoseTracker
	 */
	@ThreadSafe
	public List<? extends ComputedHumanPoseTracker> getComputedTracker() {
		return computedTrackers;
	}

	/**
	 * Get the corresponding computed tracker for a given TrackerRole
	 *
	 * @param trackerRole the role of the tracker which we want
	 * @return the corresponding computed tracker for the trackerRole
	 */
	@ThreadSafe
	public ComputedHumanPoseTracker getComputedTracker(TrackerRole trackerRole) {
		if (isSkeletonPresent())
			return skeleton.getComputedTracker(trackerRole);
		return null;
	}

	/**
	 * @return the root node of the skeleton, which is the HMD
	 */
	@ThreadSafe
	public TransformNode getRootNode() {
		if (isSkeletonPresent())
			return skeleton.getRootNode();
		return null;
	}

	/**
	 * Get the tail node (away from the tracking root) of the given bone
	 *
	 * @param bone the bone from which we want the tail node
	 * @return the tail node of the bone
	 */
	@ThreadSafe
	public TransformNode getTailNodeOfBone(BoneType bone) {
		if (isSkeletonPresent())
			return skeleton.getTailNodeOfBone(bone);
		return null;
	}

	/**
	 * @return the HMD's y position from the skeleton
	 */
	@ThreadSafe
	public float getHmdHeight() {
		if (isSkeletonPresent())
			return skeleton.getHmdHeight();
		return 0.0f;
	}

	/**
	 * Runs checks to know if we should (and are) performing the tracking of the
	 * left arm from the controller.
	 *
	 * @return a bool telling us if we are tracking the left arm from the
	 * controller or not.
	 */
	@ThreadSafe
	public boolean isTrackingLeftArmFromController() {
		if (isSkeletonPresent())
			return skeleton.isTrackingLeftArmFromController();
		return false;
	}

	/**
	 * Runs checks to know if we should (and are) performing the tracking of the
	 * right arm from the controller.
	 *
	 * @return a bool telling us if we are tracking the right arm from the
	 * controller or not.
	 */
	@ThreadSafe
	public boolean isTrackingRightArmFromController() {
		if (isSkeletonPresent())
			return skeleton.isTrackingRightArmFromController();
		return false;
	}

	/**
	 * @return All skeleton bones as BoneInfo
	 */
	@ThreadSafe
	public List<BoneInfo> getAllBoneInfo() {
		if (isSkeletonPresent())
			return skeleton.allBoneInfo;
		return null;
	}

	/**
	 * @return All shareable bones as BoneInfo
	 */
	@ThreadSafe
	public List<BoneInfo> getShareableBoneInfo() {
		if (isSkeletonPresent())
			return skeleton.shareableBoneInfo;
		return null;
	}

	/**
	 * @return The bone as BoneInfo for the given BoneType
	 */
	@ThreadSafe
	public BoneInfo getBoneInfoForBoneType(BoneType boneType) {
		if (isSkeletonPresent())
			return skeleton.getBoneInfoForBoneType(boneType);
		return null;
	}

	// #endregion
	// #region config methods
	/**
	 * @param key the offset from which to get the corresponding value
	 * @return the offset in config corresponding to the key
	 */
	@ThreadSafe
	public float getOffset(SkeletonConfigOffsets key) {
		return skeletonConfigManager.getOffset(key);
	}

	/**
	 * @param key the offset to set the length to
	 * @param newLength the new attributed length to the offset
	 */
	@ThreadSafe
	public void setOffset(SkeletonConfigOffsets key, Float newLength) {
		skeletonConfigManager.setOffset(key, newLength);
	}

	/**
	 * Resets all the offsets in the current SkeletonConfigManager
	 */
	@ThreadSafe
	public void resetOffsets() {
		skeletonConfigManager.resetOffsets();
	}

	/**
	 * @param key the toggle from which to get the corresponding value
	 * @return the toggle in config corresponding to the key
	 */
	@ThreadSafe
	public boolean getToggle(SkeletonConfigToggles key) {
		return skeletonConfigManager.getToggle(key);
	}

	/**
	 * @param key the toggle to set the value to
	 * @param newValue the new attributed value to the toggle
	 */
	@ThreadSafe
	public void setToggle(SkeletonConfigToggles key, Boolean newValue) {
		skeletonConfigManager.setToggle(key, newValue);
	}

	/**
	 * Resets all the toggles in the current SkeletonConfigManager
	 */
	@ThreadSafe
	public void resetToggles() {
		skeletonConfigManager.resetToggles();
	}

	/**
	 * @param key the value from which to get the corresponding value
	 * @return the value in config corresponding to the key
	 */
	@ThreadSafe
	public float getValue(SkeletonConfigValues key) {
		return skeletonConfigManager.getValue(key);
	}

	/**
	 * @param key the value to set the value to
	 * @param newValue the new attributed value to the value
	 */
	@ThreadSafe
	public void setValue(SkeletonConfigValues key, Float newValue) {
		skeletonConfigManager.setValue(key, newValue);
	}

	/**
	 * Resets all the values in the current SkeletonConfigManager
	 */
	@ThreadSafe
	public void resetValues() {
		skeletonConfigManager.resetValues();
	}

	/**
	 * Resets all the skeleton configs in the current SkeletonConfigManager
	 */
	@ThreadSafe
	public void resetAllConfigs() {
		skeletonConfigManager.resetAllConfigs();
	}

	/**
	 * Writes the skeleton configs
	 */
	@ThreadSafe
	public void saveConfig() {
		skeletonConfigManager.save();
	}

	/**
	 * Update the given node with the given offset
	 *
	 * @param node the node to update
	 * @param offset the new offset to apply to the node
	 */
	@ThreadSafe
	public void updateNodeOffset(BoneType node, Vector3f offset) {
		if (isSkeletonPresent())
			skeleton.updateNodeOffset(node, offset);
	}

	/**
	 * Update the given toggle to the new given value in the skeleton
	 *
	 * @param configToggle the toggle to update
	 * @param newValue the new value to apply to the toggle
	 */
	@ThreadSafe
	public void updateToggleState(SkeletonConfigToggles configToggle, boolean newValue) {
		if (isSkeletonPresent())
			skeleton.updateToggleState(configToggle, newValue);
	}

	/**
	 * Update the given value to the new given value in the skeleton
	 *
	 * @param configValue the value to update
	 * @param newValue the new value to apply to the value
	 */
	@ThreadSafe
	public void updateValueState(SkeletonConfigValues configValue, float newValue) {
		if (isSkeletonPresent())
			skeleton.updateValueState(configValue, newValue);
	}

	/**
	 * Compute the offset for the given node and apply the new offset
	 *
	 * @param node the node to update
	 */
	public void computeNodeOffset(BoneType node) {
		skeletonConfigManager.computeNodeOffset(node);
	}

	@VRServerThread
	public void resetTrackersFull() {
		if (isSkeletonPresent()) {
			skeleton.resetTrackersFull();
			if (server != null)
				server.getVrcOSCHandler().yawAlign();
		}
	}

	@VRServerThread
	public void resetTrackersMounting() {
		if (isSkeletonPresent())
			skeleton.resetTrackersMounting();
	}

	@VRServerThread
	public void resetTrackersYaw() {
		if (isSkeletonPresent()) {
			skeleton.resetTrackersYaw();
			if (server != null)
				server.getVrcOSCHandler().yawAlign();
		}
	}

	@ThreadSafe
	public boolean[] getLegTweaksState() {
		return skeleton.getLegTweaksState();
	}

	@VRServerThread
	public void setLegTweaksEnabled(boolean value) {
		if (isSkeletonPresent())
			skeleton.setLegTweaksEnabled(value);
	}

	@VRServerThread
	public void setFloorClipEnabled(boolean value) {
		if (isSkeletonPresent()) {
			skeleton.setFloorclipEnabled(value);
			if (server != null) {
				server
					.getConfigManager()
					.getVrConfig()
					.getSkeleton()
					.getToggles()
					.put(SkeletonConfigToggles.FLOOR_CLIP.configKey, value);
				server.getConfigManager().saveConfig();
			}
		}
	}

	@VRServerThread
	public void setSkatingCorrectionEnabled(boolean value) {
		if (isSkeletonPresent()) {
			skeleton.setSkatingCorrectionEnabled(value);
			if (server != null) {
				server
					.getConfigManager()
					.getVrConfig()
					.getSkeleton()
					.getToggles()
					.put(SkeletonConfigToggles.SKATING_CORRECTION.configKey, value);
				server.getConfigManager().saveConfig();
			}
		}
	}

	public void updateTapDetectionConfig() {
		if (isSkeletonPresent())
			skeleton.updateTapDetectionConfig();
	}

	public void updateLegTweaksConfig() {
		if (isSkeletonPresent())
			skeleton.updateLegTweaksConfig();
	}

	@ThreadSafe
	public float getUserHeightFromConfig() {
		if (isSkeletonPresent()) {
			return skeletonConfigManager.getUserHeightFromOffsets();
		}
		return 0f;
	}
	// #endregion
	// #endregion
}
