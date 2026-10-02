package dev.slimevr.desktop.config

import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.TextFileHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class DesktopConfigStorage(
	private val root: File,
) : ConfigStorage {
	override suspend fun read(path: String): String? = withContext(Dispatchers.IO) {
		val file = resolve(path)
		if (file.exists()) file.readText() else null
	}

	override suspend fun write(path: String, content: String) = withContext(Dispatchers.IO) {
		val file = resolve(path)
		file.parentFile?.mkdirs()
		val tmp = File(file.parent, "${file.name}.tmp")
		tmp.writeText(content)
		Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
		Unit
	}

	override suspend fun backup(path: String) = withContext(Dispatchers.IO) {
		val file = resolve(path)
		if (!file.exists()) return@withContext
		val bakTmp = File(file.parent, "${file.name}.bak.tmp")
		file.copyTo(bakTmp, overwrite = true)
		Files.move(
			bakTmp.toPath(),
			File(file.parent, "${file.name}.bak").toPath(),
			StandardCopyOption.ATOMIC_MOVE,
			StandardCopyOption.REPLACE_EXISTING,
		)
	}

	override suspend fun exists(path: String): Boolean = withContext(Dispatchers.IO) {
		resolve(path).exists()
	}

	override suspend fun ensureDirectory(path: String): Boolean = withContext(Dispatchers.IO) {
		val directory = resolve(path)
		directory.isDirectory || directory.mkdirs()
	}

	override suspend fun openTextFile(path: String): TextFileHandle = withContext(Dispatchers.IO) {
		val file = resolve(path)
		file.parentFile?.mkdirs()
		RandomAccessTextFileHandle(RandomAccessFile(file, "rw").also { handle -> handle.setLength(0L) })
	}

	override fun displayPath(path: String): String = resolve(path).absolutePath

	private fun resolve(path: String): File {
		val requested = File(path)
		return if (requested.isAbsolute) requested else File(root, path)
	}
}

private class RandomAccessTextFileHandle(
	private val file: RandomAccessFile,
) : TextFileHandle {
	override suspend fun write(text: String) = withContext(Dispatchers.IO) {
		file.write(text.encodeToByteArray())
	}

	override suspend fun flush() = withContext(Dispatchers.IO) {
		file.fd.sync()
	}

	override suspend fun position(): Long = withContext(Dispatchers.IO) {
		file.filePointer
	}

	override suspend fun seek(position: Long) = withContext(Dispatchers.IO) {
		file.seek(position)
	}

	override suspend fun close() = withContext(Dispatchers.IO) {
		file.fd.sync()
		file.close()
	}
}
