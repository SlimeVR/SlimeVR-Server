package dev.slimevr.vr.poserecorder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.processor.TrackerBodyPosition;

public final class PoseFrameIO {

	private PoseFrameIO() {
		// Do not allow instantiating
	}

	public static boolean writeToFile(File file, PoseFrame[] frames) {
		try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			// Write every frame
			outputStream.writeInt(frames.length);
			for (PoseFrame frame : frames) {
				writeFrame(outputStream, frame);
			}
		} catch (Exception e) {
			LogManager.log.severe("Error writing frames to file", e);
			return false;
		}

		return true;
	}

	public static boolean writeFrame(DataOutputStream outputStream, PoseFrame frame) {
		try {
			if (frame != null && frame.trackerFrames != null) {
				outputStream.writeInt(frame.trackerFrames.size());

				for (TrackerFrame trackerFrame : frame.trackerFrames.values()) {
					outputStream.writeInt(trackerFrame.getDataFlags());

					if (trackerFrame.hasData(TrackerFrameData.DESIGNATION)) {
						outputStream.writeUTF(trackerFrame.designation.designation);
					}

					if (trackerFrame.hasData(TrackerFrameData.ROTATION)) {
						outputStream.writeFloat(trackerFrame.rotation.getX());
						outputStream.writeFloat(trackerFrame.rotation.getY());
						outputStream.writeFloat(trackerFrame.rotation.getZ());
						outputStream.writeFloat(trackerFrame.rotation.getW());
					}

					if (trackerFrame.hasData(TrackerFrameData.POSITION)) {
						outputStream.writeFloat(trackerFrame.position.getX());
						outputStream.writeFloat(trackerFrame.position.getY());
						outputStream.writeFloat(trackerFrame.position.getZ());
					}
				}
			} else {
				outputStream.writeInt(0);
			}
		} catch (Exception e) {
			LogManager.log.severe("Error writing frame to stream", e);
			return false;
		}

		return true;
	}

	public static boolean writeFrame(File file, PoseFrame frame) {
		try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			writeFrame(outputStream, frame);
		} catch (Exception e) {
			LogManager.log.severe("Error writing frame to file", e);
			return false;
		}

		return true;
	}

	public static PoseFrame[] readFromFile(File file) {
		try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			int frameCount = inputStream.readInt();

			PoseFrame[] frames = new PoseFrame[frameCount];
			for (int i = 0; i < frameCount; i++) {
				frames[i] = readFrame(inputStream);
			}

			return frames;
		} catch (Exception e) {
			LogManager.log.severe("Error reading frames from file", e);
		}

		return null;
	}

	public static PoseFrame readFrame(DataInputStream inputStream) {
		try {
			int trackerFrameCount = inputStream.readInt();

			List<TrackerFrame> trackerFrames = new FastList<TrackerFrame>(trackerFrameCount);
			for (int i = 0; i < trackerFrameCount; i++) {
				int dataFlags = inputStream.readInt();

				TrackerBodyPosition designation = null;
				if (TrackerFrameData.DESIGNATION.check(dataFlags)) {
					designation = TrackerBodyPosition.getByDesignation(inputStream.readUTF());
				}

				Quaternion rotation = null;
				if (TrackerFrameData.ROTATION.check(dataFlags)) {
					float quatX = inputStream.readFloat();
					float quatY = inputStream.readFloat();
					float quatZ = inputStream.readFloat();
					float quatW = inputStream.readFloat();
					rotation = new Quaternion(quatX, quatY, quatZ, quatW);
				}

				Vector3f position = null;
				if (TrackerFrameData.POSITION.check(dataFlags)) {
					float posX = inputStream.readFloat();
					float posY = inputStream.readFloat();
					float posZ = inputStream.readFloat();
					position = new Vector3f(posX, posY, posZ);
				}

				trackerFrames.add(new TrackerFrame(designation, rotation, position));
			}

			return new PoseFrame(trackerFrames);
		} catch (Exception e) {
			LogManager.log.severe("Error reading frame from stream", e);
		}

		return null;
	}

	public static PoseFrame readFrame(File file) {
		try {
			return readFrame(new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
		} catch (Exception e) {
			LogManager.log.severe("Error reading frame from file", e);
		}

		return null;
	}
}
