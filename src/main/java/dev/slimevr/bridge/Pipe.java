package dev.slimevr.bridge;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class Pipe {
	
	public final String name;
	public final HANDLE pipeHandle;
	public PipeState state = PipeState.CREATED;
	
	public Pipe(HANDLE pipeHandle, String name) {
		this.pipeHandle = pipeHandle;
		this.name = name;
	}
	
	public static void safeDisconnect(Pipe pipe) {
		try {
			if(pipe != null && pipe.pipeHandle != null)
				Kernel32.INSTANCE.DisconnectNamedPipe(pipe.pipeHandle);
		} catch(Exception e) {
		}
	}
	
	enum PipeState {
		CREATED,
		OPEN,
		ERROR;
	}
}