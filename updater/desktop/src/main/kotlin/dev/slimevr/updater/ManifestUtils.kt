package dev.slimevr.updater

class ManifestUtils {

	companion object {
		@JvmStatic
		fun getChannels(manifest: ManifestObject): List<String> {
			val channels = mutableListOf<String>()
			manifest.channels.forEach { channel ->
				channels.add(channel.key)
			}
			return channels
		}

		@JvmStatic
		fun getVersionTags(manifest: ManifestObject, channel: String?): List<String> {
			val versionTags = mutableListOf<String>()
			manifest.channels[channel]?.versions?.forEach { version ->
				versionTags.add(version.key)
			}
			return versionTags
		}

		@JvmStatic
		fun getPlatforms(manifest: ManifestObject, channel: String, versionTag: String): List<String> {
			val platforms = mutableListOf<String>()
			manifest.channels[channel]?.versions[versionTag]?.builds?.forEach { build ->
				platforms.add(build.key)
			}
			return platforms
		}

		@JvmStatic
		fun getVersionTag(manifest: ManifestObject, channel: String, version: String, platform: String, architecture: String): Release? {
			val res = manifest.channels[channel]?.versions[version]?.builds[platform]?.get(architecture)
			return res
		}

		@JvmStatic
		fun getRelease(manifest: ManifestObject, channel: String, version: String, platform: String, architecture: String): Release? {
			val res = manifest.channels[channel]?.versions[version]?.builds[platform]?.get(architecture)
			return res
		}

		@JvmStatic
		fun getCurrentVersionTag(manifest: ManifestObject, platform: String, architecture: String): String? {
			val currentVersionTag = manifest.channels["stable"]?.currentVersion
			return currentVersionTag
		}

		@JvmStatic
		fun getCurrentVersion(manifest: ManifestObject, platform: String, architecture: String): Release? {
			println(platform)
			println(architecture)
			val currentVersionTag = manifest.channels["stable"]?.currentVersion
			val currentVersion = manifest.channels["stable"]?.versions[currentVersionTag]?.builds[platform]?.get(architecture)
			return currentVersion
		}
	}
}
