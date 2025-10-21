package dev.slimevr.tracking

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortIn
import com.illposed.osc.transport.OSCPortOut
import dev.slimevr.VRServer
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.osc.OSCHandler
import dev.slimevr.osc.VRCOSCHandler
import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerRole
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.io.Closeable
import java.net.InetAddress
import kotlin.math.*

public class ToesOSCHandler(
	private val server: VRServer,
	private val config: VRCOSCConfig,
) : OSCHandler {
	private var oscPort: OSCPortOut? = null
	private val persistedValues = mutableMapOf<String, Boolean>()
	private var oscPortAddress: InetAddress? = null
	private var oscPortPort: Int = 9000

	override fun refreshSettings(refreshRouterSettings: Boolean) {
		updateOscSender(config.portOut, config.address);
	}

	override fun updateOscReceiver(portIn: Int, args: Array<out String?>?) {
		// We don't currently do any OSC receiving here
	}

	init {
		refreshSettings(false)
	}
	override fun updateOscSender(portOut: Int, address: String?) {
		try {
			oscPort?.close()
			if (portOut != null) {
				val inetAddress = InetAddress.getByName(address)
				oscPort = OSCPortOut(inetAddress, portOut)
				oscPortAddress = inetAddress
				oscPortPort = portOut
			}
		} catch (e: Exception) {
			println("Failed to open Toes OSC sender: ${e.message}")
		}
	}


	public override fun update() {
		if (!config.enabled) return

		try {
			val trackers = server.humanPoseManager

			// LEFT FOOT + TOES
			trackers.getBone(BoneType.LEFT_FOOT)?.let { leftFoot ->
				val leftToes = listOf(
					BoneType.LEFT_TOES_ABDUCTOR_HALLUCIS,
					BoneType.LEFT_TOES_DIGITORUM_BREVIS,
					BoneType.LEFT_TOES_ABDUCTOR_DIGITI_MINIMI
				).mapNotNull { pos -> trackers.getBone(pos) }

				processToesForFoot(leftFoot, leftToes, FootSide.Left)
			}

			// RIGHT FOOT + TOES
			trackers.getBone(BoneType.RIGHT_FOOT)?.let { rightFoot ->
				val rightToes = listOf(
					BoneType.RIGHT_TOES_ABDUCTOR_HALLUCIS,
					BoneType.RIGHT_TOES_DIGITORUM_BREVIS,
					BoneType.RIGHT_TOES_ABDUCTOR_DIGITI_MINIMI
				).mapNotNull { pos -> trackers.getBone(pos) }

				processToesForFoot(rightFoot, rightToes, FootSide.Right)
			}

		} catch (ex: Exception) {
			println("ToeLoop error: ${ex.message}")
		}
	}


	private fun processToesForFoot(foot: Bone, toeTrackers: List<Bone>, side: FootSide) {
		var lastAssigned: Bone? = null

		for ((segmentIndex, tracker) in toeTrackers.withIndex()) {
			if (tracker != null) {
				lastAssigned = tracker
			}

			when (segmentIndex) {
				0 -> processToe(foot, lastAssigned!!, side, 0)
				1 -> {
					processToe(foot, lastAssigned!!, side, 1)
					processToe(foot, lastAssigned!!, side, 2)
				}
				2 -> {
					processToe(foot, lastAssigned!!, side, 3)
					processToe(foot, lastAssigned!!, side, 4)
				}
			}
		}
	}

	override fun getOscSender(): OSCPortOut? = oscPort
	override fun getPortOut(): Int = oscPortPort
	override fun getAddress(): InetAddress? = oscPortAddress

	override fun getOscReceiver(): OSCPortIn? = null
	override fun getPortIn(): Int = -1

	private fun processToe(foot: Bone, toe: Bone, side: FootSide, toeNumber: Int) {
		val footRot = foot.getGlobalRotation()
		val toeRot = toe.getGlobalRotation()
		val currentRelative = (footRot.inv() * toeRot)

		val euler = quaternionToEulerDegrees(currentRelative)

		val pitch = (euler.z + 90)

		val tipToe = pitch < -14f
		val bending = pitch > 15f && !tipToe

		setTipToeValueBool(side, tipToe)
		setToeValueBool(toeNumber, side, bending)
		setToeValueFloat(toeNumber, side, clamp(pitch / 30f, -1f, 1f))
	}

	fun setToeValueBool(toeNumber: Int, footSide: FootSide, value: Boolean) {
		val key = "/avatar/parameters/Toe${footSide.name}${toeNumber + 1}Bool"
		if (persistedValues[key] != value) {
			sendOsc(key, value)
			persistedValues[key] = value
		}
	}

	fun setTipToeValueBool(footSide: FootSide, value: Boolean) {
		val key = "/avatar/parameters/TipToes${footSide.name}"
		if (persistedValues[key] != value) {
			sendOsc(key, value)
			persistedValues[key] = value
		}
	}

	fun setToeValueFloat(toeNumber: Int, footSide: FootSide, value: Float) {
		val key = "/avatar/parameters/Toe${footSide.name}${toeNumber + 1}Float"
		sendOsc(key, value)
	}

	fun getToeValue(toeNumber: Int, footSide: FootSide): Boolean {
		val key = "/avatar/parameters/Toe${footSide.name}${toeNumber + 1}"
		return persistedValues[key] ?: false
	}

	fun getTipToeValue(footSide: FootSide): Boolean {
		val key = "/avatar/parameters/Toe${footSide.name}"
		return persistedValues[key] ?: false
	}

	private fun sendOsc(address: String, value: Any) {
		try {
			val message = OSCMessage(address, listOf(value))
			oscPort?.send(message)
		} catch (e: Exception) {
			println("OSC send failed for $address: ${e.message}")
		}
	}

	enum class FootSide { Left, Right }
}

private fun quaternionToEulerDegrees(q: Quaternion): Vector3 {
	val qn = q

	val sinrCosp = 2f * (qn.w * qn.x + qn.y * qn.z)
	val cosrCosp = 1f - 2f * (qn.x * qn.x + qn.y * qn.y)
	val roll = atan2(sinrCosp, cosrCosp)

	val sinp = 2f * (qn.w * qn.y - qn.z * qn.x)
	val pitch = if (abs(sinp) >= 1f)
		(PI.toFloat() / 2f) * sign(sinp)
	else asin(sinp)

	val sinyCosp = 2f * (qn.w * qn.z + qn.x * qn.y)
	val cosyCosp = 1f - 2f * (qn.y * qn.y + qn.z * qn.z)
	val yaw = atan2(sinyCosp, cosyCosp)

	return Vector3(
		pitch.toDegrees(),
		yaw.toDegrees(),
		roll.toDegrees()
	)
}


private fun Float.toDegrees() = Math.toDegrees(this.toDouble()).toFloat()
private fun clamp(v: Float, min: Float, max: Float): Float = max(min, min(v, max))
