package dev.slimevr.firmware

data class UpdateDeviceId<T>(
	val type: FirmwareUpdateMethod,
	val id: T,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as UpdateDeviceId<*>

		if (type != other.type) return false
		if (id != other.id) return false

		return true
	}

	override fun hashCode(): Int {
		var result = type.hashCode()
		result = 31 * result + (id?.hashCode() ?: 0)
		return result
	}
}
