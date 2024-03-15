package dev.slimevr.setup

import java.util.concurrent.CopyOnWriteArrayList

class HandshakeHandler {
	private val listeners: MutableList<HandshakeListener> = CopyOnWriteArrayList()

	fun addListener(listener: HandshakeListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: HandshakeListener) {
		listeners.remove(listener)
	}

	fun sendUnknownHandshake(macAddress: String) {
		listeners.forEach { it.onUnknownHandshake(macAddress) }
	}
}

interface HandshakeListener {
	fun onUnknownHandshake(macAddress: String)
}
