package dev.slimevr.posestreamer;

import java.io.File;
import java.util.List;

import dev.slimevr.poserecorder.PoseFrameIO;
import dev.slimevr.poserecorder.PoseFrameSkeleton;
import dev.slimevr.poserecorder.PoseFrames;

public class PoseFrameStreamer extends PoseStreamer {

	protected PoseFrames frames;

	public PoseFrameStreamer(String path) {
		super(null);

		PoseFrames frames = PoseFrameIO.readFromFile(new File(path));
		this.frames = frames;

		PoseFrameSkeleton skeleton = new PoseFrameSkeleton(frames.getTrackers(), null);
		this.skeleton = skeleton;
	}

	public PoseFrames getFrames() {
		return frames;
	}
}
