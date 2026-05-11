package dev.slimevr.updater

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpMethod
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
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.time.Duration.Companion.seconds

class UpdaterIO(
	private val state: UpdaterState,
) {
	private val client = HttpClient(CIO)

	fun executeShellCommand(vararg command: String): String = try {
		val process = ProcessBuilder(*command)
			.redirectErrorStream(true)
			.start()

		process.inputStream.bufferedReader().readText().also {
			process.waitFor()
		}
	} catch (e: IOException) {
		state.hasError = true
		"Error executing shell command: ${e.message}"
	}

	suspend fun getReleaseFromApi(): List<Release>  {
		val client = HttpClient(CIO) {
			install(ContentNegotiation) {
				json(Json {
					prettyPrint = true
					isLenient = true
					ignoreUnknownKeys = true
				})
			}
		}
		val releases: List<Release> = client.get("http://localhost:3000/releases").body()

		return releases
	}


	suspend fun downloadFile(fileUrl: String, fileName: String, checksum: String = "") {
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
				validateChecksum(fileName, checksum)
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

	private suspend fun unzipWorker(zipFile: ZipFile, zipEntry: ZipEntry, destDir: String) {
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

	fun validateChecksum(file: String, checksum: String): String {
		val data = Files.readAllBytes(Paths.get(file))
		val hash = MessageDigest.getInstance("SHA-256").digest(data)
		val result = BigInteger(1, hash).toString(16).padStart(64, '0')

		if (checksum.isNotEmpty() && result != checksum.lowercase()) {
			throw IOException("Checksum mismatch! Expected $checksum but got $result")
		}
		return result
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
			System.exit(0)
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
