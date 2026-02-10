package dev.slimevr.desktop.updater

import dev.slimevr.desktop.updater.Updater.GHResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


fun executeShellCommand(vararg command: String): String {
	return try {
		val process = ProcessBuilder(*command)
			.redirectErrorStream(true)
			.start()
		process.inputStream.bufferedReader().readText().also {
			process.waitFor()
		}
	} catch (e: IOException) {
		"Error executing shell command: ${e.message}"
	}
}

//TODO: This function is really really slow, make it faster, also give feedback
fun downloadFile(fileUrl: String, filename: String) {
	println("Downloading $filename from $fileUrl")
	val url = URL(fileUrl)
	try {
		val bufferedInputStream = BufferedInputStream(url.openStream())
		val fileOutputStream = FileOutputStream(filename)
		val dataBuffer = ByteArray(1024)
		var bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)
		while (bytesRead != -1) {
			fileOutputStream.write(dataBuffer, 0, bytesRead)
			bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)
		}
		val inputStream = url.openStream()
		Files.copy(inputStream, Paths.get(filename), StandardCopyOption.REPLACE_EXISTING)
	} catch ( e: IOException) {
		println("Error downloading file, ${e.message}")
	}
}

//Guard against zip slip
fun newFile(destinationPath: File, zipEntry: ZipEntry): File {
	val destFile = File(destinationPath, zipEntry.name)

	val destinationDirPath = destinationPath.getCanonicalPath()
	val destinationFilePath = destFile.getCanonicalPath()

	if (!destinationFilePath.startsWith(destinationDirPath + File.separator)) {
		throw IOException("Entry is outside of the target dir: ${zipEntry.name}")
	}

	return destFile
}

fun unzip(file: String, destDir: String) {
	try {
		val zipFile = File(destDir)
		val dataBuffer = ByteArray(1024)
		val zis = ZipInputStream(FileInputStream(file))
		var zipEntry = zis.nextEntry

		while (zipEntry != null) {
			val file = newFile(zipFile, zipEntry)
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
			zipEntry = zis.nextEntry
		}

		zis.closeEntry()
		zis.close()
	} catch (e: Exception) {
		println("Error during unzip: ${e.message}")
	}
}

suspend fun shouldUpdate(): Boolean {
	println("Current version ${VERSION}")
	//We're running from a git branch don't update
	if (VERSION.contains("dirty")) {
		return false
	}
	val client = HttpClient(CIO) {
		install(ContentNegotiation) {
			json(Json {
				ignoreUnknownKeys = true
			})
		}
	}
	try {
		val response: GHResponse = client.get("https://api.github.com/repos/slimevr/slimevr-server/releases/latest").body()
		client.close()
		//Replace this if versioning ever changes
		val githubVersionArr = response.tag_name.replace("v", "").split(".")
		val localVersionArr = VERSION.replace("v", "").split(".")
		//Cursed?
		return if (githubVersionArr[0] > localVersionArr[0]) {
			true
		} else if (githubVersionArr[0] < localVersionArr[0] && githubVersionArr[1] > localVersionArr[1]) {
			true
		} else if (githubVersionArr[0] < localVersionArr[0] && githubVersionArr[1] < localVersionArr[1] && githubVersionArr[2] > localVersionArr[2]) {
			true
		} else {
			false
		}
	} catch(e: Exception) {
		println("Error getting github release info: ${e.message}")
	}
	return false
}
