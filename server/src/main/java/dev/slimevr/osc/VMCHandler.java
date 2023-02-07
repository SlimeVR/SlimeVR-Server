package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.config.VMCConfig;
import dev.slimevr.tracking.processor.BoneInfo;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.trackers.ShareableTracker;
import dev.slimevr.tracking.trackers.TrackerRole;
import dev.slimevr.tracking.trackers.UnityBone;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;


/**
 * VMC documentation: https://protocol.vmc.info/english
 */
public class VMCHandler implements OSCHandler {
	private OSCPortIn oscReceiver;
	private OSCPortOut oscSender;
	private final VMCConfig config;
	private final VRServer server;
	private final HumanPoseManager humanPoseManager;
	private final List<? extends ShareableTracker> shareableTrackers;
	private final FastList<Object> oscArgs = new FastList<>();
	private final Vector3f vecBuf = new Vector3f();
	private final Quaternion quatBuf = new Quaternion();
	private final long startTime;
	private final Vector3f hmdOffset = new Vector3f();
	private float timeAtLastError;
	private boolean anchorHip;
	private int lastPortIn;
	private int lastPortOut;
	private InetAddress lastAddress;

	public VMCHandler(
		VRServer server,
		HumanPoseManager humanPoseManager,
		VMCConfig oscConfig,
		List<? extends ShareableTracker> shareableTrackers
	) {
		this.server = server;
		this.humanPoseManager = humanPoseManager;
		this.config = oscConfig;
		this.shareableTrackers = shareableTrackers;

		startTime = System.currentTimeMillis();

		refreshSettings(false);
	}

