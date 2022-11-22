package io.eiren.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.util.List;


public class MacOSX {

	public static void setIcons(List<? extends Image> icons) {
		try {
			Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
			Method m = applicationClass.getDeclaredMethod("getApplication");
			Object application = m.invoke(null);
			m = application.getClass().getDeclaredMethod("setDockIconImage", Image.class);
			m.invoke(application, icons.get(icons.size() - 1));
		} catch (Exception e) {}
	}

	public static void setTitle(String title) {
		try {
			Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
			Method m = applicationClass.getDeclaredMethod("getApplication");
			Object application = m.invoke(null);
			m = application.getClass().getDeclaredMethod("setDockIconImage", String.class);
			m.invoke(application, title);
		} catch (Exception e) {}
	}

	public static boolean hasRetinaDisplay() {
		Object obj = Toolkit.getDefaultToolkit().getDesktopProperty("apple.awt.contentScaleFactor");
		if (obj instanceof Float) {
			Float f = (Float) obj;
			int scale = f.intValue();
			return (scale == 2); // 1 indicates a regular mac display.
		}
		return false;
	}

}
