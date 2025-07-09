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
		rpcHandler.registerPacketListener(RpcMessage.ToggleFlightListStepRequest, ::onToggleFlightListRequest)
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
		val req = messageHeader.message(ToggleFlightListStepRequest()) as ToggleFlightListStepRequest?
			?: return
		val step = api.server.flightListManager.steps.find { it.id == req.stepId() } ?: error("invalid step id requested")

		api.server.flightListManager.toggleStep(step)

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

	override fun onStepUpdate(step: FlightListStepT) {
		val fbb = FlatBufferBuilder(32)
		val stepOffset = FlightListStep.pack(fbb, step)
		FlightListStepChangeResponse.startFlightListStepChangeResponse(fbb)
		FlightListStepChangeResponse.addStep(fbb, stepOffset)
		val response = FlightListStepChangeResponse.endFlightListStepChangeResponse(fbb)

		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.FlightListStepChangeResponse,
			response,
		)
		fbb.finish(outbound)
		this.api.apiServers.forEach { apiServer ->
			apiServer.apiConnections.forEach { it.send(fbb.dataBuffer()) }
		}
	}
}
