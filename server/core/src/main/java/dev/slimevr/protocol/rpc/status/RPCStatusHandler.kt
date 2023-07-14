package dev.slimevr.protocol.rpc.status

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.status.StatusListener
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.StatusDataUnion
import solarxr_protocol.rpc.StatusMessage
import solarxr_protocol.rpc.StatusSystemFixed
import solarxr_protocol.rpc.StatusSystemUpdate

class RPCStatusHandler(
	private val rpcHandler: RPCHandler,
	val api: ProtocolAPI,
) : StatusListener {

	init {
		api.server.statusSystem.addListener(this)
	}

	override fun onStatusChanged(
		id: UInt,
		message: StatusDataUnion,
		prioritized: Boolean,
	) {
		val fbb = FlatBufferBuilder(STATUS_EXPECTED_SIZE)

		val messageOffset = StatusDataUnion.pack(fbb, message)

		StatusMessage.startStatusMessage(fbb)
		StatusMessage.addData(fbb, messageOffset)
		StatusMessage.addId(fbb, id.toLong())
		StatusMessage.addDataType(fbb, message.type)
		StatusMessage.addPrioritized(fbb, prioritized)
		val statusOffset = StatusMessage.endStatusMessage(fbb)

		StatusSystemUpdate.startStatusSystemUpdate(fbb)
		StatusSystemUpdate.addNewStatus(fbb, statusOffset)
		val update = StatusSystemUpdate.endStatusSystemUpdate(fbb)

		val outbound = this.rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.StatusSystemUpdate,
			update
		)
		fbb.finish(outbound)

		this.api.apiServers.forEach { apiServer ->
			apiServer.apiConnections.forEach { it.send(fbb.dataBuffer()) }
		}
	}

	override fun onStatusRemoved(id: UInt) {
		val fbb = FlatBufferBuilder(4)

		StatusSystemFixed.startStatusSystemFixed(fbb)
		StatusSystemFixed.addFixedStatusId(fbb, id.toLong())
		val update = StatusSystemFixed.endStatusSystemFixed(fbb)

		val outbound = this.rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.StatusSystemFixed,
			update
		)
		fbb.finish(outbound)

		this.api.apiServers.forEach { apiServer ->
			apiServer.apiConnections.forEach { it.send(fbb.dataBuffer()) }
		}
	}

	companion object {
		const val STATUS_EXPECTED_SIZE = 32
	}
}
