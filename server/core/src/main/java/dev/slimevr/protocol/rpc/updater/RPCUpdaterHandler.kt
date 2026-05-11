package dev.slimevr.protocol.rpc.updater

import Manifest
import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import io.eiren.util.logging.LogManager
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.UpdaterChannel
import solarxr_protocol.rpc.UpdaterVersion
import solarxr_protocol.rpc.UpdatesResponse
import solarxr_protocol.rpc.UpdatesResponse.createUpdatesResponse
import java.io.File
import java.net.URL

class RPCUpdaterHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI) {
	init {
		rpcHandler.registerPacketListener(RpcMessage.UpdatesRequest, ::onGetVersionsListRequest)
	}

	// TODO Make finding manifest more robust
	fun onGetVersionsListRequest(conn: GenericConnection, messageHeader: RpcMessageHeader?) {
		try {
			val jsonText = URL("http://localhost:3000/manifest").readText()
			val file = File("update-manifest.json")
			file.writeText(jsonText)
		} catch (e: Error) {
			LogManager.warning("Error in downloading manifest: $e")
		}
		val manifest = Manifest("update-manifest.json").getManifest()
		val fbb = FlatBufferBuilder(1024)

		val channelOffsets = IntArray(manifest.channels.size)
		var channelIdx = 0

		for ((channelName, channelData) in manifest.channels) {
			val channelNameOffset = fbb.createString(channelName)

			val versionsMap = channelData.versions
			val versionTableOffsets = IntArray(versionsMap.size)
			var vIdx = 0

			for ((vName, vData) in versionsMap) {
				val vNameStr = fbb.createString(vName)
				val vLogStr = fbb.createString(vData.releaseNotes)

				UpdaterVersion.startUpdaterVersion(fbb)
				UpdaterVersion.addVersion(fbb, vNameStr)
				UpdaterVersion.addChangeLog(fbb, vLogStr)
				versionTableOffsets[vIdx++] = UpdaterVersion.endUpdaterVersion(fbb)
			}

			val versionsVector = UpdaterChannel.createVersionsVector(fbb, versionTableOffsets)

			UpdaterChannel.startUpdaterChannel(fbb)
			UpdaterChannel.addChannel(fbb, channelNameOffset)
			UpdaterChannel.addVersions(fbb, versionsVector)
			channelOffsets[channelIdx++] = UpdaterChannel.endUpdaterChannel(fbb)
		}

		val channelsVector = UpdatesResponse.createChannelsVector(fbb, channelOffsets)

		UpdatesResponse.startUpdatesResponse(fbb)
		UpdatesResponse.addChannels(fbb, channelsVector)
		val responseOffset = UpdatesResponse.endUpdatesResponse(fbb)

		val outboundOffset = this.rpcHandler.createRPCMessage(
			fbb,
			 RpcMessage.UpdatesResponse,
			responseOffset
		)

		fbb.finish(outboundOffset)

		val data = fbb.dataBuffer()
		conn.send(data)
	}
}
