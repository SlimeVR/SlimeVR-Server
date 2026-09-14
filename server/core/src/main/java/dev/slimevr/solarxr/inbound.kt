package dev.slimevr.solarxr

import solarxr_protocol.MessageBundle
import solarxr_protocol.connection.ConnectionError
import solarxr_protocol.connection.InitializationRequiredError

suspend fun onSolarXRMessage(message: MessageBundle, context: SolarXRBridge) {
	val wasReady = context.isReady
	message.connectionMsgs?.forEach { header -> onConnectionMessage(header.message, context) }
	if (!wasReady) {
		if (message.dataFeedMsgs != null || message.rpcMsgs != null || message.driverMsgs != null) {
			context.sendConnectionMessage(ConnectionError(message = "Configuration has not completed", data = InitializationRequiredError()))
		}
		return
	}
	message.dataFeedMsgs?.forEach {
		val msg = it.message ?: return
		context.dataFeedDispatcher.emit(msg)
	}

	message.rpcMsgs?.forEach {
		val msg = it.message ?: return
		val replyTo = it.replyTo
		if (replyTo != 0u) {
			context.rpcRequests.tryResolve(replyTo, msg)
			return@forEach
		}
		context.rpcReplies.record(msg, it.txId)
		context.rpcDispatcher.emit(msg)
	}

	message.driverMsgs?.forEach {
		val msg = it.message ?: return
		val replyTo = it.replyTo
		if (replyTo != 0u) {
			context.driverRequests.tryResolve(replyTo, msg)
			return@forEach
		}
		context.driverReplies.record(msg, it.txId)
		context.driverDispatcher.emit(msg)
	}
}
