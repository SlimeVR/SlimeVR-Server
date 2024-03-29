package dev.slimevr.desktop.platform

import dev.slimevr.VRServer.Companion.instance
import dev.slimevr.bridge.BridgeThread
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.desktop.platform.ProtobufMessages.*
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.TrackerStatus.Companion.getById
import dev.slimevr.util.ann.VRServerThread
import io.eiren.util.ann.Synchronize
import io.eiren.util.ann.ThreadSafe
import io.eiren.util.collections.FastList
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.util.Queue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashMap

abstract class ProtobufBridge(@JvmField protected val bridgeName: String) : ISteamVRBridge {
	@JvmField
	@VRServerThread
	protected val sharedTrackers: MutableList<Tracker> = FastList()

	@ThreadSafe
	private val inputQueue: Queue<ProtobufMessage> = LinkedBlockingQueue()

	@ThreadSafe
	private val outputQueue: Queue<ProtobufMessage> = LinkedBlockingQueue()

	@Synchronize("self")
	private val remoteTrackersBySerial: MutableMap<String, Tracker> = HashMap()

	@Synchronize("self")
	private val remoteTrackersByTrackerId: MutableMap<Int, Tracker> = HashMap()
	private var hadNewData = false

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
		val builder = ProtobufMessages.Position.newBuilder().setTrackerId(
			localTracker!!.id,
		)
		if (localTracker.hasPosition) {
			val pos = localTracker.position
			builder.setX(pos.x)
			builder.setY(pos.y)
			builder.setZ(pos.z)
		}
		if (localTracker.hasRotation) {
			val rot = localTracker.getRotation()
			builder.setQx(rot.x)
			builder.setQy(rot.y)
			builder.setQz(rot.z)
			builder.setQw(rot.w)
		}
		sendMessage(ProtobufMessage.newBuilder().setPosition(builder).build())
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

	companion object {
		private const val resetSourceNamePrefix = "ProtobufBridge"
	}
}
