package dev.slimevr.tracking.trackers

import dev.slimevr.VRServer
import io.eiren.util.collections.FastList

class DeviceManager(private val server: VRServer) {
	val devices = FastList<Device>()
	fun createDevice(name: String?, version: String?, manufacturer: String?): Device {
		val device = Device()
		device.name = name
		device.firmwareVersion = version
		device.manufacturer = manufacturer
		return device
	}

	fun addDevice(device: Device) {
		server.queueTask { devices.add(device) }
	}
}
