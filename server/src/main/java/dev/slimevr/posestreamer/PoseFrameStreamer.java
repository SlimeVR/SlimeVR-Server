package dev.slimevr.posestreamer;

import dev.slimevr.poserecorder.PoseFrameIO;
import dev.slimevr.poserecorder.PoseFrameSkeleton;
import dev.slimevr.poserecorder.PoseFrames;

import java.io.File;


public class PoseFrameStreamer extends PoseStreamer {

	private final PoseFrames frames;

	public PoseFrameStreamer(String path) {
		this(new File(path));
	}

	public PoseFrameStreamer(File file) {
		this(PoseFrameIO.readFromFile(file));
	}

	public PoseFrameStreamer(PoseFrames frames) {
		super(new PoseFrameSkeleton(frames.getTrackers(), null));
		this.frames = frames;
	}

	public PoseFrames getFrames() {
		return frames;
	}

	public synchronized void streamAllFrames() {
		PoseFrameSkeleton skeleton = (PoseFrameSkeleton) this.skeleton;
		for (int i = 0; i < frames.getMaxFrameCount(); i++) {
			skeleton.setCursor(i);
			skeleton.updatePose();
			captureFrame();
		}
	}
}
