package dev.slimevr.solarxr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.logging.AppLogger
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeActions
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.driver_protocol.HandshakeAvailable
import solarxr_protocol.driver_protocol.HandshakeRequest
import solarxr_protocol.driver_protocol.HandshakeResponse
import solarxr_protocol.driver_protocol.HandshakeStatus

class DriverHandshakeBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		val server = appContext.server

		appContext.config.settings.context.state
			.map { it.data.driverConfig.enabled }
			.distinctUntilChanged()
			.onEach { enabled ->
				if (enabled) {
					receiver.sendDriverMessage(HandshakeAvailable())
				} else {
					val driverName = receiver.context.state.value.driverName ?: return@onEach

					AppLogger.solarxr.info("Disconnecting driver \"$driverName\"")
					receiver.disconnectDriverTrackers()
					receiver.context.dispatch(SolarXRBridgeActions.SetDriverInfo(null, null))
					receiver.sendDriverMessage(HandshakeResponse(status = HandshakeStatus.REJECTED_DISABLED))
				}
			}.launchIn(receiver.context.scope)

		receiver.onDriverMessage<HandshakeRequest> { req, replyTo ->
			val name = req.driverName?.takeIf { it.isNotEmpty() } ?: run {
				AppLogger.solarxr.info("Rejecting driver handshake because it's unnamed")
				receiver.sendDriverMessage(HandshakeResponse(status = HandshakeStatus.REJECTED_UNNAMED), replyTo = replyTo)
				return@onDriverMessage
			}

			val duplicate = server.context.state.value.solarxr.values.any {
				it.id != receiver.id && it.context.state.value.driverName == name
			}

			if (duplicate) {
				AppLogger.solarxr.info("Rejecting handshake from \"$name\" because it's a duplicate")
				receiver.sendDriverMessage(
					HandshakeResponse(status = HandshakeStatus.REJECTED_DUPLICATE),
					replyTo = replyTo,
				)
				receiver.context.dispatch(SolarXRBridgeActions.SetDriverInfo(null, null))
				return@onDriverMessage
			}

			AppLogger.solarxr.info("Shook hands with \"$name\"")
			receiver.context.dispatch(SolarXRBridgeActions.SetDriverInfo(name, req.boneMask))
			receiver.sendDriverMessage(
				HandshakeResponse(status = HandshakeStatus.ACCEPTED),
				replyTo = replyTo,
			)
		}.launchIn(receiver.context.scope)
	}
}
