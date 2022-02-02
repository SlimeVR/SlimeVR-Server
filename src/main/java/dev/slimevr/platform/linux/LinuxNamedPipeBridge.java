package dev.slimevr.platform.linux;

import com.google.protobuf.CodedOutputStream;
import java.nio.*;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import dev.slimevr.Main;
import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.bridge.PipeState;
import dev.slimevr.bridge.ProtobufBridge;
import dev.slimevr.bridge.ProtobufMessages;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.List;

public class LinuxNamedPipeBridge extends ProtobufBridge<VRTracker> implements Runnable {

	private final TrackerRole[] defaultRoles = new TrackerRole[] {TrackerRole.WAIST, TrackerRole.LEFT_FOOT, TrackerRole.RIGHT_FOOT};

	private final byte[] buffArray = new byte[2048];

	protected LinuxPipe pipe;
	protected final String pipeName;
	protected final String bridgeSettingsKey;
	protected final Thread runnerThread;
	private final List<? extends ShareableTracker> shareableTrackers;

	public LinuxNamedPipeBridge(HMDTracker hmd, String bridgeSettingsKey, String bridgeName, String pipeName, List<? extends ShareableTracker> shareableTrackers) {
		super(bridgeName, hmd);
		this.pipeName = pipeName;
		this.bridgeSettingsKey = bridgeSettingsKey;
		this.runnerThread = new Thread(this, "Named pipe thread");
		this.shareableTrackers = shareableTrackers;
	}

	@Override
	@VRServerThread
	public void startBridge() {
		for(TrackerRole role : defaultRoles) {
			changeShareSettings(role, Main.vrServer.config.getBoolean("bridge." + bridgeSettingsKey + ".trackers." + role.name().toLowerCase(), true));
		}
		for(int i = 0; i < shareableTrackers.size(); ++i) {
			ShareableTracker tr = shareableTrackers.get(i);
			TrackerRole role = tr.getTrackerRole();
			changeShareSettings(role, Main.vrServer.config.getBoolean("bridge." + bridgeSettingsKey + ".trackers." + role.name().toLowerCase(), false));
		}
		runnerThread.start();
	}

	@VRServerThread
	public boolean getShareSetting(TrackerRole role) {
		for(int i = 0; i < shareableTrackers.size(); ++i) {
			ShareableTracker tr = shareableTrackers.get(i);
			if(tr.getTrackerRole() == role) {
				return sharedTrackers.contains(tr);
			}
		}
		return false;
	}

	@VRServerThread
	public void changeShareSettings(TrackerRole role, boolean share) {
		if(role == null)
			return;
		for(int i = 0; i < shareableTrackers.size(); ++i) {
			ShareableTracker tr = shareableTrackers.get(i);
			if(tr.getTrackerRole() == role) {
				if(share) {
					addSharedTracker(tr);
				} else {
					removeSharedTracker(tr);
				}
				Main.vrServer.config.setProperty("bridge." + bridgeSettingsKey + ".trackers." + role.name().toLowerCase(), share);
				Main.vrServer.saveConfig();
			}
		}
	}

	@Override
	@VRServerThread
	protected VRTracker createNewTracker(ProtobufMessages.TrackerAdded trackerAdded) {
		VRTracker tracker = new VRTracker(trackerAdded.getTrackerId(), trackerAdded.getTrackerSerial(), trackerAdded.getTrackerName(), true, true);
		TrackerRole role = TrackerRole.getById(trackerAdded.getTrackerRole());
		if(role != null) {
			tracker.setBodyPosition(TrackerPosition.getByRole(role));
		}
		return tracker;
	}

