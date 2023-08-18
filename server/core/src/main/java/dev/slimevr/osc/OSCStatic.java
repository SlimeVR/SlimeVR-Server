package dev.slimevr.osc;

import com.illposed.osc.OSCSerializerAndParserBuilder;


public class OSCStatic {
	public static OSCSerializerAndParserBuilder serializer;

	static {
		// Really hacky workaround for getting the OSC library to work on
		// Android (Removes reference to java.awt.Color)
		OSCSerializerAndParserBuilder serializer = new OSCSerializerAndParserBuilder();
		serializer.setUsingDefaultHandlers(true);
		serializer.unregisterArgumentHandler('r');

		OSCStatic.serializer = serializer;
	}
}
