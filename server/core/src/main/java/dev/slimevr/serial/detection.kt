package dev.slimevr.serial

import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration.Companion.milliseconds

private val HOTPLUG_SETTLE = 250.milliseconds

class PortDetectionBehaviour(private val watcher: SerialPortWatcher) : SerialServerBehaviour {
	@OptIn(FlowPreview::class)
	override fun observe(receiver: SerialServer) {
		watcher.changes
			.debounce(HOTPLUG_SETTLE)
			.onStart { emit(Unit) }
			.onEach { receiver.refresh() }
			.catch { e -> AppLogger.serial.error(e, "Serial hotplug events stopped") }
			.launchIn(receiver.context.scope)
	}
}
