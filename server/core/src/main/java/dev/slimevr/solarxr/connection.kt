package dev.slimevr.solarxr

import solarxr_protocol.connection.BoneRegistryRequest
import solarxr_protocol.connection.ClientHello
import solarxr_protocol.connection.ConfigurationDone
import solarxr_protocol.connection.ConnectionError
import solarxr_protocol.connection.ConnectionMessage
import solarxr_protocol.connection.HelloStatus
import solarxr_protocol.connection.InitializationRequiredError
import solarxr_protocol.connection.ServerHello
import solarxr_protocol.connection.UnsupportedRequestError

suspend fun onConnectionMessage(message: ConnectionMessage?, bridge: SolarXRBridge) {
	when (bridge.context.state.value.phase) {
		ConnectionPhase.HELLO -> {
			val hello = message as? ClientHello
			if (hello == null) {
				bridge.sendConnectionMessage(
					ConnectionError(message = "The first message on a connection must be ClientHello", data = InitializationRequiredError()),
					closeReason = "First message was not ClientHello",
				)
				return
			}
			val accepted = hello.protocolVersion == SOLARXR_PROTOCOL_VERSION
			bridge.sendConnectionMessage(
				ServerHello(if (accepted) HelloStatus.ACCEPTED else HelloStatus.REJECTED_UNSUPPORTED_VERSION, SOLARXR_PROTOCOL_VERSION),
				closeReason = if (accepted) null else "Unsupported SolarXR protocol version ${hello.protocolVersion}",
			)
			if (accepted) bridge.context.dispatch(SolarXRBridgeActions.SetPhase(ConnectionPhase.CONFIGURING))
		}

		ConnectionPhase.CONFIGURING -> when (message) {
			is BoneRegistryRequest -> bridge.sendConnectionMessage(bridge.registry.value)

			is ConfigurationDone -> {
				bridge.sendConnectionMessage(ConfigurationDone())
				bridge.completeConfiguration()
			}

			null -> bridge.sendConnectionMessage(ConnectionError(message = "Unrecognized connection message", data = UnsupportedRequestError()))

			else -> Unit
		}

		ConnectionPhase.READY -> Unit
	}
}
