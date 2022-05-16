package dev.slimevr.platform.windows;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.Bridge;
import dev.slimevr.bridge.PipeState;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class WindowsNamedPipeVRBridge extends Thread implements Bridge {

	public static final String HMDPipeName = "\\\\.\\pipe\\HMDPipe";
	public static final String TrackersPipeName = "\\\\.\\pipe\\TrackPipe";
	public static final Charset ASCII = StandardCharsets.US_ASCII;
	private static final int MAX_COMMAND_LENGTH = 2048;
	private final byte[] buffArray = new byte[1024];
	private final StringBuilder commandBuilder = new StringBuilder(1024);
	private final StringBuilder sbBuffer = new StringBuilder(1024);
	private final Vector3f vBuffer = new Vector3f();
	private final Vector3f vBuffer2 = new Vector3f();
	private final Quaternion qBuffer = new Quaternion();
	private final Quaternion qBuffer2 = new Quaternion();
	private final HMDTracker hmd;
	private final List<WindowsPipe> trackerPipes;
	private final List<? extends Tracker> shareTrackers;
	private final List<ComputedTracker> internalTrackers;
	private final HMDTracker internalHMDTracker = new HMDTracker("internal://HMD");
	private final AtomicBoolean newHMDData = new AtomicBoolean(false);
	private WindowsPipe hmdPipe;

	public WindowsNamedPipeVRBridge(
		HMDTracker hmd,
		List<? extends Tracker> shareTrackers,
		VRServer server
	) {
		super("Named Pipe VR Bridge");
		this.hmd = hmd;
		this.shareTrackers = new FastList<>(shareTrackers);
		this.trackerPipes = new FastList<>(shareTrackers.size());
		this.internalTrackers = new FastList<>(shareTrackers.size());
		for (Tracker t : shareTrackers) {
			ComputedTracker ct = new ComputedTracker(
				t.getTrackerId(),
				"internal://" + t.getName(),
				true,
				true
			);
			ct.setStatus(TrackerStatus.OK);
			this.internalTrackers.add(ct);
		}
	}

	public static void safeDisconnect(WindowsPipe pipe) {
		try {
			if (pipe != null && pipe.pipeHandle != null)
				Kernel32.INSTANCE.DisconnectNamedPipe(pipe.pipeHandle);
		} catch (Exception e) {}
	}

	@Override
	public void run() {
		try {
			createPipes();
			while (true) {
				waitForPipesToOpen();
				if (areAllPipesOpen()) {
					boolean hmdUpdated = updateHMD(); // Update at HMDs
														// frequency
					for (int i = 0; i < trackerPipes.size(); ++i) {
						updateTracker(i, hmdUpdated);
					}
					if (!hmdUpdated) {
						Thread.sleep(5); // Up to 200Hz
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dataRead() {
		if (newHMDData.compareAndSet(true, false)) {
			hmd.position.set(internalHMDTracker.position);
			hmd.rotation.set(internalHMDTracker.rotation);
			hmd.dataTick();
		}
	}

	@Override
	public void dataWrite() {
		for (int i = 0; i < shareTrackers.size(); ++i) {
			Tracker t = shareTrackers.get(i);
			ComputedTracker it = this.internalTrackers.get(i);
			if (t.getPosition(vBuffer2))
				it.position.set(vBuffer2);
			if (t.getRotation(qBuffer2))
				it.rotation.set(qBuffer2);
		}
	}

	private void waitForPipesToOpen() {
		if (hmdPipe.state == PipeState.CREATED) {
			if (tryOpeningPipe(hmdPipe))
				initHMDPipe(hmdPipe);
		}
		for (int i = 0; i < trackerPipes.size(); ++i) {
			WindowsPipe trackerPipe = trackerPipes.get(i);
			if (trackerPipe.state == PipeState.CREATED) {
				if (tryOpeningPipe(trackerPipe))
					initTrackerPipe(trackerPipe, i);
			}
		}
	}

	public boolean updateHMD() throws IOException {
		if (hmdPipe.state == PipeState.OPEN) {
			IntByReference bytesAvailable = new IntByReference(0);
			if (
				Kernel32.INSTANCE
					.PeekNamedPipe(hmdPipe.pipeHandle, null, 0, null, bytesAvailable, null)
			) {
				if (bytesAvailable.getValue() > 0) {
					while (
						Kernel32.INSTANCE
							.ReadFile(
								hmdPipe.pipeHandle,
								buffArray,
								buffArray.length,
								bytesAvailable,
								null
							)
					) {
						int bytesRead = bytesAvailable.getValue();
						for (int i = 0; i < bytesRead; ++i) {
							char c = (char) buffArray[i];
							if (c == '\n') {
								executeHMDInput();
								commandBuilder.setLength(0);
							} else {
								commandBuilder.append(c);
								if (commandBuilder.length() >= MAX_COMMAND_LENGTH) {
									LogManager
										.severe(
											"[VRBridge] Command from the pipe is too long, flushing buffer"
										);
									commandBuilder.setLength(0);
								}
							}
						}
						if (bytesRead < buffArray.length)
							break; // Don't repeat, we read all available bytes
					}
					return true;
				}
			}
		}
		return false;
	}

	private void executeHMDInput() throws IOException {
		String[] split = commandBuilder.toString().split(" ");
		if (split.length < 7) {
			LogManager.severe("[VRBridge] Short HMD data received: " + commandBuilder);
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void updateTracker(int trackerId, boolean hmdUpdated) {
		Tracker sensor = internalTrackers.get(trackerId);
		if (sensor.getStatus().sendData) {
			WindowsPipe trackerPipe = trackerPipes.get(trackerId);
			if (hmdUpdated && trackerPipe.state == PipeState.OPEN) {
				sbBuffer.setLength(0);
				sensor.getPosition(vBuffer);
				sensor.getRotation(qBuffer);
				sbBuffer
					.append(vBuffer.x)
					.append(' ')
					.append(vBuffer.y)
					.append(' ')
					.append(vBuffer.z)
					.append(' ');
				sbBuffer
					.append(qBuffer.getW())
					.append(' ')
					.append(qBuffer.getX())
					.append(' ')
					.append(qBuffer.getY())
					.append(' ')
					.append(qBuffer.getZ())
					.append('\n');
				String str = sbBuffer.toString();
				System.arraycopy(str.getBytes(ASCII), 0, buffArray, 0, str.length());
				buffArray[str.length()] = '\0';
				IntByReference lpNumberOfBytesWritten = new IntByReference(0);
				Kernel32.INSTANCE
					.WriteFile(
						trackerPipe.pipeHandle,
						buffArray,
						str.length() + 1,
						lpNumberOfBytesWritten,
						null
					);
			}
		}
	}

	private void initHMDPipe(WindowsPipe pipe) {
		hmd.setStatus(TrackerStatus.OK);
	}

	private void initTrackerPipe(WindowsPipe pipe, int trackerId) {
		String trackerHello = this.shareTrackers.size() + " 0";
		System.arraycopy(trackerHello.getBytes(ASCII), 0, buffArray, 0, trackerHello.length());
		buffArray[trackerHello.length()] = '\0';
		IntByReference lpNumberOfBytesWritten = new IntByReference(0);
		Kernel32.INSTANCE
			.WriteFile(
				pipe.pipeHandle,
				buffArray,
				trackerHello.length() + 1,
				lpNumberOfBytesWritten,
				null
			);
	}

	private boolean tryOpeningPipe(WindowsPipe pipe) {
		if (Kernel32.INSTANCE.ConnectNamedPipe(pipe.pipeHandle, null)) {
			pipe.state = PipeState.OPEN;
			LogManager.info("[VRBridge] Pipe " + pipe.name + " is open");
			return true;
		}

		LogManager
			.info(
				"[VRBridge] Error connecting to pipe "
					+ pipe.name
					+ ": "
					+ Kernel32.INSTANCE.GetLastError()
			);
		return false;
	}

	private boolean areAllPipesOpen() {
		if (hmdPipe == null || hmdPipe.state == PipeState.CREATED) {
			return false;
		}
		for (WindowsPipe pipe : trackerPipes) {
			if (pipe.state == PipeState.CREATED) {
				return false;
			}
		}
		return true;
	}

	private void createPipes() throws IOException {
		try {
			hmdPipe = new WindowsPipe(
				Kernel32.INSTANCE
					.CreateNamedPipe(
						HMDPipeName,
						WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
						WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
						1, // nMaxInstances,
						1024 * 16, // nOutBufferSize,
						1024 * 16, // nInBufferSize,
						0, // nDefaultTimeOut,
						null
					),
				HMDPipeName
			); // lpSecurityAttributes
			LogManager.info("[VRBridge] Pipe " + hmdPipe.name + " created");
			if (WinBase.INVALID_HANDLE_VALUE.equals(hmdPipe.pipeHandle))
				throw new IOException(
					"Can't open " + HMDPipeName + " pipe: " + Kernel32.INSTANCE.GetLastError()
				);
			for (int i = 0; i < this.shareTrackers.size(); ++i) {
				String pipeName = TrackersPipeName + i;
				HANDLE pipeHandle = Kernel32.INSTANCE
					.CreateNamedPipe(
						pipeName,
						WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
						WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
						1, // nMaxInstances,
						1024 * 16, // nOutBufferSize,
						1024 * 16, // nInBufferSize,
						0, // nDefaultTimeOut,
						null
					); // lpSecurityAttributes
				if (WinBase.INVALID_HANDLE_VALUE.equals(pipeHandle))
					throw new IOException(
						"Can't open " + pipeName + " pipe: " + Kernel32.INSTANCE.GetLastError()
					);
				LogManager.info("[VRBridge] Pipe " + pipeName + " created");
				trackerPipes.add(new WindowsPipe(pipeHandle, pipeName));
			}
			LogManager.info("[VRBridge] Pipes are open");
		} catch (IOException e) {
			safeDisconnect(hmdPipe);
			for (WindowsPipe pipe : trackerPipes) {
				safeDisconnect(pipe);
			}
			trackerPipes.clear();
			throw e;
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
