package dev.slimevr.osc;

import com.illposed.osc.*;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.config.VMCConfig;
import dev.slimevr.vr.processor.HumanPoseProcessor;
import dev.slimevr.vr.processor.TransformNode;
import dev.slimevr.vr.processor.skeleton.BoneInfo;
import dev.slimevr.vr.processor.skeleton.BoneType;
import dev.slimevr.vr.processor.skeleton.Skeleton;
import dev.slimevr.vr.trackers.UnityBone;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import solarxr_protocol.datatypes.BodyPart;

import java.io.IOException;
import java.net.InetAddress;


/**
 * VMC documentation: https://protocol.vmc.info/english
 */
public class VMCHandler implements OSCHandler {
	private OSCPortIn oscReceiver;
	private OSCPortOut oscSender;
	private final VMCConfig config;
	private final VRServer server;
	private final HumanPoseProcessor humanPoseProcessor;
	private final FastList<Object> oscArgs = new FastList<>();
	private final Vector3f vecBuf = new Vector3f();
	private final Quaternion quatBuf = new Quaternion();
	private final long startTime;
	private float timeAtLastError;
	private boolean anchorHip;
	private int lastPortIn;
	private int lastPortOut;
	private InetAddress lastAddress;

	public VMCHandler(
		VRServer server,
		HumanPoseProcessor humanPoseProcessor,
		VMCConfig oscConfig
	) {
		this.server = server;
		this.humanPoseProcessor = humanPoseProcessor;
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
				MessageSelector selector = new OSCPatternAddressMessageSelector(
					"/VMC//"
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
		}

		if (refreshRouterSettings && server.getOSCRouter() != null)
			server.getOSCRouter().refreshSettings(false);
	}

	void handleReceivedMessage(OSCMessageEvent event) {
		// TODO ?
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

			Skeleton skeleton = humanPoseProcessor.getSkeleton();
			if (skeleton != null) {
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

				// Add the tracking root
				anchorHip = true;
				if (anchorHip) {
					// Hip anchor at fixed position
					vecBuf.zero();
				} else {
					// Head anchor
					vecBuf.zero();
					TransformNode headNode = skeleton.getTailNodeOfBone(BoneType.HEAD);
					TransformNode secondNode = skeleton.getTailNodeOfBone(BoneType.HIP);

					headNode.worldTransform.getTranslation(vecBuf);

					while (secondNode.getParent() != null && secondNode.getParent() != headNode) {
						secondNode = secondNode.getParent();

						vecBuf
							.addLocal(
								secondNode.worldTransform
									.getTranslation()
									.subtract(
										secondNode.getParent().worldTransform.getTranslation()
									)
							);
					}
				}
				quatBuf.loadIdentity();
				oscArgs.clear();
				oscArgs.add("root");
				oscArgs.add(vecBuf.getX());
				oscArgs.add(vecBuf.getY());
				oscArgs.add(-vecBuf.getZ());
				oscArgs.add(quatBuf.getX());
				oscArgs.add(quatBuf.getY());
				oscArgs.add(quatBuf.getZ());
				oscArgs.add(-quatBuf.getW());
				oscBundle
					.addPacket(
						new OSCMessage(
							"/VMC/Ext/Root/Pos",
							oscArgs.clone()
						)
					);

				// Add Unity humanoid bones transforms
				for (UnityBone bone : UnityBone.values) {
					// TODO fix head not rotating
					BoneInfo boneInfo = skeleton
						.getBoneInfoForBodyPart(
							bone.bodyPart
						);
					if (boneInfo != null) {
						vecBuf
							.set(
								boneInfo
									.getLocalBoneTranslationFromRoot(
										skeleton
											.getBoneInfoForBodyPart(
												BodyPart.HIP
											),
										true
									)
							);
						quatBuf
							.set(
								boneInfo
									.getLocalBoneRotationFromRoot(
										skeleton
											.getBoneInfoForBodyPart(
												BodyPart.HIP
											),
										true
									)
							);
						oscArgs.clear();
						oscArgs.add(bone.stringVal);
						oscArgs.add(vecBuf.x);
						oscArgs.add(vecBuf.y);
						oscArgs.add(-vecBuf.z);
						oscArgs.add(quatBuf.getX());
						oscArgs.add(quatBuf.getY());
						oscArgs.add(quatBuf.getZ());
						oscArgs.add(-quatBuf.getW());
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

	public void calibrateVMCHeadPosition() {
		// TODO ?
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
