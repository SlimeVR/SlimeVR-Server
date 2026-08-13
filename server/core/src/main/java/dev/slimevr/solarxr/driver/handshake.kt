package dev.slimevr.solarxr.driver

import dev.slimevr.VRServer
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeActions
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.SolarXRBridgeState
import solarxr_protocol.driver_protocol.HandshakeStatus
import solarxr_protocol.driver_protocol.HandshakeRequest
import solarxr_protocol.driver_protocol.HandshakeResponse

class DriverHandshakeBehaviour(
	private val server: VRServer,
) : SolarXRBridgeBehaviour {
	override fun reduce(state: SolarXRBridgeState, action: SolarXRBridgeActions) = when (action) {
		is SolarXRBridgeActions.SetDriverInfo -> state.copy(driverName = action.name, boneMask = action.boneMask)
		else -> state
	}

	override fun observe(receiver: SolarXRBridge) {
		receiver.onDriverMessage<HandshakeRequest> { req, replyTo ->
			val name = req.driverName ?: run {
				receiver.sendDriverMessage(HandshakeResponse(status = HandshakeStatus.REJECTED_UNNAMED), replyTo = replyTo)
				return@onDriverMessage
			}

			val duplicate = name != null &&
				server.context.state.value.solarxr.values.any {
					it.id != receiver.id && it.context.state.value.driverName == name
				}

			if (duplicate) {
				receiver.sendDriverMessage(
					HandshakeResponse(status = HandshakeStatus.REJECTED_DUPLICATE),
					replyTo = replyTo,
				)
				receiver.context.dispatch(SolarXRBridgeActions.SetDriverInfo(null, null))
				return@onDriverMessage
			}

			receiver.context.dispatch(SolarXRBridgeActions.SetDriverInfo(name, req.boneMask))
			receiver.sendDriverMessage(
				HandshakeResponse(status = HandshakeStatus.ACCEPTED),
				replyTo = replyTo,
			)
		}.launchIn(receiver.context.scope)
	}
}
