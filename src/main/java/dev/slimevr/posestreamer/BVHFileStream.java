package dev.slimevr.posestreamer;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.processor.TransformNode;
import dev.slimevr.vr.processor.skeleton.Skeleton;
import org.apache.commons.lang3.StringUtils;

import java.io.*;


public class BVHFileStream extends PoseDataStream {

	private static final int LONG_MAX_VALUE_DIGITS = Long.toString(Long.MAX_VALUE).length();
	private static final float OFFSET_SCALE = 100f;
	private static final float POSITION_SCALE = 100f;
	private final BufferedWriter writer;
	private long frameCount = 0;
	private long frameCountOffset;

	private float[] angleBuf = new float[3];
	private Quaternion rotBuf = new Quaternion();

	private Skeleton wrappedSkeleton;
	private TransformNodeWrapper rootNode;

	public BVHFileStream(OutputStream outputStream) {
		super(outputStream);
		writer = new BufferedWriter(new OutputStreamWriter(outputStream), 4096);
	}

	public BVHFileStream(File file) throws FileNotFoundException {
		super(file);
		writer = new BufferedWriter(new OutputStreamWriter(outputStream), 4096);
	}

	public BVHFileStream(String file) throws FileNotFoundException {
		super(file);
		writer = new BufferedWriter(new OutputStreamWriter(outputStream), 4096);
	}

	private String getBufferedFrameCount(long frameCount) {
		String frameString = Long.toString(frameCount);
		int bufferCount = LONG_MAX_VALUE_DIGITS - frameString.length();

		return bufferCount > 0 ? frameString + StringUtils.repeat(' ', bufferCount) : frameString;
	}

	private TransformNodeWrapper wrapSkeletonIfNew(Skeleton skeleton) {
		TransformNodeWrapper wrapper = rootNode;

		// If the wrapped skeleton is missing or the skeleton is updated
		if (wrapper == null || skeleton != wrappedSkeleton) {
			wrapper = wrapSkeleton(skeleton);
		}

		return wrapper;
	}

	private TransformNodeWrapper wrapSkeleton(Skeleton skeleton) {
		TransformNodeWrapper wrapper = wrapSkeletonNodes(skeleton.getRootNode());

		wrappedSkeleton = skeleton;
		rootNode = wrapper;

		return wrapper;
	}

	protected TransformNodeWrapper wrapSkeletonNodes(TransformNode rootNode) {
		return TransformNodeWrapper.wrapFullHierarchy(rootNode);
	}

	private void writeNodeHierarchy(TransformNodeWrapper node) throws IOException {
		writeNodeHierarchy(node, 0);
	}

	private void writeNodeHierarchy(TransformNodeWrapper node, int level) throws IOException {
		// Don't write end sites at populated nodes
		if (node.children.isEmpty() && node.getParent().children.size() > 1) {
			return;
		}

		String indentLevel = StringUtils.repeat("\t", level);
		String nextIndentLevel = indentLevel + "\t";

		// Handle ends
		if (node.children.isEmpty()) {
			writer.write(indentLevel + "End Site\n");
		} else {
			writer.write((level > 0 ? indentLevel + "JOINT " : "ROOT ") + node.getName() + "\n");
		}
		writer.write(indentLevel + "{\n");

		// Ignore the root offset and original root offset
		if (level > 0 && node.wrappedNode.getParent() != null) {
			Vector3f offset = node.localTransform.getTranslation();
			float reverseMultiplier = node.hasReversedHierarchy() ? -1 : 1;
			writer
				.write(
					nextIndentLevel
						+ "OFFSET "
						+ offset.getX() * OFFSET_SCALE * reverseMultiplier
						+ " "
						+ offset.getY() * OFFSET_SCALE * reverseMultiplier
						+ " "
						+ offset.getZ() * OFFSET_SCALE * reverseMultiplier
						+ "\n"
				);
		} else {
			writer.write(nextIndentLevel + "OFFSET 0.0 0.0 0.0\n");
		}

		// Handle ends
		if (!node.children.isEmpty()) {
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

			for (TransformNodeWrapper childNode : node.children) {
				writeNodeHierarchy(childNode, level + 1);
			}
		}

		writer.write(indentLevel + "}\n");
	}

	@Override
	public void writeHeader(Skeleton skeleton, PoseStreamer streamer) throws IOException {
		if (skeleton == null) {
			throw new NullPointerException("skeleton must not be null");
		}
		if (streamer == null) {
			throw new NullPointerException("streamer must not be null");
		}

		writer.write("HIERARCHY\n");
		writeNodeHierarchy(wrapSkeletonIfNew(skeleton));

		writer.write("MOTION\n");
		writer.write("Frames: ");

		// Get frame offset for finishing writing the file
		if (outputStream instanceof FileOutputStream) {
			FileOutputStream fileOutputStream = (FileOutputStream) outputStream;
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

	private void writeNodeHierarchyRotation(TransformNodeWrapper node, Quaternion inverseRootRot)
		throws IOException {
		Transform transform = node.worldTransform;

		/*
		 * if (node.hasReversedHierarchy()) { for (TransformNodeWrapper
		 * childNode : node.children) { // If the hierarchy is fully reversed,
		 * set the rotation for the upper bone if
		 * (childNode.hasReversedHierarchy()) { transform =
		 * childNode.worldTransform; break; } } }
		 */

		rotBuf = transform.getRotation(rotBuf);

		// Adjust to local rotation
		if (inverseRootRot != null) {
			rotBuf = inverseRootRot.mult(rotBuf, rotBuf);
		}

		// Yaw (Z), roll (X), pitch (Y) (intrinsic)
		// angleBuf = rotBuf.toAngles(angleBuf);

		// Roll (X), pitch (Y), yaw (Z) (intrinsic)
		angleBuf = quatToXyzAngles(rotBuf.normalizeLocal(), angleBuf);

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
		if (!node.children.isEmpty()) {
			Quaternion inverseRot = transform.getRotation().inverse();
			for (TransformNodeWrapper childNode : node.children) {
				if (childNode.children.isEmpty()) {
					// If it's an end node, skip
					continue;
				}

				// Add spacing
				writer.write(" ");
				writeNodeHierarchyRotation(childNode, inverseRot);
			}
		}
	}

	@Override
	public void writeFrame(Skeleton skeleton) throws IOException {
		if (skeleton == null) {
			throw new NullPointerException("skeleton must not be null");
		}

		TransformNodeWrapper rootNode = wrapSkeletonIfNew(skeleton);

		Vector3f rootPos = rootNode.worldTransform.getTranslation();

		// Write root position
		writer
			.write(
				rootPos.getX() * POSITION_SCALE
					+ " "
					+ rootPos.getY() * POSITION_SCALE
					+ " "
					+ rootPos.getZ() * POSITION_SCALE
					+ " "
			);
		writeNodeHierarchyRotation(rootNode, null);

		writer.newLine();

		frameCount++;
	}

	@Override
	public void writeFooter(Skeleton skeleton) throws IOException {
		// Write the final frame count for files
		if (outputStream instanceof FileOutputStream) {
			FileOutputStream fileOutputStream = (FileOutputStream) outputStream;
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
