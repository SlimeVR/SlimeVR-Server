package dev.slimevr.platform.windows;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.ptr.IntByReference;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.Bridge;
import dev.slimevr.bridge.PipeState;
import dev.slimevr.vr.trackers.ShareableTracker;
import dev.slimevr.vr.trackers.TrackerPosition;
import dev.slimevr.vr.trackers.TrackerStatus;
import dev.slimevr.vr.trackers.VRTracker;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class WindowsSteamVRPipeInputBridge extends Thread implements Bridge {

	public static final String PipeName = "\\\\.\\pipe\\SlimeVRInput";
	private static final int MAX_COMMAND_LENGTH = 2048;
	private final byte[] buffArray = new byte[1024];
	private final VRServer server;
	private final StringBuilder commandBuilder = new StringBuilder(1024);
	private final List<VRTracker> trackers = new FastList<>();
	private final Map<Integer, VRTracker> trackersInternal = new HashMap<>();
	private final Vector3f vBuffer = new Vector3f();
	private final Quaternion qBuffer = new Quaternion();
	private final AtomicBoolean newData = new AtomicBoolean(false);
	private WindowsPipe pipe;

	public WindowsSteamVRPipeInputBridge(VRServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		try {
			createPipes();
			while (true) {
				boolean pipesUpdated = false;
				if (pipe.state == PipeState.CREATED) {
					tryOpeningPipe(pipe);
				}
				if (pipe.state == PipeState.OPEN) {
					pipesUpdated = updatePipes();
				}
				if (pipe.state == PipeState.ERROR) {
					resetPipe();
				}
				if (!pipesUpdated) {
					try {
						Thread.sleep(5); // Up to 200Hz
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean updatePipes() throws IOException {
		if (pipe.state == PipeState.OPEN) {
			IntByReference bytesAvailable = new IntByReference(0);
			if (
				Kernel32.INSTANCE
					.PeekNamedPipe(pipe.pipeHandle, null, 0, null, bytesAvailable, null)
			) {
				if (bytesAvailable.getValue() > 0) {
					while (
						Kernel32.INSTANCE
							.ReadFile(
								pipe.pipeHandle,
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
								executeInputCommand();
								commandBuilder.setLength(0);
							} else {
								commandBuilder.append(c);
								if (commandBuilder.length() >= MAX_COMMAND_LENGTH) {
									LogManager
										.severe(
											"[SteamVRPipeInputBridge] Command from the pipe is too long, flushing buffer"
										);
									commandBuilder.setLength(0);
								}
							}
						}
						if (bytesRead < buffArray.length)
							return true; // All pipe data read
					}
				} else {
					return false; // Pipe was empty, it's okay
				}
			}
			// PeekNamedPipe or ReadFile returned an error
			pipe.state = PipeState.ERROR;
			LogManager
				.severe("[SteamVRPipeInputBridge] Pipe error: " + Kernel32.INSTANCE.GetLastError());
		}
		return false;
	}

	private void executeInputCommand() throws IOException {
		String[] command = commandBuilder.toString().split(" ");
		switch (command[0]) {
			case "ADD": // Add new tracker
				if (command.length < 4) {
					LogManager
						.severe(
							"[SteamVRPipeInputBridge] Error in ADD command. Command requires at least 4 arguments. Supplied: "
								+ commandBuilder
						);
					return;
				}
				VRTracker internalTracker = new VRTracker(
					Integer.parseInt(command[1]),
					StringUtils.join(command, " ", 3, command.length),
					true,
					true
				);
				int roleId = Integer.parseInt(command[2]);
				if (roleId >= 0 && roleId < SteamVRInputRoles.values.length) {
					SteamVRInputRoles svrRole = SteamVRInputRoles.values[roleId];
					internalTracker.bodyPosition = svrRole.bodyPosition;
				}
				VRTracker oldTracker;
				synchronized (trackersInternal) {
					oldTracker = trackersInternal
						.put(internalTracker.getTrackerId(), internalTracker);
				}
				if (oldTracker != null) {
					LogManager
						.severe(
							"[SteamVRPipeInputBridge] New tracker added with the same id. Supplied: "
								+ commandBuilder
						);
					return;
				}
				newData.set(true);
				break;
			case "UPD": // Update tracker data
				if (command.length < 9) {
					LogManager
						.severe(
							"[SteamVRPipeInputBridge] Error in UPD command. Command requires at least 9 arguments. Supplied: "
								+ commandBuilder
						);
					return;
				}
				int id = Integer.parseInt(command[1]);
				double x = Double.parseDouble(command[2]);
				double y = Double.parseDouble(command[3]);
				double z = Double.parseDouble(command[4]);
				double qw = Double.parseDouble(command[5]);
				double qx = Double.parseDouble(command[6]);
				double qy = Double.parseDouble(command[7]);
				double qz = Double.parseDouble(command[8]);
				internalTracker = trackersInternal.get(id);
				if (internalTracker != null) {
					internalTracker.position.set((float) x, (float) y, (float) z);
					internalTracker.rotation.set((float) qx, (float) qy, (float) qz, (float) qw);
					internalTracker.dataTick();
					newData.set(true);
				}
				break;
			case "STA": // Update tracker status
				if (command.length < 3) {
					LogManager
						.severe(
							"[SteamVRPipeInputBridge] Error in STA command. Command requires at least 3 arguments. Supplied: "
								+ commandBuilder
						);
					return;
				}
				id = Integer.parseInt(command[1]);
				int status = Integer.parseInt(command[2]);
				TrackerStatus st = TrackerStatus.getById(status);
				if (st == null) {
					LogManager
						.severe(
							"[SteamVRPipeInputBridge] Unrecognized status id. Supplied: "
								+ commandBuilder
						);
					return;
				}
				internalTracker = trackersInternal.get(id);
				if (internalTracker != null) {
					internalTracker.setStatus(st);
					newData.set(true);
				}
				break;
		}
	}

	@Override
	public void dataRead() {
		if (newData.getAndSet(false)) {
			if (trackers.size() < trackersInternal.size()) {
				// Add new trackers
				synchronized (trackersInternal) {
					Iterator<VRTracker> iterator = trackersInternal.values().iterator();
					internal: while (iterator.hasNext()) {
						VRTracker internalTracker = iterator.next();
						for (VRTracker t : trackers) {
							if (t.getTrackerId() == internalTracker.getTrackerId())
								continue internal;
						}
						// Tracker is not found in current trackers
						VRTracker tracker = new VRTracker(
							internalTracker.getTrackerId(),
							internalTracker.getName(),
							true,
							true
						);
						tracker.bodyPosition = internalTracker.bodyPosition;
						trackers.add(tracker);
						server.registerTracker(tracker);
					}
				}
			}
			for (VRTracker tracker : trackers) {
				VRTracker internal = trackersInternal.get(tracker.getTrackerId());
				if (internal == null)
					throw new NullPointerException(
						"Lost internal tracker somehow: " + tracker.getTrackerId()
					); // Shouln't
				// really
				// happen
				// even,
				// but
				// better
				// to
				// catch
				// it
				// like
				// this
				if (internal.getPosition(vBuffer))
					tracker.position.set(vBuffer);
				if (internal.getRotation(qBuffer))
					tracker.rotation.set(qBuffer);
				tracker.setStatus(internal.getStatus());
				tracker.dataTick();
			}
		}
	}

	@Override
	public void dataWrite() {
		// Not used, only input
	}

	private void resetPipe() {
		WindowsPipe.safeDisconnect(pipe);
		pipe.state = PipeState.CREATED;
		// Main.vrServer.queueTask(this::disconnected);
	}

	private boolean tryOpeningPipe(WindowsPipe pipe) {
		if (
			Kernel32.INSTANCE.ConnectNamedPipe(pipe.pipeHandle, null)
				|| Kernel32.INSTANCE.GetLastError() == WinError.ERROR_PIPE_CONNECTED
		) {
			pipe.state = PipeState.OPEN;
			LogManager.info("[SteamVRPipeInputBridge] Pipe " + pipe.name + " is open");
			return true;
		}

		LogManager
			.info(
				"[SteamVRPipeInputBridge] Error connecting to pipe "
					+ pipe.name
					+ ": "
					+ Kernel32.INSTANCE.GetLastError()
			);
		return false;
	}

	private void createPipes() throws IOException {
		try {
			pipe = new WindowsPipe(
				Kernel32.INSTANCE
					.CreateNamedPipe(
						PipeName,
						WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
						WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
						1, // nMaxInstances,
						1024 * 16, // nOutBufferSize,
						1024 * 16, // nInBufferSize,
						0, // nDefaultTimeOut,
						null
					),
				PipeName
			); // lpSecurityAttributes
			LogManager.info("[SteamVRPipeInputBridge] Pipe " + pipe.name + " created");
			if (WinBase.INVALID_HANDLE_VALUE.equals(pipe.pipeHandle))
				throw new IOException(
					"Can't open " + PipeName + " pipe: " + Kernel32.INSTANCE.GetLastError()
				);
			LogManager.info("[SteamVRPipeInputBridge] Pipes are open");
		} catch (IOException e) {
			WindowsPipe.safeDisconnect(pipe);
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

	public enum SteamVRInputRoles {
		HEAD(TrackerPosition.HMD), LEFT_HAND(TrackerPosition.LEFT_CONTROLLER),
		RIGHT_HAND(TrackerPosition.RIGHT_CONTROLLER), LEFT_FOOT(TrackerPosition.LEFT_FOOT),
		RIGHT_FOOT(TrackerPosition.RIGHT_FOOT), LEFT_SHOULDER(TrackerPosition.NONE),
		RIGHT_SHOULDER(TrackerPosition.NONE), LEFT_ELBOW(TrackerPosition.LEFT_FOREARM),
		RIGHT_ELBOW(TrackerPosition.RIGHT_FOREARM), LEFT_KNEE(TrackerPosition.LEFT_KNEE),
		RIGHT_KNEE(TrackerPosition.RIGHT_KNEE), WAIST(TrackerPosition.WAIST),
		CHEST(TrackerPosition.CHEST),;

		private static final SteamVRInputRoles[] values = values();
		public final TrackerPosition bodyPosition;

		SteamVRInputRoles(TrackerPosition slimeVrPosition) {
			this.bodyPosition = slimeVrPosition;
		}
	}
}
