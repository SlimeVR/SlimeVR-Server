package dev.slimevr.android.tracking.trackers.hid

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import java.io.Closeable

/**
 * A wrapper over Android's [UsbDevice] for HID devices.
 */
class AndroidHIDDevice(hidDevice: UsbDevice, usbManager: UsbManager) : Closeable {

	val deviceName = hidDevice.deviceName
	val serialNumber = hidDevice.serialNumber
	val manufacturerName = hidDevice.manufacturerName
	val productName = hidDevice.productName

	val hidInterface: UsbInterface

	val endpointIn: UsbEndpoint
	val endpointOut: UsbEndpoint?

	val deviceConnection: UsbDeviceConnection

	init {
		hidInterface = findHidInterface(hidDevice)!!

		val (endpointIn, endpointOut) = findHidIO(hidInterface)
		this.endpointIn = endpointIn!!
		this.endpointOut = endpointOut

		deviceConnection = usbManager.openDevice(hidDevice)!!

		deviceConnection.claimInterface(hidInterface, true)
	}

	override fun close() {
		deviceConnection.releaseInterface(hidInterface)
		deviceConnection.close()
	}

	companion object {
		/**
		 * Find the HID interface.
		 *
		 * @return
		 * Return the HID interface if found, otherwise null.
		 */
		private fun findHidInterface(usbDevice: UsbDevice): UsbInterface? {
			val interfaceCount: Int = usbDevice.interfaceCount

			for (interfaceIndex in 0 until interfaceCount) {
				val usbInterface = usbDevice.getInterface(interfaceIndex)

				if (usbInterface.interfaceClass == UsbConstants.USB_CLASS_HID) {
					return usbInterface
				}
			}

			return null
		}

		/**
		 * Find the HID endpoints.
		 *
		 * @return
		 * Return the HID endpoints if found, otherwise null.
		 */
		private fun findHidIO(usbInterface: UsbInterface): Pair<UsbEndpoint?, UsbEndpoint?> {
			val endpointCount: Int = usbInterface.endpointCount

			var usbEndpointIn: UsbEndpoint? = null
			var usbEndpointOut: UsbEndpoint? = null

			for (endpointIndex in 0 until endpointCount) {
				val usbEndpoint = usbInterface.getEndpoint(endpointIndex)

				if (usbEndpoint.type == UsbConstants.USB_ENDPOINT_XFER_INT) {
					if (usbEndpoint.direction == UsbConstants.USB_DIR_OUT) {
						usbEndpointOut = usbEndpoint
					} else {
						usbEndpointIn = usbEndpoint
					}
				}
			}

			return Pair(usbEndpointIn, usbEndpointOut)
		}
	}
}
