package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import dev.slimevr.VRServer;
import dev.slimevr.autobone.errors.BodyProportionError;
import dev.slimevr.config.VMCConfig;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.processor.TransformNode;
import dev.slimevr.tracking.trackers.Device;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerPosition;
import dev.slimevr.tracking.trackers.TrackerStatus;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
	private final List<Tracker> computedTrackers = new FastList<>();
	private final FastList<Object> oscArgs = new FastList<>();
	private final long startTime;
	private final Map<String, Tracker> byTrackerNameTracker = new HashMap<>();
	private Quaternion yawOffset = Quaternion.Companion.getIDENTITY();
	private UnityArmature inputUnityArmature;
	private UnityArmature outputUnityArmature;
	private float vrmHeight;
	private Device trackerDevice;
	private long timeAtLastError;
	private long timeAtLastSend;
	private boolean anchorHip;
	private int lastPortIn;
	private int lastPortOut;
	private InetAddress lastAddress;

	public VMCHandler(
		VRServer server,
		HumanPoseManager humanPoseManager,
		VMCConfig oscConfig,
		List<Tracker> computedTrackers
	) {
		this.server = server;
		this.humanPoseManager = humanPoseManager;
		this.config = oscConfig;

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
				oscReceiver = new OSCPortIn(port);
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
				oscSender = new OSCPortOut(new InetSocketAddress(address, port));
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
				for (UnityBone unityBone : UnityBone.values()) {
					TransformNode node = outputUnityArmature.getHeadNodeOfBone(unityBone);
					if (node != null)
						node
							.getLocalTransform()
							.setTranslation(vrmReader.getOffsetForBone(unityBone));
				}
				vrmHeight = vrmReader
					.getOffsetForBone(UnityBone.HIPS)
					.plus(vrmReader.getOffsetForBone(UnityBone.SPINE))
					.plus(vrmReader.getOffsetForBone(UnityBone.CHEST))
					.plus(vrmReader.getOffsetForBone(UnityBone.UPPER_CHEST))
					.plus(vrmReader.getOffsetForBone(UnityBone.NECK))
					.plus(vrmReader.getOffsetForBone(UnityBone.HEAD))
					.len();
			}
		}

		if (refreshRouterSettings && server.getOSCRouter() != null)
			server.getOSCRouter().refreshSettings(false);
	}

	private void handleReceivedMessage(OSCMessageEvent event) {
		switch (event.getMessage().getAddress()) {
			case "/VMC/Ext/Bone/Pos":
				// Is bone (rotation)
				TrackerPosition trackerPosition = null;
				UnityBone bone = UnityBone
					.getByStringVal(String.valueOf(event.getMessage().getArguments().get(0)));
				if (bone != null)
					trackerPosition = bone.getTrackerPosition();

				// If received bone is part of SlimeVR's skeleton
				if (trackerPosition != null) {
					handleReceivedTracker(
						"VMC-Bone-" + event.getMessage().getArguments().get(0),
						trackerPosition,
						null,
						new Quaternion(
							-((float) event.getMessage().getArguments().get(7)),
							(float) event.getMessage().getArguments().get(4),
							(float) event.getMessage().getArguments().get(5),
							-((float) event.getMessage().getArguments().get(6))
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
					new Vector3(
						(float) event.getMessage().getArguments().get(1),
						(float) event.getMessage().getArguments().get(2),
						-((float) event.getMessage().getArguments().get(3))
					),
					new Quaternion(
						-((float) event.getMessage().getArguments().get(7)),
						(float) event.getMessage().getArguments().get(4),
						(float) event.getMessage().getArguments().get(5),
						-((float) event.getMessage().getArguments().get(6))
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
							new Vector3(
								(float) event.getMessage().getArguments().get(1),
								(float) event.getMessage().getArguments().get(2),
								-((float) event.getMessage().getArguments().get(3))
							),
							new Quaternion(
								-((float) event.getMessage().getArguments().get(7)),
								(float) event.getMessage().getArguments().get(4),
								(float) event.getMessage().getArguments().get(5),
								-((float) event.getMessage().getArguments().get(6))
							)
						);
				}
				break;
		}
	}

	private void handleReceivedTracker(
		String name,
		TrackerPosition trackerPosition,
		Vector3 position,
		Quaternion rotation,
		boolean localRotation,
		UnityBone unityBone
	) {
		// Create device if it doesn't exist
		if (trackerDevice == null) {
			trackerDevice = server.deviceManager.createDevice("VMC receiver", "1.0", "VMC");
			server.deviceManager.addDevice(trackerDevice);
		}

		// Try to get tracker
		Tracker tracker = byTrackerNameTracker.get(name);

		// Create tracker if trying to get it returned null
		if (tracker == null) {
			tracker = new Tracker(
				trackerDevice,
				VRServer.getNextLocalTrackerId(),
				name,
				"VMC Tracker #" + VRServer.getCurrentLocalTrackerId(),
				trackerPosition,
				null,
				position != null,
				rotation != null,
				false,
				true,
				false,
				position != null,
				null,
				true,
				false,
				position != null
			);
			trackerDevice.getTrackers().put(trackerDevice.getTrackers().size(), tracker);
			byTrackerNameTracker.put(name, tracker);
			server.registerTracker(tracker);
		}
		tracker.setStatus(TrackerStatus.OK);

		// Set position
		if (position != null) {
			tracker.setPosition(position);
		}

		// Set rotation
		if (rotation != null) {
			if (localRotation) {
				// Instantiate unityHierarchy if not done
				if (inputUnityArmature == null)
					inputUnityArmature = new UnityArmature(true);
				inputUnityArmature.setLocalRotationForBone(unityBone, rotation);
				rotation = inputUnityArmature.getGlobalRotationForBone(unityBone);
				rotation = yawOffset.times(rotation);
			}
			tracker.setRotation(rotation);
		}

		tracker.dataTick();
	}

	@Override
	public void update() {
		// Update unity hierarchy
		if (inputUnityArmature != null)
			inputUnityArmature.updateNodes();

		long currentTime = System.currentTimeMillis();
		if (currentTime - timeAtLastSend > 3) { // 200hz to not crash VSF
			timeAtLastSend = currentTime;
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

					oscArgs.clear();
					oscArgs.add("root");
					addTransformToArgs(
						Vector3.Companion.getNULL(),
						Quaternion.Companion.getIDENTITY()
					);
					oscBundle
						.addPacket(
							new OSCMessage(
								"/VMC/Ext/Root/Pos",
								oscArgs.clone()
							)
						);

					for (UnityBone bone : UnityBone.values()) {
						// Get tailNode for bone
						TransformNode tailNode = humanPoseManager
							.getTailNodeOfBone(
								bone.getBoneType()
							);
						// Update unity hierarchy from bone's global rotation
						if (tailNode != null && tailNode.getParent() != null)
							outputUnityArmature
								.setGlobalRotationForBone(
									bone,
									tailNode.getParent().getWorldTransform().getRotation()
								);
					}
					if (!anchorHip) {
						// Anchor from head
						// Gets the SlimeVR head position, scales it to the VRM,
						// and subtracts the difference between the VRM's head
						// and hip
						// FIXME this way isn't perfect, but I give up - Erimel
						Vector3 upperLegsAverage = (outputUnityArmature
							.getHeadNodeOfBone(UnityBone.LEFT_UPPER_LEG)
							.getWorldTransform()
							.getTranslation()
							.plus(
								outputUnityArmature
									.getHeadNodeOfBone(UnityBone.RIGHT_UPPER_LEG)
									.getWorldTransform()
									.getTranslation()
							)).times(0.5f);
						Vector3 scaledHead = humanPoseManager
							.getTailNodeOfBone(BoneType.HEAD)
							.getWorldTransform()
							.getTranslation()
							.times(
								vrmHeight
									/ (humanPoseManager.getUserHeightFromConfig()
										* BodyProportionError.eyeHeightToHeightRatio)
							);
						Vector3 pos = scaledHead
							.minus(
								(outputUnityArmature
									.getHeadNodeOfBone(UnityBone.HEAD)
									.getParent()
									.getWorldTransform()
									.getTranslation()
									.minus(upperLegsAverage))
							);


						outputUnityArmature
							.getHeadNodeOfBone(UnityBone.HIPS)
							.getLocalTransform()
							.setTranslation(pos);
					}

					// Update Unity skeleton
					outputUnityArmature.updateNodes();

					// Add Unity humanoid bones transforms
					for (UnityBone bone : UnityBone.values()) {
						if (
							!(humanPoseManager.isTrackingLeftArmFromController()
								&& isLeftArmUnityBone(bone))
								&& !(humanPoseManager.isTrackingRightArmFromController()
									&& isRightArmUnityBone(bone))
						) {
							oscArgs.clear();
							oscArgs.add(bone.getStringVal());
							addTransformToArgs(
								outputUnityArmature.getLocalTranslationForBone(bone),
								outputUnityArmature.getLocalRotationForBone(bone)
							);
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

				for (Tracker tracker : computedTrackers) {
					if (!tracker.getStatus().getReset()) {
						oscArgs.clear();

						String name = tracker.getName();
						oscArgs.add(name);

						addTransformToArgs(
							tracker.getPosition(),
							tracker.getRotation()
						);

						String address;
						TrackerPosition role = tracker.getTrackerPosition();
						if (role == TrackerPosition.HEAD) {
							address = "/VMC/Ext/Hmd/Pos";
						} else if (
							role == TrackerPosition.LEFT_HAND || role == TrackerPosition.RIGHT_HAND
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
	}

	/**
	 * Set the Quaternion to shift the received VMC tracking rotations' yaw
	 *
	 * @param reference the head's rotation
	 */
	public void alignVMCTracking(Quaternion reference) {
		yawOffset = reference.project(Vector3.Companion.getPOS_Y()).unit();
	}

	/**
	 * Add a computed tracker to the list of trackers to send.
	 *
	 * @param computedTracker the computed tracker
	 */
	public void addComputedTracker(Tracker computedTracker) {
		computedTrackers.add(computedTracker);
	}

	private void addTransformToArgs(Vector3 pos, Quaternion rot) {
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
