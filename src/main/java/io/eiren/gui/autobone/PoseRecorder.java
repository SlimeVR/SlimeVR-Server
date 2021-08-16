package io.eiren.gui.autobone;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.HumanSkeletonWithLegs;

public class PoseRecorder {

	protected final FastList<PoseFrame> frames = new FastList<PoseFrame>();

	protected int numFrames = -1;
	protected long frameRecordingInterval = 60L;
	protected long nextFrameTimeMs = -1L;

	protected CompletableFuture<PoseFrame[]> currentRecording;

	protected final VRServer server;
	HumanSkeletonWithLegs skeleton = null;

	public PoseRecorder(VRServer server) {
		this.server = server;
		server.addOnTick(this::onTick);
		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}

	@VRServerThread
	public void onTick() {
		HumanSkeletonWithLegs skeleton = this.skeleton;
		if (skeleton != null && frames.size() < numFrames && System.currentTimeMillis() >= nextFrameTimeMs) {
			nextFrameTimeMs = System.currentTimeMillis() + frameRecordingInterval;

			frames.add(new PoseFrame(skeleton));

			// If done recording
			CompletableFuture<PoseFrame[]> currentRecording = this.currentRecording;
			if (currentRecording != null && frames.size() >= numFrames) {
				currentRecording.complete(frames.toArray(new PoseFrame[0]));
			}
		}
	}

	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		if (newSkeleton instanceof HumanSkeletonWithLegs) {
			skeleton = (HumanSkeletonWithLegs) newSkeleton;
		}
	}

	public synchronized Future<PoseFrame[]> startFrameRecording(int numFrames, long interval) {
		stopFrameRecording();

		// Clear old frames and ensure new size can be held
		frames.clear();
		frames.ensureCapacity(numFrames);

		this.numFrames = numFrames;

		frameRecordingInterval = interval;
		nextFrameTimeMs = -1L;

		LogManager.log.info("[PoseRecorder] Recording " + numFrames + " samples at a " + interval + " ms frame interval");

		currentRecording = new CompletableFuture<PoseFrame[]>();
		return currentRecording;
	}

	public synchronized void stopFrameRecording() {
		numFrames = -1;

		// Synchronized, this value can be expected to stay the same
		if (currentRecording != null) {
			currentRecording.complete(frames.toArray(new PoseFrame[0]));
		}
	}

	public boolean isRecording() {
		return numFrames > frames.size();
	}

	public PoseFrame[] getFrames() throws ExecutionException, InterruptedException {
		CompletableFuture<PoseFrame[]> currentRecording = this.currentRecording;
		return currentRecording != null ? currentRecording.get() : null;
	}
}
