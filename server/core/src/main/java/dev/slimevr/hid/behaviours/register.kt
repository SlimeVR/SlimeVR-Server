package dev.slimevr.hid.behaviours

import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverActions
import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType

/**
 * Associates an `hidId` with a [Device], reusing one already known by hardware address (a
 * reconnect) or creating it. Returns its device id.
 */
internal suspend fun registerHidDevice(
	receiver: HIDReceiver,
	hidId: Int,
	address: String,
	protocolVersion: Int,
	sensorCount: Int = 1,
): Int {
	receiver.context.state.value.trackers[hidId]?.let { return it.deviceId }

	val existingDevice = receiver.appContext.server.context.state.value.devices.values
		.find { it.context.state.value.macAddress == address && it.context.state.value.origin == DeviceOrigin.HID }

	if (existingDevice != null) {
		val id = existingDevice.context.state.value.id
		receiver.context.dispatch(HIDReceiverActions.DeviceRegistered(hidId, address, id, sensorCount))
		AppLogger.hid.info("Reconnected HID device $address (hidId=$hidId)")
		return id
	}

	val deviceId = receiver.appContext.server.nextHandle()
	val device = Device.create(
		scope = receiver.appContext.server.context.scope,
		appContext = receiver.appContext,
		id = deviceId,
		address = address,
		macAddress = address,
		origin = DeviceOrigin.HID,
		protocolVersion = protocolVersion,
	)
	receiver.appContext.server.context.dispatch(VRServerActions.NewDevice(deviceId, device))
	receiver.context.dispatch(HIDReceiverActions.DeviceRegistered(hidId, address, deviceId, sensorCount))
	AppLogger.hid.info("Registered HID device $address (hidId=$hidId)")
	return deviceId
}

/** Creates or finds the [Tracker] for an already-registered HID device and links it to [hidId]. */
internal fun ensureHidTracker(receiver: HIDReceiver, hidId: Int, deviceId: Int, imuType: ImuType? = null): Tracker? {
	val deviceState = receiver.appContext.server.getDevice(deviceId)?.context?.state?.value ?: return null

	val tracker = receiver.getTracker(hidId)
		?: receiver.appContext.server.context.state.value.trackers.values
			.find { it.context.state.value.hardwareId == deviceState.address && it.context.state.value.origin == DeviceOrigin.HID }
		?: run {
			val trackerId = receiver.appContext.server.nextHandle()
			val newTracker = Tracker.create(
				scope = receiver.appContext.server.context.scope,
				id = trackerId,
				deviceId = deviceState.id,
				imuType = imuType,
				hardwareId = deviceState.address,
				origin = DeviceOrigin.HID,
				appContext = receiver.appContext,
			)
			receiver.appContext.server.context.dispatch(VRServerActions.NewTracker(trackerId, newTracker))
			// HID does not have a rest calibration signal
			newTracker.context.dispatch(TrackerActions.Update { copy(completedRestCalibration = true) })
			newTracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
			newTracker
		}
	receiver.context.dispatch(HIDReceiverActions.TrackerRegistered(hidId, tracker.context.state.value.id))
	return tracker
}
