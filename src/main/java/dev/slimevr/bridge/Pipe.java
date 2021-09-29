package dev.slimevr.bridge;

import com.sun.jna.platform.win32.WinNT.HANDLE;

class Pipe {
	final String name;
	final HANDLE pipeHandle;
	PipeState state = PipeState.CREATED;
	
	public Pipe(HANDLE pipeHandle, String name) {
		this.pipeHandle = pipeHandle;
		this.name = name;
	}
}