package dev.slimevr.poserecorder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.eiren.vr.processor.HumanSkeleton;

public abstract class PoseFileStream implements AutoCloseable {

	protected final OutputStream parentStream;
	protected final BufferedOutputStream outputStream;

	protected PoseFileStream(OutputStream outputStream) {
		this.parentStream = outputStream;
		this.outputStream = new BufferedOutputStream(outputStream);
	}

	protected PoseFileStream(File file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}

	protected PoseFileStream(String file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}

	public void writeHeader(HumanSkeleton skeleton, PoseStreamer streamer) throws IOException {
	}

	abstract void writeFrame(HumanSkeleton skeleton) throws IOException;

	public void writeFooter(HumanSkeleton skeleton) throws IOException {
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}
}
