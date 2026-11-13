package dev.slimevr.config

class MemoryStorage : ConfigStorage {
	val files = mutableMapOf<String, String>()
	override suspend fun read(path: String): String? = files[path]
	override suspend fun write(path: String, content: String, overwrite: Boolean): Boolean {
		if (!overwrite && path in files) return false
		files[path] = content
		return true
	}
	override suspend fun backup(path: String) = Unit
	override suspend fun exists(path: String): Boolean = path in files
	override suspend fun list(dir: String): List<String> = files.keys.filter { it.substringBeforeLast('/', "") == dir }.map { it.substringAfterLast('/') }
	override suspend fun delete(path: String) {
		files.remove(path)
	}
	override suspend fun move(from: String, to: String) {
		check(to !in files) { "$to exists" }
		files[to] = files.remove(from) ?: error("No file $from")
	}
	override suspend fun ensureDirectory(path: String): Boolean = true
	override suspend fun openTextFile(path: String): TextFileHandle = error("Not used in tests")
}
