package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import com.jme3.math.FastMath;
import dev.slimevr.VRServer;
import dev.slimevr.config.VRCOSCConfig;
import dev.slimevr.platform.SteamVRBridge;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerPosition;
import dev.slimevr.tracking.trackers.TrackerRole;
import dev.slimevr.tracking.trackers.TrackerStatus;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.github.axisangles.ktmath.EulerAngles;
import io.github.axisangles.ktmath.EulerOrder;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;


/**
 * VRChat OSCTracker documentation: https://docs.vrchat.com/docs/osc-trackers
 */
public class VRCOSCHandler implements OSCHandler {
	private OSCPortIn oscReceiver;
	private OSCPortOut oscSender;
	private OSCMessage oscMessage;
	private final VRCOSCConfig config;
	private final VRServer server;
	private final Tracker vrcHmd;
	private final SteamVRBridge steamvrBridge;
	private final HumanPoseManager humanPoseManager;
	private final List<Tracker> computedTrackers;
	private final FastList<Float> oscArgs = new FastList<>(3);
	private final boolean[] trackersEnabled;
	private int lastPortIn;
	private int lastPortOut;
	private InetAddress lastAddress;
	private long timeAtLastError;

	public VRCOSCHandler(
		VRServer server,
		HumanPoseManager humanPoseManager,
		SteamVRBridge steamvrBridge,
		VRCOSCConfig oscConfig,
		List<Tracker> computedTrackers
	) {
		this.server = server;
		this.humanPoseManager = humanPoseManager;
		this.steamvrBridge = steamvrBridge;
		this.config = oscConfig;
		this.computedTrackers = computedTrackers;

		vrcHmd = new Tracker(
			null,
			VRServer.getNextLocalTrackerId(),
			"VRC HMD",
			"VRC HMD",
			TrackerPosition.HEAD,
			true,
			false,
			false,
			false,
			true,
			true
		);

		trackersEnabled = new boolean[computedTrackers.size()];

		refreshSettings(false);
	}

