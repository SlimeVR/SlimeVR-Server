package dev.slimevr.updater

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.*
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.time.Duration.Companion.seconds

class UpdaterIO(
	private val state: UpdaterState
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
		"Error executing shell command: ${e.message}"
	}

	fun downloadFile(
		fileUrl: String,
		fileName: String,
	) {
		state.subProgress = 0f

		val outputStream = FileOutputStream(fileName)

		kotlinx.coroutines.runBlocking {
			client.prepareGet(fileUrl) {
				val timeout = 30.seconds.inWholeMilliseconds
				timeout {
					requestTimeoutMillis = timeout
					connectTimeoutMillis = timeout
					socketTimeoutMillis = timeout
				}

				onDownload { sent, total ->
					val progress = sent.toFloat() / (total?.toFloat() ?: 1f)
					state.subProgress = progress
				}
			}.execute { response ->
				if (response.status.value in 200..299) {
					response.bodyAsChannel().copyTo(outputStream)
				}
			}
		}

		checksum(fileName)
		state.subProgress = 1f
	}

	fun unzip(
		file: String,
		destDir: String,
	) {
		val destFolder = File(destDir)
		if (!destFolder.exists()) destFolder.mkdirs()

		state.subProgress = 0f

		val zipFile = ZipFile(file)
		val entries = zipFile.entries().toList()
		val total = entries.size.coerceAtLeast(1)

		kotlinx.coroutines.runBlocking {
			withContext(Dispatchers.IO) {
				val semaphore = Semaphore(4)

				var index = 0

				for (entry in entries) {
					launch {
						semaphore.withPermit {
							unzipWorker(zipFile, entry, destDir)
						}
					}

					index++
					state.subProgress = index.toFloat() / total
				}
			}
		}

		state.subProgress = 1f
	}

	private suspend fun unzipWorker(zipFile: ZipFile, zipEntry: ZipEntry, destDir: String) {
		val destFolder = File(destDir)
		val targetFile = File(destFolder, zipEntry.name)

		if (!targetFile.canonicalPath.startsWith(destFolder.canonicalPath)) {
			throw IOException("Entry is outside target dir: ${zipEntry.name}")
		}

		withContext(Dispatchers.IO) {
			try {
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
			} catch (e: Exception) {
				state.statusText = "Unzip error: ${zipEntry.name}"
			}
		}
	}

	fun deleteFile(file: File) {
		if (file.exists() && file.isFile) file.delete()
	}

	fun checksum(file: String): String {
		val data = Files.readAllBytes(Paths.get(file))
		val hash = MessageDigest.getInstance("SHA-256").digest(data)
		return BigInteger(1, hash).toString(16)
	}

	fun shouldUpdate(latest: String): Boolean {
		if (VERSION.contains("dirty")) return false

		val local = VERSION.replace("v", "").split(".")
		val remote = latest.replace("v", "").split(".")

		return remote.zip(local).any { (r, l) ->
			r.toIntOrNull() ?: 0 > (l.toIntOrNull() ?: 0)
		}
	}
}
