package dev.slimevr.protocol.rpc.reset

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.protocol.rpc.TransactionInfo
import dev.slimevr.reset.ResetListener
import solarxr_protocol.rpc.ClearMountingResetRequest
import solarxr_protocol.rpc.ResetRequest
import solarxr_protocol.rpc.ResetResponse
import solarxr_protocol.rpc.ResetStatus
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import java.nio.ByteBuffer

class RPCResetHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI) : ResetListener {
	val resetsConfig = api.server.configManager.vrConfig.resetsConfig

	init {
		this.api.server.resetHandler.addListener(this)

		rpcHandler.registerPacketListener(RpcMessage.ResetRequest, ::onResetRequest)
		rpcHandler.registerPacketListener(RpcMessage.ClearMountingResetRequest, ::onClearMountingResetRequest)
	}

	fun onResetRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(ResetRequest()) as? ResetRequest ?: return

		// Get the list of bodyparts we want to reset
		// If empty, check in HumanSkeleton will reset all
		val bodyParts = mutableListOf<Int>()
		if (req.bodyPartsLength() > 0) {
			val buffer = req.bodyPartsAsByteBuffer()
			while (buffer.hasRemaining()) {
				bodyParts.add(buffer.get().toInt())
			}
		}

		val tx = messageHeader.txId()?.id()?.let {
			TransactionInfo(it, conn)
		}

		if (req.resetType() == ResetType.Yaw) {
			val delay = if (req.hasDelay()) {
				req.delay()
			} else {
				resetsConfig.yawResetDelay
			}
			if (bodyParts.isEmpty()) {
				api.server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, (delay * 1000).toLong(), tx = tx)
			} else {
				api.server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, (delay * 1000).toLong(), bodyParts.toList(), tx = tx)
			}
		}
		if (req.resetType() == ResetType.Full) {
			val delay = if (req.hasDelay()) {
				req.delay()
			} else {
				resetsConfig.fullResetDelay
			}
			if (bodyParts.isEmpty()) {
				api.server.scheduleResetTrackersFull(RESET_SOURCE_NAME, (delay * 1000).toLong(), tx = tx)
			} else {
				api.server.scheduleResetTrackersFull(RESET_SOURCE_NAME, (delay * 1000).toLong(), bodyParts.toList(), tx = tx)
			}
		}
		if (req.resetType() == ResetType.Mounting) {
			val delay = if (req.hasDelay()) {
				req.delay()
			} else {
				resetsConfig.mountingResetDelay
			}
			if (bodyParts.isEmpty()) {
				api.server.scheduleResetTrackersMounting(RESET_SOURCE_NAME, (delay * 1000).toLong(), tx = tx)
			} else {
				api.server.scheduleResetTrackersMounting(RESET_SOURCE_NAME, (delay * 1000).toLong(), bodyParts.toList(), tx = tx)
			}
		}
	}

	fun sendResetStatusResponse(resetType: Int, status: Int, tx: TransactionInfo? = null, bodyParts: List<Int>? = null, progress: Int = 0, duration: Int = 0) {
		fun buildMessage(txId: Long?): ByteBuffer {
			val fbb = FlatBufferBuilder(32)

			val bodyPartsOffset = bodyParts?.let {
				ResetResponse.createBodyPartsVector(
					fbb,
					bodyParts.map { it.toByte() }.toByteArray(),
				)
			} ?: 0

			ResetResponse.startResetResponse(fbb)
			ResetResponse.addResetType(fbb, resetType)
			ResetResponse.addStatus(fbb, status)

			if (bodyPartsOffset >= 0) {
				ResetResponse.addBodyParts(fbb, bodyPartsOffset)
			}

			ResetResponse.addProgress(fbb, progress)
			ResetResponse.addDuration(fbb, duration)

			val update = ResetResponse.endResetResponse(fbb)
			val outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.ResetResponse, update, txId)
			fbb.finish(outbound)

			return fbb.dataBuffer()
		}

		tx?.let { tx ->
			tx.conn.send(buildMessage(tx.id))
		}

		buildMessage(null).let {
			this.forAllListeners { conn: GenericConnection ->
				if (tx == null || tx.conn != conn) {
					conn.send(it)
				}
			}
		}
	}

	fun onClearMountingResetRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		if (messageHeader
				.message(ClearMountingResetRequest()) !is ClearMountingResetRequest
		) {
			return
		}

		api.server.clearTrackersMounting(RESET_SOURCE_NAME)
	}

	override fun onStarted(resetType: Int, tx: TransactionInfo?, bodyParts: List<Int>?, progress: Int, duration: Int) {
		sendResetStatusResponse(resetType, ResetStatus.STARTED, tx, bodyParts, progress, duration)
	}

	override fun onFinished(resetType: Int, tx: TransactionInfo?, bodyParts: List<Int>?, duration: Int) {
		sendResetStatusResponse(resetType, ResetStatus.FINISHED, tx, bodyParts, duration, duration)
	}

	fun forAllListeners(action: ((GenericConnection) -> Unit)?) {
		if (action == null) return
		this.api
			.apiServers
			.forEach { server: ProtocolAPIServer ->
				server
					.apiConnections
					.forEach(action)
			}
	}

	companion object {
		const val RESET_SOURCE_NAME = "WebSocketAPI"
	}
}
