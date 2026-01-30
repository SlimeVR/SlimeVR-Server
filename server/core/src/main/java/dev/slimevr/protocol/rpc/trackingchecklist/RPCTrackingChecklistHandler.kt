package dev.slimevr.protocol.rpc.trackingchecklist

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.trackingchecklist.TrackingChecklistListener
import dev.slimevr.trackingchecklist.TrackingChecklistManager
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.TrackerId
import solarxr_protocol.rpc.*

class RPCTrackingChecklistHandler(
	private val rpcHandler: RPCHandler,
	var api: ProtocolAPI,
) : TrackingChecklistListener {

	init {
		api.server.trackingChecklistManager.addListener(this)

		rpcHandler.registerPacketListener(RpcMessage.TrackingChecklistRequest, ::onTrackingChecklistRequest)
		rpcHandler.registerPacketListener(RpcMessage.IgnoreTrackingChecklistStepRequest, ::onToggleTrackingChecklistRequest)
	}

	fun createTrackerId(fbb: FlatBufferBuilder, tracker: Tracker): Int {
		val deviceIdOffset = tracker.device?.let { device ->
			DeviceId.createDeviceId(fbb, device.id.toUByte())
		}
		TrackerId.startTrackerId(fbb)
		deviceIdOffset?.let { offset ->
			TrackerId.addDeviceId(fbb, offset)
		}
		TrackerId.addTrackerNum(fbb, tracker.trackerNum.toUByte())
		return TrackerId.endTrackerId(fbb)
	}

	fun createTrackerIds(fbb: FlatBufferBuilder, trackers: List<Tracker>): Int {
		val trackerIds = trackers.map {
			createTrackerId(fbb, it)
		}.toIntArray()

		return TrackingChecklistTrackerError.createTrackersIdVector(fbb, trackerIds)
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	fun buildTrackingChecklistResponse(fbb: FlatBufferBuilder): Int {
		val stepsOffsets = api.server.trackingChecklistManager.steps.map {
			var extraDataType: UByte = TrackingChecklistExtraData.NONE
			var dataOffset = 0
			when (it.extraData) {
				is TrackingChecklistManager.TrackerErrorData -> {
					val data = (it.extraData as TrackingChecklistManager.TrackerErrorData)
					extraDataType = TrackingChecklistExtraData.TrackingChecklistTrackerError
					dataOffset = TrackingChecklistTrackerError.createTrackingChecklistTrackerError(fbb, createTrackerIds(fbb, data.trackers))
				}
				is TrackingChecklistManager.TrackerResetData -> {
					val data = it.extraData as TrackingChecklistManager.TrackerResetData
					extraDataType = TrackingChecklistExtraData.TrackingChecklistTrackerReset
					dataOffset = TrackingChecklistTrackerReset.createTrackingChecklistTrackerReset(fbb, createTrackerIds(fbb, data.trackers))
				}
				is TrackingChecklistManager.SteamVRDisconnectedData -> {
					val data = it.extraData as TrackingChecklistManager.SteamVRDisconnectedData
					extraDataType = TrackingChecklistExtraData.TrackingChecklistSteamVRDisconnected

					val sOffset = fbb.createString(data.bridgeName)
					dataOffset = TrackingChecklistSteamVRDisconnected.createTrackingChecklistSteamVRDisconnected(fbb, sOffset)
				}
				is TrackingChecklistManager.UnassignedHMDData -> {
					val data = it.extraData as TrackingChecklistManager.UnassignedHMDData
					extraDataType = TrackingChecklistExtraData.TrackingChecklistUnassignedHMD
					dataOffset = TrackingChecklistUnassignedHMD.createTrackingChecklistUnassignedHMD(fbb, createTrackerId(fbb, data.tracker))
				}
				is TrackingChecklistManager.TrackerNeedCalibrationData -> {
					val data = it.extraData as TrackingChecklistManager.TrackerNeedCalibrationData
					extraDataType = TrackingChecklistExtraData.TrackingChecklistNeedCalibration
					dataOffset = TrackingChecklistNeedCalibration.createTrackingChecklistNeedCalibration(fbb, createTrackerIds(fbb, data.trackers))
				}
				is TrackingChecklistManager.PublicNetworksData -> {
					val data = it.extraData as TrackingChecklistManager.PublicNetworksData
					extraDataType = TrackingChecklistExtraData.TrackingChecklistPublicNetworks

					val adaptersOffsets = data.adapters.map {
						fbb.createString(it)
					}.toIntArray()
					val adaptersOffset = TrackingChecklistPublicNetworks.createAdaptersVector(fbb, adaptersOffsets)
					dataOffset = TrackingChecklistPublicNetworks.createTrackingChecklistPublicNetworks(fbb, adaptersOffset)
				}
			}
			TrackingChecklistStep.createTrackingChecklistStep(fbb, it.id, it.valid, it.enabled, it.visibility, it.optional, it.ignorable, extraDataType, dataOffset)
		}.toIntArray()

		val stepsOffset = TrackingChecklistResponse.createStepsVector(fbb, stepsOffsets)
		val ignoredStepsOffset = TrackingChecklistResponse.createIgnoredStepsVector(fbb, api.server.configManager.vrConfig.trackingChecklist.ignoredStepsIds.map { it.toUByte() }.toUByteArray())
		return TrackingChecklistResponse.createTrackingChecklistResponse(
			fbb,
			stepsOffset,
			ignoredStepsOffset,
		)
	}

	private fun onTrackingChecklistRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val fbb = FlatBufferBuilder(32)
		val response = buildTrackingChecklistResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.TrackingChecklistResponse,
			response,
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	private fun onToggleTrackingChecklistRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(IgnoreTrackingChecklistStepRequest()) as IgnoreTrackingChecklistStepRequest?
			?: return
		val step = api.server.trackingChecklistManager.steps.find { it.id == req.stepId } ?: error("invalid step id requested")

		api.server.trackingChecklistManager.ignoreStep(step, req.ignore)

		val fbb = FlatBufferBuilder(32)
		val response = buildTrackingChecklistResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.TrackingChecklistResponse,
			response,
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	override fun onStepsUpdate() {
		val fbb = FlatBufferBuilder(32)
		val response = buildTrackingChecklistResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.TrackingChecklistResponse,
			response,
		)
		fbb.finish(outbound)
		this.api.apiServers.forEach { apiServer ->
			apiServer.apiConnections.forEach { it.send(fbb.dataBuffer()) }
		}
	}
}
