package io.eiren.util

import java.io.File
import java.util.*

enum class OperatingSystem(
    override val name: String, //@fomatter: on
    private val aliases: Array<String?>
) {
    //@formatter:off
    LINUX("linux", arrayOf("linux", "unix")),
    WINDOWS("windows", arrayOf("win")),
    OSX("osx", arrayOf("mac")),
    UNKNOWN("unknown", arrayOfNulls(0));
     companion object {
        private var currentPlatform:OperatingSystem? = null
         fun getJavaExecutable(forceConsole:Boolean): String {
             val separator = System.getProperty("file.separator")
             val path = System.getProperty("java.home") + separator + "bin" + separator
            return if (getCurrentPlatform() == WINDOWS) {
        if (!forceConsole && File(path + "javaw.exe").isFile) path + "javaw.exe" else path + "java.exe"
    } else path + "java"
            }
         fun getCurrentPlatform(): OperatingSystem? {
            if (currentPlatform != null)return currentPlatform
             val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
            for (os in entries)  {
                for (alias in os.aliases)  {
                    if (osName.contains(alias!!))return os.also{ currentPlatform = it }
                }
            }
            return UNKNOWN
        }
         val tempDirectory:String
get() {
            if (currentPlatform == LINUX) {
                 val tmp = System.getenv("XDG_RUNTIME_DIR")
                if (tmp != null)return tmp
            }
            return System.getProperty("java.io.tmpdir")
        }
    }
}
