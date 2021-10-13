package dev.slimevr.poserecorder;

import java.io.IOException;

import io.eiren.util.ann.VRServerThread;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;

public class PoseStreamer {

	protected long frameRecordingInterval = 60L;
	protected long nextFrameTimeMs = -1L;

	private HumanSkeleton skeleton;
	private PoseDataStream poseFileStream;

	protected final VRServer server;

	public PoseStreamer(VRServer server) {
		this.server = server;

		// Register callbacks/events
		server.addSkeletonUpdatedCallback(this::onSkeletonUpdated);
		server.addOnTick(this::onTick);
	}

	@VRServerThread
	public void onSkeletonUpdated(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}

	@VRServerThread
	public void onTick() {
		PoseDataStream poseFileStream = this.poseFileStream;
		if (poseFileStream != null) {

			long curTime = System.currentTimeMillis();
			if (curTime >= nextFrameTimeMs) {
				nextFrameTimeMs += frameRecordingInterval;

				// To prevent duplicate frames, make sure the frame time is always in the future
				if (nextFrameTimeMs <= curTime) {
					nextFrameTimeMs = curTime + frameRecordingInterval;
				}
				
				try {
					poseFileStream.writeFrame(skeleton);
				} catch (Exception e) {
					// Handle any exceptions without crashing the program
					LogManager.log.severe("[PoseStreamer] Exception while saving frame", e);
				}
			}
		}
	}

	public void setFrameInterval(long intervalMs) {
		if(intervalMs < 1) {
			throw new IllegalArgumentException("intervalMs must at least have a value of 1");
		}

		this.frameRecordingInterval = intervalMs;
	}

	public void setOutput(PoseDataStream poseFileStream) throws IOException {
		poseFileStream.writeHeader(skeleton, this);
		this.poseFileStream = poseFileStream;
		nextFrameTimeMs = -1L; // Reset the frame timing
	}

	public void setOutput(PoseDataStream poseFileStream, long intervalMs) throws IOException {
		setFrameInterval(intervalMs);
		setOutput(poseFileStream);
	}

	public PoseDataStream getOutput() {
		return poseFileStream;
	}

	public void closeOutput() throws IOException {
		PoseDataStream poseFileStream = this.poseFileStream;

		if (poseFileStream != null) {
			poseFileStream.writeFooter(skeleton);
			poseFileStream.close();
			this.poseFileStream = null;
		}
	}
}