	@Override
	@BridgeThread
	public void run() {
		try {
			createPipe();
			while(true) {
				boolean pipesUpdated = false;
				if(pipe.state == PipeState.CREATED) {
					tryOpeningPipe(pipe);
				}
				if(pipe.state == PipeState.OPEN) {
					pipesUpdated = updatePipe();
					updateMessageQueue();
				}
				if(pipe.state == PipeState.ERROR) {
					resetPipe();
				}
				if(!pipesUpdated) {
					try {
						Thread.sleep(5); // Up to 200Hz
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	@BridgeThread
	protected boolean sendMessageReal(ProtobufMessages.ProtobufMessage message) {
		if(pipe.state == PipeState.OPEN) {
			try {
				int size = message.getSerializedSize();
				CodedOutputStream os = CodedOutputStream.newInstance(buffArray, 4, size);
				message.writeTo(os);
				size += 4;
				buffArray[0] = (byte) (size & 0xFF);
				buffArray[1] = (byte) ((size >> 8) & 0xFF);
				buffArray[2] = (byte) ((size >> 16) & 0xFF);
				buffArray[3] = (byte) ((size >> 24) & 0xFF);
				try {
					pipe.pipe.write(ByteBuffer.wrap(buffArray));
					return true;
				} catch (IOException e) {
					pipe.state = PipeState.ERROR;
					LogManager.log.severe("[" + bridgeName + "] Pipe error: " + Native.getLastError());
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean updatePipe() throws IOException {
		if(pipe.state == PipeState.OPEN) {
			boolean readAnything = false;
			IntByReference bytesAvailable = new IntByReference(0);
			while(pipe.pipe.isOpen()) {
				if(bytesAvailable.getValue() >= 4) { // Got size
					int messageLength = (buffArray[3] << 24) | (buffArray[2] << 16) | (buffArray[1] << 8) | buffArray[0];
					if(messageLength > 1024) { // Overflow
						LogManager.log.severe("[" + bridgeName + "] Pipe overflow. Message length: " + messageLength);
						pipe.state = PipeState.ERROR;
						return readAnything;
					}
					if(bytesAvailable.getValue() >= messageLength) {
						if(pipe.pipe.read(ByteBuffer.wrap(buffArray)) == 0) {
							ProtobufMessages.ProtobufMessage message = ProtobufMessages.ProtobufMessage.parser().parseFrom(buffArray, 4, messageLength - 4);
							messageRecieved(message);
							readAnything = true;
						} else {
							pipe.state = PipeState.ERROR;
							LogManager.log.severe("[" + bridgeName + "] Pipe error: " + Native.getLastError());
							return readAnything;
						}
					} else {
						return readAnything; // Wait for more data
					}
				} else {
					return readAnything; // Wait for more data
				}
			}
			pipe.state = PipeState.ERROR;
			LogManager.log.severe("[" + bridgeName + "] Pipe error: " + Native.getLastError());
		}
		return false;
	}

	private void resetPipe() {
		LinuxPipe.safeDisconnect(pipe);
		pipe.state = PipeState.CREATED;
		Main.vrServer.queueTask(this::disconnected);
	}

	private void createPipe() throws IOException {
		try {
			RandomAccessFile rw = new RandomAccessFile(pipeName, "rw");
			FileChannel fc = rw.getChannel();
			pipe = new LinuxPipe(fc, pipeName); // lpSecurityAttributes
			LogManager.log.info("[SteamVRPipeInputBridge] Pipe " + pipe.name + " created");
			//if(pipe.pipe.)
			//	throw new IOException("Can't open " + pipeName + " pipe: " + Native.getLastError());
			LogManager.log.info("[SteamVRPipeInputBridge] Pipes are open");
		} catch(IOException e) {
			LinuxPipe.safeDisconnect(pipe);
			throw e;
		}
	}


	private boolean tryOpeningPipe(LinuxPipe pipe) {
		if(pipe.pipe.isOpen()) {
			pipe.state = PipeState.OPEN;
			LogManager.log.info("[" + bridgeName + "] Pipe " + pipe.name + " is open");
			Main.vrServer.queueTask(this::reconnected);
			return true;
		}
		LogManager.log.info("[" + bridgeName + "] Error connecting to pipe " + pipe.name + ": " + Native.getLastError());
		return false;
	}
}
