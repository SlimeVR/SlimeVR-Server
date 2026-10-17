@file:Suppress("FunctionName", "ktlint:standard:function-naming")

package dev.slimevr.desktop.serial

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Guid
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.win32.StdCallLibrary
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.ref.Reference

// GUID_DEVINTERFACE_COMPORT
private const val COM_PORT_INTERFACE = "{86E0D1E0-8089-11D0-9CE4-08003E301F73}"

private const val CR_SUCCESS = 0
private const val ERROR_SUCCESS = 0
private const val CM_NOTIFY_FILTER_TYPE_DEVICEINTERFACE = 0

// CM_NOTIFY_FILTER: cbSize, Flags, FilterType, Reserved, then a union whose largest member is 200 WCHARs
private const val FILTER_SIZE = 416
private const val FILTER_TYPE_OFFSET = 8
private const val FILTER_CLASS_GUID_OFFSET = 16
private const val GUID_SIZE = 16

internal fun interface DeviceNotificationCallback : StdCallLibrary.StdCallCallback {
	/** [action] is a CM_NOTIFY_ACTION. Returns ERROR_SUCCESS */
	fun invoke(notification: Pointer?, context: Pointer?, action: Int, eventData: Pointer?, eventDataSize: Int): Int
}

internal interface CfgMgr32 : StdCallLibrary {
	fun CM_Register_Notification(filter: Pointer, context: Pointer?, callback: DeviceNotificationCallback, notification: PointerByReference): Int
	fun CM_Unregister_Notification(notification: Pointer): Int
}

/** A change per COM port interface arrival or removal. The callback runs on a system thread */
fun createWindowsPortChanges(): Flow<Unit> = callbackFlow {
	val cfgMgr = Native.load("CfgMgr32", CfgMgr32::class.java)

	val filter = Memory(FILTER_SIZE.toLong()).apply {
		clear()
		setInt(0, FILTER_SIZE)
		setInt(FILTER_TYPE_OFFSET.toLong(), CM_NOTIFY_FILTER_TYPE_DEVICEINTERFACE)
		val guid = Guid.GUID(COM_PORT_INTERFACE).apply { write() }
		write(FILTER_CLASS_GUID_OFFSET.toLong(), guid.pointer.getByteArray(0, GUID_SIZE), 0, GUID_SIZE)
	}

	// Referenced until the flow closes so the native side keeps a live callback
	val callback = DeviceNotificationCallback { _, _, _, _, _ ->
		trySend(Unit)
		ERROR_SUCCESS
	}
	val notification = PointerByReference()
	val result = cfgMgr.CM_Register_Notification(filter, null, callback, notification)
	check(result == CR_SUCCESS) { "CM_Register_Notification failed with $result" }

	awaitClose {
		cfgMgr.CM_Unregister_Notification(notification.value)
		Reference.reachabilityFence(callback)
	}
}
