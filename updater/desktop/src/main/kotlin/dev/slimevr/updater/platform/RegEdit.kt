package dev.slimevr.updater.platform

import com.sun.jna.Memory
import com.sun.jna.platform.win32.Advapi32
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinReg
import com.sun.jna.ptr.IntByReference
import dev.slimevr.updater.utils.TerminalUtil

abstract class AbstractRegEdit {
	abstract fun getKeyByPath(hkey: WinReg.HKEY, path: String): Map<String, String>
}

class RegEdit : AbstractRegEdit() {
	override fun getKeyByPath(hkey: WinReg.HKEY, path: String): Map<String, String> {
		val keysMap = mutableMapOf<String, String>()

		try {
			Advapi32Util.registryGetValues(hkey, path).forEach {
				keysMap[it.key.replace("""_h\d+$""".toRegex(), "")] = it.value.toString()
			}
		} catch (e: Exception) {
			TerminalUtil.error("[RegEdit] Error reading values from registry $e")
		}
		return keysMap
	}
}
