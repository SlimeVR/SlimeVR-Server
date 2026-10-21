package dev.slimevr.solarxr.rpc

import dev.slimevr.logging.LogSourceProvider
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solarxr_protocol.rpc.LogEvent
import solarxr_protocol.rpc.LogFile
import solarxr_protocol.rpc.LogFileChunkRequest
import solarxr_protocol.rpc.LogFileChunkResponse
import solarxr_protocol.rpc.LogSourceInfo
import solarxr_protocol.rpc.LogStreamRequest
import solarxr_protocol.rpc.LogStreamResponse
import solarxr_protocol.rpc.LogsRequest
import solarxr_protocol.rpc.LogsResponse
import java.io.InputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val BATCH_WINDOW = 100.milliseconds
private const val CHUNK_SIZE = 128 * 1024

private fun readChunk(input: InputStream, offset: Long): ByteArray {
	var toSkip = offset
	while (toSkip > 0) {
		val skipped = input.skip(toSkip)
		if (skipped <= 0) return ByteArray(0)
		toSkip -= skipped
	}

	val buffer = ByteArray(CHUNK_SIZE)
	var read = 0
	while (read < CHUNK_SIZE) {
		val n = input.read(buffer, read, CHUNK_SIZE - read)
		if (n < 0) break
		read += n
	}
	return if (read == CHUNK_SIZE) buffer else buffer.copyOf(read)
}

private fun batchByTime(events: Flow<LogEvent>, window: Duration): Flow<List<LogEvent>> = channelFlow {
	val pending = Channel<LogEvent>(Channel.UNLIMITED)
	launch { events.collect { pending.send(it) } }
	for (first in pending) {
		delay(window)
		val batch = mutableListOf(first)
		while (true) batch += pending.tryReceive().getOrNull() ?: break
		send(batch)
	}
}

class LogsBehaviour(private val sources: List<LogSourceProvider>) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		var streamJob: Job? = null

		receiver.rpcDispatcher.on<LogsRequest> {
			val infos = sources.map { LogSourceInfo(source = it.source, live = it.events != null) }
			val files = withContext(Dispatchers.IO) {
				sources.flatMap { provider ->
					provider.files().map {
						LogFile(
							source = provider.source,
							name = it.name,
							size = it.size.toULong(),
							modifiedMs = it.modifiedMs.toULong(),
							current = it.current,
						)
					}
				}
			}
			receiver.sendRpc(LogsResponse(sources = infos, files = files))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<LogStreamRequest> { req ->
			streamJob?.cancelAndJoin()
			val requested = req.sources.orEmpty().toSet()
			val flows = sources.filter { it.source in requested }.mapNotNull { it.events }
			streamJob = if (flows.isEmpty()) {
				null
			} else {
				receiver.context.scope.launch {
					batchByTime(flows.merge(), BATCH_WINDOW).collect { batch ->
						receiver.sendRpc(LogStreamResponse(events = batch))
					}
				}
			}
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<LogFileChunkRequest> { req ->
			val entry = withContext(Dispatchers.IO) {
				sources.firstOrNull { it.source == req.source }?.files()?.firstOrNull { it.name == req.name }
			}
			val data = entry?.let { withContext(Dispatchers.IO) { it.open().use { input -> readChunk(input, req.offset.toLong()) } } }
			receiver.sendRpc(
				LogFileChunkResponse(
					source = req.source,
					name = req.name,
					offset = req.offset,
					data = data?.asList()?.map { it.toUByte() },
					eof = data == null || data.size < CHUNK_SIZE,
				),
			)
		}.launchIn(receiver.context.scope)
	}
}
