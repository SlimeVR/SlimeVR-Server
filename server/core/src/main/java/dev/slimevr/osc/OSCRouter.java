package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import dev.slimevr.config.OSCRouterConfig;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class OSCRouter {
	private OSCPortIn oscReceiver;
	private OSCPortOut oscSender;
	private final OSCRouterConfig config;
	private final FastList<OSCHandler> oscHandlers;
	private int lastPortIn;
	private int lastPortOut;
	private InetAddress lastAddress;
	private long timeAtLastError;

	public float scaleTrackingVolume = 1f;

	public OSCRouter(
		OSCRouterConfig oscRouterConfig,
		FastList<OSCHandler> oscHandlers
	) {
		this.config = oscRouterConfig;
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
					oscSender = new OSCPortOut(new InetSocketAddress(address, portOut));
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
				oscReceiver.getDispatcher().setAlwaysDispatchingImmediately(true);
				if (!oscReceiver.isListening())
					oscReceiver.startListening();
			}
		}
	}

	void handleReceivedMessage(OSCMessageEvent event) {
		if (oscSender != null && oscSender.isConnected()) {
			var address = event.getMessage().getAddress();
			var args = event.getMessage().getArguments();
			OSCMessage oscMessageA = null, oscMessageB = null;
			if (
				config.getRescaleTracking()
					&& scaleTrackingVolume != 1.0f
					&& address.endsWith("/Pos")
			) {
				// Original message with coordinates in device scale
				oscMessageA = new OSCMessage(address + "/Local", args);
				// Modified message with coordinates in avatar scale
				ArrayList<Object> argsMod = new ArrayList<Object>(args);
				argsMod.set(1, (Float) argsMod.get(1) * scaleTrackingVolume);
				argsMod.set(2, (Float) argsMod.get(2) * scaleTrackingVolume);
				argsMod.set(3, (Float) argsMod.get(3) * scaleTrackingVolume);
				oscMessageB = new OSCMessage(address, argsMod);
			} else {
				oscMessageA = new OSCMessage(address, args);
			}

			try {
				oscSender.send(oscMessageA);
				if (oscMessageB != null)
					oscSender.send(oscMessageB);
			} catch (IOException | OSCSerializeException e) {
				// Avoid spamming AsynchronousCloseException too many
				// times per second
				if (System.currentTimeMillis() - timeAtLastError > 100) {
					timeAtLastError = System.currentTimeMillis();
					LogManager
						.warning(
							"[OSCRouter] Error sending OSC packet: "
								+ e
						);
				}
			}
		}
	}
}

