package dev.slimevr.updater

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.time.Duration.Companion.seconds

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

val sendSubProgress: (Float) -> Unit = { progress -> updaterGui.subProgressBar.setProgress(progress.toInt()) }

fun downloadFile(
	fileUrl: String,
	fileName: String,
	onProgress: (Float) -> Unit = sendSubProgress,
) {
	onProgress(0f)
	val client = HttpClient(CIO)
	val outputStream = FileOutputStream(fileName)

	runBlocking {
		client.prepareGet(
			fileUrl,
			block = {
				val timeout = 30.seconds.inWholeMilliseconds
				timeout {
					requestTimeoutMillis = timeout
					connectTimeoutMillis = timeout
					socketTimeoutMillis = timeout
				}
				onDownload { bytesSentTotal, contentLength ->
					val progress = (
						bytesSentTotal.toFloat() /
							(
								(
									(
										contentLength?.toFloat()
											?: 1f
										)
									)
								)
						)
					onProgress(progress * 100)
				}
			},
		).execute { httpResponse ->
			if (httpResponse.status.value in 200..299) {
				val byteReadChannel = httpResponse.bodyAsChannel()
				byteReadChannel.copyTo(outputStream)
			}
		}
	}
	checksum(fileName)
}

// Guard against zip slip
fun resolveSaveFile(destinationPath: File, zipEntry: ZipEntry): File {
	val destFile = File(destinationPath, zipEntry.name)

	val destinationDirPath = destinationPath.getCanonicalPath()
	val destinationFilePath = destFile.getCanonicalPath()

	if (!destinationFilePath.startsWith(destinationDirPath + File.separator)) {
		throw IOException("Entry is outside of the target dir: ${zipEntry.name}")
	}

	return destFile
}

suspend fun unzip(
	file: String,
	destDir: String,
	onProgress: (Float) -> Unit = sendSubProgress,
) {
	val destFolder = File(destDir)
	if (!destFolder.exists()) {
		destFolder.mkdirs()
	}
	onProgress(0f)
	withContext(Dispatchers.IO) {
		val zipFile = ZipFile(file)
		var progress = 0f
		val semaphore = Semaphore(4)
		for (entry in zipFile.entries()) {
			this.launch() {
				semaphore.withPermit {
					unzipWorker(zipFile, entry, destDir)
				}
			}
			onProgress(progress / zipFile.size() * 100)
			progress++
		}
	}
	onProgress(100f)
}

suspend fun unzipWorker(zipFile: ZipFile, zipEntry: ZipEntry, destDir: String) {
	val destFolder = File(destDir)
	val targetFile = File(destFolder, zipEntry.name)
	if (!targetFile.canonicalPath.startsWith(destFolder.canonicalPath)) {
		throw IOException("Entry is outside of the target dir: ${zipEntry.name}")
	}

	withContext(Dispatchers.IO) {
		try {
			if (zipEntry.isDirectory) {
				if (!targetFile.exists() && !targetFile.mkdirs()) {
					throw IOException("Failed to create directory: $targetFile")
				}
			} else {
				targetFile.parentFile?.let { parent ->
					if (!parent.exists() && !parent.mkdirs() && !parent.exists()) {
						throw IOException("Failed to create parent directory: $parent")
					}
				}

				zipFile.getInputStream(zipEntry).use { input ->
					FileOutputStream(targetFile).use { output ->
						input.copyTo(output, bufferSize = 8192)
					}
				}
			}
		} catch (e: Exception) {
			println("Error during unzip of ${zipEntry.name}: ${e.message}")
		}
	}
}

fun deleteFile(file: File) {
	if (file.exists() && file.isFile) {
		file.delete()
	}
}

fun checksum(file: String) {
	val data = Files.readAllBytes(Paths.get(file))
	val hash = MessageDigest.getInstance("SHA-256").digest(data)
	val checksum = BigInteger(1, hash).toString(16)
}

suspend fun shouldUpdate(): Boolean {
	println("Current version $VERSION")
	// We're running from a git branch don't update
	if (VERSION.contains("dirty")) {
		return false
	}
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
		val response: Updater.GHResponse = client.get("https://api.github.com/repos/slimevr/slimevr-server/releases/latest").body()
		client.close()
		// Replace this if versioning ever changes
		val githubVersionArr = response.tag_name.replace("v", "").split(".")
		val localVersionArr = VERSION.replace("v", "").split(".")
		// Cursed?
		return if (githubVersionArr[0] > localVersionArr[0]) {
			true
		} else if (githubVersionArr[0] < localVersionArr[0] && githubVersionArr[1] > localVersionArr[1]) {
			true
		} else if (githubVersionArr[0] < localVersionArr[0] && githubVersionArr[1] < localVersionArr[1] && githubVersionArr[2] > localVersionArr[2]) {
			true
		} else {
			false
		}
	} catch (e: Exception) {
		println("Error getting github release info: ${e.message}")
	}
	return false
}
