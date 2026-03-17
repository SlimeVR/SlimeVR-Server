package dev.slimevr.tracking.videocalibration.networking

import io.eiren.util.logging.LogManager
import java.net.Inet4Address
import java.net.NetworkInterface
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener
import kotlin.collections.iterator

/**
 * Registry of published mDNS services.
 */
class MDNSRegistry {

	/**
	 * Services to monitor.
	 */
	enum class ServiceType(val mDNSServiceType: String) {
		WEBCAM("_slimevr-camera._tcp.local."),
	}

	/**
	 * An available service.
	 */
	data class Service(
		val type: ServiceType,
		val host: Inet4Address,
		val port: Int,
	)

	private val lock = Any()
	private val jmDNSs = mutableListOf<JmDNS>()
	private val services = mutableMapOf<ServiceType, Service>()

	/**
	 * Starts listening for mDNS services.
	 */
	fun start() {
		LogManager.info("Setting up mDNS discovery...")

		try {
			for (address in enumerateNetworks()) {
				LogManager.info("Listening for mDNS on network $address...")
				try {
					val jmdns = JmDNS.create(address)
					jmDNSs.add(jmdns)
				} catch (e: Exception) {
					LogManager.warning("Failed to create mDNS instance for $address: $e", e)
				}
			}
		} catch (e: Exception) {
			LogManager.warning("Failed to enumerate network instances", e)
		}

		for (serviceType in ServiceType.entries) {
			LogManager.info("Listening for mDNS service $serviceType...")
			for (jmDNS in jmDNSs) {
				jmDNS.addServiceListener(
					serviceType.mDNSServiceType,
					object : ServiceListener {

						override fun serviceAdded(event: ServiceEvent) {
							LogManager.debug("Resolving mDNS service $serviceType on ${jmDNS.inetAddress}...")
							jmDNS.requestServiceInfo(event.type, event.name)
						}

						override fun serviceRemoved(event: ServiceEvent) {
							synchronized(lock) {
								LogManager.debug("Removed mDNS service $serviceType")
								services.remove(serviceType)
							}
						}

						override fun serviceResolved(event: ServiceEvent) {
							val info = event.info
							val host = info.inetAddresses.filterIsInstance<Inet4Address>().firstOrNull() ?: return
							val port = info.port
							val service = Service(serviceType, host, port)
							synchronized(lock) {
								LogManager.debug("Added mDNS service $service")
								services[service.type] = service
							}
						}
					},
				)
			}
		}

		LogManager.info("mDNS discovery started")
	}

	/**
	 * Gets a discovered service.
	 */
	fun findService(serviceType: ServiceType): Service? {
		synchronized(lock) {
			return services[serviceType]
		}
	}

	companion object {

		private fun enumerateNetworks() = iterator {
			for (network in NetworkInterface.getNetworkInterfaces()) {
				if (network.isUp && !network.isLoopback) {
					for (address in network.inetAddresses) {
						if (address is Inet4Address) {
							yield(address)
						}
					}
				}
			}
		}
	}
}
