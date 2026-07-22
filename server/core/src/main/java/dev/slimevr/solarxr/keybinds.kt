package dev.slimevr.solarxr

import dev.slimevr.config.KeybindConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.defaultKeybinds
import dev.slimevr.keybind.KeybindActions
import dev.slimevr.keybind.KeybindManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeKeybindRequest
import solarxr_protocol.rpc.Keybind
import solarxr_protocol.rpc.KeybindId
import solarxr_protocol.rpc.KeybindRequest
import solarxr_protocol.rpc.KeybindResponse
import solarxr_protocol.rpc.SetKeybindRecordingRequest

private fun keybindNameId(id: KeybindId): String = id.name.lowercase().replace('_', '-')

private fun keybindConfigToProto(config: KeybindConfig): Keybind = Keybind(
	keybindId = config.id,
	keybindNameId = keybindNameId(config.id),
	keybindValue = config.binding,
	keybindDelay = config.delay,
)

private val MODIFIER_ORDER = listOf("CTRL", "ALT", "SHIFT", "SUPER")

private val NON_SHIFT_MODIFIERS = setOf("CTRL", "ALT", "SUPER")

fun canonicalKeybind(binding: String): String {
	val parts = binding.split('+').map { it.trim().uppercase() }.filter { it.isNotEmpty() }
	return (MODIFIER_ORDER.filter { it in parts } + parts.filterNot { it in MODIFIER_ORDER }).joinToString("+")
}

fun isValidKeybind(binding: String): Boolean {
	val parts = binding.split('+').map { it.trim().uppercase() }.filter { it.isNotEmpty() }
	val key = parts.filterNot { it in MODIFIER_ORDER }.singleOrNull() ?: return false
	if (!key.matches(Regex("[A-Z0-9]"))) return false
	return parts.any { it in NON_SHIFT_MODIFIERS }
}

class KeybindsBehaviour(
	private val settings: Settings,
	private val keybindManager: KeybindManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<SetKeybindRecordingRequest> { req ->
			keybindManager.context.dispatch(KeybindActions.SetRecording(req.recording ?: false))
		}

		fun buildResponse(keybinds: List<KeybindConfig>) = KeybindResponse(
			keybind = keybinds.map { keybindConfigToProto(it) },
			defaultKeybinds = defaultKeybinds().map { keybindConfigToProto(it) },
			support = receiver.appContext.featureFlags.keybindSupport,
		)

		receiver.rpcDispatcher.on<KeybindRequest> {
			receiver.sendRpc(buildResponse(settings.context.state.value.data.keybinds))
		}

		settings.context.state
			.map { it.data.keybinds }
			.distinctUntilChanged()
			.drop(1)
			.onEach { receiver.sendRpc(buildResponse(it)) }
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeKeybindRequest> { req ->
			val keybind = req.keybind ?: return@on
			val id = keybind.keybindId?.takeUnless { it == KeybindId.NONE } ?: return@on
			val newBinding = keybind.keybindValue?.let(::canonicalKeybind)
			if (!newBinding.isNullOrEmpty() && !isValidKeybind(newBinding)) return@on
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						keybinds = keybinds.map {
							if (it.id == id) {
								it.copy(
									binding = newBinding ?: it.binding,
									delay = keybind.keybindDelay ?: 0f,
								)
							} else {
								it
							}
						},
					)
				},
			)
		}
	}
}
