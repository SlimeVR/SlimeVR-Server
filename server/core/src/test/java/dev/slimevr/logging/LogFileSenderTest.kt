package dev.slimevr.logging

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.io.path.deleteRecursively
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogFileSenderTest {

	@OptIn(kotlin.io.path.ExperimentalPathApi::class)
	private fun withTempDir(block: (Path) -> Unit) {
		val dir = Files.createTempDirectory("slimevr-logs")
		try {
			block(dir)
		} finally {
			dir.deleteRecursively()
		}
	}

	private fun Path.logNames(): List<String> = listDirectoryEntries("*.log").map { it.name }.sorted()

	/** Writes a log file and backdates it, so ordering by modification time has something to see. */
	private fun Path.staleLog(daysAgo: Long): Path = apply {
		Files.writeString(this, "old run\n")
		Files.setLastModifiedTime(this, FileTime.from(Instant.now().minus(daysAgo, ChronoUnit.DAYS)))
	}

	@Test
	fun `writes every event as its own line`() = withTempDir { dir ->
		val sender = LogFileSender(dir)
		sender("first")
		sender("second")
		sender.close()

		val logs = dir.logNames()
		assertEquals(1, logs.size)
		assertEquals(listOf("first", "second"), dir.resolve(logs.single()).readText().trim().lines())
	}

	@Test
	fun `creates the folder when it does not exist yet`() = withTempDir { dir ->
		val nested = dir.resolve("logs")
		val sender = LogFileSender(nested)
		sender("hello")
		sender.close()

		assertEquals(1, nested.logNames().size)
	}

	@Test
	fun `rolls over to a new file past the size limit`() = withTempDir { dir ->
		val sender = LogFileSender(dir, fileSizeLimit = 16)
		repeat(3) { sender("0123456789") }
		sender.close()

		// Each event writes 11 bytes, so the second one takes the first file past the 16 byte limit.
		val logs = dir.logNames()
		assertEquals(2, logs.size)
		assertEquals(2, dir.resolve(logs[0]).readText().trim().lines().size)
		assertEquals(1, dir.resolve(logs[1]).readText().trim().lines().size)
	}

	@Test
	fun `keeps at most the configured number of files`() = withTempDir { dir ->
		val sender = LogFileSender(dir, fileSizeLimit = 1, maxFileCount = 3)
		repeat(10) { sender("event $it") }
		sender.close()

		assertEquals(3, dir.logNames().size)
	}

	@Test
	fun `keeps writing to the newest file when only one is allowed`() = withTempDir { dir ->
		val sender = LogFileSender(dir, fileSizeLimit = 1, maxFileCount = 1)
		repeat(3) { sender("event $it") }
		sender.close()

		val logs = dir.logNames()
		assertEquals(1, logs.size)
		assertEquals("event 2", dir.resolve(logs.single()).readText().trim())
	}

	@Test
	fun `deletes logs left by previous runs, oldest first`() = withTempDir { dir ->
		val older = dir.resolve("slimevr-server_2020-01-01_00-00-00_0.log").staleLog(daysAgo = 30)
		val newer = dir.resolve("slimevr-server_2020-01-01_00-00-00_1.log").staleLog(daysAgo = 1)
		val unrelated = dir.resolve("notes.txt")
		Files.writeString(unrelated, "keep me\n")

		// Room for one file from a previous run next to the one we are about to open.
		val sender = LogFileSender(dir, maxFileCount = 2)
		sender("new run")
		sender.close()

		assertTrue(Files.notExists(older), "the least recently written log should have gone first")
		assertTrue(Files.exists(newer), "the more recent log should have been kept")
		assertTrue(Files.exists(unrelated), "files that are not logs must be left alone")
	}
}
