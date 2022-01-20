package dev.slimevr.posestreamer;

import java.io.IOException;

import dev.slimevr.VRServer;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import io.eiren.util.logging.LogManager;

public class PoseStreamer {
	
	protected long frameRecordingInterval = 60L;
	protected long nextFrameTimeMs = -1L;
	
	private HumanSkeleton skeleton;
	private PoseDataStream poseFileStream;
	
	protected final VRServer server;
	
	public PoseStreamer(VRServer server) {
		this.server = server;
		
		// Register callbacks/events
		server.addSkeletonUpdatedCallback(this::onSkeletonUpdated);
		server.addOnTick(this::onTick);
	}
	
	@VRServerThread
	public void onSkeletonUpdated(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}
	
	@VRServerThread
	public void onTick() {
		PoseDataStream poseFileStream = this.poseFileStream;
		if(poseFileStream == null) {
			return;
		}
		
		HumanSkeleton skeleton = this.skeleton;
		if(skeleton == null) {
			return;
		}
		
		long curTime = System.currentTimeMillis();
		if(curTime < nextFrameTimeMs) {
			return;
		}
		
		nextFrameTimeMs += frameRecordingInterval;
		
		// To prevent duplicate frames, make sure the frame time is always in the future
		if(nextFrameTimeMs <= curTime) {
			nextFrameTimeMs = curTime + frameRecordingInterval;
		}
		
		// Make sure it's synchronized since this is the server thread interacting with
		// an unknown outside thread controlling this class
		synchronized(this) {
			// Make sure the stream is open before trying to write
			if(poseFileStream.isClosed()) {
				return;
			}
			
			try {
				poseFileStream.writeFrame(skeleton);
			} catch(Exception e) {
				// Handle any exceptions without crashing the program
				LogManager.log.severe("[PoseStreamer] Exception while saving frame", e);
			}
		}
	}
	
	public synchronized void setFrameInterval(long intervalMs) {
		if(intervalMs < 1) {
			throw new IllegalArgumentException("intervalMs must at least have a value of 1");
		}
		
		this.frameRecordingInterval = intervalMs;
	}
	
	public synchronized long getFrameInterval() {
		return frameRecordingInterval;
	}
	
	public synchronized void setOutput(PoseDataStream poseFileStream) throws IOException {
		poseFileStream.writeHeader(skeleton, this);
		this.poseFileStream = poseFileStream;
		nextFrameTimeMs = -1L; // Reset the frame timing
	}
	
	public synchronized void setOutput(PoseDataStream poseFileStream, long intervalMs) throws IOException {
		setFrameInterval(intervalMs);
		setOutput(poseFileStream);
	}
	
	public synchronized PoseDataStream getOutput() {
		return poseFileStream;
	}
	
	public synchronized void closeOutput() throws IOException {
		PoseDataStream poseFileStream = this.poseFileStream;
		
		if(poseFileStream != null) {
			closeOutput(poseFileStream);
			this.poseFileStream = null;
		}
	}
	
	public synchronized void closeOutput(PoseDataStream poseFileStream) throws IOException {
		if(poseFileStream != null) {
			poseFileStream.writeFooter(skeleton);
			poseFileStream.close();
		}
	}
}
