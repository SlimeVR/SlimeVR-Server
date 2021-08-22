package io.eiren.vr.bridge;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;

import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.TrackerBodyPosition;
import io.eiren.vr.trackers.SteamVRTracker;
import io.eiren.vr.trackers.TrackerStatus;

public class SteamVRPipeInputBridge extends Thread implements VRBridge {

	private static final int MAX_COMMAND_LENGTH = 2048;
	public static final String PipeName = "\\\\.\\pipe\\SlimeVRInput";

	private final byte[] buffArray = new byte[1024];
	private final VRServer server;
	private final StringBuilder commandBuilder = new StringBuilder(1024);
	private final List<SteamVRTracker> trackers = new FastList<>();
	private final Map<Integer, SteamVRTracker> trackersInternal = new HashMap<>();
	private AtomicBoolean newData = new AtomicBoolean(false);
	private final Vector3f vBuffer = new Vector3f();
	private final Quaternion qBuffer = new Quaternion();
	private Pipe pipe;
	
	public SteamVRPipeInputBridge(VRServer server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		try {
			createPipes();
			while(true) {
				waitForPipesToOpen();
				if(areAllPipesOpen()) {
					boolean pipesUpdated = updatePipes(); // Update at HMDs frequency
					if(!pipesUpdated) {
						Thread.sleep(5); // Up to 200Hz
					}
				} else {
					Thread.sleep(10);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void waitForPipesToOpen() {
		if(pipe.state == PipeState.CREATED) {
			tryOpeningPipe(pipe);
		}
	}
	
	public boolean updatePipes() throws IOException {
		if(pipe.state == PipeState.OPEN) {
			IntByReference bytesAvailable = new IntByReference(0);
			if(Kernel32.INSTANCE.PeekNamedPipe(pipe.pipeHandle, null, 0, null, bytesAvailable, null)) {
				if(bytesAvailable.getValue() > 0) {
					while(Kernel32.INSTANCE.ReadFile(pipe.pipeHandle, buffArray, buffArray.length, bytesAvailable, null)) {
						int bytesRead = bytesAvailable.getValue();
						for(int i = 0; i < bytesRead; ++i) {
							char c = (char) buffArray[i];
							if(c == '\n') {
								executeInputCommand();
								commandBuilder.setLength(0);
							} else {
								commandBuilder.append(c);
								if(commandBuilder.length() >= MAX_COMMAND_LENGTH) {
									LogManager.log.severe("[SteamVRPipeInputBridge] Command from the pipe is too long, flushing buffer");
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
		}
		return false;
	}
	
	private void executeInputCommand() throws IOException {
		String[] command = commandBuilder.toString().split(" ");
		switch(command[0]) {
		case "ADD": // Add new tracker
			if(command.length < 4) {
				LogManager.log.severe("[SteamVRPipeInputBridge] Error in ADD command. Command requires at least 4 arguments. Supplied: " + commandBuilder.toString());
				return;
			}
			SteamVRTracker internalTracker = new SteamVRTracker(Integer.parseInt(command[1]), StringUtils.join(command, " ", 3, command.length));
			int roleId = Integer.parseInt(command[2]);
			if(roleId >= 0 && roleId < SteamVRInputRoles.values.length) {
				SteamVRInputRoles svrRole = SteamVRInputRoles.values[roleId];
				internalTracker.bodyPosition = svrRole.bodyPosition;
			}
			SteamVRTracker oldTracker;
			synchronized(trackersInternal) {
				oldTracker = trackersInternal.put(internalTracker.id, internalTracker);
			}
			if(oldTracker != null) {
				LogManager.log.severe("[SteamVRPipeInputBridge] New tracker added with the same id. Supplied: " + commandBuilder.toString());
				return;
			}
			newData.set(true);
			break;
		case "UPD": // Update tracker data
			if(command.length < 9) {
				LogManager.log.severe("[SteamVRPipeInputBridge] Error in UPD command. Command requires at least 9 arguments. Supplied: " + commandBuilder.toString());
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
			if(internalTracker != null) {
				internalTracker.position.set((float) x, (float) y, (float) z);
				internalTracker.rotation.set((float) qx, (float) qy, (float) qz, (float) qw);
				internalTracker.dataTick();
				newData.set(true);
			}
			break;
		case "STA": // Update tracker status
			if(command.length < 3) {
				LogManager.log.severe("[SteamVRPipeInputBridge] Error in STA command. Command requires at least 3 arguments. Supplied: " + commandBuilder.toString());
				return;
			}
			id = Integer.parseInt(command[1]);
			int status = Integer.parseInt(command[2]);
			TrackerStatus st = TrackerStatus.getById(status);
			if(st == null) {
				LogManager.log.severe("[SteamVRPipeInputBridge] Unrecognized status id. Supplied: " + commandBuilder.toString());
				return;
			}
			internalTracker = trackersInternal.get(id);
			if(internalTracker != null) {
				internalTracker.setStatus(st);
				newData.set(true);
			}
			break;
		}
	}
	
	@Override
	public void dataRead() {
		// Not used, only input
	}
	
	@Override
	public void dataWrite() {
		if(newData.getAndSet(false)) {
			if(trackers.size() < trackersInternal.size()) {
				// Add new trackers
				synchronized(trackersInternal) {
					Iterator<SteamVRTracker> iterator = trackersInternal.values().iterator();
					internal: while(iterator.hasNext()) {
						SteamVRTracker internalTracker = iterator.next();
						for(int i = 0; i < trackers.size(); ++i) {
							SteamVRTracker t = trackers.get(i);
							if(t.id == internalTracker.id)
								continue internal;
						}
						// Tracker is not found in current trackers
						SteamVRTracker tracker = new SteamVRTracker(internalTracker.id, internalTracker.getName());
						tracker.bodyPosition = internalTracker.bodyPosition;
						trackers.add(tracker);
						server.registerTracker(tracker);
					}
				}
			}
			for(int i = 0; i < trackers.size(); ++i) {
				SteamVRTracker tracker = trackers.get(i);
				SteamVRTracker internal = trackersInternal.get(tracker.id);
				if(internal == null)
					throw new NullPointerException("Lost internal tracker somehow: " + tracker.id); // Shouln't really happen even, but better to catch it like this
				if(internal.getPosition(vBuffer))
					tracker.position.set(vBuffer);
				if(internal.getRotation(qBuffer))
					tracker.rotation.set(qBuffer);
				tracker.dataTick();
			}
		}
	}
	
	private boolean tryOpeningPipe(Pipe pipe) {
		if(Kernel32.INSTANCE.ConnectNamedPipe(pipe.pipeHandle, null)) {
			pipe.state = PipeState.OPEN;
			LogManager.log.info("[SteamVRPipeInputBridge] Pipe " + pipe.name + " is open");
			return true;
		}
		
		LogManager.log.info("[SteamVRPipeInputBridge] Error connecting to pipe " + pipe.name + ": " + Kernel32.INSTANCE.GetLastError());
		return false;
	}
	
	private boolean areAllPipesOpen() {
		if(pipe == null || pipe.state == PipeState.CREATED) {
			return false;
		}
		return true;
	}
	
	private void createPipes() throws IOException {
		try {
			pipe = new Pipe(Kernel32.INSTANCE.CreateNamedPipe(PipeName, WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
					WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
					1, // nMaxInstances,
					1024 * 16, // nOutBufferSize,
					1024 * 16, // nInBufferSize,
					0, // nDefaultTimeOut,
					null), PipeName); // lpSecurityAttributes
			LogManager.log.info("[SteamVRPipeInputBridge] Pipe " + pipe.name + " created");
			if(WinBase.INVALID_HANDLE_VALUE.equals(pipe.pipeHandle))
				throw new IOException("Can't open " + PipeName + " pipe: " + Kernel32.INSTANCE.GetLastError());
			LogManager.log.info("[SteamVRPipeInputBridge] Pipes are open");
		} catch(IOException e) {
			safeDisconnect(pipe);
			throw e;
		}
	}
	
	public static void safeDisconnect(Pipe pipe) {
		try {
			if(pipe != null && pipe.pipeHandle != null)
				Kernel32.INSTANCE.DisconnectNamedPipe(pipe.pipeHandle);
		} catch(Exception e) {
		}
	}
	
	public enum SteamVRInputRoles {
		HEAD(TrackerBodyPosition.HMD),
		LEFT_HAND(TrackerBodyPosition.LEFT_CONTROLLER),
		RIGHT_HAND(TrackerBodyPosition.RIGHT_CONTROLLER),
		LEFT_FOOT(TrackerBodyPosition.LEFT_FOOT),
		RIGHT_FOOT(TrackerBodyPosition.RIGHT_FOOT),
		LEFT_SHOULDER(TrackerBodyPosition.NONE),
		RIGHT_SHOULDER(TrackerBodyPosition.NONE),
		LEFT_ELBOW(TrackerBodyPosition.NONE),
		RIGHT_ELBOW(TrackerBodyPosition.NONE),
		LEFT_KNEE(TrackerBodyPosition.LEFT_LEG),
		RIGHT_KNEE(TrackerBodyPosition.RIGHT_LEG),
		WAIST(TrackerBodyPosition.WAIST),
		CHEST(TrackerBodyPosition.CHEST),
		;
		
		private static final SteamVRInputRoles[] values = values();
		public final TrackerBodyPosition bodyPosition;
		
		private SteamVRInputRoles(TrackerBodyPosition slimeVrPosition) {
			this.bodyPosition = slimeVrPosition;
		}
	}
}
