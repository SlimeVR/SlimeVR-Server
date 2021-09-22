package dev.slimevr.poserecorder;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.eiren.vr.processor.HumanSkeleton;

public abstract class PoseFileStream implements AutoCloseable {
	
	protected DataOutputStream dataStream;

	protected PoseFileStream(OutputStream outputStream) {
		this.dataStream = new DataOutputStream(new BufferedOutputStream(outputStream));
	}

	protected PoseFileStream(File file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}

	protected PoseFileStream(String file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}

	abstract boolean writeHeader(HumanSkeleton skeleton) throws IOException;

	abstract boolean writeFrame(HumanSkeleton skeleton) throws IOException;

	abstract boolean writeFooter(HumanSkeleton skeleton) throws IOException;

	@Override
	public void close() throws IOException {
		dataStream.close();
	}
}
