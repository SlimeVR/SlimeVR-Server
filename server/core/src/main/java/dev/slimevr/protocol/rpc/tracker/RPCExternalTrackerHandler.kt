package dev.slimevr.protocol.rpc.tracker

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.VRServer.Companion.instance
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.datafeed.createTrackerId
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.protocol.rpc.createRPCMessage
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.DeviceOrigin
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.TrackerIdT
import solarxr_protocol.rpc.AddTrackerRequest
import solarxr_protocol.rpc.AddTrackerResponse
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.UpdateTrackerBattery
import solarxr_protocol.rpc.UpdateTrackerPose
import solarxr_protocol.rpc.UpdateTrackerStatus
import kotlin.text.ifEmpty

class RPCExternalTrackerHandler(
	rpcHandler: RPCHandler,
	val api: ProtocolAPI,
) {
	init {
		rpcHandler.registerPacketListener(RpcMessage.AddTrackerRequest, ::onAddTrackerRequest)
		rpcHandler.registerPacketListener(RpcMessage.UpdateTrackerPose, ::onUpdateTrackerPose)
		rpcHandler.registerPacketListener(RpcMessage.UpdateTrackerStatus, ::onUpdateTrackerStatus)
		rpcHandler.registerPacketListener(RpcMessage.UpdateTrackerBattery, ::onUpdateTrackerBattery)
	}

	private fun getTracker(conn: GenericConnection, trackerId: TrackerIdT): Tracker? = api.server.getTrackerById(trackerId)?.takeIf {
		it.device?.origin == DeviceOrigin.SOLARXR && conn.context.createdTrackers.contains(it)
	}

	fun onAddTrackerRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(AddTrackerRequest()) as? AddTrackerRequest ?: return

		val fbb = FlatBufferBuilder(32)
		// Try to find existing device.
		val existingDevice = api.server.deviceManager.devices.find {
			it.hardwareIdentifier == req.name()
		}
		val tracker = if (existingDevice != null) {
			if (existingDevice.origin != DeviceOrigin.SOLARXR) {
				LogManager.warning("[RPCExternalTrackerHandler] Client tried to create a tracker which exists and isn't external")
				return
			}

			// There should only be one tracker
			existingDevice.trackers[0] ?: error("SolarXR device didn't have tracker!?")
		} else {
			val device = instance.deviceManager
				.createDevice(
					DeviceOrigin.SOLARXR,
					req.displayName(),
					null,
					req.manufacturer() ?: "External",
				)
			device.hardwareIdentifier = req.name()

			val id = getNextLocalTrackerId()
			val tracker = Tracker(
				device = device,
				id = id,
				trackerNum = 0,
				name = req.name(),
				displayName = req.displayName() ?: "Tracker #$id",
				trackerPosition = TrackerPosition.getByBodyPart(req.roleHint()),
				hasPosition = req.tracksPosition(),
				hasRotation = req.tracksRotation(),
				hasAcceleration = req.tracksAcceleration(),
				userEditable = true,
				isComputed = true,
				allowReset = true,
				isHmd = req.isHmd,
			)

			device.trackers[0] = tracker
			instance.deviceManager.addDevice(device)
			instance.registerTracker(tracker)
			tracker
		}

		LogManager.info("[RPCExternalTrackerHandler] Added $tracker")
		conn.context.createdTrackers.add(tracker)
		val response = AddTrackerResponse.createAddTrackerResponse(fbb, createTrackerId(fbb, tracker))
		fbb.finish(createRPCMessage(fbb, RpcMessage.AddTrackerResponse, response, messageHeader))
		conn.send(fbb.dataBuffer())
	}

	fun onUpdateTrackerPose(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(UpdateTrackerPose()) as? UpdateTrackerPose ?: return
		val tracker = getTracker(conn, req.trackerId().unpack()) ?: return

		req.rotation()?.let {
			tracker.setRotation(Quaternion(it.w(), it.x(), it.y(), it.z()))
		}
		req.position()?.let {
			tracker.position = Vector3(it.x(), it.y(), it.z())
		}
		req.rawAcceleration()?.let {
			tracker.setAcceleration(Vector3(it.x(), it.y(), it.z()))
		}
		tracker.dataTick()
	}

	fun onUpdateTrackerStatus(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(UpdateTrackerStatus()) as? UpdateTrackerStatus
			?: return
		val tracker = getTracker(conn, req.trackerId().unpack()) ?: return

		val status = TrackerStatus.getById(req.status() - 1) ?: return
		LogManager.info("[RPCExternalTrackerHandler] Setting status of $tracker to $status")
		tracker.status = status
	}

	fun onUpdateTrackerBattery(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(UpdateTrackerBattery()) as? UpdateTrackerBattery ?: return
		val tracker = getTracker(conn, req.trackerId().unpack()) ?: return

		tracker.batteryLevel = req.batteryPercentage()

		// Purely for cosmetic purposes, external trackers do not report device voltage.
		tracker.batteryVoltage = if (req.charging()) 4.3f else 3.7f
	}
}
