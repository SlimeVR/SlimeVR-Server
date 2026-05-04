package dev.slimevr.steamvr

import dev.slimevr.VRServer
import io.eiren.util.logging.LogManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class DriverManifest(
	val directory: String,
	@SerialName("hmd_presence")
	val hmdPresence: List<String>?,
	val name: String,
)

@Serializable
data class Driver(
	@SerialName("always_activate")
	val alwaysActivate: Boolean,
	@SerialName("blocked_by_safe_mode")
	val blockedBySafeMode: Boolean,
	val enabled: Boolean,
	@SerialName("enabled_by_default")
	val enabledByDefault: Boolean,
	val id: Int,
	@SerialName("load_priority")
	val loadPriority: Int,
	val manifest: DriverManifest,
	@SerialName("on_safemode_whitelist")
	val onSafeModeWhitelist: Boolean,
	@SerialName("show_enable_in_settings")
	val showEnableInSettings: Boolean,
)

@Serializable
private data class DriverListResponse(
	val drivers: List<Driver>,
	@SerialName("jsonid")
	val jsonId: String,
)

class SteamVRUtils {
	companion object {
		const val SERVER_URL = "http://127.0.0.1:27062"
		const val REFERER = "$SERVER_URL/dashboard/index.html"
		private val jsonIgnoreUnknownKeys = Json { ignoreUnknownKeys = true }

		suspend fun getDriversList(client: HttpClient): List<Driver>? {
			val resp = try {
				client.request("$SERVER_URL/drivers/list.json") {
					header("Referer", REFERER)
				}
			} catch (_: Exception) {
				return null
			}

			if (!resp.status.isSuccess()) {
				LogManager.warning("[SteamVRUtils] Failed to connect to SteamVR web server (status ${resp.status})")
				return null
			}

			val body: String = try {
				resp.body()
			} catch (_: Exception) {
				return null
			}

			val driverList: DriverListResponse = try {
				jsonIgnoreUnknownKeys.decodeFromString(body)
			} catch (e: Exception) {
				LogManager.warning("[SteamVRUtils] Failed to decode SteamVR drivers list", e)
				return null
			}

			if (driverList.jsonId != "vr_driver_list") {
				LogManager.severe("[SteamVRUtils] SteamVR driver list response had wrong jsonId (${driverList.jsonId})")
				return null
			}

			return driverList.drivers
		}

		suspend fun unblockDriver(client: HttpClient, driver: String) {
			val unblockReq = client.get("$SERVER_URL/drivers/unblock") {
				method = HttpMethod.Post
				header("Referer", REFERER)
				setBody("""{"driver":"$driver"}""")
			}
			if (!unblockReq.status.isSuccess()) {
				LogManager.severe("[SteamVRUtils] Failed to unblock driver: ${unblockReq.status}")
			}

			val enableReq = client.get("$SERVER_URL/drivers/setenable") {
				method = HttpMethod.Post
				header("Referer", REFERER)
				setBody("""{"driver":"$driver","enable":true}""")
			}
			if (!enableReq.status.isSuccess()) {
				LogManager.severe("[SteamVRUtils] Failed to enable driver: ${enableReq.status}")
			}

			delay(500.milliseconds)

			VRServer.instance.tryOpenUri("vrmonitor://restartsystem")
		}
	}
}
