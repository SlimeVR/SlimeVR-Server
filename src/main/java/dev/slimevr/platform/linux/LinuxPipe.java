package dev.slimevr.platform.linux;

import dev.slimevr.bridge.PipeState;

import java.io.RandomAccessFile;

public class LinuxPipe {

	public final String name;

	public RandomAccessFile pipe;
	public PipeState state = PipeState.CREATED;

	public LinuxPipe(RandomAccessFile pipe, String name) {
		this.pipe = pipe;
		this.name = name;
	}
	public static void safeDisconnect(RandomAccessFile pipe) {
		try {
			if(pipe != null) {
				pipe.close();
			}
		} catch (Exception e) {
		}
	}
}
