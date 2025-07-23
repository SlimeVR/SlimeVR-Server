package dev.slimevr.desktop.platform

import dev.slimevr.VRServer.Companion.instance
import dev.slimevr.bridge.BridgeThread
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.desktop.platform.ProtobufMessages.*
import dev.slimevr.inputs.InputType
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.processor.ShareableBone
import dev.slimevr.tracking.processor.isLeftFinger
import dev.slimevr.tracking.processor.isRightFinger
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.TrackerStatus.Companion.getById
import dev.slimevr.util.ann.VRServerThread
import io.eiren.util.ann.Synchronize
import io.eiren.util.ann.ThreadSafe
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

abstract class ProtobufBridge(@JvmField protected val bridgeName: String) : ISteamVRBridge {
	@JvmField
	@VRServerThread
	protected val sharedTrackers: MutableList<Tracker> = FastList()

	@JvmField
	@VRServerThread
	protected val fingerBones: MutableList<ShareableBone> = FastList()

	@ThreadSafe
	private val inputQueue: Queue<ProtobufMessage> = LinkedBlockingQueue()

	@ThreadSafe
	private val outputQueue: Queue<ProtobufMessage> = LinkedBlockingQueue()

	@Synchronize("self")
	private val remoteTrackersBySerial: MutableMap<String, Tracker> = HashMap()

	@Synchronize("self")
	private val remoteTrackersByTrackerId: MutableMap<Int, Tracker> = HashMap()
	private var hadNewData = false

	private var remoteProtocolVersion: Int = 0

	private val inputs: MutableList<dev.slimevr.inputs.Input> = FastList()

	/**
	 * Wakes the bridge thread, implementation is platform-specific.
	 */
	@ThreadSafe
	protected abstract fun signalSend()

	@BridgeThread
	protected abstract fun sendMessageReal(message: ProtobufMessage?): Boolean

	@BridgeThread
	protected fun messageReceived(message: ProtobufMessage) {
		inputQueue.add(message)
	}

	@ThreadSafe
	protected fun sendMessage(message: ProtobufMessage) {
		outputQueue.add(message)
		signalSend()
	}

	@BridgeThread
	protected fun updateMessageQueue() {
		var message: ProtobufMessage?
		while ((outputQueue.poll().also { message = it }) != null) {
			if (!sendMessageReal(message)) return
		}
	}

	@VRServerThread
	override fun dataRead() {
		hadNewData = false
		var message: ProtobufMessage?
		while ((inputQueue.poll().also { message = it }) != null) {
			processMessageReceived(message)
			hadNewData = true
		}
	}

	@VRServerThread
	protected fun trackerOverrideUpdate(source: Tracker, target: Tracker) {
		target.position = source.position
		target.setRotation(source.getRotation())
		target.status = source.status
		target.batteryLevel = source.batteryLevel
		target.batteryVoltage = source.batteryVoltage
		target.dataTick()
	}

	@VRServerThread
	override fun dataWrite() {
		if (!hadNewData) {
			// Don't write anything if no message were received, we
			// always process at the
			// speed of the other side
			return
		}
		for (tracker in sharedTrackers) {
			writeTrackerUpdate(tracker)
			writeBatteryUpdate(tracker)
		}
	}

