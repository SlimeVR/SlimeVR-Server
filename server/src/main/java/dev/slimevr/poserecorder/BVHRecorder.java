package dev.slimevr.poserecorder;

import dev.slimevr.VRServer;
import dev.slimevr.posestreamer.BVHFileStream;
import dev.slimevr.posestreamer.PoseDataStream;
import dev.slimevr.posestreamer.ServerPoseStreamer;
import io.eiren.util.logging.LogManager;

import java.io.File;
import java.io.IOException;


public class BVHRecorder {

	private static final File bvhSaveDir = new File("BVH Recordings");
	private final ServerPoseStreamer poseStreamer;
	private PoseDataStream poseDataStream = null;

	public BVHRecorder(VRServer server) {
		this.poseStreamer = new ServerPoseStreamer(server);
	}

	public void startRecording() {
		File bvhFile = getBvhFile();
		if (bvhFile != null) {
			try {
				poseDataStream = new BVHFileStream(bvhFile);
				poseStreamer.setOutput(poseDataStream, 1000L / 100L);
			} catch (IOException e1) {
				LogManager
					.severe(
						"[BVH] Failed to create the recording file \"" + bvhFile.getPath() + "\"."
					);
			}
		} else {
			LogManager.severe("[BVH] Unable to get file to save to");
		}
	}

	public void endRecording() {

		try {
			poseStreamer.closeOutput(poseDataStream);
		} catch (Exception e1) {
			LogManager.severe("[BVH] Exception while closing poseDataStream", e1);
		} finally {
			poseDataStream = null;
		}
	}

	private File getBvhFile() {
		if (bvhSaveDir.isDirectory() || bvhSaveDir.mkdirs()) {
			File saveRecording;
			int recordingIndex = 1;
			do {
				saveRecording = new File(bvhSaveDir, "BVH-Recording" + recordingIndex++ + ".bvh");
			} while (saveRecording.exists());

			return saveRecording;
		} else {
			LogManager
				.severe(
					"[BVH] Failed to create the recording directory \""
						+ bvhSaveDir.getPath()
						+ "\"."
				);
		}

		return null;
	}

	public boolean isRecording() {
		return poseDataStream != null;
	}
}
