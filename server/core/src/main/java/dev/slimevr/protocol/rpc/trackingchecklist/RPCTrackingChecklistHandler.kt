package dev.slimevr.protocol.rpc.trackingchecklist

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.trackingchecklist.TrackingChecklistListener
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

	fun buildTrackingChecklistResponse(fbb: FlatBufferBuilder): Int = TrackingChecklistResponse.pack(
		fbb,
		TrackingChecklistResponseT().apply {
			steps = api.server.trackingChecklistManager.steps.toTypedArray()
			ignoredSteps = api.server.configManager.vrConfig.trackingChecklist.ignoredStepsIds.toIntArray()
		},
	)

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
		val step = api.server.trackingChecklistManager.steps.find { it.id == req.stepId() } ?: error("invalid step id requested")

		api.server.trackingChecklistManager.ignoreStep(step, req.ignore())

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
