package dev.slimevr.desktop

import dev.slimevr.logging.AppLogger
import dev.slimevr.tryOpenUri
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

private const val SERVER_URL = "http://127.0.0.1:27062"
private const val REFERER = "$SERVER_URL/dashboard/index.html"
private val jsonIgnoreUnknownKeys = Json { ignoreUnknownKeys = true }

suspend fun getSteamVRDriversList(client: HttpClient): List<Driver> {
	val resp = client.request("$SERVER_URL/drivers/list.json") {
		header("Referer", REFERER)
	}

	if (!resp.status.isSuccess()) {
		throw RuntimeException("Failed to connect to SteamVR web server (status ${resp.status})")
	}

	val body: String = resp.body()

	val driverList: DriverListResponse = try {
		jsonIgnoreUnknownKeys.decodeFromString(body)
	} catch (e: Exception) {
		throw RuntimeException("Failed to decode SteamVR drivers list", e)
	}

	if (driverList.jsonId != "vr_driver_list") {
		throw RuntimeException("Failed to decode SteamVR drivers list", RuntimeException("SteamVR driver list response had wrong jsonId (${driverList.jsonId})"))
	}

	return driverList.drivers
}

suspend fun unblockSteamVRDriver(client: HttpClient, driver: String) {
	val unblockReq = client.get("$SERVER_URL/drivers/unblock") {
		method = HttpMethod.Post
		header("Referer", REFERER)
		setBody("""{"driver":"$driver"}""")
	}
	if (!unblockReq.status.isSuccess()) {
		throw RuntimeException("Failed to unblock SteamVR driver \"$driver\": got HTTP status code ${unblockReq.status}")
	}

	val enableReq = client.get("$SERVER_URL/drivers/setenable") {
		method = HttpMethod.Post
		header("Referer", REFERER)
		setBody("""{"driver":"$driver","enable":true}""")
	}
	if (!enableReq.status.isSuccess()) {
		throw RuntimeException("Failed to enable SteamVR driver \"$driver\": got HTTP status code ${enableReq.status}")
	}

	delay(500)

	tryOpenUri("vrmonitor://restartsystem")
}
