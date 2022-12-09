package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import dev.slimevr.config.OSCConfig;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class OSCRouter {
	private OSCPortIn oscReceiver;
	private OSCPortOut oscSender;
	private final OSCConfig config;
	private final FastList<OSCHandler> oscHandlers;
	private int lastPortIn;
	private int lastPortOut;
	private InetAddress lastAddress;
	private float timeAtLastError;

	public OSCRouter(
		OSCConfig oscConfig,
		FastList<OSCHandler> oscHandlers
	) {
		this.config = oscConfig;
		this.oscHandlers = oscHandlers;

		refreshSettings(false);
	}

	public void refreshSettings(boolean refreshHandlersSettings) {
		if (refreshHandlersSettings) {
			for (OSCHandler oscHandler : oscHandlers) {
				oscHandler.refreshSettings(false);
			}
		}

		// Stops listening and closes OSC port
		boolean wasListening = oscReceiver != null && oscReceiver.isListening();
		if (wasListening) {
			oscReceiver.stopListening();
		}
		oscReceiver = null;
		boolean wasConnected = oscSender != null && oscSender.isConnected();
		if (wasConnected) {
			try {
				oscSender.close();
			} catch (IOException e) {
				LogManager.severe("[OSCRouter] Error closing the OSC sender: " + e);
			}
		}
		oscSender = null;

		if (config.getEnabled()) {
			// Instantiates the OSC receiver
			int portIn = config.getPortIn();
			// Check if another OSC receiver with same port exists
			for (OSCHandler oscHandler : oscHandlers) {
				if (oscHandler.getPortIn() == portIn) {
					if (oscHandler.getOscReceiver().isListening()) {
						oscReceiver = oscHandler.getOscReceiver();
						LogManager.info("[OSCRouter] Listening to port " + portIn);
					}
				}
			}
			// Else, create our own OSC receiver
			if (oscReceiver == null) {
				try {
					oscReceiver = new OSCPortIn(portIn);
					if (lastPortIn != portIn || !wasListening) {
						LogManager.info("[OSCRouter] Listening to port " + portIn);
					}
					lastPortIn = portIn;
				} catch (IOException e) {
					LogManager
						.severe(
							"[OSCRouter] Error listening to the port "
								+ config.getPortIn()
								+ ": "
								+ e
						);
				}
			}

			// Instantiate the OSC sender
			int portOut = config.getPortOut();
			InetAddress address;
			try {
				address = InetAddress.getByName(config.getAddress());
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
			// Check if another OSC sender with same port and address exists
			for (OSCHandler oscHandler : oscHandlers) {
				if (oscHandler.getPortOut() == portOut && oscHandler.getAddress() == address) {
					if (oscHandler.getOscSender().isConnected()) {
						oscSender = oscHandler.getOscSender();
						LogManager
							.info(
								"[OSCRouter] Sending to port "
									+ portOut
									+ " at address "
									+ address.toString()
							);
					}
				}
			}
			// Else, create our own OSC sender
			if (oscSender == null) {
				try {
					oscSender = new OSCPortOut(
						address,
						portOut
					);
					if ((lastPortOut != portOut && lastAddress != address) || !wasConnected) {
						LogManager
							.info(
								"[OSCRouter] Sending to port "
									+ portOut
									+ " at address "
									+ address.toString()
							);
					}
					lastPortOut = portOut;
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
			}

			// Starts listening to messages
			if (oscReceiver != null) {
				OSCMessageListener listener = this::handleReceivedMessage;
				// Listens for any message ("//" is a wildcard)
				MessageSelector selector = new OSCPatternAddressMessageSelector("//");
				oscReceiver.getDispatcher().addListener(selector, listener);
				if (!oscReceiver.isListening())
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

