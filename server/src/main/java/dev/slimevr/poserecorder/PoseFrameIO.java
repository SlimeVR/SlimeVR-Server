package dev.slimevr.poserecorder;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.tracking.trackers.TrackerPosition;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.*;


public final class PoseFrameIO {

	private PoseFrameIO() {
		// Do not allow instantiating
	}

	private static void writeVector3f(DataOutputStream outputStream, Vector3f vector)
		throws IOException {
		outputStream.writeFloat(vector.getX());
		outputStream.writeFloat(vector.getY());
		outputStream.writeFloat(vector.getZ());
	}

	private static void writeQuaternion(DataOutputStream outputStream, Quaternion quaternion)
		throws IOException {
		outputStream.writeFloat(quaternion.getX());
		outputStream.writeFloat(quaternion.getY());
		outputStream.writeFloat(quaternion.getZ());
		outputStream.writeFloat(quaternion.getW());
	}

	public static boolean writeFrames(DataOutputStream outputStream, PoseFrames frames) {
		try {
			if (frames != null) {
				outputStream.writeInt(frames.getTrackerCount());
				for (PoseFrameTracker tracker : frames.getTrackers()) {
					outputStream.writeUTF(tracker.name);
					outputStream.writeInt(tracker.getFrameCount());
					for (int i = 0; i < tracker.getFrameCount(); i++) {
						TrackerFrame trackerFrame = tracker.safeGetFrame(i);
						if (trackerFrame == null) {
							outputStream.writeInt(0);
							continue;
						}

						int dataFlags = trackerFrame.getDataFlags();

						// Don't write destination strings anymore, replace with
						// the enum
						if (trackerFrame.hasData(TrackerFrameData.DESIGNATION_STRING)) {
							dataFlags = TrackerFrameData.DESIGNATION_ENUM
								.add(TrackerFrameData.DESIGNATION_STRING.remove(dataFlags));
						}

						outputStream.writeInt(dataFlags);

						if (trackerFrame.hasData(TrackerFrameData.ROTATION)) {
							writeQuaternion(outputStream, trackerFrame.rotation);
						}

						if (trackerFrame.hasData(TrackerFrameData.POSITION)) {
							writeVector3f(outputStream, trackerFrame.position);
						}

						if (TrackerFrameData.DESIGNATION_ENUM.check(dataFlags)) {
							outputStream.writeInt(trackerFrame.designation.ordinal());
						}

						if (trackerFrame.hasData(TrackerFrameData.ACCELERATION)) {
							writeVector3f(outputStream, trackerFrame.acceleration);
						}

						if (trackerFrame.hasData(TrackerFrameData.RAW_ROTATION)) {
							writeQuaternion(outputStream, trackerFrame.rawRotation);
						}
					}
				}
			} else {
				outputStream.writeInt(0);
			}
		} catch (Exception e) {
			LogManager.severe("Error writing frame to stream", e);
			return false;
		}

		return true;
	}

	public static boolean writeToFile(File file, PoseFrames frames) {
		try (
			DataOutputStream outputStream = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(file))
			)
		) {
			writeFrames(outputStream, frames);
		} catch (Exception e) {
			LogManager.severe("Error writing frames to file", e);
			return false;
		}

		return true;
	}

	private static Vector3f readVector3f(DataInputStream inputStream) throws IOException {
		return new Vector3f(
			inputStream.readFloat(),
			inputStream.readFloat(),
			inputStream.readFloat()
		);
	}

	private static Quaternion readQuaternion(DataInputStream inputStream) throws IOException {
		return new Quaternion(
			inputStream.readFloat(),
			inputStream.readFloat(),
			inputStream.readFloat(),
			inputStream.readFloat()
		);
	}

	public static PoseFrames readFrames(DataInputStream inputStream) {
		try {

			int trackerCount = inputStream.readInt();
			FastList<PoseFrameTracker> trackers = new FastList<>(trackerCount);
			for (int i = 0; i < trackerCount; i++) {

				String name = inputStream.readUTF();
				int trackerFrameCount = inputStream.readInt();
				FastList<TrackerFrame> trackerFrames = new FastList<>(
					trackerFrameCount
				);
				for (int j = 0; j < trackerFrameCount; j++) {
					int dataFlags = inputStream.readInt();

					TrackerPosition designation = null;
					if (TrackerFrameData.DESIGNATION_STRING.check(dataFlags)) {
						designation = TrackerPosition
							.getByDesignation(inputStream.readUTF())
							.orElse(null);
					}

					Quaternion rotation = null;
					if (TrackerFrameData.ROTATION.check(dataFlags)) {
						rotation = readQuaternion(inputStream);
					}

					Vector3f position = null;
					if (TrackerFrameData.POSITION.check(dataFlags)) {
						position = readVector3f(inputStream);
					}

					if (TrackerFrameData.DESIGNATION_ENUM.check(dataFlags)) {
						designation = TrackerPosition.values[inputStream.readInt()];
					}

					Vector3f acceleration = null;
					if (TrackerFrameData.ACCELERATION.check(dataFlags)) {
						acceleration = readVector3f(inputStream);
					}

					Quaternion rawRotation = null;
					if (TrackerFrameData.RAW_ROTATION.check(dataFlags)) {
						rawRotation = readQuaternion(inputStream);
					}

					trackerFrames
						.add(
							new TrackerFrame(
								designation,
								rotation,
								position,
								acceleration,
								rawRotation
							)
						);
				}

				trackers.add(new PoseFrameTracker(name, trackerFrames));
			}

			return new PoseFrames(trackers);
		} catch (Exception e) {
			LogManager.severe("Error reading frame from stream", e);
		}

		return null;
	}

	public static PoseFrames readFromFile(File file) {
		try {
			return readFrames(
				new DataInputStream(new BufferedInputStream(new FileInputStream(file)))
			);
		} catch (Exception e) {
			LogManager.severe("Error reading frame from file", e);
		}

		return null;
	}
}