	@Override
	public void refreshSettings(boolean refreshRouterSettings) {
		anchorHip = config.getAnchorHip();

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
				LogManager.severe("[VMCHandler] Error closing the OSC sender: " + e);
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
					LogManager.info("[VMCHandler] Listening to port " + port);
				}
				lastPortIn = port;
			} catch (IOException e) {
				LogManager
					.severe(
						"[VMCHandler] Error listening to the port "
							+ config.getPortIn()
							+ ": "
							+ e
					);
			}

			// Starts listening for VMC messages
			if (oscReceiver != null) {
				OSCMessageListener listener = this::handleReceivedMessage;
				MessageSelector rcvSelector = new OSCPatternAddressMessageSelector(
					"/VMC/Ext/Rcv"
				);
				MessageSelector boneSelector = new OSCPatternAddressMessageSelector(
					"/VMC/Ext/Bone/Pos"
				);
				MessageSelector hmdSelector = new OSCPatternAddressMessageSelector(
					"/VMC/Ext/Hmd/Pos"
				);
				MessageSelector controllerSelector = new OSCPatternAddressMessageSelector(
					"/VMC/Ext/Con/Pos"
				);
				MessageSelector trackerSelector = new OSCPatternAddressMessageSelector(
					"/VMC/Ext/Tra/Pos"
				);
				oscReceiver.getDispatcher().addListener(rcvSelector, listener);
				oscReceiver.getDispatcher().addListener(boneSelector, listener);
				oscReceiver.getDispatcher().addListener(hmdSelector, listener);
				oscReceiver.getDispatcher().addListener(controllerSelector, listener);
				oscReceiver.getDispatcher().addListener(trackerSelector, listener);
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
							"[VMCHandler] Sending to port "
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
						"[VMCHandler] Error connecting to port "
							+ config.getPortOut()
							+ " at the address "
							+ config.getAddress()
							+ ": "
							+ e
					);
			}

			// Load VRM data
			VMCReader.readVMC(config.getVRMAddress());
		}

		if (refreshRouterSettings && server.getOSCRouter() != null)
			server.getOSCRouter().refreshSettings(false);
	}

	void handleReceivedMessage(OSCMessageEvent event) {
		String address = event.getMessage().getAddress();
		if (address.equalsIgnoreCase("/VMC/Ext/Bone/Pos")) {
			// TODO add rotation trackers
		} else if (
			address.equalsIgnoreCase("/VMC/Ext/Hmd/Pos")
				|| address.equalsIgnoreCase("/VMC/Ext/Con/Pos")
				|| address.equalsIgnoreCase("/VMC/Ext/Tra/Pos")
		) {
			// TODO add position + rotation trackers
		}
	}

	@Override
	public void update() {
		// Send OSC data
		if (oscSender != null && oscSender.isConnected()) {
			// Create new OSC Bundle
			OSCBundle oscBundle = new OSCBundle();

			// Add our relative time
			oscArgs.clear();
			oscArgs.add((System.currentTimeMillis() - startTime) / 1000f);
			oscBundle.addPacket(new OSCMessage("/VMC/Ext/T", oscArgs.clone()));

			if (humanPoseManager.isSkeletonPresent()) {
				// Indicate tracking is available
				oscArgs.clear();
				oscArgs.add(1);
				oscBundle
					.addPacket(
						new OSCMessage(
							"/VMC/Ext/OK",
							oscArgs.clone()
						)
					);

				vecBuf.zero();
				quatBuf.loadIdentity();
				oscArgs.clear();
				oscArgs.add("root");
				addTransformToArgs(vecBuf, quatBuf);
				oscBundle
					.addPacket(
						new OSCMessage(
							"/VMC/Ext/Root/Pos",
							oscArgs.clone()
						)
					);

				// Add Unity humanoid bones transforms
				for (UnityBone bone : UnityBone.values) {
					if (
						!(humanPoseManager.isTrackingLeftArmFromController()
							&& isLeftArmUnityBone(bone))
							&& !(humanPoseManager.isTrackingRightArmFromController()
								&& isRightArmUnityBone(bone))
					) {
						// Get BoneInfo for the bone
						BoneInfo boneInfo = humanPoseManager
							.getBoneInfoForBoneType(
								bone.boneType
							);

						// TODO : implement reading/loading VRM avatars
						// to get proportions.
						// User needs to agree to the VRM's license
						float height = 0.7f;
						float heightMultiplier = height
							/ humanPoseManager.getUserHeightFromConfig();
						if (bone == UnityBone.HIPS) {
							// Add the tracking root
							// Get position
							if (anchorHip) {
								// Hip anchor
								vecBuf.set(0f, height / 2f, 0f);
							} else {
								// Head anchor
								vecBuf
									.set(
										humanPoseManager
											.getTailNodeOfBone(BoneType.HEAD)
											.getParent().worldTransform.getTranslation()
									)
									.multLocal(heightMultiplier);
								vecBuf
									.addLocal(
										humanPoseManager
											.getTailNodeOfBone(BoneType.HIP).worldTransform
												.getTranslation()
												.mult(heightMultiplier)
									);
								vecBuf.setY(vecBuf.y - height);
							}

							// Get rotation
							quatBuf.set(boneInfo.getGlobalRotation(true));
						} else {
							if (boneInfo != null) {
								// Get position
								vecBuf
									.set(
										boneInfo
											.getLocalTranslationFromRoot(
												BoneType.HIP,
												true
											)
									)
									.multLocal(heightMultiplier);

								// Get rotation
								quatBuf
									.set(
										boneInfo
											.getLocalRotationFromRoot(
												BoneType.HIP,
												true
											)
									);
							}
						}

						oscArgs.clear();
						oscArgs.add(bone.stringVal);
						addTransformToArgs(vecBuf, quatBuf);
						oscBundle
							.addPacket(
								new OSCMessage(
									"/VMC/Ext/Bone/Pos",
									oscArgs.clone()
								)
							);
					}


				}
			}

			for (ShareableTracker shareableTracker : shareableTrackers) {
				shareableTracker.getPosition(vecBuf);
				shareableTracker.getRotation(quatBuf);
				oscArgs.clear();
				oscArgs.add(String.valueOf(shareableTracker.getTrackerId()));
				addTransformToArgs(vecBuf, quatBuf);
				String address;
				if (shareableTracker.getTrackerRole() == TrackerRole.HMD) {
					address = "/VMC/Ext/Hmd/Pos";
				} else if (
					shareableTracker.getTrackerRole() == TrackerRole.LEFT_CONTROLLER
						|| shareableTracker.getTrackerRole() == TrackerRole.RIGHT_CONTROLLER
				) {
					address = "/VMC/Ext/Con/Pos";
				} else {
					address = "/VMC/Ext/Tra/Pos";
				}
				oscBundle
					.addPacket(
						new OSCMessage(
							address,
							oscArgs.clone()
						)
					);
			}

			// Send OSC packets as bundle
			try {
				oscSender.send(oscBundle);
			} catch (IOException | OSCSerializeException e) {
				// Avoid spamming AsynchronousCloseException too many
				// times per second
				if (System.currentTimeMillis() - timeAtLastError > 100) {
					timeAtLastError = System.currentTimeMillis();
					LogManager
						.warning(
							"[VMCHandler] Error sending OSC packets: "
								+ e
						);
				}
			}
		}
	}

	private void addTransformToArgs(Vector3f pos, Quaternion rot) {
		oscArgs.add(pos.getX());
		oscArgs.add(pos.getY());
		oscArgs.add(-pos.getZ());
		oscArgs.add(rot.getX());
		oscArgs.add(rot.getY());
		oscArgs.add(-rot.getZ());
		oscArgs.add(-rot.getW());
	}

	private boolean isLeftArmUnityBone(UnityBone bone) {
		return bone == UnityBone.LEFT_UPPER_ARM
			|| bone == UnityBone.LEFT_LOWER_ARM
			|| bone == UnityBone.LEFT_HAND;
	}

	private boolean isRightArmUnityBone(UnityBone bone) {
		return bone == UnityBone.RIGHT_UPPER_ARM
			|| bone == UnityBone.RIGHT_LOWER_ARM
			|| bone == UnityBone.RIGHT_HAND;
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
