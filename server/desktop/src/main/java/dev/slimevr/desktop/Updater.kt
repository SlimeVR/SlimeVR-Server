package dev.slimevr.desktop


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



class Updater {

	val os = System.getProperty("os.name").lowercase()

	fun runUpdater() {

		when (os) {
			"linux" -> {
				println("Running linux updater")
				updateLinux()
			}
			"windows" -> {
				println("Running windows updater")
				updateWindows()
			}
			"macos" -> {
				println("I dunno")
			}
			else -> {
				println("guess I'll die")
			}
		}
	}

	fun executeShellCommand(command: String): String {
		return try {
			val process = ProcessBuilder(*command.split(" ").toTypedArray())
				.redirectErrorStream(true)
				.start()
			process.inputStream.bufferedReader().readText().also {
				process.waitFor()
			}
		} catch (e: IOException) {
			"Error executing shell command: ${e.message}"
		}
	}

	fun downloadFile(url: URL, filename: String) {
		println("Downloading $filename from $url")
		try {
			val bufferedInputStream = BufferedInputStream(url.openStream())
			val fileOutputStream = FileOutputStream(WINDOWSSTEAMVRDRIVERNAME)
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

	fun updateWindows() {
		//First check if everything is already installed. Install it if it isn't
		checkForInstalledUsbDriverWindows()
	}

	fun updateLinux() {
		updateLinuxSteamVRDriver()
	}


	fun checkForInstalledUsbDriverWindows() {
		val installedDriversList = executeShellCommand("powershell.exe  pnputil /enum-drivers")
		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")
		val path = Paths.get("").toAbsolutePath().toString()


		if (ch341ser && ch343ser && silabser) {
			println("drivers already installed!")
			return
		}

		println("Cannot find one of the drivers, installing drivers")
		val driverinstallOutput = executeShellCommand("$path\\installusbdrivers.bat")
		println(driverinstallOutput)
	}


	fun checkForUpdates() {

	}

	fun updateWindowsServer() {

	}

	fun updateWindowsUSBDrivers() {

	}

	fun updateWindowsSteamVRDriver() {
		val steamVRLocation = executeShellCommand("(Get-ItemProperty \"HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820\").InstallLocation")
		if (steamVRLocation == "") {
			println("SteamVR not installed, cannot install SlimeVR Steam driver.")
			return;
		}
		val vrPathRegContents = executeShellCommand("${steamVRLocation}\\Vrpathreg.exe")
		val isDriverRegistered = vrPathRegContents.contains("WINDOWSSTEAMVRDRIVERDIRECTORY")
		if (isDriverRegistered) {
			println("steamVR driver is already registered. Skipping...")
			return
		}
		println("Installing SteamVR Driver")
		println("Downloading SteamVR driver")
		downloadFile(URL(WINDOWSSTEAMVRDRIVERURL), WINDOWSSTEAMVRDRIVERNAME)
		unzip(WINDOWSSTEAMVRDRIVERNAME, WINDOWSSTEAMVRDRIVERDIRECTORY)
		println("Driver downloaded")
		println("Registering driver with steamvr")
		executeShellCommand(
			"${steamVRLocation}\\Vrpathreg.exe adddriver ${
				Paths.get(
					""
				).toAbsolutePath()
			}/${WINDOWSSTEAMVRDRIVERDIRECTORY}"
		)
	}

	fun updateLinuxServer() {

	}

	fun updateUdev() {

	}

	fun updateLinuxSteamVRDriver() {
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("LINUXSTEAMVRDRIVERDIRECTORY")
		if (!isDriverRegistered) {
			println("Downloading driver")
			downloadFile(URL(LINUXSTEAMVRDRIVERURL), LINUXSTEAMVRDRIVERNAME)
			unzip(LINUXSTEAMVRDRIVERNAME, LINUXSTEAMVRDRIVERDIRECTORY)
			println("Driver downloaded")
			println("Registering driver with steamvr")
			executeShellCommand(
				"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh adddriver ${
					Paths.get(
						""
					).toAbsolutePath()
				}/${LINUXSTEAMVRDRIVERDIRECTORY}"
			)
		} else {
			println("steamVR driver is already registered. Skipping...")
		}
	}

	companion object {
		//Windows URL's
		private const val WINDOWSSTEAMVRDRIVERURL = "https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-win64.zip"
		private const val WINDOWSSTEAMVRDRIVERNAME = "slimevr-openvr-driver-win64.zip"
		private const val WINDOWSSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-win64"
		private const val WINDOWSFEEDERURL = "https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-win64.zip"
		private const val WINDOWSFEEDERNAME = "SlimeVR-Feeder-App-win64.zip"

		//Linux URL's
		private const val LINUXSTEAMVRDRIVERURL = "https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-x64-linux.zip"
		private const val LINUXSTEAMVRDRIVERNAME = "slimevr-openvr-driver-x64-linux.zip"
		private const val LINUXSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-x64-linux"
		private const val LINUXFEEDERURL = "https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-linux64.zip"
		private const val LINUXFEEDERNAME = "SlimeVR-Feeder-App-linux64.zip"
		private const val LINUXFEEDERDIRECTORY = "SlimeVR-Feeder-App-linux64"

		//MacOS URL's
	}
}
