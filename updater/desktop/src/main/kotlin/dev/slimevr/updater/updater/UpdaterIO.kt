package dev.slimevr.updater.updater

import dev.slimevr.updater.utils.TerminalUtil
import dev.slimevr.updater.gui.UpdaterState
import dev.slimevr.updater.updater.Constants.Companion.CDN_CHANNELS
import dev.slimevr.updater.updater.Constants.Companion.CDN_RELEASES
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.seconds

class UpdaterIO(
	private val state: UpdaterState,
) {
	private val client = HttpClient(CIO)

	fun executeShellCommand(vararg command: String): Pair<Int, String>? = try {
		val process = ProcessBuilder(*command)
			.redirectErrorStream(true)
			.start()

		process.waitFor()
		Pair(process.exitValue(), process.inputStream.bufferedReader().readText())
	} catch (e: IOException) {
		TerminalUtil.warn("Error executing shell command $e")
		null
	}

	fun shouldInstallDriver(exitCode: Int): Boolean = exitCode == 1

	fun getDriverInstallSkipReason(exitCode: Int): String = when (exitCode) {
		0 -> "driver already installed"
		2 -> "driver installed more than once"
		-1 -> "SteamVR is misconfigured"
		else -> "unknown reason ($exitCode)"
	}

	fun backupConfig(versionTag: String, configDir: String, vrConfig: String) {
		TerminalUtil.info("Backing up Config")
		try {
			val targetDir =
				File(Paths.get(configDir, versionTag).toAbsolutePath().toString())
			targetDir.mkdirs()
			val config = File(vrConfig)
			val destination = "$targetDir/vrconfig.yml"
			config.copyTo(File(destination), true)
			TerminalUtil.success("Config backed up to $destination")
		} catch (e: IOException) {
			state.hasError = true
			state.errorText = "Error backing up config"
			TerminalUtil.error("Error backing up config")
		}
	}

	fun restoreConfig(versionTag: String, configDir: String, vrConfig: String) {
		try {
			val sourceDir =
				File(Paths.get(configDir, versionTag).toAbsolutePath().toString())
			if (!sourceDir.exists()) return
			val config = File("$sourceDir/vrconfig.yml")
			val destination = "$configDir/vrconfig.yml"
			config.copyTo(File(destination), true)
			TerminalUtil.success("Config restored up to $destination")
		} catch (e: IOException) {
			state.hasError = true
			state.errorText = "Error restoring config"
			TerminalUtil.error("Error restoring config")
		}
	}

	suspend fun getReleases(): List<Release>  {
		try {
			val client = HttpClient(CIO) {
				install(ContentNegotiation) {
					json(Json {
						prettyPrint = true
						isLenient = true
						ignoreUnknownKeys = true
					})
				}
			}
			TerminalUtil.info(CDN_RELEASES)
			val releases: List<Release> = client.get(CDN_RELEASES).body()

			return releases
		} catch (e: Exception) {
			state.hasError = true
			TerminalUtil.error("Error retrieving releases")
			return emptyList()
		}
	}

	suspend fun getChannels(): List<Channel>  {
		try {
			val client = HttpClient(CIO) {
				install(ContentNegotiation) {
					json(Json {
						prettyPrint = true
						isLenient = true
						ignoreUnknownKeys = true
					})
				}
			}
			TerminalUtil.info(CDN_RELEASES)
			val channels: List<Channel> = client.get(CDN_CHANNELS).body()

			return channels
		} catch (e: Exception) {
			state.hasError = true
			TerminalUtil.error("Error retrieving releases")
			return emptyList()
		}
	}

	suspend fun downloadFile(fileUrl: String, fileName: String, checksum: String) {
		state.subProgress = 0f
		val targetFile = File(fileName)

		try {
			client.prepareGet(fileUrl) {
				val timeoutVal = 30.seconds.inWholeMilliseconds
				timeout {
					requestTimeoutMillis = timeoutVal
					connectTimeoutMillis = timeoutVal
					socketTimeoutMillis = timeoutVal
				}

				onDownload { sent, total ->
					val totalBytes = total ?: 1L
					val progress = sent.toFloat() / (totalBytes.toFloat())
					state.subProgress = progress
				}
			}.execute { response ->
				if (response.status.isSuccess()) {
					val channel: ByteReadChannel = response.bodyAsChannel()

					targetFile.outputStream().use { output ->
						channel.copyTo(output)
					}
				} else {
					throw IOException("Server returned HTTP ${response.status.value}")
				}
			}

			if (checksum.isNotEmpty()) {
				val match = validateChecksum(fileName, checksum)

				if (!match) {
					TerminalUtil.warn("Could not verify integrity of downloaded file, aborting!")
					state.hasError = true
					state.statusText = "Error downloading $fileName"
				}
			}

			state.subProgress = 1f
		} catch (e: Exception) {
			state.hasError = true
			state.statusText = "Error downloading $fileName"
			if (targetFile.exists()) targetFile.delete()
		}
	}

	fun unzip(file: String) {
		val zipFileObject = File(file)
		val destDir = zipFileObject.parent ?: "."

		val destFolder = File(destDir)
		if (!destFolder.exists()) destFolder.mkdirs()

		state.subProgress = 0f

		try {
			ZipFile(zipFileObject).use { zipFile ->
				val entries = zipFile.entries().toList()
				val total = entries.size.coerceAtLeast(1)
				val completedCount = AtomicInteger(0)

				runBlocking {
					coroutineScope {
						val semaphore = Semaphore(4)

						for (entry in entries) {
							launch(Dispatchers.IO) {
								semaphore.withPermit {
									try {
										unzipWorker(zipFile, entry, destDir)
									} finally {
										val current = completedCount.incrementAndGet()
										state.subProgress = current.toFloat() / total
									}
								}
							}
						}
					}
				}
			}
			state.subProgress = 1f
		} catch (e: Exception) {
			state.hasError = true
			state.statusText = "Unzip error: ${e.message}"
		}
	}

	private fun unzipWorker(zipFile: ZipFile, zipEntry: ZipEntry, destDir: String) {
		val destFolder = File(destDir)
		val targetFile = File(destFolder, zipEntry.name)

		if (!targetFile.canonicalPath.startsWith(destFolder.canonicalPath)) {
			throw IOException("Entry is outside target dir: ${zipEntry.name}")
		}

		if (zipEntry.isDirectory) {
			targetFile.mkdirs()
		} else {
			targetFile.parentFile?.mkdirs()

			zipFile.getInputStream(zipEntry).use { input ->
				FileOutputStream(targetFile).use { output ->
					input.copyTo(output, 8192)
				}
			}
		}
	}

	fun deleteFile(file: File) {
		if (file.exists() && file.isFile) file.delete()
	}

	fun validateChecksum(filePath: String, expectedChecksum: String): Boolean {
		TerminalUtil.info("Checking checksum: $filePath")

		val file = File(filePath)
		if (!file.exists()) {
			TerminalUtil.info("File not found for checksum validation: $filePath")
			return false
		}

		val digest = MessageDigest.getInstance("SHA-256")
		try {
			file.inputStream().use { inputStream ->
				val buffer = ByteArray(8192)
				var bytesRead: Int
				while (inputStream.read(buffer).also { bytesRead = it } != -1) {
					digest.update(buffer, 0, bytesRead)
				}
			}
		} catch (e: Exception) {
			TerminalUtil.info("Failed to read file during checksum calculation: ${e.message}")
			return false
		}

		val hashBytes = digest.digest()
		val calculatedHash = hashBytes.joinToString("") { "%02x".format(it) }
		TerminalUtil.info("Calculated checksum: $calculatedHash")

		val cleanedExpected = expectedChecksum
			.trim()
			.lowercase()
			.removePrefix("sha256:")

		if (cleanedExpected.isEmpty() || cleanedExpected == "n/a") {
			TerminalUtil.info("No valid expected checksum provided. Skipping validation match.")
			return true
		}

		val matches = calculatedHash == cleanedExpected
		if (!matches) {
			TerminalUtil.info("Checksum mismatch! Expected $expectedChecksum but got $calculatedHash")
		} else {
			TerminalUtil.info("Checksum matches successfully.")
		}

		return matches
	}

	fun restartApplication() {
		try {
			val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
			val currentJar = File(UpdaterIO::class.java.protectionDomain.codeSource.location.toURI())

			val pb = if (!currentJar.name.endsWith(".jar")) {
				ProcessBuilder(System.getProperty("eclipse.launcher") ?: System.getProperty("sun.java.command"))
			} else {
				ProcessBuilder(javaBin, "-jar", currentJar.path)
			}

			pb.start()
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			exitProcess(0)
		}
	}

	fun shouldUpdate(latest: String, currentVersion: String): Boolean {
		if (currentVersion.contains("dirty")) return false

		val local = currentVersion.replace("v", "").split(".")
		val remote = latest.replace("v", "").split(".")

		return remote.zip(local).any { (r, l) ->
			(r.toIntOrNull() ?: 0) > (l.toIntOrNull() ?: 0)
		}
	}
}
