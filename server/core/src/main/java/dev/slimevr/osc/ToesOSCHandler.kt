package dev.slimevr.tracking

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortIn
import com.illposed.osc.transport.OSCPortOut
import dev.slimevr.VRServer
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.osc.OSCHandler
import dev.slimevr.osc.VRCOSCHandler
import dev.slimevr.tracking.trackers.Tracker
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
		if (config.enabled) {
			try {
				// LEFT FOOT + TOES
				server.humanPoseManager.skeleton.leftFootTracker?.let { leftFoot ->
					var lastAssigned: Tracker? = null
					var next: Tracker?

					next = server.humanPoseManager.skeleton.leftToe1Tracker
					if (next != null) {
						lastAssigned = next
						processToeSide(leftFoot, lastAssigned, FootSide.Left, 0)

						next = server.humanPoseManager.skeleton.leftToe2Tracker
						if (next != null) lastAssigned = next
						processToeSide(leftFoot, lastAssigned, FootSide.Left, 1)
						processToeSide(leftFoot, lastAssigned, FootSide.Left, 2)

						next = server.humanPoseManager.skeleton.leftToe3Tracker
						if (next != null) lastAssigned = next
						processToeSide(leftFoot, lastAssigned, FootSide.Left, 3)
						processToeSide(leftFoot, lastAssigned, FootSide.Left, 4)
					}
				}

				// RIGHT FOOT + TOES
				server.humanPoseManager.skeleton.rightFootTracker?.let { rightFoot ->
					var lastAssigned: Tracker? = null
					var next: Tracker?

					next = server.humanPoseManager.skeleton.rightToe1Tracker
					if (next != null) {
						lastAssigned = next
						processToeSide(rightFoot, lastAssigned, FootSide.Right, 0)

						next = server.humanPoseManager.skeleton.rightToe2Tracker
						if (next != null) lastAssigned = next
						processToeSide(rightFoot, lastAssigned, FootSide.Right, 1)
						processToeSide(rightFoot, lastAssigned, FootSide.Right, 2)

						next = server.humanPoseManager.skeleton.rightToe3Tracker
						if (next != null) lastAssigned = next
						processToeSide(rightFoot, lastAssigned, FootSide.Right, 3)
						processToeSide(rightFoot, lastAssigned, FootSide.Right, 4)
					}
				}
			} catch (ex: Exception) {
				println("ToeLoop error: ${ex.message}")
			}
		}
	}


	override fun getOscSender(): OSCPortOut? = oscPort
	override fun getPortOut(): Int = oscPortPort
	override fun getAddress(): InetAddress? = oscPortAddress

	override fun getOscReceiver(): OSCPortIn? = null
	override fun getPortIn(): Int = -1

	private fun processToeSide(foot: Tracker, toe: Tracker, side: FootSide, toeNumber: Int) {
		val footRot = foot.getRotation()
		val toeRot = toe.getRotation()
		val currentRelative = (footRot.conj() * toeRot)

		val euler = quaternionToEulerDegrees(currentRelative)

		// Left foot seems to give us inverted values for some reason, so we undo the inversion.
		val pitch = euler.y * if (side == FootSide.Left) -1f else 1f

		val tipToe = pitch < -14f
		val bending = pitch > 15f && !tipToe

		setTipToeValueBool(side, tipToe)
		setToeValueBool(toeNumber, side, bending)
		setToeValueFloat(toeNumber, side, -clamp(pitch / 30f, -1f, 1f))
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
