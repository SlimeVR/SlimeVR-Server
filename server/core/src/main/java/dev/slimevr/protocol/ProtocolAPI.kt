package dev.slimevr.protocol

import dev.slimevr.VRServer
import dev.slimevr.protocol.datafeed.DataFeedHandler
import dev.slimevr.protocol.pubsub.PubSubHandler
import dev.slimevr.protocol.rpc.RPCHandler
import solarxr_protocol.MessageBundle
import java.nio.ByteBuffer

class ProtocolAPI(val server: VRServer) {
	val apiServers: MutableList<ProtocolAPIServer> = ArrayList()
	val dataFeedHandler: DataFeedHandler = DataFeedHandler(this)
	val pubSubHandler: PubSubHandler = PubSubHandler(this)
	val rpcHandler: RPCHandler = RPCHandler(this)

	fun onMessage(conn: GenericConnection, message: ByteBuffer) {
		val messageBundle = MessageBundle.getRootAsMessageBundle(message)

		try {
			for (index in 0..<messageBundle.dataFeedMsgsLength) {
				messageBundle.dataFeedMsgs(index)?.let {
					this.dataFeedHandler.onMessage(conn, it)
				}
			}

			for (index in 0..<messageBundle.rpcMsgsLength) {
				messageBundle.rpcMsgs(index)?.let {
					this.rpcHandler.onMessage(conn, it)
				}
			}

			for (index in 0..<messageBundle.pubSubMsgsLength) {
				messageBundle.pubSubMsgs(index)?.let{
					this.pubSubHandler.onMessage(conn, it)
				}
			}
		} catch (e: AssertionError) {
			// Catch flatbuffer errors
			e.printStackTrace()
		}
	}

	fun registerAPIServer(server: ProtocolAPIServer) {
		this.apiServers.add(server)
	}

	fun removeAPIServer(server: ProtocolAPIServer) {
		this.apiServers.remove(server)
	}
}
