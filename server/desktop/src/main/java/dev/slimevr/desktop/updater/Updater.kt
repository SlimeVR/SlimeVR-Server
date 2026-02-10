package dev.slimevr.desktop.updater

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*


class Updater {

	val os = System.getProperty("os.name").lowercase()

	suspend fun runUpdater() {

		val latestVersion: Int = queryLatestVersion()
		val currentVersion = VERSION


		//Compare versions

		if (os.contains("linux")) {
			println("Running linux updater")
			val linuxUpdater = Linux()
			linuxUpdater.updateLinux()
		}
		else if (os.contains("windows")) {
			println("Running windows updater")
			val windowsUpdater = Windows()
			windowsUpdater.updateWindows()

		}
		else if (os.contains("darwin")) {
			println("I dunno")
		}
		else {
			println("guess I'll die")
		}
		println("Done Updating")
	}



	suspend fun queryLatestVersion(): Int {
		val client = HttpClient(CIO)
		val response: HttpResponse = client.get("https://api.github.com/repos/slimevr/slimevr-server/releases/latest")
		println(response)
		client.close()
		return 0
	}



}