	@VRServerThread
	protected fun writeTrackerUpdate(localTracker: Tracker?) {
		val builder = Position.newBuilder().setTrackerId(
			localTracker!!.id,
		)

		// localTracker position
		if (localTracker.hasPosition) {
			val pos = localTracker.position
			builder.setX(pos.x)
			builder.setY(pos.y)
			builder.setZ(pos.z)
		}

		// localTracker rotation
		if (localTracker.hasRotation) {
			val rot = localTracker.getRotation()
			builder.setQx(rot.x)
			builder.setQy(rot.y)
			builder.setQz(rot.z)
			builder.setQw(rot.w)
		}

		// localTracker's associated fingers' rotations
		if (remoteProtocolVersion >= 1) {
			val trackerIsLeftHand = localTracker.trackerPosition == TrackerPosition.LEFT_HAND
			val trackerIsRightHand = localTracker.trackerPosition == TrackerPosition.RIGHT_HAND
			if (trackerIsLeftHand || trackerIsRightHand) {
				for (fingerBone in fingerBones) {
					// Only send finger data if the finger bone matches the hand tracker side
					if ((trackerIsLeftHand && fingerBone.boneType.isLeftFinger()) ||
						(trackerIsRightHand && fingerBone.boneType.isRightFinger())
					) {
						val fingerBuilder = FingerBoneRotation.newBuilder()
							.setName(boneTypeToFingerBoneName(fingerBone.boneType))
							.setX(fingerBone.localRotation.x)
							.setY(fingerBone.localRotation.y)
							.setZ(fingerBone.localRotation.z)
							.setW(fingerBone.localRotation.w)

						builder.addFingerBoneRotations(fingerBuilder.build())
					}
				}
			}
		}

		// Double and triple tap inputs
		if (remoteProtocolVersion >= 1) {
			val trackerIsLeftHand = localTracker.trackerPosition == TrackerPosition.LEFT_HAND
			val trackerIsRightHand = localTracker.trackerPosition == TrackerPosition.RIGHT_HAND
			if (trackerIsLeftHand || trackerIsRightHand) {
				val iterator = inputs.iterator()
				while (iterator.hasNext()) {
					val input = iterator.next()
					if ((input.rightHand && trackerIsRightHand) || (!input.rightHand && trackerIsLeftHand)) {
						val inputBuilder = Input.newBuilder()
						if (input.type == InputType.DOUBLE_TAP) {
							inputBuilder.setType(Input.InputType.DOUBLE_TAP)
						} else if (input.type == InputType.TRIPLE_TAP) {
							inputBuilder.setType(Input.InputType.TRIPLE_TAP)
						} else {
							continue
						}

						builder.addInput(inputBuilder.build())

						iterator.remove()
					} else {
						// Input side doesn't match controller side
					}
				}
			}
		}

		sendMessage(ProtobufMessage.newBuilder().setPosition(builder).build())
	}

	@VRServerThread
	override fun sendInput(input: dev.slimevr.inputs.Input) {
		inputs.add(input)
	}

	@VRServerThread
	protected open fun writeBatteryUpdate(localTracker: Tracker) {
		return
	}

	@VRServerThread
	protected fun processMessageReceived(message: ProtobufMessage?) {
		// if(!message.hasPosition())
		// LogManager.log.info("[" + bridgeName + "] MSG: " + message);
		if (message!!.hasPosition()) {
			positionReceived(message.position)
		} else if (message.hasUserAction()) {
			userActionReceived(message.userAction)
		} else if (message.hasTrackerStatus()) {
			trackerStatusReceived(message.trackerStatus)
		} else if (message.hasTrackerAdded()) {
			trackerAddedReceived(message.trackerAdded)
		} else if (message.hasBattery()) {
			batteryReceived(message.battery)
		} else if (message.hasVersion()) {
			versionReceived(message.version)
		}
	}

	@VRServerThread
	protected fun positionReceived(positionMessage: ProtobufMessages.Position) {
		val tracker = getInternalRemoteTrackerById(positionMessage.trackerId)
		if (tracker != null) {
			if (positionMessage.hasX()) {
				tracker
					.position = Vector3(
					positionMessage.x,
					positionMessage.y,
					positionMessage.z,
				)
			}

			tracker
				.setRotation(
					Quaternion(
						positionMessage.qw,
						positionMessage.qx,
						positionMessage.qy,
						positionMessage.qz,
					),

				)
			tracker.dataTick()
		}
	}

	@VRServerThread
	protected open fun batteryReceived(batteryMessage: Battery) {
		return
	}

