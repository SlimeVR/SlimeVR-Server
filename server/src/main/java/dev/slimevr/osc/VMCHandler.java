package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.config.VMCConfig;
import dev.slimevr.tracking.Device;
import dev.slimevr.tracking.processor.BoneInfo;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.processor.skeleton.UnityHierarchy;
import dev.slimevr.tracking.trackers.*;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * VMC documentation: https://protocol.vmc.info/english
 * <p>
 * Notes: VMC uses local rotation from hip (unlike SlimeVR, which uses rotations
 * from head). VMC works with Unity's coordinate system, which means
 * Quaternions' z and w components and Vectors' z components need to be inverse
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
	private final Map<String, VRTracker> byTrackerNameTracker = new HashMap<>();
	private final Map<BoneType, VRTracker> byBonetypeTracker = new HashMap<>();
	private final Map<String, Long> byTrackerNameTimeout = new HashMap<>();
	private final static Long TRACKER_TIMEOUT_MS = 2000L;
	private final FastList<VRTracker> trackersList = new FastList<>();
	private UnityHierarchy unityHierarchy;
	private Device trackerDevice;
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

				oscReceiver
					.getDispatcher()
					.addListener(
						new OSCPatternAddressMessageSelector(
							"/VMC/Ext/Rcv"
						),
						listener
					);
				oscReceiver
					.getDispatcher()
					.addListener(
						new OSCPatternAddressMessageSelector(
							"/VMC/Ext/Bone/Pos"
						),
						listener
					);
				oscReceiver
					.getDispatcher()
					.addListener(
						new OSCPatternAddressMessageSelector(
							"/VMC/Ext/Hmd/Pos"
						),
						listener
					);
				oscReceiver
					.getDispatcher()
					.addListener(
						new OSCPatternAddressMessageSelector(
							"/VMC/Ext/Con/Pos"
						),
						listener
					);
				oscReceiver
					.getDispatcher()
					.addListener(
						new OSCPatternAddressMessageSelector(
							"/VMC/Ext/Tra/Pos"
						),
						listener
					);
				oscReceiver
					.getDispatcher()
					.addListener(
						new OSCPatternAddressMessageSelector(
							"/VMC/Ext/Root/Pos"
						),
						listener
					);

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

	private void handleReceivedMessage(OSCMessageEvent event) {
		switch (event.getMessage().getAddress()) {
			case "/VMC/Ext/Bone/Pos":
				// Is bone (rotation)
				TrackerPosition trackerPosition = UnityBone
					.getByStringVal(
						String.valueOf(event.getMessage().getArguments().get(0))
					).trackerPosition;
				// If received bone is part of SlimeVR's skeleton
				if (trackerPosition != null) {
					handleReceivedTracker(
						"VMC-Bone-" + event.getMessage().getArguments().get(0),
						trackerPosition,
						null,
						new Quaternion(
							(float) event.getMessage().getArguments().get(4),
							(float) event.getMessage().getArguments().get(5),
							-((float) event.getMessage().getArguments().get(6)),
							-((float) event.getMessage().getArguments().get(7))
						),
						true,
						UnityBone
							.getByStringVal(
								String.valueOf(event.getMessage().getArguments().get(0))
							).boneType
					);
				}
				break;
			case "/VMC/Ext/Hmd/Pos":
			case "/VMC/Ext/Con/Pos":
			case "/VMC/Ext/Tra/Pos":
				// Is tracker (position + rotation)
				handleReceivedTracker(
					"VMC-Tracker-" + event.getMessage().getArguments().get(0),
					null,
					new Vector3f(
						(float) event.getMessage().getArguments().get(1),
						(float) event.getMessage().getArguments().get(2),
						-((float) event.getMessage().getArguments().get(3))
					),
					new Quaternion(
						(float) event.getMessage().getArguments().get(4),
						(float) event.getMessage().getArguments().get(5),
						-((float) event.getMessage().getArguments().get(6)),
						-((float) event.getMessage().getArguments().get(7))
					),
					false,
					null
				);
				break;
			case "/VMC/Ext/Root/Pos":
				// Is VMC tracking root (offsets all rotations)
				if (unityHierarchy != null) {
					unityHierarchy
						.setRootRotation(
							new Quaternion(
								(float) event.getMessage().getArguments().get(4),
								(float) event.getMessage().getArguments().get(5),
								-((float) event.getMessage().getArguments().get(6)),
								-((float) event.getMessage().getArguments().get(7))
							)
						);
				}
				break;
		}
	}

	private void handleReceivedTracker(
		String name,
		TrackerPosition trackerPosition,
		Vector3f position,
		Quaternion rotation,
		boolean localRotation,
		BoneType boneType
	) {
		// Create device if it doesn't exist
		if (trackerDevice == null) {
			trackerDevice = server.getDeviceManager().createDevice("VMCReceiver", "1.0", "VMC");
			server.getDeviceManager().addDevice(trackerDevice);
		}

		// Try to get tracker
		VRTracker tracker = byTrackerNameTracker.get(name);

		// Create tracker if trying to get it returned null
		if (tracker == null) {
			tracker = new VRTracker(
				Tracker.getNextLocalTrackerId(),
				name,
				name,
				rotation != null,
				position != null,
				trackerDevice
			);
			tracker.setBodyPosition(trackerPosition);
			trackerDevice.getTrackers().put(trackerDevice.getTrackers().size(), tracker);
			byTrackerNameTracker.put(name, tracker);
			if (boneType != null)
				byBonetypeTracker.put(boneType, tracker);
			server.registerTracker(tracker);
			trackersList.add(tracker);
		}
		if (tracker.getStatus() != TrackerStatus.OK) {
			tracker.setStatus(TrackerStatus.OK);
		}

		// Set position
		if (position != null) {
			tracker.position.set(position);
		}

		// Set rotation
		if (rotation != null) {
			if (localRotation) {
				// Instantiate unityHierarchy if not done
				if (unityHierarchy == null)
					unityHierarchy = new UnityHierarchy();
				// TODO bones need mounting reset once trackers are rewritten
				unityHierarchy.updateBone(boneType, rotation);
				rotation.set(unityHierarchy.getGlobalRotForBone(boneType));
			}
			tracker.rotation.set(rotation);
		}

		byTrackerNameTimeout.put(name, System.currentTimeMillis());
		tracker.dataTick();
	}

	@Override
	public void update() {
		// Update unity hierarchy
		if (unityHierarchy != null)
			unityHierarchy.updateNodes();

		// Manage tracker timeout
		for (VRTracker tracker : trackersList) {
			if (
				System.currentTimeMillis() - byTrackerNameTimeout.get(tracker.getName())
					> TRACKER_TIMEOUT_MS
					&& tracker.getStatus() != TrackerStatus.DISCONNECTED
			) {
				tracker.setStatus(TrackerStatus.DISCONNECTED);
			}
		}

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
