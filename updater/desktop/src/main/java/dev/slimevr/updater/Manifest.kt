package dev.slimevr.updater

import dev.slimevr.updater.Updater.Companion.CDN
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

class Channels {
/*
	@Serializable
	data class ServerResponse(
		val channels: JsonArray
	)

	suspend fun getChannels(product: String) {
		val client = HttpClient(CIO) {
			install(ContentNegotiation) {
				json(
					Json {
						ignoreUnknownKeys = true
					},
				)
			}
		}
		try {
			val response = client.get("${CDN}/${product}").body()
			client.close()
			println(response)
			val json = Json {
				ignoreUnknownKeys = true
			}
			//val channels = json.decodeFromString<Array<ServerResponse>>(response.channels.toString())
			//println(channels)
		} catch (e: Exception) {
			println("Error fetching release info: ${e.message}")
		}


	}

 */
}
