package dev.slimevr.logging

import io.klogging.Level
import io.klogging.sending.SendString
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream

private val realOut: PrintStream = System.out

/**
 * Console sink target, bound to the stream the process started with. Klogging's `STDOUT` writes
 * through [System.out], which [captureStandardStreams] replaces with a stream that logs whatever
 * is written to it, so a sink using it would feed on its own output forever.
 */
val CONSOLE: SendString = SendString { realOut.println(it) }

/** Routes [System.out] and [System.err] into the log, so `println` and stack traces reach the files. */
fun captureStandardStreams() {
	System.setOut(PrintStream(LineLogger(Level.INFO), true))
	System.setErr(PrintStream(LineLogger(Level.ERROR), true))
}

private class LineLogger(private val level: Level) : OutputStream() {
	private val line = ByteArrayOutputStream()

	@Synchronized
	override fun write(b: Int) {
		if (b != '\n'.code) {
			line.write(b)
			return
		}

		val text = line.toString(Charsets.UTF_8).trimEnd('\r')
		line.reset()
		if (text.isNotEmpty()) AppLogger.console.log(level, text)
	}
}
