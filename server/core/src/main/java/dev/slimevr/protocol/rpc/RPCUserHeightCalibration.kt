package dev.slimevr.protocol.rpc

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.tracking.processor.skeleton.UserHeightCalibrationListener
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.UserHeightRecordingStatusResponse

class RPCUserHeightCalibration(var rpcHandler: RPCHandler, var api: ProtocolAPI) : UserHeightCalibrationListener {
	val userHeightCal = this.api.server.humanPoseManager.skeleton.userHeightCalibration

	init {
		userHeightCal?.addListener(
			this,
		) ?: error(
			"unavailable",
		)

		rpcHandler.registerPacketListener(
			RpcMessage.StartUserHeightCalibration,
			::onStartUserHeightCalibration,
		)
		rpcHandler.registerPacketListener(
			RpcMessage.CancelUserHeightCalibration,
			::onCancelUserHeightCalibration,
		)
	}

	fun onCancelUserHeightCalibration(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		userHeightCal?.clear()
	}

	fun onStartUserHeightCalibration(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		userHeightCal?.start()
	}

	override fun onStatusChange(hmdHeight: Float, status: UByte) {
		val fbb = FlatBufferBuilder(32)

		val res = UserHeightRecordingStatusResponse.createUserHeightRecordingStatusResponse(fbb, hmdHeight, status)

		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.UserHeightRecordingStatusResponse,
			res,
		)
		fbb.finish(outbound)

		api
			.apiServers.forEach { server ->
				server.apiConnections.forEach { conn ->
					conn.send(fbb.dataBuffer())
				}
			}
	}
}
