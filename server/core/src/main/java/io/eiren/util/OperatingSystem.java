package io.eiren.util;

import java.io.File;


public enum OperatingSystem {

	//@formatter:off
	LINUX("linux", new String[]{"linux", "unix"}),
	WINDOWS("windows", new String[]{"win"}),
	OSX("osx", new String[]{"mac"}),
	UNKNOWN("unknown", new String[0]);
	//@fomatter: on

	private final String[] aliases;
	public final String name;
	private static OperatingSystem currentPlatform;

	OperatingSystem(String name, String[] aliases) {
		this.aliases = aliases;
		this.name = name;
	}

	public static String getJavaExecutable(boolean forceConsole) {
		String separator = System.getProperty("file.separator");
		String path = System.getProperty("java.home") + separator + "bin" + separator;
		if(getCurrentPlatform() == WINDOWS) {
			if(!forceConsole && new File(path + "javaw.exe").isFile())
				return path + "javaw.exe";
			return path + "java.exe";
		}
		return path + "java";
	}

	public static OperatingSystem getCurrentPlatform() {
		if(currentPlatform != null)
			return currentPlatform;
		String osName = System.getProperty("os.name").toLowerCase();
		for(OperatingSystem os : values()) {
			for(String alias : os.aliases) {
				if(osName.contains(alias))
					return currentPlatform = os;
			}
		}
		return UNKNOWN;
	}

	public static String getTempDirectory() {
		if(currentPlatform == LINUX) {
			String tmp = System.getenv("XDG_RUNTIME_DIR");
			if(tmp != null) return tmp;
		}
		return System.getProperty("java.io.tmpdir");
	}
}
