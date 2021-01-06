package io.eiren.vr.bridge;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

import essentia.util.collections.FastList;
import io.eiren.util.StringUtils;
import io.eiren.util.logging.LogManager;

public class NamedPipeVRBridge extends Thread implements VRBridge {
	
	public static final String HMDPipeName = "\\\\.\\pipe\\HMDPipe";
	public static final String TrackersPipeName = "\\\\.\\pipe\\TrackPipe";
	
	public static final int TRACKERS = 3;
	private static final byte[] buffer = new byte[1024];
	
	private Pipe hmdPipe;
	private List<Pipe> trackerPipes = new FastList<>();
	protected VRBridgeState bridgeState = VRBridgeState.NOT_STARTED;
	
	public NamedPipeVRBridge() {
		super("Named Pipe VR Bridge");
	}
	
	@Override
	public VRBridgeState getBridgeState() {
		return bridgeState;
	}
	
	@Override
	public void run() {
		try {
			createPipes();
			bridgeState = VRBridgeState.STARTED;
			while(true) {
				if(bridgeState == VRBridgeState.STARTED) {
					if(hmdPipe != null && hmdPipe.state == PipeState.CREATED) {
						if(tryOpeningPipe(hmdPipe))
							initHMDPipe(hmdPipe);
					}
					for(int i = 0; i < trackerPipes.size(); ++i) {
						Pipe trackerPipe = trackerPipes.get(i);
						if(trackerPipe.state == PipeState.CREATED)
							if(tryOpeningPipe(trackerPipe))
								initTrackerPipe(trackerPipe, i);
					}
					if(areAllPipesOpen()) {
						bridgeState = VRBridgeState.CONNECTED;
						LogManager.log.info("[VRBridge] All pipes are connected!");
					} else {
						Thread.sleep(200L);
					}
				} else {
					updateHMD();
					for(int i = 0; i < trackerPipes.size(); ++i) {
						updateTracker(trackerPipes.get(i), i);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			bridgeState = VRBridgeState.ERROR;
		}
	}
	
	public void updateHMD() {
		IntByReference bytesAvailable = new IntByReference(0);
		if(Kernel32.INSTANCE.PeekNamedPipe(hmdPipe.pipeHandle, null, 0, null, bytesAvailable, null)) {
			if(bytesAvailable.getValue() > 0) {
				if(Kernel32.INSTANCE.ReadFile(hmdPipe.pipeHandle, buffer, buffer.length, bytesAvailable, null)) {
					String str = new String(buffer, 0, bytesAvailable.getValue() - 1, Charset.forName("ASCII"));
					String[] split = str.split("\n")[0].split(" ");
					try {
						double x = Double.parseDouble(split[0]);
						double y = Double.parseDouble(split[1]);
						double z = Double.parseDouble(split[2]);
						double qw = Double.parseDouble(split[3]);
						double qx = Double.parseDouble(split[4]);
						double qy = Double.parseDouble(split[5]);
						double qz = Double.parseDouble(split[6]);
						LogManager.log.info("[VRBridge] New HMD position:"
								+ " " + StringUtils.prettyNumber((float) x, 2)
								+ " " + StringUtils.prettyNumber((float) y, 2)
								+ " " + StringUtils.prettyNumber((float) z, 2)
								+ " " + StringUtils.prettyNumber((float) qw, 2)
								+ " " + StringUtils.prettyNumber((float) qx, 2)
								+ " " + StringUtils.prettyNumber((float) qy, 2)
								+ " " + StringUtils.prettyNumber((float) qz, 2));
					} catch(NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void updateTracker(Pipe pipe, int trackerId) {
		
	}
	
	private void initHMDPipe(Pipe pipe) {
		
	}
	
	private void initTrackerPipe(Pipe pipe, int trackerId) {
		String trackerHello = TRACKERS + " 0";
		byte[] buff = new byte[trackerHello.length() + 1];// = length of string + terminating '\0' !!!
		System.arraycopy(trackerHello.getBytes(Charset.forName("ASCII")), 0, buff, 0, trackerHello.length());
		IntByReference lpNumberOfBytesWritten = new IntByReference(0);
		Kernel32.INSTANCE.WriteFile(pipe.pipeHandle,
			buff,
			buff.length,
			lpNumberOfBytesWritten,
			null);
	}
	
	private boolean tryOpeningPipe(Pipe pipe) {
		if(Kernel32.INSTANCE.ConnectNamedPipe(pipe.pipeHandle, null)) {
			pipe.state = NamedPipeVRBridge.PipeState.OPEN;
			LogManager.log.info("[VRBridge] Pipe " + pipe.name + " is open");
			return true;
		}
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
			hmdPipe = new Pipe(Kernel32.INSTANCE.CreateNamedPipe(HMDPipeName, WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
					WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
					1, // nMaxInstances,
					1024 * 16, // nOutBufferSize,
					1024 * 16, // nInBufferSize,
					0, // nDefaultTimeOut,
					null), HMDPipeName); // lpSecurityAttributes
			LogManager.log.info("[VRBridge] Pipe " + hmdPipe.name + " created");
			if(WinBase.INVALID_HANDLE_VALUE.equals(hmdPipe.pipeHandle))
				throw new IOException("Can't open " + HMDPipeName + " pipe: " + Kernel32.INSTANCE.GetLastError());
			for(int i = 0; i < TRACKERS; ++i) {
				String pipeName = TrackersPipeName + i;
				HANDLE pipeHandle = Kernel32.INSTANCE.CreateNamedPipe(pipeName, WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
					WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
					1, // nMaxInstances,
					1024 * 16, // nOutBufferSize,
					1024 * 16, // nInBufferSize,
					0, // nDefaultTimeOut,
					null); // lpSecurityAttributes
				if(WinBase.INVALID_HANDLE_VALUE.equals(pipeHandle))
					throw new IOException("Can't open " + pipeName + " pipe: " + Kernel32.INSTANCE.GetLastError());
				LogManager.log.info("[VRBridge] Pipe " + pipeName + " created");
				trackerPipes.add(new Pipe(pipeHandle, pipeName));
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
	
	public static void safeDisconnect(Pipe pipe) {
		try {
			if(pipe != null && pipe.pipeHandle != null)
				Kernel32.INSTANCE.DisconnectNamedPipe(pipe.pipeHandle);
		} catch(Exception e) {
		}
	}
	
	private static class Pipe {
		final String name;
		final HANDLE pipeHandle;
		PipeState state = PipeState.CREATED;
		
		public Pipe(HANDLE pipeHandle, String name) {
			this.pipeHandle = pipeHandle;
			this.name = name;
		}
	}
	
	private static enum PipeState {
		CREATED,
		OPEN,
		ERROR;
	}
}
