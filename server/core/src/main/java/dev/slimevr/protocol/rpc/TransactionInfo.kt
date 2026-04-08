package dev.slimevr.protocol.rpc

import dev.slimevr.protocol.GenericConnection

data class TransactionInfo(
	val id: Long,
	val conn: GenericConnection,
)
