package io.eiren.gui.autobone;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.HumanSkeletonWithWaist;

public class AutoBone {

	protected final VRServer server;

	protected PoseFrame[] frames = new PoseFrame[0];
	protected int frameRecordingCursor = -1;

	protected final SimpleSkeleton skeleton1;
	protected final SimpleSkeleton skeleton2;

	// Waist
	protected float chestDistance = 0.42f;
	/**
	 * Distance from eyes to waist
	 */
	protected float waistDistance = 0.85f;
	/**
	 * Distance from eyes to the base of the neck
	 */
	protected float neckLength = HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT;
	/**
	 * Distance from eyes to ear
	 */
	protected float headShift = HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT;

	// Legs
	/**
	 * Distance between centers of both hips
	 */
	protected float hipsWidth = HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT;
	/**
	 * Length from waist to knees
	 */
	protected float kneeHeight = 0.42f;
	/**
	 * Distance from waist to ankle
	 */
	protected float legsLength = 0.84f;
	protected float footLength = HumanSkeletonWithLegs.FOOT_LENGTH_DEFAULT;

	public AutoBone(VRServer server) {
		this.server = server;

		reloadConfigValues();

		this.skeleton1 = new SimpleSkeleton(server);
		this.skeleton2 = new SimpleSkeleton(server);

		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}

	public void reloadConfigValues() {
		// Load waist configs
		headShift = server.config.getFloat("body.headShift", HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT);
		neckLength = server.config.getFloat("body.neckLength", HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT);
		chestDistance = server.config.getFloat("body.chestDistance", 0.42f);
		waistDistance = server.config.getFloat("body.waistDistance", 0.85f);

		// Load leg configs
		hipsWidth = server.config.getFloat("body.hipsWidth", HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT);
		kneeHeight = server.config.getFloat("body.kneeHeight", 0.42f);
		legsLength = server.config.getFloat("body.legsLength", 0.84f);
		footLength = server.config.getFloat("body.footLength", HumanSkeletonWithLegs.FOOT_LENGTH_DEFAULT);
	}

	public void setSkeletonLengths(SimpleSkeleton skeleton) {
		skeleton.setSkeletonConfig("Head", headShift);
		skeleton.setSkeletonConfig("Neck", neckLength);
		skeleton.setSkeletonConfig("Waist", waistDistance);
		skeleton.setSkeletonConfig("Chest", chestDistance);
		skeleton.setSkeletonConfig("Hips width", hipsWidth);
		skeleton.setSkeletonConfig("Knee height", kneeHeight);
		skeleton.setSkeletonConfig("Legs length", legsLength);
		skeleton.setSkeletonConfig("Foot length", footLength);
	}

	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		if (frameRecordingCursor >= 0 && frameRecordingCursor < frames.length && newSkeleton instanceof HumanSkeletonWithLegs) {
			HumanSkeletonWithLegs newLegSkeleton = (HumanSkeletonWithLegs)newSkeleton;
			PoseFrame frame = new PoseFrame(newLegSkeleton);
			frames[frameRecordingCursor++] = frame;
		}
	}

	public void startFrameRecording(int numFrames) {
		frames = new PoseFrame[numFrames];
		frameRecordingCursor = 0;
	}

	public void stopFrameRecording() {
		// Set to end of the frame array to prevent race condition with `frameRecordingCursor++`
		frameRecordingCursor = frames.length;
	}

	public void processFrames() {
		int cursorOffset = 1;

		for (;;) {
			// Detect end of iteration
			if (cursorOffset >= frames.length)
				break;

			int frameCursor1 = 0;
			int frameCursor2 = cursorOffset++;

			do {
				PoseFrame frame1 = frames[frameCursor1];
				PoseFrame frame2 = frames[frameCursor2];

				skeleton1.setPoseFromFrame(frame1);
				skeleton2.setPoseFromFrame(frame2);
			} while (++frameCursor1 < frames.length && ++frameCursor2 < frames.length);
		}
	}
}
