package dev.slimevr.solarxr

import dev.slimevr.config.KeybindConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.defaultKeybinds
import solarxr_protocol.rpc.ChangeKeybindRequest
import solarxr_protocol.rpc.Keybind
import solarxr_protocol.rpc.KeybindId
import solarxr_protocol.rpc.KeybindRequest
import solarxr_protocol.rpc.KeybindResponse

private fun keybindNameId(id: KeybindId): String = id.name.lowercase().replace('_', '-')

private fun keybindConfigToProto(config: KeybindConfig): Keybind = Keybind(
	keybindId = config.id,
	keybindNameId = keybindNameId(config.id),
	keybindValue = config.binding,
	keybindDelay = config.delay,
)

class KeybindsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<KeybindRequest> {
			val keybinds = settings.context.state.value.data.keybinds
			receiver.sendRpc(
				KeybindResponse(
					keybind = keybinds.map { keybindConfigToProto(it) },
					defaultKeybinds = defaultKeybinds().map { keybindConfigToProto(it) },
					support = receiver.appContext.featureFlags.keybindSupport,
				),
			)
		}

		receiver.rpcDispatcher.on<ChangeKeybindRequest> { req ->
			val keybind = req.keybind ?: return@on
			val id = keybind.keybindId?.takeUnless { it == KeybindId.NONE } ?: return@on
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						keybinds = keybinds.map {
							if (it.id == id) {
								it.copy(
									binding = keybind.keybindValue ?: it.binding,
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
