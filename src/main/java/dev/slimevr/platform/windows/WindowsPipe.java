package dev.slimevr.platform.windows;


import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import dev.slimevr.bridge.Pipe;
import dev.slimevr.bridge.PipeState;
import io.eiren.util.logging.LogManager;

import java.io.IOException;


public class WindowsPipe {

	public final String name;

	public final HANDLE pipeHandle;
	public PipeState state = PipeState.CREATED;

	public WindowsPipe(HANDLE pipeHandle, String name) {
		this.pipeHandle = pipeHandle;
		this.name = name;
	}

	public static void safeDisconnect(WindowsPipe pipe) {
		try {
			if(pipe != null && pipe.pipeHandle != null)
				Kernel32.INSTANCE.DisconnectNamedPipe(pipe.pipeHandle);
		} catch(Exception e) {
		}
	}
}
