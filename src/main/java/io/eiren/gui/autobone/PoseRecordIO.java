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

public class PoseRecordIO {

	protected final File file;

	public PoseRecordIO(String file) {
		this.file = new File(file);
	}

	public boolean writeToFile(PoseFrame[] frames) {
		try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			// Write every frame
			outputStream.writeInt(frames.length);
			for (PoseFrame frame : frames) {
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
			}
		} catch (Exception e) {
			LogManager.log.severe("Error writing to file", e);
			return false;
		}

		return true;
	}

	public PoseFrame[] readFromFile() {
		try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			int frameCount = inputStream.readInt();

			PoseFrame[] frames = new PoseFrame[frameCount];
			for (int i = 0; i < frameCount; i++) {
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

				frames[i] = new PoseFrame(vector, rotations);
			}

			return frames;
		} catch (Exception e) {
			LogManager.log.severe("Error reading from file", e);
		}

		return null;
	}
}
