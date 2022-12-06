package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import dev.slimevr.config.OSCRouterConfig;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.net.InetAddress;


public class OSCRouter {
	private OSCPortIn oscReceiver;
	private OSCPortOut oscSender;
	private final OSCRouterConfig config;
	private int lastPortIn;
	private int lastPortOut;
	private InetAddress lastAddress;
	private float timeAtLastError;

	public OSCRouter(
		OSCRouterConfig oscConfig
	) {
		this.config = oscConfig;

		refreshSettings();
	}

	public void refreshSettings() {
		// Stops listening and closes OSC port
		boolean wasListening = oscReceiver != null && oscReceiver.isListening();
		if (wasListening) {
			oscReceiver.stopListening();
		}
		boolean wasConnected = oscSender != null && oscSender.isConnected();
		if (wasConnected) {
			try {
				oscSender.close();
			} catch (IOException e) {
				LogManager.severe("[OSCRouter] Error closing the OSC sender: " + e);
			}
		}

		if (config.getEnabled()) {
			// Instantiates the OSC receiver
			try {
				int port = config.getPortIn();
				oscReceiver = new OSCPortIn(
					port
				);
				if (lastPortIn != port || !wasListening) {
					LogManager.info("[OSCRouter] Listening to port " + port);
				}
				lastPortIn = port;
			} catch (IOException e) {
				LogManager
					.severe(
						"[OSCRouter] Error listening to the port " + config.getPortIn() + ": " + e
					);
			}

			// Instantiate the OSC sender
			try {
				InetAddress address = InetAddress.getByName(config.getAddress());
				int port = config.getPortOut();
				oscSender = new OSCPortOut(
					address,
					port
				);
				if ((lastPortOut != port && lastAddress != address) || !wasConnected) {
					LogManager
						.info(
							"[OSCRouter] Sending to port "
								+ port
								+ " at address "
								+ address.toString()
						);
				}
				lastPortOut = port;
				lastAddress = address;

				oscSender.connect();
			} catch (IOException e) {
				LogManager
					.severe(
						"[OSCRouter] Error connecting to port "
							+ config.getPortOut()
							+ " at the address "
							+ config.getAddress()
							+ ": "
							+ e
					);
			}

			// Starts listening to messages
			if (oscReceiver != null) {
				OSCMessageListener listener = this::handleReceivedMessage;
				// Listens for any message ("//" is a wildcard)
				MessageSelector selector = new OSCPatternAddressMessageSelector("//");
				oscReceiver.getDispatcher().addListener(selector, listener);
				oscReceiver.startListening();
			}
		}
	}

	void handleReceivedMessage(OSCMessageEvent event) {
		if (oscSender != null && oscSender.isConnected()) {
			OSCMessage oscMessage = new OSCMessage(
				event.getMessage().getAddress(),
				event.getMessage().getArguments()
			);
			try {
				oscSender.send(oscMessage);
			} catch (IOException | OSCSerializeException e) {
				// Avoid spamming AsynchronousCloseException too many
				// times per second
				if (System.currentTimeMillis() - timeAtLastError > 100) {
					timeAtLastError = System.currentTimeMillis();
					LogManager
						.warning(
							"[OSCRouter] Error sending OSC message: "
								+ e
						);
				}
			}
		}
	}
}

