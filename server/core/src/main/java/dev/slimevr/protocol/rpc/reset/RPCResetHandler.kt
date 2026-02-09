package dev.slimevr.protocol.rpc.reset

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.reset.ResetListener
import solarxr_protocol.rpc.ClearMountingResetRequest
import solarxr_protocol.rpc.ResetRequest
import solarxr_protocol.rpc.ResetResponse
import solarxr_protocol.rpc.ResetStatus
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import java.util.function.Consumer

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

		if (req.resetType() == ResetType.Yaw) {
			val delay = if (req.hasDelay()) {
				req.delay()
			} else {
				resetsConfig.yawResetDelay
			}
			if (bodyParts.isEmpty()) {
				api.server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, (delay * 1000).toLong())
			} else {
				api.server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, (delay * 1000).toLong(), bodyParts.toList())
			}
		}
		if (req.resetType() == ResetType.Full) {
			val delay = if (req.hasDelay()) {
				req.delay()
			} else {
				resetsConfig.fullResetDelay
			}
			if (bodyParts.isEmpty()) {
				api.server.scheduleResetTrackersFull(RESET_SOURCE_NAME, (delay * 1000).toLong())
			} else {
				api.server.scheduleResetTrackersFull(RESET_SOURCE_NAME, (delay * 1000).toLong(), bodyParts.toList())
			}
		}
		if (req.resetType() == ResetType.Mounting) {
			val delay = if (req.hasDelay()) {
				req.delay()
			} else {
				resetsConfig.mountingResetDelay
			}
			if (bodyParts.isEmpty()) {
				api.server.scheduleResetTrackersMounting(RESET_SOURCE_NAME, (delay * 1000).toLong())
			} else {
				api.server.scheduleResetTrackersMounting(RESET_SOURCE_NAME, (delay * 1000).toLong(), bodyParts.toList())
			}
		}
	}

	fun sendResetStatusResponse(resetType: Int, status: Int, bodyParts: List<Int>? = null, progress: Int = 0, duration: Int = 0) {
		val fbb = FlatBufferBuilder(32)

		val bodyPartsOffset = if (bodyParts != null) ResetResponse.createBodyPartsVector(fbb, bodyParts.map { it.toByte() }.toByteArray()) else 0

		ResetResponse.startResetResponse(fbb)
		ResetResponse.addResetType(fbb, resetType)
		ResetResponse.addStatus(fbb, status)
		if (bodyPartsOffset >= 0) {
			ResetResponse.addBodyParts(fbb, bodyPartsOffset)
		}
		ResetResponse.addProgress(fbb, progress)
		ResetResponse.addDuration(fbb, duration)
		val update = ResetResponse.endResetResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.ResetResponse, update)
		fbb.finish(outbound)

		this.forAllListeners(
			Consumer { conn: GenericConnection ->
				conn.send(fbb.dataBuffer())
			},
		)
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

	override fun onStarted(resetType: Int, bodyParts: List<Int>?, progress: Int, duration: Int) {
		sendResetStatusResponse(resetType, ResetStatus.STARTED, bodyParts, progress, duration)
	}

	override fun onFinished(resetType: Int, bodyParts: List<Int>?, duration: Int) {
		sendResetStatusResponse(resetType, ResetStatus.FINISHED, bodyParts, duration, duration)
	}

	fun forAllListeners(action: Consumer<in GenericConnection?>?) {
		this.api
			.apiServers
			.forEach(
				Consumer { server: ProtocolAPIServer ->
					server
						.apiConnections
						.forEach(action)
				},
			)
	}

	companion object {
		const val RESET_SOURCE_NAME = "WebSocketAPI"
	}
}
