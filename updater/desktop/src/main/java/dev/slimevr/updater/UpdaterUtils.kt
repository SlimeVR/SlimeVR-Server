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
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.time.Duration.Companion.minutes

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

val sendProgress: (Float) -> Unit = { progress ->  subProgressBar.value = progress.toInt() }

fun downloadFile(
	fileUrl: String,
	fileName: String,
	onProgress: (Float) -> Unit = sendProgress
) {
	val client = HttpClient(CIO)
	val outputStream = FileOutputStream(fileName)

	runBlocking {
		client.prepareGet(
			fileUrl,
			block = {
				val timeout = 30.minutes.inWholeMilliseconds
				timeout {
					requestTimeoutMillis = timeout
					connectTimeoutMillis = timeout
					socketTimeoutMillis = timeout
				}
				onDownload { bytesSentTotal, contentLength ->
					val progress = (bytesSentTotal.toFloat() / (((contentLength?.toFloat()
						?: 1F))))
					onProgress(progress * 100)
				}
			}).execute { httpResponse ->
				if (httpResponse.status.value in 200..299) {
					val byteReadChannel = httpResponse.bodyAsChannel()
					byteReadChannel.copyTo(outputStream)
				}
		}
	}
}

// Guard against zip slip
fun newFile(destinationPath: File, zipEntry: ZipEntry): File {
	val destFile = File(destinationPath, zipEntry.name)

	val destinationDirPath = destinationPath.getCanonicalPath()
	val destinationFilePath = destFile.getCanonicalPath()

	if (!destinationFilePath.startsWith(destinationDirPath + File.separator)) {
		throw IOException("Entry is outside of the target dir: ${zipEntry.name}")
	}

	return destFile
}

/*
fun unzip(
	file: String,
	destDir: String,
	onProgress: (Float) -> Unit = sendProgress) {
	try {
		val destFile = File(destDir)
		val zipFile = ZipFile(File(file))
		val dataBuffer = ByteArray(1024)
		val zis = ZipInputStream(zipFile.getInputStream())
		var currentEntryCount = 0
		val zipSize = zipFile.size()

		var zipEntry = zis.nextEntry

		while (zipEntry != null) {
			val file = newFile(destFile, zipEntry)
			if (zipEntry.isDirectory) {
				if (!file.isDirectory && !file.mkdirs()) {
					throw IOException("Failed to create directory: $file")
				} else {
					val parent = file.parentFile
					if (!parent.isDirectory && !parent.mkdirs()) {
						throw IOException("Failed to create directory: $parent")
					}
				}
			} else {
				val fileOutputStream = FileOutputStream(file)
				var len = zis.read(dataBuffer, 0, 1024)
				while (len > 0) {
					fileOutputStream.write(dataBuffer, 0, len)
					len = zis.read(dataBuffer, 0, len)
				}
				fileOutputStream.close()
			}
			onProgress(currentEntryCount.toFloat() / zipSize.toFloat() * 100)
			currentEntryCount++
			zipEntry = zis.nextEntry
		}

		zis.closeEntry()
		zis.close()
	} catch (e: Exception) {
		println("Error during unzip: ${e.message}")
	}
}

 */

fun unzip(
	file: String,
	destDir: String,
	onProgress: (Float) -> Unit = sendProgress) {
	try {
		val destFile = File(destDir)
		val zipFile = ZipFile(file)
		val dataBuffer = ByteArray(1024)
		val zipEntries = zipFile.entries()
		val zipSize = zipFile.size()
		var currentEntryCount = 0
		while (zipEntries.hasMoreElements()) {
			val currentEntry = zipEntries.nextElement()
			val inputStream = zipFile.getInputStream(currentEntry)
			val file = newFile(destFile, currentEntry)
			if (currentEntry.isDirectory) {
				if (!file.isDirectory && !file.mkdirs()) {
					throw IOException("Failed to create directory: $file")
				} else {
					val parent = file.parentFile
					if (!parent.isDirectory && !parent.mkdirs()) {
						throw IOException("Failed to create directory: $parent")
					}
				}
			} else {
				val fileOutputStream = FileOutputStream(file)
				var len = inputStream.read(dataBuffer, 0, 1024)
				while (len > 0) {
					fileOutputStream.write(dataBuffer, 0, len)
					len = inputStream.read(dataBuffer, 0, len)
				}
				fileOutputStream.close()
			}
			onProgress(currentEntryCount.toFloat() / zipSize.toFloat() * 100)
			currentEntryCount++
		}
	} catch (e: Exception) {
		println("Error during unzip: ${e.message}")
	}
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
