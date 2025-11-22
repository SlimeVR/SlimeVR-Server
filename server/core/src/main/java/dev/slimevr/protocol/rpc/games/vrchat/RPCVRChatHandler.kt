package dev.slimevr.protocol.rpc.games.vrchat

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.games.vrchat.VRCConfigListener
import dev.slimevr.games.vrchat.VRCConfigRecommendedValues
import dev.slimevr.games.vrchat.VRCConfigValidity
import dev.slimevr.games.vrchat.VRCConfigValues
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import solarxr_protocol.rpc.*

class RPCVRChatHandler(
	private val rpcHandler: RPCHandler,
	var api: ProtocolAPI,
) : VRCConfigListener {

	init {
		api.server.vrcConfigManager.addListener(this)

		rpcHandler.registerPacketListener(RpcMessage.VRCConfigStateRequest, ::onConfigStateRequest)
		rpcHandler.registerPacketListener(RpcMessage.VRCConfigSettingToggleMute, ::onToggleMuteRequest)
	}

	private fun onConfigStateRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val fbb = FlatBufferBuilder(32)

		val configManager = api.server.vrcConfigManager
		val values = configManager.currentValues
		val recommended = configManager.recommendedValues()
		// FUCKING KOTLIN BRING ME BACK MY FUCKING TERNARY OPERATORS!!!!!!!!!!!!!!!!! - With love <3 Futura
		val validity = if (values !== null) configManager.checkValidity(values, recommended) else null

		val response = buildVRCConfigStateResponse(
			fbb,
			isSupported = api.server.vrcConfigManager.isSupported,
			validity = validity,
			values = values,
			recommended = api.server.vrcConfigManager.recommendedValues(),
			muted = api.server.configManager.vrConfig.vrcConfig.mutedWarnings,
		)

		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.VRCConfigStateChangeResponse,
			response,
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	private fun onToggleMuteRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(VRCConfigSettingToggleMute()) as VRCConfigSettingToggleMute?
			?: return
		api.server.vrcConfigManager.toggleMuteWarning(req.key())
	}

	override fun onChange(validity: VRCConfigValidity, values: VRCConfigValues, recommended: VRCConfigRecommendedValues, muted: List<String>) {
		val fbb = FlatBufferBuilder(32)

		val response = buildVRCConfigStateResponse(
			fbb,
			isSupported = api.server.vrcConfigManager.isSupported,
			validity = validity,
			values = values,
			recommended = recommended,
			muted,
		)

		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.VRCConfigStateChangeResponse,
			response,
		)
		fbb.finish(outbound)

		this.api.apiServers.forEach { apiServer ->
			apiServer.apiConnections.forEach { it.send(fbb.dataBuffer()) }
		}
	}
}
