package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.config.OSCConfig;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.vr.processor.HumanPoseProcessor;
import dev.slimevr.vr.trackers.HMDTracker;
import dev.slimevr.vr.trackers.ShareableTracker;
import dev.slimevr.vr.trackers.TrackerRole;
import dev.slimevr.vr.trackers.TrackerStatus;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;


/**
 * VRChat OSCTracker documentation: https://docs.vrchat.com/docs/osc-trackers
 */
public class VRCOSCHandler {
	private OSCPortIn oscReceiver;
	private OSCPortOut oscSender;
	private OSCMessage oscMessage;
	private final OSCConfig config;
	private final HMDTracker hmd;
	private final WindowsNamedPipeBridge steamvrBridge;
	private final HumanPoseProcessor humanPoseProcessor;
	private final List<? extends ShareableTracker> shareableTrackers;
	private final FastList<Float> oscArgs = new FastList<>(3);
	private final Vector3f vec = new Vector3f();
	private final Quaternion quatBuf = new Quaternion();
	private final float[] floatBuf = new float[3];
	private final boolean[] trackersEnabled;
	private long timeAtLastOSCMessageReceived;
	private static final long HMD_TIMEOUT = 15000;
	private int lastPortIn;
	private int lastPortOut;
	private InetAddress lastAddress;
	private float timeAtLastError;

	public VRCOSCHandler(
		HMDTracker hmd,
		HumanPoseProcessor humanPoseProcessor,
		WindowsNamedPipeBridge steamvrBridge,
		OSCConfig oscConfig,
		List<? extends ShareableTracker> shareableTrackers
	) {
		this.hmd = hmd;
		this.humanPoseProcessor = humanPoseProcessor;
		this.steamvrBridge = steamvrBridge;
		this.config = oscConfig;
		this.shareableTrackers = shareableTrackers;

		trackersEnabled = new boolean[shareableTrackers.size()];

		refreshSettings();
	}

	public void refreshSettings() {
		// Sets which trackers are enabled and force HEAD to false
		for (int i = 0; i < shareableTrackers.size(); i++) {
			if (
				shareableTrackers.get(i).getTrackerRole() != TrackerRole.HEAD
					|| shareableTrackers.get(i).getTrackerRole() != TrackerRole.LEFT_HAND
					|| shareableTrackers.get(i).getTrackerRole() != TrackerRole.RIGHT_HAND
			) {
				trackersEnabled[i] = config
					.getOSCTrackerRole(shareableTrackers.get(i).getTrackerRole(), false);
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
					.severe("[VRCOSCHandler] Error listening to the port " + config.getPortIn());
			}

			// Starts listening for the Upright parameter from VRC
			OSCMessageListener listener = this::handleReceivedMessage;
			MessageSelector selector = new OSCPatternAddressMessageSelector(
				"/avatar/parameters/Upright"
			);
			oscReceiver.getDispatcher().addListener(selector, listener);
			oscReceiver.startListening();

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
					);
			}
		}
	}

	void handleReceivedMessage(OSCMessageEvent event) {
		timeAtLastOSCMessageReceived = System.currentTimeMillis();

		if (steamvrBridge != null && !steamvrBridge.isConnected()) {
			// Sets HMD status to OK
			if (hmd.getStatus() != TrackerStatus.OK) {
				hmd.setStatus(TrackerStatus.OK);
			}

			// Sets the HMD y position to
			// the vrc Upright parameter (0-1) * the user's height
			hmd.position
				.set(
					0f,
					(float) event
						.getMessage()
						.getArguments()
						.get(0) * humanPoseProcessor.getUserHeightFromConfig(),
					0f
				);
			hmd.rotation.set(Quaternion.IDENTITY);

			hmd.dataTick();
		}
	}

	public void update() {
		float currentTime = System.currentTimeMillis();
		// Manage HMD state with timeout
		if (oscReceiver != null) {
			if (
				((steamvrBridge != null
					&& steamvrBridge.isConnected())
					||
					currentTime - timeAtLastOSCMessageReceived > HMD_TIMEOUT
					||
					!oscReceiver.isListening())
					&& hmd.getStatus() == TrackerStatus.OK
			) {
				hmd.setStatus(TrackerStatus.DISCONNECTED);
			}
		}

		// Send OSC data
		if (oscSender != null && oscSender.isConnected()) {
			for (int i = 0; i < shareableTrackers.size(); i++) {
				if (trackersEnabled[i]) {
					// Send regular trackers' positions
					shareableTrackers.get(i).getPosition(vec);
					oscArgs.clear();
					oscArgs.add(vec.x);
					oscArgs.add(vec.y);
					oscArgs.add(-vec.z);
					oscMessage = new OSCMessage(
						"/tracking/trackers/" + (i + 1) + "/position",
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
					shareableTrackers.get(i).getRotation(quatBuf);
					// Convert between OpenGl/OpenVR to Unity
					quatBuf.set(-quatBuf.getX(), -quatBuf.getY(), -quatBuf.getZ(), quatBuf.getW());
					quatBuf.toAngles(floatBuf);
					oscArgs.clear();
					oscArgs.add(floatBuf[0] * FastMath.RAD_TO_DEG);
					oscArgs.add(floatBuf[1] * FastMath.RAD_TO_DEG);
					oscArgs.add(floatBuf[2] * FastMath.RAD_TO_DEG);
					oscMessage = new OSCMessage(
						"/tracking/trackers/" + (i + 1) + "/rotation",
						oscArgs
					);
					try {
						oscSender.send(oscMessage);
					} catch (IOException | OSCSerializeException e) {
						// Don't do anything.
						// Previous code already logs the exception.
					}
				}

				if (shareableTrackers.get(i).getTrackerRole() == TrackerRole.HEAD) {
					// Send HMD position
					shareableTrackers.get(i).getPosition(vec);
					oscArgs.clear();
					oscArgs.add(vec.x);
					oscArgs.add(vec.y);
					oscArgs.add(-vec.z);
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
			for (ShareableTracker shareableTracker : shareableTrackers) {
				if (shareableTracker.getTrackerRole() == TrackerRole.HEAD) {
					Quaternion hmdYawQuatBuf = new Quaternion();
					shareableTracker.getRotation(hmdYawQuatBuf);
					oscArgs.clear();
					oscArgs.add(0f);
					oscArgs.add(-hmdYawQuatBuf.getYaw() * FastMath.RAD_TO_DEG);
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
}
