package dev.slimevr.firmware.behaviours

import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.firmware.FirmwareManagerBehaviour
import dev.slimevr.firmware.OTA_CALLBACK_PORT
import dev.slimevr.firmware.OtaConnection
import dev.slimevr.firmware.isAwaitingOtaCallback
import dev.slimevr.logging.AppLogger
import dev.slimevr.util.stripIpAddressPort
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.toJavaAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Listens on a fixed TCP port for trackers connecting back during OTA, so a single firewall
 * rule covers it. Sockets are routed to the job by remote IP, so parallel updates share the port.
 */
class OtaCallbackBehaviour(
	private val port: Int = OTA_CALLBACK_PORT,
) : FirmwareManagerBehaviour {
	override fun observe(receiver: FirmwareManager) {
		receiver.context.scope.launch(Dispatchers.IO) { listen(receiver) }
	}

	private suspend fun listen(manager: FirmwareManager) {
		SelectorManager(Dispatchers.IO).use { selectorManager ->
			val serverSocket = try {
				aSocket(selectorManager).tcp().bind(InetSocketAddress("0.0.0.0", port)) { reuseAddress = true }
			} catch (e: Exception) {
				AppLogger.firmware.error(e, "Failed to bind the OTA port $port")
				return
			}
			serverSocket.use {
				while (currentCoroutineContext().isActive) {
					val socket = serverSocket.accept()
					// Same as the UDP address resolvers, hostString avoids a reverse DNS lookup
					val ip = (socket.remoteAddress.toJavaAddress() as? java.net.InetSocketAddress)?.hostString
					val waiting = manager.context.state.value.jobs.values.any { job ->
						isAwaitingOtaCallback(job) && stripIpAddressPort(job.portLocation) == ip
					}
					if (ip != null && waiting && manager.otaConnections.subscriptionCount.value > 0) {
						manager.otaConnections.emit(OtaConnection(ip, socket))
					} else {
						AppLogger.firmware.warn("Rejected unexpected OTA connection from $ip")
						socket.close()
					}
				}
			}
		}
	}
}
