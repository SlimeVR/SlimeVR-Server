package dev.slimevr.keybinding

import org.freedesktop.dbus.DBusPath
import org.freedesktop.dbus.annotations.DBusInterfaceName
import org.freedesktop.dbus.interfaces.DBusInterface
import org.freedesktop.dbus.messages.DBusSignal
import org.freedesktop.dbus.types.Variant

@DBusInterfaceName("org.freedesktop.portal.GlobalKeybinds")
interface GlobalKeybinds : DBusInterface {

	// Creates a session for the shortcuts
	fun CreateSession(options: Map<String, Variant<*>>): DBusPath

	fun BindShortcuts(
		sessionHandle: DBusPath,
		shortcuts: Array<Shortcut>,
		parentWindow: String,
		options: Map<String, Variant<*>>
	): DBusPath

	class Activated(
		path: String,
		val sessionHandle: DBusPath,
		val shortcutId: String,
		val timestamp: Long,
		val options: Map<String, Variant<*>>
	) : DBusSignal(path, sessionHandle, shortcutId, timestamp, options)
}

data class Shortcut(val id: String, val properties: Map<String, Variant<*>>)
