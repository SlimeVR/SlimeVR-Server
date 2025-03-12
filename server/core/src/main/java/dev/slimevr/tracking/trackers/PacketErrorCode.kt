package dev.slimevr.tracking.trackers

enum class PacketErrorCode(val id: Int) {

    NOT_APPLICABLE(0),
    POWER_ON_RESET(1),
    INTERNAL_SYSTEM_RESET(2),
    WATCHDOG_TIMEOUT(3),
    EXTERNAL_RESET(4),
    OTHER(5);

    companion object {
        private val byId = entries.associateBy { it.id }

        @JvmStatic
        fun getById(id: Int): PacketErrorCode = byId[id] ?: OTHER
    }
}
