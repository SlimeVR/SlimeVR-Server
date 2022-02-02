package dev.slimevr.platform.linux;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.Bridge;
import dev.slimevr.bridge.PipeState;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LinuxNamedPipeVRBridge extends Thread implements Bridge {

	private static final int MAX_COMMAND_LENGTH = 2048;
	public static final String HMDPipeName = "\\\\.\\pipe\\HMDPipe";
	public static final String TrackersPipeName = "\\\\.\\pipe\\TrackPipe";
	public static final Charset ASCII = Charset.forName("ASCII");

	private final byte[] buffArray = new byte[1024];
	private final StringBuilder commandBuilder = new StringBuilder(1024);
	private final StringBuilder sbBuffer = new StringBuilder(1024);
	private final Vector3f vBuffer = new Vector3f();
	private final Vector3f vBuffer2 = new Vector3f();
	private final Quaternion qBuffer = new Quaternion();
	private final Quaternion qBuffer2 = new Quaternion();

	private LinuxPipe hmdPipe;
	private final HMDTracker hmd;
	private final List<LinuxPipe> trackerPipes;
	private final List<? extends Tracker> shareTrackers;
	private final List<ComputedTracker> internalTrackers;

	private final HMDTracker internalHMDTracker = new HMDTracker("internal://HMD");
	private final AtomicBoolean newHMDData = new AtomicBoolean(false);

	public LinuxNamedPipeVRBridge(HMDTracker hmd, List<? extends Tracker> shareTrackers, VRServer server) {
		super("Named Pipe VR Bridge");
		this.hmd = hmd;
		this.shareTrackers = new FastList<>(shareTrackers);
		this.trackerPipes = new FastList<>(shareTrackers.size());
		this.internalTrackers = new FastList<>(shareTrackers.size());
		for(int i = 0; i < shareTrackers.size(); ++i) {
			Tracker t = shareTrackers.get(i);
			ComputedTracker ct = new ComputedTracker(t.getTrackerId(), "internal://" + t.getName(), true, true);
			ct.setStatus(TrackerStatus.OK);
			this.internalTrackers.add(ct);
		}
	}

	@Override
	public void run() {
		try {
			createPipes();
			while(true) {
				waitForPipesToOpen();
				if(areAllPipesOpen()) {
					boolean hmdUpdated = updateHMD(); // Update at HMDs frequency
					for(int i = 0; i < trackerPipes.size(); ++i) {
						updateTracker(i, hmdUpdated);
					}
					if(!hmdUpdated) {
						Thread.sleep(5); // Up to 200Hz
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dataRead() {
		if(newHMDData.compareAndSet(true, false)) {
			hmd.position.set(internalHMDTracker.position);
			hmd.rotation.set(internalHMDTracker.rotation);
			hmd.dataTick();
		}
	}

	@Override
	public void dataWrite() {
		for(int i = 0; i < shareTrackers.size(); ++i) {
			Tracker t = shareTrackers.get(i);
			ComputedTracker it = this.internalTrackers.get(i);
			if(t.getPosition(vBuffer2))
				it.position.set(vBuffer2);
			if(t.getRotation(qBuffer2))
				it.rotation.set(qBuffer2);
		}
	}

	private void waitForPipesToOpen() {
		if(hmdPipe.state == PipeState.CREATED) {
			if(tryOpeningPipe(hmdPipe))
				initHMDPipe(hmdPipe);
		}
		for(int i = 0; i < trackerPipes.size(); ++i) {
			LinuxPipe trackerPipe = trackerPipes.get(i);
			if(trackerPipe.state == PipeState.CREATED) {
				if(tryOpeningPipe(trackerPipe))
					initTrackerPipe(trackerPipe, i);
			}
		}
	}

	public boolean updateHMD() throws IOException {
		if(hmdPipe.state == PipeState.OPEN) {
			IntByReference bytesAvailable = new IntByReference(0);
				if(bytesAvailable.getValue() > 0) {
					while(hmdPipe.pipe.read(ByteBuffer.wrap(buffArray)) != 0) {
						int bytesRead = bytesAvailable.getValue();
						for(int i = 0; i < bytesRead; ++i) {
							char c = (char) buffArray[i];
							if(c == '\n') {
								executeHMDInput();
								commandBuilder.setLength(0);
							} else {
								commandBuilder.append(c);
								if(commandBuilder.length() >= MAX_COMMAND_LENGTH) {
									LogManager.log.severe("[VRBridge] Command from the pipe is too long, flushing buffer");
									commandBuilder.setLength(0);
								}
							}
						}
						if(bytesRead < buffArray.length)
							break; // Don't repeat, we read all available bytes
					}
					return true;
				}
		}
		return false;
	}

	private void executeHMDInput() throws IOException {
		String[] split = commandBuilder.toString().split(" ");
		if(split.length < 7) {
			LogManager.log.severe("[VRBridge] Short HMD data received: " + commandBuilder.toString());
			return;
		}
		try {
			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			double z = Double.parseDouble(split[2]);
			double qw = Double.parseDouble(split[3]);
			double qx = Double.parseDouble(split[4]);
			double qy = Double.parseDouble(split[5]);
			double qz = Double.parseDouble(split[6]);

			internalHMDTracker.position.set((float) x, (float) y, (float) z);
			internalHMDTracker.rotation.set((float) qx, (float) qy, (float) qz, (float) qw);
			internalHMDTracker.dataTick();
			newHMDData.set(true);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void updateTracker(int trackerId, boolean hmdUpdated) {
		Tracker sensor = internalTrackers.get(trackerId);
		if(sensor.getStatus().sendData) {
			LinuxPipe trackerPipe = trackerPipes.get(trackerId);
			if(hmdUpdated && trackerPipe.state == PipeState.OPEN) {
				sbBuffer.setLength(0);
				sensor.getPosition(vBuffer);
				sensor.getRotation(qBuffer);
				sbBuffer.append(vBuffer.x).append(' ').append(vBuffer.y).append(' ').append(vBuffer.z).append(' ');
				sbBuffer.append(qBuffer.getW()).append(' ').append(qBuffer.getX()).append(' ').append(qBuffer.getY()).append(' ').append(qBuffer.getZ()).append('\n');
				String str = sbBuffer.toString();
				System.arraycopy(str.getBytes(ASCII), 0, buffArray, 0, str.length());
				buffArray[str.length()] = '\0';
				IntByReference lpNumberOfBytesWritten = new IntByReference(0);
				try {
					trackerPipe.pipe.write(ByteBuffer.wrap(buffArray));
				} catch(IOException e) {

				}
			}
		}
	}

	private void initHMDPipe(LinuxPipe pipe) {
		hmd.setStatus(TrackerStatus.OK);
	}

	private void initTrackerPipe(LinuxPipe pipe, int trackerId) {
		String trackerHello = this.shareTrackers.size() + " 0";
		System.arraycopy(trackerHello.getBytes(ASCII), 0, buffArray, 0, trackerHello.length());
		buffArray[trackerHello.length()] = '\0';
		IntByReference lpNumberOfBytesWritten = new IntByReference(0);
		try {
			pipe.pipe.write(ByteBuffer.wrap(buffArray));
		} catch(IOException e) {

		}
	}

	private boolean tryOpeningPipe(LinuxPipe pipe) {
		if(pipe.pipe.isOpen()) {
			pipe.state = PipeState.OPEN;
			LogManager.log.info("[VRBridge] Pipe " + pipe.name + " is open");
			return true;
		}

		LogManager.log.info("[VRBridge] Error connecting to pipe " + pipe.name + ": " + Native.getLastError());
		return false;
	}

	private boolean areAllPipesOpen() {
		if(hmdPipe == null || hmdPipe.state == PipeState.CREATED) {
			return false;
		}
		for(int i = 0; i < trackerPipes.size(); ++i) {
			if(trackerPipes.get(i).state == PipeState.CREATED)
				return false;
		}
		return true;
	}

	private void createPipes() throws IOException {
		try {
			RandomAccessFile rw = new RandomAccessFile(HMDPipeName, "rw");
			FileChannel fc = rw.getChannel();
			hmdPipe = new LinuxPipe(fc, HMDPipeName); // lpSecurityAttributes
			LogManager.log.info("[VRBridge] Pipe " + hmdPipe.name + " created");
			//if(WinBase.INVALID_HANDLE_VALUE.equals(hmdPipe.pipeHandle))
			//	throw new IOException("Can't open " + HMDPipeName + " pipe: " + Native.getLastError());
			for(int i = 0; i < this.shareTrackers.size(); ++i) {
				String pipeName = TrackersPipeName + i;
				RandomAccessFile Trw = new RandomAccessFile(pipeName, "rw");
				FileChannel Tfc = Trw.getChannel();
				 LinuxPipe pipeHandle = new LinuxPipe(Tfc, pipeName+i); // lpSecurityAttributes
				//if(WinBase.INVALID_HANDLE_VALUE.equals(pipeHandle))
				//	throw new IOException("Can't open " + pipeName + " pipe: " + Native.getLastError());
				LogManager.log.info("[VRBridge] Pipe " + pipeName + " created");
				trackerPipes.add(new LinuxPipe(pipeHandle.pipe, pipeName));
			}
			LogManager.log.info("[VRBridge] Pipes are open");
		} catch(IOException e) {
			safeDisconnect(hmdPipe);
			for(int i = 0; i < trackerPipes.size(); ++i)
				safeDisconnect(trackerPipes.get(i));
			trackerPipes.clear();
			throw e;
		}
	}

	public static void safeDisconnect(LinuxPipe pipe) {
		try {
			if(pipe != null && pipe.pipe != null) {
				pipe.pipe.close();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void addSharedTracker(ShareableTracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSharedTracker(ShareableTracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startBridge() {
		start();
	}
}
