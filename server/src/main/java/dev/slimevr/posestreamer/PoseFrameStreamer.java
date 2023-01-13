package dev.slimevr.posestreamer;

import dev.slimevr.poserecorder.PoseFrameIO;
import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.tracking.processor.HumanPoseManager;

import java.io.File;


public class PoseFrameStreamer extends PoseStreamer {

	private final PoseFrames frames;
	private final HumanPoseManager humanPoseManager;

	public PoseFrameStreamer(String path) {
		this(new File(path));
	}

	public PoseFrameStreamer(File file) {
		this(PoseFrameIO.readFromFile(file));
	}

	public PoseFrameStreamer(PoseFrames frames) {
		this.frames = new PoseFrames(frames);
		humanPoseManager = new HumanPoseManager(this.frames.getTrackers());
		skeleton = humanPoseManager.getSkeleton();
	}

	public PoseFrames getFrames() {
		return frames;
	}

	public synchronized void streamAllFrames() {
		for (int i = 0; i < frames.getMaxFrameCount(); i++) {
			frames.setCursors(i);
			humanPoseManager.update();
			captureFrame();
		}
	}
}
