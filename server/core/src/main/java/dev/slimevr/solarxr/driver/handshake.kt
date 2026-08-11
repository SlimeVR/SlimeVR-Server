package dev.slimevr.solarxr.driver

import dev.slimevr.VRServer
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeActions
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.SolarXRBridgeState
import solarxr_protocol.driver_protocol.DriverHandshakeStatus
import solarxr_protocol.driver_protocol.InboundHandshakeRequest
import solarxr_protocol.driver_protocol.InboundHandshakeResponse

class DriverHandshakeBehaviour(
	private val server: VRServer,
) : SolarXRBridgeBehaviour {
	override fun reduce(state: SolarXRBridgeState, action: SolarXRBridgeActions) = when (action) {
		is SolarXRBridgeActions.SetDriverName -> state.copy(driverName = action.name)
		else -> state
	}

	override fun observe(receiver: SolarXRBridge) {
		receiver.onDriverMessage<InboundHandshakeRequest> { req, replyTo ->
			val name = req.driverName

			val duplicate = name != null &&
				server.context.state.value.solarxr.values.any {
					it.id != receiver.id && it.context.state.value.driverName == name
				}

			if (duplicate) {
				receiver.sendDriverMessage(
					InboundHandshakeResponse(status = DriverHandshakeStatus.REJECTED_DUPLICATE),
					replyTo = replyTo,
				)
				receiver.disconnect()
				return@onDriverMessage
			}

			if (name != null) receiver.context.dispatch(SolarXRBridgeActions.SetDriverName(name))
			receiver.sendDriverMessage(
				InboundHandshakeResponse(status = DriverHandshakeStatus.ACCEPTED),
				replyTo = replyTo,
			)
		}.launchIn(receiver.context.scope)
	}
}
