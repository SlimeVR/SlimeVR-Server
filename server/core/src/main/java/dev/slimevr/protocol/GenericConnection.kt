package dev.slimevr.protocol

import java.nio.ByteBuffer
import java.util.UUID

interface GenericConnection {
	val connectionId: UUID

	val context: ConnectionContext

	fun send(bytes: ByteBuffer)
}