	@VRServerThread
	protected open fun versionReceived(versionMessage: Version) {
		remoteProtocolVersion = versionMessage.protocolVersion
		LogManager.info("[ProtobufBridge] Received driver protocol version: $remoteProtocolVersion")
		if (remoteProtocolVersion != PROTOCOL_VERSION) {
			LogManager.warning("[ProtobufBridge] Driver protocol version ($remoteProtocolVersion) doesn't match server protocol version ($PROTOCOL_VERSION)")
		}
	}

	@VRServerThread
	protected abstract fun createNewTracker(trackerAdded: TrackerAdded): Tracker

	@VRServerThread
	protected fun trackerAddedReceived(trackerAdded: TrackerAdded) {
		var tracker = getInternalRemoteTrackerById(trackerAdded.trackerId)
		if (tracker != null) {
			// TODO reinit?
			return
		}
		tracker = createNewTracker(trackerAdded)
		synchronized(remoteTrackersBySerial) {
			remoteTrackersBySerial.put(tracker!!.name, tracker)
		}
		synchronized(remoteTrackersByTrackerId) {
			remoteTrackersByTrackerId.put(tracker!!.trackerNum, tracker)
		}
		instance.registerTracker(tracker!!)
	}

	@VRServerThread
	protected fun userActionReceived(userAction: ProtobufMessages.UserAction) {
		val resetSourceName = String.format("%s: %s", resetSourceNamePrefix, bridgeName)
		when (userAction.name) {
			"reset" -> // TODO : Check pose field
				instance.resetTrackersFull(resetSourceName)

			"fast_reset" -> instance.resetTrackersYaw(resetSourceName)

			"pause_tracking" ->
				instance
					.togglePauseTracking(resetSourceName)
		}
	}

	@VRServerThread
	protected fun trackerStatusReceived(trackerStatus: ProtobufMessages.TrackerStatus) {
		val tracker = getInternalRemoteTrackerById(trackerStatus.trackerId)
		if (tracker != null) {
			tracker.status = getById(trackerStatus.statusValue)!!
		}
	}

	@ThreadSafe
	protected fun getInternalRemoteTrackerById(trackerId: Int): Tracker? {
		synchronized(remoteTrackersByTrackerId) {
			return remoteTrackersByTrackerId[trackerId]
		}
	}

	@VRServerThread
	protected fun reconnected() {
		for (tracker in sharedTrackers) {
			val builder = TrackerAdded
				.newBuilder()
				.setTrackerId(tracker.id)
				.setTrackerName(tracker.name)
				.setTrackerSerial(tracker.name)
				.setTrackerRole(tracker.trackerPosition!!.trackerRole!!.id)
			sendMessage(ProtobufMessage.newBuilder().setTrackerAdded(builder).build())
		}
	}

	@VRServerThread
	protected fun disconnected() {
		synchronized(remoteTrackersByTrackerId) {
			for ((_, value) in remoteTrackersByTrackerId) {
				value.status = TrackerStatus.DISCONNECTED
			}
		}
	}

	@VRServerThread
	override fun addSharedTracker(tracker: Tracker?) {
		if (sharedTrackers.contains(tracker) || tracker == null) return
		sharedTrackers.add(tracker)
		val builder = TrackerAdded
			.newBuilder()
			.setTrackerId(tracker.id)
			.setTrackerName(tracker.name)
			.setTrackerSerial(tracker.name)
			.setTrackerRole(tracker.trackerPosition!!.trackerRole!!.id)
		sendMessage(ProtobufMessage.newBuilder().setTrackerAdded(builder).build())
	}

	@VRServerThread
	override fun removeSharedTracker(tracker: Tracker?) {
		// Remove shared tracker
		sharedTrackers.remove(tracker)

		// Set the tracker's status as disconnected
		val statusBuilder = ProtobufMessages.TrackerStatus
			.newBuilder()
			.setTrackerId(tracker!!.id)
		statusBuilder.setStatus(ProtobufMessages.TrackerStatus.Status.DISCONNECTED)
		sendMessage(ProtobufMessage.newBuilder().setTrackerStatus(statusBuilder).build())
	}

