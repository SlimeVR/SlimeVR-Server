package dev.slimevr.bridge;

import dev.slimevr.platform.OStype;
import  dev.slimevr.platform.windows.WindowsPipe;

public class Pipe {



	public Pipe(String name) {
		if(OStype.isWindows) {
			WindowsPipe pipe = new WindowsPipe(, name);
		}
	}
}
