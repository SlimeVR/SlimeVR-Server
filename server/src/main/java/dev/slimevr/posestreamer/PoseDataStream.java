package dev.slimevr.posestreamer;

import dev.slimevr.tracking.processor.skeleton.HumanSkeleton;

import java.io.*;


public abstract class PoseDataStream implements AutoCloseable {

	protected final OutputStream outputStream;
	protected boolean closed = false;

	protected PoseDataStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	protected PoseDataStream(File file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}

	protected PoseDataStream(String file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}

	public void writeHeader(HumanSkeleton skeleton, PoseStreamer streamer) throws IOException {
	}

	abstract void writeFrame(HumanSkeleton skeleton) throws IOException;

	public void writeFooter(HumanSkeleton skeleton) throws IOException {
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
		closed = true;
	}
}
