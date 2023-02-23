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
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.processor.TransformNode;
import dev.slimevr.tracking.processor.skeleton.UnityArmature;
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
	private final Quaternion yawOffset = new Quaternion();
	private UnityArmature inputUnityArmature;
	private UnityArmature outputUnityArmature;
	private float vrmHeight;
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
				String[] listenAddresses = { "/VMC/Ext/Bone/Pos", "/VMC/Ext/Hmd/Pos",
					"/VMC/Ext/Con/Pos", "/VMC/Ext/Tra/Pos", "/VMC/Ext/Root/Pos" };

				for (String address : listenAddresses) {
					oscReceiver
						.getDispatcher()
						.addListener(new OSCPatternAddressMessageSelector(address), listener);
				}

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
				outputUnityArmature = new UnityArmature(false);
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
			if (outputUnityArmature != null && config.getVrmJson() != null) {
				VRMReader vrmReader = new VRMReader(config.getVrmJson());
				for (UnityBone unityBone : UnityBone.values) {
					TransformNode node = outputUnityArmature.getHeadNodeOfBone(unityBone);
					if (node != null)
						node.localTransform
							.setTranslation(vrmReader.getOffsetForBone(unityBone));
				}

				vrmHeight = vrmReader
					.getOffsetForBone(UnityBone.HIPS)
					.add(vrmReader.getOffsetForBone(UnityBone.SPINE))
					.addLocal(vrmReader.getOffsetForBone(UnityBone.CHEST))
					.addLocal(vrmReader.getOffsetForBone(UnityBone.UPPER_CHEST))
					.addLocal(vrmReader.getOffsetForBone(UnityBone.NECK))
					.addLocal(vrmReader.getOffsetForBone(UnityBone.HEAD))
					.length();
			}
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
							)
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
				if (inputUnityArmature != null) {
					inputUnityArmature
						.setRootPose(
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
		UnityBone unityBone
	) {
		// Create device if it doesn't exist
		if (trackerDevice == null) {
			trackerDevice = server.getDeviceManager().createDevice("VMC receiver", "1.0", "VMC");
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
				trackerDevice,
				true
			);
			tracker.setBodyPosition(trackerPosition);
			trackerDevice.getTrackers().put(trackerDevice.getTrackers().size(), tracker);
			byTrackerNameTracker.put(name, tracker);
			server.registerTracker(tracker);
		}
		tracker.setStatus(TrackerStatus.OK);

		// Set position
		if (position != null) {
			tracker.position.set(position);
		}

		// Set rotation
		if (rotation != null) {
			if (localRotation) {
				// Instantiate unityHierarchy if not done
				if (inputUnityArmature == null)
					inputUnityArmature = new UnityArmature(true);
				inputUnityArmature.setBoneRotationFromLocal(unityBone, rotation);
				rotation.set(inputUnityArmature.getGlobalRotationForBone(unityBone));
			}
			rotation.set(yawOffset.mult(rotation));
			tracker.rotation.set(rotation);
		}

		tracker.dataTick();
	}

	@Override
	public void update() {
		// Update unity hierarchy
		if (inputUnityArmature != null)
			inputUnityArmature.updateNodes();

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

				for (UnityBone bone : UnityBone.values) {
					// Get tailNode for bone
					TransformNode tailNode = humanPoseManager
						.getTailNodeOfBone(
							bone.boneType
						);
					// Update unity hierarchy from bone's global rotation
					if (tailNode != null && tailNode.getParent() != null)
						outputUnityArmature
							.setBoneRotationFromGlobal(
								bone,
								tailNode.getParent().worldTransform.getRotation()
							);
				}
				if (!anchorHip) {
					// Anchor from head
					outputUnityArmature.getRootNode().localTransform
						.setTranslation(
							humanPoseManager.getTailNodeOfBone(BoneType.HEAD).worldTransform
								.getTranslation()
								.mult(vrmHeight)
								.subtractLocal(
									(outputUnityArmature
										.getHeadNodeOfBone(UnityBone.HEAD)
										.getParent().worldTransform
											.getTranslation()
											.subtract(
												outputUnityArmature
													.getHeadNodeOfBone(
														UnityBone.HIPS
													).worldTransform.getTranslation()
											)).multLocal(10f / 9f)
								)
						);
				}

				// Update Unity skeleton
				outputUnityArmature.updateNodes();

				// Add Unity humanoid bones transforms
				for (UnityBone bone : UnityBone.values) {
					if (
						!(humanPoseManager.isTrackingLeftArmFromController()
							&& isLeftArmUnityBone(bone))
							&& !(humanPoseManager.isTrackingRightArmFromController()
								&& isRightArmUnityBone(bone))
					) {
						// Get position
						vecBuf.set(outputUnityArmature.getLocalTranslationForBone(bone));
						// Get rotation
						quatBuf.set(outputUnityArmature.getLocalRotationForBone(bone));

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
				TrackerRole role = shareableTracker.getTrackerRole();
				if (role == TrackerRole.HMD) {
					address = "/VMC/Ext/Hmd/Pos";
				} else if (
					role == TrackerRole.LEFT_CONTROLLER || role == TrackerRole.RIGHT_CONTROLLER
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

	/**
	 * Set the Quaternion to offset the received VMC tracking rotations' yaw by
	 *
	 * @param reference usually the HMD's rotation
	 */
	public void alignVMCTracking(Quaternion reference) {
		yawOffset.fromAngles(0, reference.getYaw(), 0);
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
