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
			for (index in 0..<messageBundle.dataFeedMsgsLength()) {
				val header = messageBundle.dataFeedMsgsVector().get(index)
				this.dataFeedHandler.onMessage(conn, header)
			}

			for (index in 0..<messageBundle.rpcMsgsLength()) {
				val header = messageBundle.rpcMsgsVector().get(index)
				this.rpcHandler.onMessage(conn, header)
			}

			for (index in 0..<messageBundle.pubSubMsgsLength()) {
				val header = messageBundle.pubSubMsgsVector().get(index)
				this.pubSubHandler.onMessage(conn, header)
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
