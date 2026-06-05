package dev.slimevr.updater.updater

class Constants {

	companion object {

		const val CDN = "http://127.0.0.1:3000"
		//val CDN = "https://updates.slimevr.io"
		const val CDN_RELEASES = "$CDN/releases"
		const val CDN_VERSIONS = "$CDN/versions"
		const val CDN_CHANNELS = "$CDN/channels"
		const val CDN_DOWNLOAD = "$CDN/download"

		const val LINUXCONFIGLOCATION =
			""
		const val LINUXSTEAMVRDRIVERURL =
			"https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-x64-linux.zip"

		const val LINUXSTEAMVRDRIVERNAME =
			"slimevr-openvr-driver-x64-linux.zip"

		const val LINUXSTEAMVRDRIVERDIRECTORY =
			"slimevr-openvr-driver-x64-linux"

		const val LINUXFEEDERURL =
			"https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-Linux.zip"

		const val LINUXFEEDERNAME =
			"SlimeVR-Feeder-App-Linux.zip"

		const val LINUXFEEDERDIRECTORY =
			"SlimeVR-Feeder-App-Linux"

		const val LINUXSERVERNAME =
			"SlimeVR-amd64.appimage"

		const val WINDOWSSTEAMVRDRIVERURL =
			"https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-win64.zip"

		const val WINDOWSSTEAMVRDRIVERNAME =
			"slimevr-openvr-driver-win64.zip"

		const val WINDOWSSTEAMVRDRIVERDIRECTORY =
			"slimevr"

		const val WINDOWSFEEDERURL =
			"https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-win64.zip"

		const val WINDOWSFEEDERNAME =
			"SlimeVR-Feeder-App-win64.zip"

		const val WINDOWSFEEDERDIRECTORY =
			"SlimeVR-Feeder-App-win64"

		const val WINDOWSSERVERURL =
			"https://github.com/SlimeVR/SlimeVR-Server/releases/latest/download/SlimeVR-win64.zip"

		const val WINDOWSSERVERNAME =
			"slimevr-win64.zip"

		const val WINDOWSSERVERDIRECTORY =
			"slimevr-win64"
	}
}
