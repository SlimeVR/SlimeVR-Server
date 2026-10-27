@file:Suppress("DEPRECATION") // legacy v2 packets

package dev.slimevr.hid.behaviours.legacy

import com.jme3.math.FastMath
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRotationBatteryLegacy
import dev.slimevr.hid.HIDRotationButtonLegacy
import dev.slimevr.hid.HIDRotationLegacy
import dev.slimevr.hid.HIDRotationMagLegacy
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.hardware_info.BoardType

// v0.0.1 had messed up accel offsets in firmware and is used in production for official trackers (SLIMEVR_BUTTERFLY, 26)
private val ACCEL_OFFSET_CORRECTION_0_0_1 = Quaternion.rotationAroundZAxis(FastMath.PI)
fun getCorrectedAcceleration(acceleration: Vector3, firmwareVersion: String?, boardType: BoardType) = if (firmwareVersion == "0.0.1" && boardType == BoardType.SLIMEVR_BUTTERFLY) {
	ACCEL_OFFSET_CORRECTION_0_0_1.sandwich(acceleration)
} else {
	acceleration
}

class HIDRotationBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDRotationLegacy> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			val deviceState = receiver.getDevice(packet.hidId)?.context?.state?.value ?: return@on
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = packet.rotation, acceleration = getCorrectedAcceleration(packet.acceleration, deviceState.firmwareVersion, deviceState.boardType)))
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationBatteryLegacy> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			val deviceState = receiver.getDevice(packet.hidId)?.context?.state?.value ?: return@on
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = packet.rotation, acceleration = getCorrectedAcceleration(packet.acceleration, deviceState.firmwareVersion, deviceState.boardType)))
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationMagLegacy> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = packet.rotation, magnetometer = packet.magnetometer))
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationButtonLegacy> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			val deviceState = receiver.getDevice(packet.hidId)?.context?.state?.value ?: return@on
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = packet.rotation, acceleration = getCorrectedAcceleration(packet.acceleration, deviceState.firmwareVersion, deviceState.boardType)))
		}.launchIn(receiver.context.scope)
	}
}
