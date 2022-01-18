package dev.slimevr.bridge;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import dev.slimevr.platform.OStype;
import dev.slimevr.platform.linux.LinuxPipe;
import dev.slimevr.platform.windows.WindowsPipe;
import io.eiren.util.logging.LogManager;

import java.io.IOException;

public class Pipe {

	public final String name;
	public final WinNT.HANDLE pipeHandle = null;
	public PipeState state;

	public Pipe(String name) {
		this.name = name;
	}

	public WindowsPipe createPipeWindows(String pipeName, String bridgeName) throws IOException {
		WindowsPipe pipe = null;

		if (OStype.isWindows) {

			try {
				pipe = new WindowsPipe(Kernel32.INSTANCE.CreateNamedPipe(pipeName, WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
						WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
						1, // nMaxInstances,
						1024 * 16, // nOutBufferSize,
						1024 * 16, // nInBufferSize,
						0, // nDefaultTimeOut,
						null), pipeName); // lpSecurityAttributes
				LogManager.log.info("[" + bridgeName + "] Pipe " + pipe.name + " created");
				if (WinBase.INVALID_HANDLE_VALUE.equals(pipe.pipeHandle))
					throw new IOException("Can't open " + pipeName + " pipe: " + Kernel32.INSTANCE.GetLastError());
				LogManager.log.info("[" + bridgeName + "] Pipes are created");
			} catch (IOException e) {
				WindowsPipe.safeDisconnect(pipe);
				throw e;
			}
		} else {
			LogManager.log.severe("Unsuported OS.");
		}
		return pipe;
	}

	public LinuxPipe createPipeUnix(String pipeName, String bridgeName) throws IOException {
		return null;
	}
}
