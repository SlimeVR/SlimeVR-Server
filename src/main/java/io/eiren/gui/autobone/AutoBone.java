package io.eiren.gui.autobone;

import java.util.HashMap;
import java.util.Map.Entry;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.logging.LogManager;
import io.eiren.util.StringUtils;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.HumanSkeletonWithWaist;

public class AutoBone {

	protected final VRServer server;

	HumanSkeletonWithLegs skeleton = null;

	protected PoseFrame[] frames = new PoseFrame[0];
	protected int frameRecordingCursor = -1;

	protected long frameRecordingInterval = 60L;
	protected long lastFrameTimeMs = 0L;

	protected final SimpleSkeleton skeleton1;
	protected final SimpleSkeleton skeleton2;

	public final HashMap<String, Float> configs = new HashMap<String, Float>() {{
		put("Head", HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT);
		put("Neck", HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT);
		put("Waist", 0.85f);
		put("Chest", 0.42f);
		put("Hips width", HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT);
		put("Knee height", 0.42f);
		put("Legs length", 0.84f);
		put("Foot length", HumanSkeletonWithLegs.FOOT_LENGTH_DEFAULT);
	}};

	public AutoBone(VRServer server) {
		this.server = server;

		reloadConfigValues();

		this.skeleton1 = new SimpleSkeleton(server);
		this.skeleton2 = new SimpleSkeleton(server);

		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
		server.addOnTick(this::onTick);
	}

	public void reloadConfigValues() {
		// Load waist configs
		configs.put("Head", server.config.getFloat("body.headShift", HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT));
		configs.put("Neck", server.config.getFloat("body.neckLength", HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT));
		configs.put("Waist", server.config.getFloat("body.waistDistance", 0.85f));
		configs.put("Chest", server.config.getFloat("body.chestDistance", 0.42f));

		// Load leg configs
		configs.put("Hips width", server.config.getFloat("body.hipsWidth", HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT));
		configs.put("Knee height", server.config.getFloat("body.kneeHeight", 0.42f));
		configs.put("Legs length", server.config.getFloat("body.legsLength", 0.84f));
		configs.put("Foot length", server.config.getFloat("body.footLength", HumanSkeletonWithLegs.FOOT_LENGTH_DEFAULT));
	}

	public void setSkeletonLengths(SimpleSkeleton skeleton) {
		for (Entry<String, Float> entry : configs.entrySet()) {
			skeleton.setSkeletonConfig(entry.getKey(), entry.getValue());
		}
	}

	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		java.awt.EventQueue.invokeLater(() -> {
			if (newSkeleton instanceof HumanSkeletonWithLegs) {
				skeleton = (HumanSkeletonWithLegs)newSkeleton;
				LogManager.log.info("Received updated skeleton");
			}
		});
	}

	@VRServerThread
	public void onTick() {
		if (frameRecordingCursor >= 0 && frameRecordingCursor < frames.length && skeleton != null && System.currentTimeMillis() - lastFrameTimeMs >= frameRecordingInterval) {
			lastFrameTimeMs = System.currentTimeMillis();

			PoseFrame frame = new PoseFrame(skeleton);
			frames[frameRecordingCursor++] = frame;

			LogManager.log.info("Recorded frame " + frameRecordingCursor);
		}
	}

	public void startFrameRecording(int numFrames, long interval) {
		frames = new PoseFrame[numFrames];

		frameRecordingInterval = interval;
		lastFrameTimeMs = 0L;

		frameRecordingCursor = 0;

		LogManager.log.info("[AutoBone] Recording " + numFrames + " samples at a " + interval + " ms frame interval");
	}

	public void stopFrameRecording() {
		// Set to end of the frame array to prevent race condition with `frameRecordingCursor++`
		frameRecordingCursor = frames.length;
	}

	public boolean isRecording() {
		return frameRecordingCursor >= 0 && frameRecordingCursor < frames.length;
	}

	public PoseFrame[] getFrames() {
		return frames;
	}

	public void processFrames() {
		int cursorOffset = 1;
		float adjustRate = 0.5f;

		for (;;) {
			// Detect end of iteration
			if (cursorOffset >= frames.length)
				break;

			int frameCursor1 = 0;
			int frameCursor2 = cursorOffset++;

			do {
				PoseFrame frame1 = frames[frameCursor1];
				PoseFrame frame2 = frames[frameCursor2];

				if (frame1 == null || frame2 == null) {
					continue;
				}

				setSkeletonLengths(skeleton1);
				setSkeletonLengths(skeleton2);

				skeleton1.setPoseFromFrame(frame1);
				skeleton2.setPoseFromFrame(frame2);

				skeleton1.updatePose();
				skeleton2.updatePose();

				float error = getFootError(skeleton1, skeleton2);
				float adjustVal = error * adjustRate;
				LogManager.log.info("[AutoBone] Current position error: " + error);

				for (Entry<String, Float> entry : configs.entrySet()) {
					float newLength = entry.getValue() + adjustVal;
					updateSekeletonBoneLength(entry.getKey(), newLength);
					float newError = getFootError(skeleton1, skeleton2);

					if (newError >= error) {
						newLength = entry.getValue() - adjustVal;
						updateSekeletonBoneLength(entry.getKey(), newLength);
						newError = getFootError(skeleton1, skeleton2);

						if (newError >= error) {
							// Reset value and continue without getting new error values
							updateSekeletonBoneLength(entry.getKey(), entry.getValue());
							continue;
						} else {
							configs.put(entry.getKey(), newLength);
						}
					} else {
						configs.put(entry.getKey(), newLength);
					}

					// Update values with the new length
					error = getFootError(skeleton1, skeleton2);
					adjustVal = error * adjustRate;
				}
			} while (++frameCursor1 < frames.length && ++frameCursor2 < frames.length);
		}
	}

	protected static float getFootError(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2) {
		float distLeft = skeleton1.getLeftFootPos().distance(skeleton2.getLeftFootPos());
		float distRight = skeleton1.getRightFootPos().distance(skeleton2.getRightFootPos());

		float avg = (distLeft + distRight) / 2f;

		return avg * avg;
	}

	protected void updateSekeletonBoneLength(String joint, float newLength) {
		skeleton1.setSkeletonConfig(joint, newLength);
		skeleton2.setSkeletonConfig(joint, newLength);

		skeleton1.updatePose();
		skeleton2.updatePose();
	}
}
