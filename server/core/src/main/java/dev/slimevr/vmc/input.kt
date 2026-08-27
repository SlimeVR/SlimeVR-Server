package dev.slimevr.vmc

import dev.slimevr.config.Settings
import dev.slimevr.logging.AppLogger
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.OscReceiver
import dev.slimevr.osc.forEachOscMessage
import dev.slimevr.util.formatExceptionMessage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.rpc.VMCOSCInputState

class VMCInputBehaviour(private val settings: Settings) : VMCBehaviour {
	override fun observe(receiver: VMCManager) {
		var oscReceiver: OscReceiver? = null

		settings.context.state
			.map { Pair(it.data.vmcConfig.enabled, it.data.vmcConfig.portIn) }
			.distinctUntilChanged()
			.onEach { (enabled, portIn) ->
				oscReceiver?.close()
				oscReceiver = null

				if (!enabled) {
					receiver.context.dispatch(VMCActions.SetInput(state = VMCOSCInputState.IDLE))
					return@onEach
				}

				val newReceiver = try {
					OscReceiver(portIn)
				} catch (e: Exception) {
					dispatchInputError(
						receiver = receiver,
						port = portIn,
						message = "Failed to start VMC receiver",
						throwable = e,
					)
					return@onEach
				}
				oscReceiver = newReceiver
				receiver.context.dispatch(
					VMCActions.SetInput(state = VMCOSCInputState.LISTENING, port = portIn),
				)
				AppLogger.vmc.info("VMC input listening on port $portIn")

				receiver.context.scope.launch {
					try {
						newReceiver.listenBundles { bundle ->
							forEachOscMessage(bundle) { msg -> handleMessage(msg, receiver, portIn) }
						}
					} catch (e: Exception) {
						dispatchInputError(
							receiver = receiver,
							port = portIn,
							message = "VMC receiver error",
							throwable = e,
						)
					}
				}
			}.launchIn(receiver.context.scope)
	}

	private suspend fun dispatchInputError(
		receiver: VMCManager,
		port: Int?,
		message: String,
		throwable: Throwable,
	) {
		AppLogger.vmc.error(message, throwable)
		receiver.context.dispatch(
			VMCActions.SetInput(
				state = VMCOSCInputState.ERROR,
				port = port,
				error = formatExceptionMessage(message, throwable),
			),
		)
	}

	private fun handleMessage(msg: OscMessage, receiver: VMCManager, portIn: Int) {
		when (msg.address) {
			"/VMC/Ext/Bone/Pos" -> { /* TODO: Handle incoming bone rotation data */ }
			"/VMC/Ext/Hmd/Pos" -> { /* TODO: Handle incoming HMD position + rotation */ }
			"/VMC/Ext/Con/Pos" -> { /* TODO: Handle incoming controller position + rotation */ }
			"/VMC/Ext/Tra/Pos" -> { /* TODO: Handle incoming tracker position + rotation */ }
			"/VMC/Ext/Root/Pos" -> { /* TODO: Handle incoming root position/rotation offset */ }
			else -> return
		}

		receiver.context.dispatchAll(
			listOf(
				VMCActions.SetInput(state = VMCOSCInputState.LISTENING, port = portIn),
				VMCActions.SetLastReceivedInput(System.currentTimeMillis()),
			),
		)
	}
}
