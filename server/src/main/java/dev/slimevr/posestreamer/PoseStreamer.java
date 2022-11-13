package dev.slimevr.posestreamer;

import dev.slimevr.vr.processor.skeleton.Skeleton;
import io.eiren.util.logging.LogManager;

import java.io.IOException;


public class PoseStreamer {

	protected long frameRecordingInterval = 60L;

	protected Skeleton skeleton;
	protected PoseDataStream poseFileStream;

	public PoseStreamer(Skeleton skeleton) {
		this.skeleton = skeleton;
	}

	public synchronized void captureFrame() {
		// Make sure the stream is open before trying to write
		if (poseFileStream.isClosed()) {
			return;
		}

		try {
			poseFileStream.writeFrame(skeleton);
		} catch (Exception e) {
			// Handle any exceptions without crashing the program
			LogManager.severe("[PoseStreamer] Exception while saving frame", e);
		}
	}

	public synchronized long getFrameInterval() {
		return frameRecordingInterval;
	}

	public synchronized void setFrameInterval(long intervalMs) {
		if (intervalMs < 1) {
			throw new IllegalArgumentException("intervalMs must at least have a value of 1");
		}

		this.frameRecordingInterval = intervalMs;
	}

	public synchronized Skeleton getSkeleton() {
		return skeleton;
	}

	public synchronized void setOutput(PoseDataStream poseFileStream, long intervalMs)
		throws IOException {
		setFrameInterval(intervalMs);
		setOutput(poseFileStream);
	}

	public synchronized PoseDataStream getOutput() {
		return poseFileStream;
	}

	public synchronized void setOutput(PoseDataStream poseFileStream) throws IOException {
		poseFileStream.writeHeader(skeleton, this);
		this.poseFileStream = poseFileStream;
	}

	public synchronized void closeOutput() throws IOException {
		PoseDataStream poseFileStream = this.poseFileStream;

		if (poseFileStream != null) {
			closeOutput(poseFileStream);
			this.poseFileStream = null;
		}
	}

	public synchronized void closeOutput(PoseDataStream poseFileStream) throws IOException {
		if (poseFileStream != null) {
			poseFileStream.writeFooter(skeleton);
			poseFileStream.close();
		}
	}
}
