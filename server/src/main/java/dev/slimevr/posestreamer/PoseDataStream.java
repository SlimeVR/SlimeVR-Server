package dev.slimevr.posestreamer;

import dev.slimevr.vr.processor.skeleton.Skeleton;

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

	public void writeHeader(Skeleton skeleton, PoseStreamer streamer) throws IOException {
	}

	abstract void writeFrame(Skeleton skeleton) throws IOException;

	public void writeFooter(Skeleton skeleton) throws IOException {
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