	@Override
	public void refreshSettings(boolean refreshRouterSettings) {
		// Sets which trackers are enabled and force HEAD to false
		for (int i = 0; i < computedTrackers.size(); i++) {
			if (
				computedTrackers.get(i).getTrackerPosition() != TrackerPosition.HEAD
					|| computedTrackers.get(i).getTrackerPosition() != TrackerPosition.LEFT_HAND
					|| computedTrackers.get(i).getTrackerPosition() != TrackerPosition.RIGHT_HAND
			) {
				trackersEnabled[i] = config
					.getOSCTrackerRole(
						computedTrackers.get(i).getTrackerPosition().getTrackerRole(),
						false
					);
			} else {
				trackersEnabled[i] = false;
			}
		}

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
				LogManager.severe("[VRCOSCHandler] Error closing the OSC sender: " + e);
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
					LogManager.info("[VRCOSCHandler] Listening to port " + port);
				}
				lastPortIn = port;
			} catch (IOException e) {
				LogManager
					.severe(
						"[VRCOSCHandler] Error listening to the port "
							+ config.getPortIn()
							+ ": "
							+ e
					);
			}

			// Starts listening for the Upright parameter from VRC
			if (oscReceiver != null) {
				OSCMessageListener listener = this::handleReceivedMessage;
				MessageSelector selector = new OSCPatternAddressMessageSelector(
					"/avatar/parameters/Upright"
				);
				oscReceiver.getDispatcher().addListener(selector, listener);
				oscReceiver.startListening();
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
							"[VRCOSCHandler] Sending to port "
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
						"[VRCOSCHandler] Error connecting to port "
							+ config.getPortOut()
							+ " at the address "
							+ config.getAddress()
							+ ": "
							+ e
					);
			}
		}

		if (refreshRouterSettings && server.getOSCRouter() != null)
			server.getOSCRouter().refreshSettings(false);
	}

	void handleReceivedMessage(OSCMessageEvent event) {
		if (steamvrBridge != null && !steamvrBridge.isConnected()) {
			// Sets HMD status to OK
			vrcHmd.setStatus(TrackerStatus.OK);

			// Sets the HMD y position to
			// the vrc Upright parameter (0-1) * the user's height
			vrcHmd
				.setPosition(
					new Vector3(
						0f,
						(float) event
							.getMessage()
							.getArguments()
							.get(0) * humanPoseManager.getUserHeightFromConfig(),
						0f
					)
				);

			vrcHmd.dataTick();
		}
	}

	@Override
	public void update() {
		float currentTime = System.currentTimeMillis();

		// Send OSC data
		if (oscSender != null && oscSender.isConnected()) {
			int id = 0;
			for (int i = 0; i < computedTrackers.size(); i++) {
				if (trackersEnabled[i]) {
					id++;
					// Send regular trackers' positions
					Vector3 vec = computedTrackers.get(i).getPosition();
					oscArgs.clear();
					oscArgs.add(vec.getX());
					oscArgs.add(vec.getY());
					oscArgs.add(-vec.getZ());
					oscMessage = new OSCMessage(
						"/tracking/trackers/" + id + "/position",
						oscArgs
					);
					try {
						oscSender.send(oscMessage);
					} catch (IOException | OSCSerializeException e) {
						// Avoid spamming AsynchronousCloseException too many
						// times per second
						if (currentTime - timeAtLastError > 100) {
							timeAtLastError = System.currentTimeMillis();
							LogManager
								.warning(
									"[VRCOSCHandler] Error sending OSC message to VRChat: "
										+ e
								);
						}
					}

					// Send regular trackers' rotations
					Quaternion rot = computedTrackers.get(i).getRotation();
					// We flip the X and Y components of the quaternion because
					// we flip the z direction when communicating from
					// our right-handed API to VRChat's left-handed API.
					// X quaternion represents a rotation from y to z
					// Y quaternion represents a rotation from z to x
					// When we negate the z direction, X and Y quaternion
					// components must be negated.
					EulerAngles unityAngles = new Quaternion(
						rot.getW(),
						-rot.getX(),
						-rot.getY(),
						rot.getZ()
					).toEulerAngles(EulerOrder.YXZ);
					oscArgs.clear();
					oscArgs.add(unityAngles.getX() * FastMath.RAD_TO_DEG);
					oscArgs.add(unityAngles.getY() * FastMath.RAD_TO_DEG);
					oscArgs.add(unityAngles.getZ() * FastMath.RAD_TO_DEG);

					oscMessage = new OSCMessage(
						"/tracking/trackers/" + id + "/rotation",
						oscArgs
					);
					try {
						oscSender.send(oscMessage);
					} catch (IOException | OSCSerializeException e) {
						// Don't do anything.
						// Previous code already logs the exception.
					}
				}

				if (computedTrackers.get(i).getTrackerPosition() == TrackerPosition.HEAD) {
					// Send HMD position
					var pos = computedTrackers.get(i).getPosition();
					oscArgs.clear();
					oscArgs.add(pos.getX());
					oscArgs.add(pos.getY());
					oscArgs.add(-pos.getZ());
					oscMessage = new OSCMessage(
						"/tracking/trackers/head/position",
						oscArgs
					);
					try {
						oscSender.send(oscMessage);
					} catch (IOException | OSCSerializeException e) {
						// Don't do anything.
						// Previous code already logs the exception.
					}
				}
			}
		}
	}

	/**
	 * Sends the expected HMD rotation upon reset to align the trackers in VRC
	 */
	public void yawAlign() {
		if (oscSender != null && oscSender.isConnected()) {
			for (Tracker shareableTracker : computedTrackers) {
				if (shareableTracker.getTrackerPosition().getTrackerRole() == TrackerRole.HEAD) {
					var hmdAngles = shareableTracker.getRotation().toEulerAngles(EulerOrder.XYZ);
					oscArgs.clear();
					oscArgs.add(0f);
					oscArgs.add(-hmdAngles.getY() * FastMath.RAD_TO_DEG);
					oscArgs.add(0f);
					oscMessage = new OSCMessage(
						"/tracking/trackers/head/rotation",
						oscArgs
					);
					try {
						oscSender.send(oscMessage);
					} catch (IOException | OSCSerializeException e) {
						LogManager
							.warning("[VRCOSCHandler] Error sending OSC message to VRChat: " + e);
					}
				}
			}
		}
	}

	@Override
	public OSCPortOut getOscSender() {
		return oscSender;
	}

	@Override
	public int getPortOut() {
		return lastPortOut;
	}

	@Override
	public InetAddress getAddress() {
		return lastAddress;
	}

	@Override
	public OSCPortIn getOscReceiver() {
		return oscReceiver;
	}

	@Override
	public int getPortIn() {
		return lastPortIn;
	}
}
