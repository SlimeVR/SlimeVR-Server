package dev.slimevr.protocol.rpc.flightlist

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.flightlist.FlightListListener
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import solarxr_protocol.rpc.*

class RPCFlightListHandler(
	private val rpcHandler: RPCHandler,
	var api: ProtocolAPI,
) : FlightListListener {

	init {
		api.server.flightListManager.addListener(this)

		rpcHandler.registerPacketListener(RpcMessage.FlightListRequest, ::onFlightListRequest)
		rpcHandler.registerPacketListener(RpcMessage.IgnoreFlightListStepRequest, ::onToggleFlightListRequest)
	}

	fun buildFlightListResponse(fbb: FlatBufferBuilder): Int = FlightListResponse.pack(
		fbb,
		FlightListResponseT().apply {
			steps = api.server.flightListManager.steps.toTypedArray()
			ignoredSteps = api.server.configManager.vrConfig.flightList.ignoredStepsIds.toIntArray()
		},
	)

	private fun onFlightListRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val fbb = FlatBufferBuilder(32)
		val response = buildFlightListResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.FlightListResponse,
			response,
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	private fun onToggleFlightListRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(IgnoreFlightListStepRequest()) as IgnoreFlightListStepRequest?
			?: return
		val step = api.server.flightListManager.steps.find { it.id == req.stepId() } ?: error("invalid step id requested")

		api.server.flightListManager.ignoreStep(step, req.ignore())

		val fbb = FlatBufferBuilder(32)
		val response = buildFlightListResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.FlightListResponse,
			response,
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	override fun onStepsUpdate() {
		val fbb = FlatBufferBuilder(32)
		val response = buildFlightListResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.FlightListResponse,
			response,
		)
		fbb.finish(outbound)
		this.api.apiServers.forEach { apiServer ->
			apiServer.apiConnections.forEach { it.send(fbb.dataBuffer()) }
		}
	}
}
