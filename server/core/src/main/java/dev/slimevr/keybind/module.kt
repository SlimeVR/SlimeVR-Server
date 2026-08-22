package dev.slimevr.keybind

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
import dev.slimevr.config.KeybindConfig
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.resets.ResetsManager
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.KeybindId
import solarxr_protocol.rpc.ResetType

private const val KEYBIND_SOURCE = "Keybind"

private val FEET_BODY_PARTS = listOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

private const val BINDING_SETTLE_MS = 300L

data class KeybindState(
	val recording: Boolean,
)

sealed interface KeybindActions {
	data class SetRecording(val recording: Boolean) : KeybindActions
}

sealed interface KeybindEvent {
	data class Fired(val id: KeybindId) : KeybindEvent

	data class Rebind(val keybinds: List<KeybindConfig>) : KeybindEvent
}

typealias KeybindContext = Context<KeybindState, KeybindActions>
typealias KeybindBehaviour = Behaviour<KeybindState, KeybindActions, KeybindManager>

class KeybindBasicBehaviour : KeybindBehaviour {
	override fun reduce(state: KeybindState, action: KeybindActions): KeybindState = when (action) {
		is KeybindActions.SetRecording -> state.copy(recording = action.recording)
	}
}

class KeybindConfigBehaviour(private val settings: Settings) : KeybindBehaviour {
	@OptIn(FlowPreview::class)
	override fun observe(receiver: KeybindManager) {
		settings.context.state
			.map { it.data.keybinds }
			.distinctUntilChangedBy { keybinds -> keybinds.map { it.id to it.binding } }
			.debounce(BINDING_SETTLE_MS)
			.onEach { receiver.events.emit(KeybindEvent.Rebind(it)) }
			.launchIn(receiver.context.scope)
	}
}

class KeybindTriggerBehaviour(
	private val settings: Settings,
	private val resetsManager: ResetsManager,
	private val skeleton: Skeleton,
) : KeybindBehaviour {
	override fun observe(receiver: KeybindManager) {
		receiver.events.on<KeybindEvent.Fired> { event ->
			if (receiver.recording) return@on

			val delaySeconds = settings.context.state.value.data.keybinds.find { it.id == event.id }?.delay ?: 0f
			when (event.id) {
				KeybindId.FULL_RESET -> resetsManager.scheduleReset(KEYBIND_SOURCE, ResetType.FULL, delaySeconds)

				KeybindId.YAW_RESET -> resetsManager.scheduleReset(KEYBIND_SOURCE, ResetType.YAW, delaySeconds)

				KeybindId.MOUNTING_RESET -> resetsManager.scheduleReset(KEYBIND_SOURCE, ResetType.POSE_MOUNTING, delaySeconds)

				KeybindId.FEET_MOUNTING_RESET ->
					resetsManager.scheduleReset(KEYBIND_SOURCE, ResetType.POSE_MOUNTING, delaySeconds, FEET_BODY_PARTS)

				KeybindId.PAUSE_TRACKING -> {
					if (delaySeconds > 0f) delay((delaySeconds * 1000).toLong())
					skeleton.context.dispatch(SkeletonActions.PauseTracking(!skeleton.context.state.value.paused))
				}

				KeybindId.NONE -> Unit
			}
		}.launchIn(receiver.context.scope)
	}
}

class KeybindManager(val context: KeybindContext) {
	val recording: Boolean get() = context.state.value.recording

	val events = EventDispatcher<KeybindEvent>("Keybind", context.scope, capacity = 16)

	fun startObserving(appContext: AppContextProvider) {
		context.behaviours.addAll(
			listOf(
				KeybindConfigBehaviour(appContext.config.settings),
				KeybindTriggerBehaviour(appContext.config.settings, appContext.resetsManager, appContext.skeleton),
			),
		)
		context.observeAll(this)
	}

	companion object {
		fun create(scope: CoroutineScope): KeybindManager {
			val context = Context.create(
				initialState = KeybindState(recording = false),
				scope = scope,
				behaviours = listOf(KeybindBasicBehaviour()),
				name = "KeybindManager",
			)
			return KeybindManager(context)
		}
	}
}
