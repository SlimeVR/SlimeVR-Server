package dev.slimevr.vmc

import dev.slimevr.config.Settings
import dev.slimevr.config.VMCConfig
import dev.slimevr.context.Behaviour
import dev.slimevr.logging.AppLogger
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.OscReceiver
import dev.slimevr.osc.OscSender
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Skeleton
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart

class VMCOutputBehaviour(
	private val skeleton: Skeleton,
	private val settings: Settings,
) : VMCBehaviourType {
	private val initTime = System.currentTimeMillis()

	override fun observe(receiver: VMCManager) {
		var sender: OscSender? = null
		var vrmGeometry: VrmGeometry? = null

		settings.context.state
			.map { it.data.vmcConfig.vrmJson }
			.distinctUntilChanged()
			.onEach { vrmJson ->
				vrmGeometry = vrmJson?.takeIf { value -> value.isNotEmpty() }?.let { value ->
					try {
						buildVrmGeometry(VrmReader(value))
					} catch (e: Exception) {
						AppLogger.vmc.error("Failed to parse VRM JSON", e)
						null
					}
				}
			}.launchIn(receiver.context.scope)

		settings.context.state
			.map { Triple(it.data.vmcConfig.enabled, it.data.vmcConfig.portOut, it.data.vmcConfig.address) }
			.distinctUntilChanged()
			.onEach { (enabled, port, addr) ->
				sender?.close()
				sender = null
				if (enabled) {
					sender = OscSender(addr, port)
					AppLogger.vmc.info("VMC output started: $addr:$port")
					receiver.context.scope.launch {
						try {
							sender?.send(OscMessage("/VMC/Ext/Req", emptyList()))
						} catch (e: Exception) {
							AppLogger.vmc.error("Failed to send init Req", e)
						}
					}
				}
			}.launchIn(receiver.context.scope)

		skeleton.computed
			.onEach { bones ->
				val s = sender ?: return@onEach
				val config = settings.context.state.value.data.vmcConfig
				val currentTime = System.currentTimeMillis()
				val vrm = vrmGeometry
				receiver.context.scope.launch {
					try {
						s.send(buildBundle(bones, config, currentTime, vrm))
					} catch (e: Exception) {
						AppLogger.vmc.error("Failed to send VMC frame", e)
					}
				}
			}.launchIn(receiver.context.scope)
	}

	private fun buildBundle(bones: Map<BodyPart, BoneState>, config: VMCConfig, currentTime: Long, vrm: VrmGeometry?): OscBundle = OscBundle(1L, buildMessages(bones, config, currentTime, vrm).map { msg -> OscContent.Message(msg) }.toList())

	private fun transformMessage(address: String, name: String, pos: Vector3, rot: Quaternion): OscMessage = OscMessage(
		address,
		listOf(
			OscArg.String(name),
			OscArg.Float(pos.x),
			OscArg.Float(pos.y),
			OscArg.Float(-pos.z),
			OscArg.Float(rot.x),
			OscArg.Float(rot.y),
			OscArg.Float(-rot.z),
			OscArg.Float(-rot.w),
		),
	)

	private fun buildMessages(bones: Map<BodyPart, BoneState>, config: VMCConfig, currentTime: Long, vrm: VrmGeometry?): Sequence<OscMessage> = sequence {
		val time = (currentTime - initTime) / 1000f
		yield(OscMessage("/VMC/Ext/T", listOf(OscArg.Float(time))))
		yield(OscMessage("/VMC/Ext/OK", listOf(OscArg.Int(1))))

		// Send the origin as root
		yield(transformMessage("/VMC/Ext/Root/Pos", "root", Vector3.NULL, Quaternion.IDENTITY))

		// TODO UpperChest + shoulders affecting arms local rot
		// TODO Don't send fingers if we don't have any tracker for them
		for ((targetBodyPart, unityName) in BODY_PART_TO_UNITY_BONE) {
			val targetParentBodyPart = VMC_BONE_PARENTS[targetBodyPart]
			val trackingBodyPart = if (config.mirrorTracking) vmcMirrorSource(targetBodyPart) else targetBodyPart
			val trackingBone = bones[trackingBodyPart] ?: continue

			if (targetParentBodyPart == null) {
				// TODO anchorHip https://github.com/SlimeVR/SlimeVR-Server/blob/main/server/core/src/main/java/dev/slimevr/osc/VMCHandler.kt#L371
				val pos = vrm?.hipLocalPosition ?: Vector3.NULL
				val rot = vmcLocalRotation(trackingBone, null, targetBodyPart, null, config.mirrorTracking)
				yield(transformMessage("/VMC/Ext/Bone/Pos", unityName, pos, rot))
				continue
			}

			val trackingParentBodyPart = if (config.mirrorTracking) {
				vmcMirrorSource(targetParentBodyPart)
			} else {
				targetParentBodyPart
			}
			val trackingParent = bones[trackingParentBodyPart] ?: continue

			val pos = if (vrm != null) {
				vrm.bindOffsets[targetBodyPart] ?: Vector3.NULL
			} else {
				vmcLocalPosition(trackingBone, trackingParent, targetParentBodyPart, config.mirrorTracking)
			}
			val rot = vmcLocalRotation(
				trackingBone,
				trackingParent,
				targetBodyPart,
				targetParentBodyPart,
				config.mirrorTracking,
			)
			yield(transformMessage("/VMC/Ext/Bone/Pos", unityName, pos, rot))
		}
	}
}

class VMCInputBehaviour(private val settings: Settings) : Behaviour<VMCState, VMCActions, VMCManager> {
	override fun observe(receiver: VMCManager) {
		var oscReceiver: OscReceiver? = null

		settings.context.state
			.map { it.data.vmcConfig.portIn }
			.distinctUntilChanged()
			.onEach { portIn ->
				oscReceiver?.close()
				oscReceiver = null
				if (settings.context.state.value.data.vmcConfig.enabled) {
					oscReceiver = OscReceiver(portIn)
					AppLogger.vmc.info("VMC input listening on port $portIn")
					receiver.context.scope.launch {
						try {
							oscReceiver?.listenBundles { bundle ->
								for (content in bundle.contents) {
									when (content) {
										is OscContent.Message -> handleMessage(content.msg)

										is OscContent.Bundle -> content.bundle.contents.forEach {
											if (it is OscContent.Message) handleMessage(it.msg)
										}
									}
								}
							}
						} catch (e: Exception) {
							AppLogger.vmc.error("VMC receiver error: ${e.message}", e)
						}
					}
				}
			}.launchIn(receiver.context.scope)
	}

	private fun handleMessage(msg: OscMessage) {
		when (msg.address) {
			"/VMC/Ext/Bone/Pos" -> { /* TODO: Handle incoming bone rotation data */ }
			"/VMC/Ext/Hmd/Pos" -> { /* TODO: Handle incoming HMD position + rotation */ }
			"/VMC/Ext/Con/Pos" -> { /* TODO: Handle incoming controller position + rotation */ }
			"/VMC/Ext/Tra/Pos" -> { /* TODO: Handle incoming tracker position + rotation */ }
			"/VMC/Ext/Root/Pos" -> { /* TODO: Handle incoming root position/rotation offset */ }
		}
	}
}
