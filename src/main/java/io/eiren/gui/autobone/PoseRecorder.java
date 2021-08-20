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
		if (numFrames > 0) {
			HumanSkeletonWithLegs skeleton = this.skeleton;
			if (skeleton != null) {
				if (frames.size() < numFrames) {
					if (System.currentTimeMillis() >= nextFrameTimeMs) {
						nextFrameTimeMs = System.currentTimeMillis() + frameRecordingInterval;
						frames.add(new PoseFrame(skeleton));

						// If done, send finished recording
						if (frames.size() >= numFrames) {
							internalStopRecording();
						}
					}
				} else {
					// If done and hasn't yet, send finished recording
					internalStopRecording();
				}
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
		if (numFrames < 1) {
			throw new IllegalArgumentException("numFrames must at least have a value of 1");
		}
		if (interval < 1) {
			throw new IllegalArgumentException("interval must at least have a value of 1");
		}
		if (!isReadyToRecord()) {
			throw new IllegalStateException("PoseRecorder isn't ready to record!");
		}

		cancelFrameRecording();

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

	private void internalStopRecording() {
		CompletableFuture<PoseFrame[]> currentRecording = this.currentRecording;
		if (currentRecording != null && !currentRecording.isDone()) {
			// Stop the recording, returning the frames recorded
			currentRecording.complete(frames.toArray(new PoseFrame[0]));
		}

		numFrames = -1;
	}

	public synchronized void stopFrameRecording() {
		internalStopRecording();
	}

	public synchronized void cancelFrameRecording() {
		CompletableFuture<PoseFrame[]> currentRecording = this.currentRecording;
		if (currentRecording != null && !currentRecording.isDone()) {
			// Cancel the current recording and return nothing
			currentRecording.cancel(true);
		}

		numFrames = -1;
	}

	public boolean isReadyToRecord() {
		return skeleton != null;
	}

	public boolean isRecording() {
		return numFrames > frames.size();
	}

	public boolean hasRecording() {
		return currentRecording != null;
	}

	public Future<PoseFrame[]> getFramesAsync() {
		return currentRecording;
	}

	public PoseFrame[] getFrames() throws ExecutionException, InterruptedException {
		CompletableFuture<PoseFrame[]> currentRecording = this.currentRecording;
		return currentRecording != null ? currentRecording.get() : null;
	}
}
