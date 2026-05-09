package dev.slimevr.vrcosc

import dev.slimevr.config.DEFAULT_VRC_OSC_PORT_IN
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.osc.OscArg
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.PI

internal const val TRACKING_VRSYSTEM_PATH: String = "/tracking/vrsystem"

internal fun vrcOscPortIn(config: VRCOSCConfig): Int = config.manualNetwork?.portIn ?: DEFAULT_VRC_OSC_PORT_IN

internal fun parsePosition(args: List<OscArg>, startIndex: Int = 0): Vector3? {
	val x = args.getOrNull(startIndex)?.asFloatOrNull() ?: return null
	val y = args.getOrNull(startIndex + 1)?.asFloatOrNull() ?: return null
	val z = args.getOrNull(startIndex + 2)?.asFloatOrNull() ?: return null
	return Vector3(x, y, -z)
}

internal fun parseVrcEulerRotation(args: List<OscArg>, startIndex: Int = 0): Quaternion? {
	val x = args.getOrNull(startIndex)?.asFloatOrNull() ?: return null
	val y = args.getOrNull(startIndex + 1)?.asFloatOrNull() ?: return null
	val z = args.getOrNull(startIndex + 2)?.asFloatOrNull() ?: return null
	val (w, rx, ry, rz) = EulerAngles(
		EulerOrder.YXZ,
		x * PI.toFloat() / 180f,
		y * PI.toFloat() / 180f,
		z * PI.toFloat() / 180f,
	).toQuaternion()
	return Quaternion(w, -rx, -ry, rz)
}

internal fun formatExceptionMessage(prefix: String, throwable: Throwable): String {
	val detail = throwable.message?.takeIf { it.isNotBlank() }
		?: throwable::class.simpleName.orEmpty()
	return if (detail.isBlank()) prefix else "$prefix: $detail"
}
