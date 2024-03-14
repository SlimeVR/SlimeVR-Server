package dev.slimevr.protocol.rpc.setup

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.datafeed.DataFeedBuilder
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.setup.TapSetupListener
import dev.slimevr.tracking.trackers.Tracker
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.TapDetectionSetupNotification

class RPCTapSetupHandler(
	private val rpcHandler: RPCHandler,
	val api: ProtocolAPI,
) : TapSetupListener {
	init {
		this.api.server.tapSetupHandler.addListener(this)
	}

	override fun onStarted(tracker: Tracker) {
		val fbb = FlatBufferBuilder(32)
		val idOffset = DataFeedBuilder.createTrackerId(fbb, tracker)
		val update = TapDetectionSetupNotification.createTapDetectionSetupNotification(fbb, idOffset)
		val outbound =
			rpcHandler.createRPCMessage(fbb, RpcMessage.TapDetectionSetupNotification, update)
		fbb.finish(outbound)

		forAllListeners {
			it.send(
				fbb.dataBuffer(),
			)
		}
	}

	private fun forAllListeners(action: (GenericConnection) -> Unit) {
		api
			.apiServers
			.forEach {
				it
					.apiConnections
					.forEach(action)
			}
	}
}
