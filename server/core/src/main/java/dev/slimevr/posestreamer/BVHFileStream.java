package dev.slimevr.posestreamer;

import com.jme3.math.FastMath;
import dev.slimevr.tracking.processor.Bone;
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;
import org.apache.commons.lang3.StringUtils;

import java.io.*;


public class BVHFileStream extends PoseDataStream {
	private static final int LONG_MAX_VALUE_DIGITS = Long.toString(Long.MAX_VALUE).length();

	private BVHSettings bvhSettings = BVHSettings.BLENDER;

	private final BufferedWriter writer;
	private long frameCount = 0;
	private long frameCountOffset;

	private float[] angleBuf = new float[3];

	public BVHFileStream(OutputStream outputStream) {
		super(outputStream);
		writer = new BufferedWriter(new OutputStreamWriter(outputStream), 4096);
	}

	public BVHFileStream(OutputStream outputStream, BVHSettings bvhSettings) {
		this(outputStream);
		this.bvhSettings = bvhSettings;
	}

	public BVHFileStream(File file) throws FileNotFoundException {
		super(file);
		writer = new BufferedWriter(new OutputStreamWriter(outputStream), 4096);
	}

	public BVHFileStream(File file, BVHSettings bvhSettings) throws FileNotFoundException {
		this(file);
		this.bvhSettings = bvhSettings;
	}

	public BVHFileStream(String file) throws FileNotFoundException {
		super(file);
		writer = new BufferedWriter(new OutputStreamWriter(outputStream), 4096);
	}

	public BVHFileStream(String file, BVHSettings bvhSettings) throws FileNotFoundException {
		this(file);
		this.bvhSettings = bvhSettings;
	}

	public BVHSettings getBvhSettings() {
		return bvhSettings;
	}

	public void setBvhSettings(BVHSettings bvhSettings) {
		this.bvhSettings = bvhSettings;
	}

	private String getBufferedFrameCount(long frameCount) {
		String frameString = Long.toString(frameCount);
		int bufferCount = LONG_MAX_VALUE_DIGITS - frameString.length();

		return bufferCount > 0 ? frameString + StringUtils.repeat(' ', bufferCount) : frameString;
	}

	private boolean isEndBone(Bone bone) {
		return bone == null || (!bvhSettings.shouldWriteEndNodes() && bone.getChildren().isEmpty());
	}

	private void writeBoneHierarchy(Bone bone) throws IOException {
		writeBoneHierarchy(bone, 0);
	}

	private void writeBoneHierarchy(Bone bone, int level) throws IOException {
		// Treat null as bone. This allows for simply writing empty end bones
		boolean isEndBone = isEndBone(bone);

		// Don't write end sites at populated bones, BVH parsers don't like that
		// Ex case caught: `joint{ joint{ end }, end, end }` outputs `joint{ end
		// }` instead
		// Ex case let through: `joint{ end }`
		boolean isSingleChild = bone == null
			|| bone.getParent() == null
			|| bone.getParent().getChildren().size() <= 1;
		if (isEndBone && !isSingleChild) {
			return;
		}

		String indentLevel = StringUtils.repeat("\t", level);
		String nextIndentLevel = indentLevel + "\t";

		// Handle ends
		if (isEndBone) {
			writer.write(indentLevel + "End Site\n");
		} else {
			writer.write((level > 0 ? indentLevel + "JOINT " : "ROOT ") + bone.getBoneType() + "\n");
		}
		writer.write(indentLevel + "{\n");

		// Ignore the root offset and original root offset
		if (level > 0 && bone != null && bone.getParent() != null) {
			float offsetScale = bvhSettings.getOffsetScale();
			writer
				.write(
					nextIndentLevel
						+ "OFFSET "
						+ 0
						+ " "
						+ -bone.getParent().getLength() * offsetScale
						+ " "
						+ 0
						+ "\n"
				);
		} else {
			writer.write(nextIndentLevel + "OFFSET 0.0 0.0 0.0\n");
		}

		// Handle ends
		if (!isEndBone) {
			// Only give position for root
			if (level > 0) {
				writer.write(nextIndentLevel + "CHANNELS 3 Zrotation Xrotation Yrotation\n");
			} else {
				writer
					.write(
						nextIndentLevel
							+ "CHANNELS 6 Xposition Yposition Zposition Zrotation Xrotation Yrotation\n"
					);
			}

			// If the bone has children
			if (!bone.getChildren().isEmpty()) {
				for (Bone childBone : bone.getChildren()) {
					writeBoneHierarchy(childBone, level + 1);
				}
			} else {
				// Write an empty end bone
				writeBoneHierarchy(null, level + 1);
			}
		}

		writer.write(indentLevel + "}\n");
	}

