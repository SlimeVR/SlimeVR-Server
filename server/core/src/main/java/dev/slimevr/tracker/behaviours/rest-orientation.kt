package dev.slimevr.tracker.behaviours

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.resets.ResetBodyParts
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ArmsResetMode

/**
 * Sets the orientation the tracker should match after a Full Reset.
 * Used for T-Pose down.
 */
class TrackerRestOrientationBehaviour(
	private val settings: Settings,
) : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		val armsResetModeFlow = settings.context.state.map { it.data.resetsConfig.armsResetMode }
		val bodyPartFlow = receiver.context.state.map { it.bodyPart }.distinctUntilChanged()

		combine(armsResetModeFlow, bodyPartFlow) { armsResetMode, bodyPart ->
			getRestOrientation(bodyPart, armsResetMode)
		}
			.distinctUntilChanged()
			.onEach { restOrientation ->
				receiver.context.dispatch(TrackerActions.SetRestOrientation(restOrientation))
			}
			.launchIn(receiver.context.scope)
	}

	private val quarterRollLeft = EulerAngles(EulerOrder.YZX, 0f, 0f, -FastMath.HALF_PI).toQuaternion()
	private val quarterRollRight = EulerAngles(EulerOrder.YZX, 0f, 0f, FastMath.HALF_PI).toQuaternion()
	private fun getRestOrientation(bodyPart: BodyPart?, armsResetMode: ArmsResetMode) = if (armsResetMode == ArmsResetMode.T_POSE_DOWN) {
		when (bodyPart) {
			in ResetBodyParts.LEFT_ARM -> quarterRollLeft
			in ResetBodyParts.RIGHT_ARM -> quarterRollRight
			else -> Quaternion.IDENTITY
		}
	} else {
		Quaternion.IDENTITY
	}
}
