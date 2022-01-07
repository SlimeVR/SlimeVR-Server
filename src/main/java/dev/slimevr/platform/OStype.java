package dev.slimevr.platform;

import org.apache.commons.lang3.SystemUtils;

public class OStype {
	public static boolean isWindows = SystemUtils.IS_OS_WINDOWS;
	public static boolean isUnix = SystemUtils.IS_OS_UNIX;
}
