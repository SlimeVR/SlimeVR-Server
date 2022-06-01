package dev.slimevr.posestreamer;

import dev.slimevr.vr.processor.skeleton.Skeleton;

import java.io.IOException;


public class TickPoseStreamer extends PoseStreamer {

	protected long nextFrameTimeMs = -1L;

	public TickPoseStreamer(Skeleton skeleton) {
		super(skeleton);
	}

	public void doTick() {
		PoseDataStream poseFileStream = this.poseFileStream;
		if (poseFileStream == null) {
			return;
		}

		Skeleton skeleton = this.skeleton;
		if (skeleton == null) {
			return;
		}

		long curTime = System.currentTimeMillis();
		if (curTime < nextFrameTimeMs) {
			return;
		}

		nextFrameTimeMs += frameRecordingInterval;

		// To prevent duplicate frames, make sure the frame time is always in
		// the future
		if (nextFrameTimeMs <= curTime) {
			nextFrameTimeMs = curTime + frameRecordingInterval;
		}

		captureFrame();
	}

	@Override
	public synchronized void setOutput(PoseDataStream poseFileStream) throws IOException {
		super.setOutput(poseFileStream);
		nextFrameTimeMs = -1L; // Reset the frame timing
	}
}