	@Override
	public void writeHeader(HumanSkeleton skeleton, PoseStreamer streamer) throws IOException {
		if (skeleton == null) {
			throw new NullPointerException("skeleton must not be null");
		}
		if (streamer == null) {
			throw new NullPointerException("streamer must not be null");
		}

		writer.write("HIERARCHY\n");
		writeBoneHierarchy(skeleton.getHeadBone());

		writer.write("MOTION\n");
		writer.write("Frames: ");

		// Get frame offset for finishing writing the file
		if (outputStream instanceof FileOutputStream fileOutputStream) {
			// Flush buffer to get proper offset
			writer.flush();
			frameCountOffset = fileOutputStream.getChannel().position();
		}

		writer.write(getBufferedFrameCount(frameCount) + "\n");

		// Frame time in seconds
		writer.write("Frame Time: " + (streamer.getFrameInterval() / 1000d) + "\n");
	}

	// Roughly based off code from
	// https://github.com/TrackLab/ViRe/blob/50a987eff4db31036b2ebaeb5a28983cd473f267/Assets/Scripts/BVH/BVHRecorder.cs
	private float[] quatToXyzAngles(Quaternion q, float[] angles) {
		if (angles == null) {
			angles = new float[3];
		} else if (angles.length != 3) {
			throw new IllegalArgumentException("Angles array must have three elements");
		}

		float x = q.getX();
		float y = q.getY();
		float z = q.getZ();
		float w = q.getW();

		// Roll (X)
		float sinrCosp = -2f * (x * y - w * z);
		float cosrCosp = w * w - x * x + y * y - z * z;
		angles[0] = FastMath.atan2(sinrCosp, cosrCosp);

		// Pitch (Y)
		float sinp = 2f * (y * z + w * x);
		// Use 90 degrees if out of range
		angles[1] = FastMath.abs(sinp) >= 1f
			? FastMath.copysign(FastMath.PI / 2f, sinp)
			: FastMath
				.asin(sinp);

		// Yaw (Z)
		float sinyCosp = -2f * (x * z - w * y);
		float cosyCosp = w * w - x * x - y * y + z * z;
		angles[2] = FastMath.atan2(sinyCosp, cosyCosp);

		return angles;
	}

	private void writeBoneHierarchyRotation(Bone bone, Quaternion inverseRootRot)
		throws IOException {
		Quaternion rot = bone.getGlobalRotation();

		// Adjust to local rotation
		if (inverseRootRot != null) {
			rot = inverseRootRot.times(rot);
		}

		// Roll (X), pitch (Y), yaw (Z) (intrinsic)
		// TODO: `quatToXyzAngles` should no longer be needed, this should be
		// changed to use `Quaternion.toEulerAngles()` instead
		angleBuf = quatToXyzAngles(rot.unit(), angleBuf);

		// Output in order of roll (Z), pitch (X), yaw (Y) (extrinsic)
		writer
			.write(
				angleBuf[0] * FastMath.RAD_TO_DEG
					+ " "
					+ angleBuf[1] * FastMath.RAD_TO_DEG
					+ " "
					+ angleBuf[2] * FastMath.RAD_TO_DEG
			);

		// Get inverse rotation for child local rotations
		if (!bone.getChildren().isEmpty()) {
			Quaternion inverseRot = bone.getGlobalRotation().inv();
			for (Bone childBode : bone.getChildren()) {
				if (isEndBone(childBode)) {
					// If it's an end bone, skip
					continue;
				}

				// Add spacing
				writer.write(" ");
				writeBoneHierarchyRotation(childBode, inverseRot);
			}
		}
	}

	@Override
	public void writeFrame(HumanSkeleton skeleton) throws IOException {
		if (skeleton == null) {
			throw new NullPointerException("skeleton must not be null");
		}

		Bone rootBone = skeleton.getHeadBone();

		Vector3 rootPos = rootBone.getPosition();

		// Write root position
		float positionScale = bvhSettings.getPositionScale();
		writer
			.write(
				rootPos.getX() * positionScale
					+ " "
					+ rootPos.getY() * positionScale
					+ " "
					+ rootPos.getZ() * positionScale
					+ " "
			);
		writeBoneHierarchyRotation(rootBone, null);

		writer.newLine();

		frameCount++;
	}

	@Override
	public void writeFooter(HumanSkeleton skeleton) throws IOException {
		// Write the final frame count for files
		if (outputStream instanceof FileOutputStream fileOutputStream) {
			// Flush before anything else
			writer.flush();
			// Seek to the count offset
			fileOutputStream.getChannel().position(frameCountOffset);
			// Overwrite the count with a new value
			writer.write(Long.toString(frameCount));
		}
	}

	@Override
	public void close() throws IOException {
		writer.close();
		super.close();
	}
}
