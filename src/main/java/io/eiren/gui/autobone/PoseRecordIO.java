package io.eiren.gui.autobone;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.logging.LogManager;

public final class PoseRecordIO {

	private PoseRecordIO() {
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
			// Write root position vector
			outputStream.writeFloat(frame.rootPos.x);
			outputStream.writeFloat(frame.rootPos.y);
			outputStream.writeFloat(frame.rootPos.z);

			// Write rotations
			outputStream.writeInt(frame.rotations.size());
			for (Entry<String, Quaternion> entry : frame.rotations.entrySet()) {
				// Write the label string
				outputStream.writeUTF(entry.getKey());

				// Write the rotation quaternion
				Quaternion quat = entry.getValue();
				outputStream.writeFloat(quat.getX());
				outputStream.writeFloat(quat.getY());
				outputStream.writeFloat(quat.getZ());
				outputStream.writeFloat(quat.getW());
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
			float vecX = inputStream.readFloat();
			float vecY = inputStream.readFloat();
			float vecZ = inputStream.readFloat();

			Vector3f vector = new Vector3f(vecX, vecY, vecZ);

			int rotationCount = inputStream.readInt();
			HashMap<String, Quaternion> rotations = new HashMap<String, Quaternion>(rotationCount);
			for (int j = 0; j < rotationCount; j++) {
				String label = inputStream.readUTF();

				float quatX = inputStream.readFloat();
				float quatY = inputStream.readFloat();
				float quatZ = inputStream.readFloat();
				float quatW = inputStream.readFloat();
				Quaternion quaternion = new Quaternion(quatX, quatY, quatZ, quatW);

				rotations.put(label, quaternion);
			}

			return new PoseFrame(vector, rotations);
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
