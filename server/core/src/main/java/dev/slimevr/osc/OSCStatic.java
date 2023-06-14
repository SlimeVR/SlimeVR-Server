package dev.slimevr.osc;

import com.illposed.osc.OSCSerializerAndParserBuilder;
import com.illposed.osc.argument.ArgumentHandler;
import com.illposed.osc.argument.handler.Activator;

import java.util.List;

public class OSCStatic {
	public static OSCSerializerAndParserBuilder serializer;

	static {
		// Really hacky workaround for getting the OSC library to work on Android
		// (Removes reference to java.awt.Color)
		// https://github.com/hoijui/JavaOSC/issues/60#issuecomment-960713779
		OSCSerializerAndParserBuilder serializer = new OSCSerializerAndParserBuilder();
		serializer.setUsingDefaultHandlers(false);
		List<ArgumentHandler> defaultParserTypes = Activator.createSerializerTypes();
		defaultParserTypes.remove(16);
		char typeChar = 'a';
		for (ArgumentHandler argumentHandler : defaultParserTypes) {
			serializer.registerArgumentHandler(argumentHandler, typeChar);
			typeChar++;
		}

		OSCStatic.serializer = serializer;
	}
}
