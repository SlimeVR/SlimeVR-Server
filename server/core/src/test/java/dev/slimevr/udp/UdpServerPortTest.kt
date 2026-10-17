package dev.slimevr.udp

import dev.slimevr.TestAppContext
import dev.slimevr.config.AppConfig
import dev.slimevr.config.MemoryStorage
import dev.slimevr.config.SettingsConfigState
import dev.slimevr.config.TrackersConfig
import dev.slimevr.config.jsonConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import solarxr_protocol.rpc.ProfileKind
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.PortUnreachableException
import java.net.SocketTimeoutException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private fun freeUdpPort(): Int = DatagramSocket(0).use { it.localPort }

// Probes with a datagram so the probe never holds the port, a closed port answers with ICMP unreachable
private fun isBound(port: Int): Boolean = DatagramSocket().use { probe ->
	probe.connect(InetAddress.getLoopbackAddress(), port)
	probe.soTimeout = 50
	try {
		probe.send(DatagramPacket(ByteArray(1), 1))
		probe.receive(DatagramPacket(ByteArray(1), 1))
		true
	} catch (_: PortUnreachableException) {
		false
	} catch (_: SocketTimeoutException) {
		true
	}
}

private suspend fun awaitUntil(what: String, condition: () -> Boolean) {
	try {
		withTimeout(5.seconds) {
			while (!condition()) delay(20.milliseconds)
		}
	} catch (e: kotlinx.coroutines.TimeoutCancellationException) {
		throw AssertionError("Timed out waiting for $what", e)
	}
}

private class Harness(
	val scope: CoroutineScope,
	val config: AppConfig,
	val server: UdpServer,
	val failures: List<Throwable>,
)

private fun profile(storage: MemoryStorage, name: String, port: Int) {
	storage.files["settings/$name.json"] = jsonConfig.encodeToString(SettingsConfigState(trackersConfig = TrackersConfig(trackerPort = port)))
}

private fun withUdpServer(storage: MemoryStorage, block: suspend (Harness) -> Unit) = runBlocking {
	val failures = mutableListOf<Throwable>()
	val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineExceptionHandler { _, e -> failures += e })
	try {
		val config = AppConfig.create(scope, storage)
		val appContext = object : TestAppContext() {
			override val config: AppConfig = config
		}
		val server = UdpServer.create(scope, bind = ::bindTestUdp)
		server.startReceiving(appContext, scope)
		block(Harness(scope, config, server, failures))
		server.dispose()
	} finally {
		scope.cancel()
	}
}

class UdpServerPortTest {

	@Test
	fun `the server listens on the port of the selected profile`() {
		val storage = MemoryStorage()
		val first = freeUdpPort()
		profile(storage, "default", first)

		withUdpServer(storage) {
			awaitUntil("the first port to be bound") { isBound(first) }
		}
	}

	@Test
	fun `switching profile moves the server to the new port and releases the old one`() {
		val storage = MemoryStorage()
		val first = freeUdpPort()
		val second = generateSequence { freeUdpPort() }.first { it != first }
		profile(storage, "default", first)
		profile(storage, "other", second)

		withUdpServer(storage) { h ->
			awaitUntil("the first port to be bound") { isBound(first) }

			h.config.switchProfile(ProfileKind.SETTINGS, "other")

			awaitUntil("the second port to be bound") { isBound(second) }
			awaitUntil("the first port to be released") { !isBound(first) }
		}
	}

	@Test
	fun `switching to a profile with the same port keeps listening`() {
		val storage = MemoryStorage()
		val port = freeUdpPort()
		profile(storage, "default", port)
		profile(storage, "same", port)

		withUdpServer(storage) { h ->
			awaitUntil("the port to be bound") { isBound(port) }

			h.config.switchProfile(ProfileKind.SETTINGS, "same")
			delay(300.milliseconds)

			assertTrue(isBound(port))
		}
	}

	@Test
	fun `a port that is already in use is logged and the next profile can bind again`() {
		val storage = MemoryStorage()
		val first = freeUdpPort()
		val busy = generateSequence { freeUdpPort() }.first { it != first }
		val good = generateSequence { freeUdpPort() }.first { it != first && it != busy }
		profile(storage, "default", first)
		profile(storage, "busy", busy)
		profile(storage, "good", good)

		withUdpServer(storage) { h ->
			awaitUntil("the first port to be bound") { isBound(first) }

			DatagramSocket(busy).use {
				h.config.switchProfile(ProfileKind.SETTINGS, "busy")
				awaitUntil("the first port to be released") { !isBound(first) }
				delay(300.milliseconds)
				assertEquals(emptyList(), h.failures)
			}

			h.config.switchProfile(ProfileKind.SETTINGS, "good")
			awaitUntil("the good port to be bound") { isBound(good) }
		}
	}

	@Test
	fun `dispose releases the port`() {
		val storage = MemoryStorage()
		val port = freeUdpPort()
		profile(storage, "default", port)

		withUdpServer(storage) { h ->
			awaitUntil("the port to be bound") { isBound(port) }

			h.server.dispose()

			awaitUntil("the port to be released") { !isBound(port) }
		}
	}
}
