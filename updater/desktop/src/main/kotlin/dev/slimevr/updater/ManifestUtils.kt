package dev.slimevr.updater

class ManifestUtils {

	companion object {
		@JvmStatic
		fun listChannels(manifest: ManifestObject) {
			manifest.channels.forEach { channel ->
				println(channel.key)
			}
		}

		@JvmStatic
		fun listVersions(manifest: ManifestObject, channel: String) {
			manifest.channels[channel]?.versions?.forEach { version ->
				println(version)
			}
		}

		@JvmStatic
		fun getRelease(manifest: ManifestObject, channel: String, version: String, platform: String, release: String): Release? {
			val res = manifest.channels[channel]?.versions[version]?.builds[platform]?.get(release)
			return res
		}
	}
}