	@VRServerThread
	override fun addFingerBones(bones: List<ShareableBone>) {
		fingerBones.addAll(bones)
	}

	private fun boneTypeToFingerBoneName(boneType: BoneType): FingerBoneRotation.FingerBoneName = when (boneType) {
		BoneType.LEFT_THUMB_METACARPAL, BoneType.RIGHT_THUMB_METACARPAL -> FingerBoneRotation.FingerBoneName.THUMB_METACARPAL

		BoneType.LEFT_THUMB_PROXIMAL, BoneType.RIGHT_THUMB_PROXIMAL -> FingerBoneRotation.FingerBoneName.THUMB_PROXIMAL

		BoneType.LEFT_THUMB_DISTAL, BoneType.RIGHT_THUMB_DISTAL -> FingerBoneRotation.FingerBoneName.THUMB_DISTAL

		BoneType.LEFT_INDEX_PROXIMAL, BoneType.RIGHT_INDEX_PROXIMAL -> FingerBoneRotation.FingerBoneName.INDEX_PROXIMAL

		BoneType.LEFT_INDEX_INTERMEDIATE, BoneType.RIGHT_INDEX_INTERMEDIATE -> FingerBoneRotation.FingerBoneName.INDEX_INTERMEDIATE

		BoneType.LEFT_INDEX_DISTAL, BoneType.RIGHT_INDEX_DISTAL -> FingerBoneRotation.FingerBoneName.INDEX_DISTAL

		BoneType.LEFT_MIDDLE_PROXIMAL, BoneType.RIGHT_MIDDLE_PROXIMAL -> FingerBoneRotation.FingerBoneName.MIDDLE_PROXIMAL

		BoneType.LEFT_MIDDLE_INTERMEDIATE, BoneType.RIGHT_MIDDLE_INTERMEDIATE -> FingerBoneRotation.FingerBoneName.MIDDLE_INTERMEDIATE

		BoneType.LEFT_MIDDLE_DISTAL, BoneType.RIGHT_MIDDLE_DISTAL -> FingerBoneRotation.FingerBoneName.MIDDLE_DISTAL

		BoneType.LEFT_RING_PROXIMAL, BoneType.RIGHT_RING_PROXIMAL -> FingerBoneRotation.FingerBoneName.RING_PROXIMAL

		BoneType.LEFT_RING_INTERMEDIATE, BoneType.RIGHT_RING_INTERMEDIATE -> FingerBoneRotation.FingerBoneName.RING_INTERMEDIATE

		BoneType.LEFT_RING_DISTAL, BoneType.RIGHT_RING_DISTAL -> FingerBoneRotation.FingerBoneName.RING_DISTAL

		BoneType.LEFT_LITTLE_PROXIMAL, BoneType.RIGHT_LITTLE_PROXIMAL -> FingerBoneRotation.FingerBoneName.LITTLE_PROXIMAL

		BoneType.LEFT_LITTLE_INTERMEDIATE, BoneType.RIGHT_LITTLE_INTERMEDIATE -> FingerBoneRotation.FingerBoneName.LITTLE_INTERMEDIATE

		BoneType.LEFT_LITTLE_DISTAL, BoneType.RIGHT_LITTLE_DISTAL -> FingerBoneRotation.FingerBoneName.LITTLE_DISTAL

		else -> {
			LogManager.severe("[ProtobufBridge] Tried to get FingerBoneName from invalid BoneType " + boneType.name)
			FingerBoneRotation.FingerBoneName.UNRECOGNIZED
		}
	}

	companion object {
		private const val resetSourceNamePrefix = "ProtobufBridge"
		private const val PROTOCOL_VERSION = 1
	}
}
