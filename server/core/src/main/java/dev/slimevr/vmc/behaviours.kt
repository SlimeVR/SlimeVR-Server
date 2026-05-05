package dev.slimevr.vmc

import dev.slimevr.AppLogger
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.context.Behaviour
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.OscReceiver
import dev.slimevr.osc.OscSender
import dev.slimevr.skeleton.BONE_TAIL_DIRECTIONS
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.buildBones
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

	override fun reduce(state: VMCState, action: VMCActions) = when (action) {
		is VMCActions.UpdateConfig -> state.copy(config = action.config)
	}

	override fun observe(receiver: VMCManager) {
		var sender: OscSender? = null

		receiver.context.state
			.map { it.config }
			.distinctUntilChanged()
			.onEach { config ->
				settings.context.dispatch(SettingsActions.Update { copy(vmcConfig = config) })
			}.launchIn(receiver.context.scope)

		receiver.context.state
			.map { Triple(it.config.enabled, it.config.portOut, it.config.address) }
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
				val config = receiver.context.state.value.config
				val currentTime = System.currentTimeMillis()
				receiver.context.scope.launch {
					try {
						s.send(buildBundle(bones, config, currentTime))
					} catch (e: Exception) {
						AppLogger.vmc.error("Failed to send VMC frame", e)
					}
				}
			}.launchIn(receiver.context.scope)
	}

	private fun buildBundle(bones: Map<BodyPart, BoneState>, config: VMCConfig, currentTime: Long): OscBundle = OscBundle(1L, buildMessages(bones, config, currentTime).map { OscContent.Message(it) }.toList())

	// TODO: coordinate space conversion to VMC space
	private fun boneMessage(address: String, name: String, pos: Vector3, rot: Quaternion): OscMessage = OscMessage(
		address,
		listOf(
			OscArg.String(name),
			OscArg.Float(pos.x),
			OscArg.Float(pos.y),
			OscArg.Float(pos.z),
			OscArg.Float(rot.x),
			OscArg.Float(rot.y),
			OscArg.Float(rot.z),
			OscArg.Float(rot.w),
		),
	)

	private fun buildMessages(bones: Map<BodyPart, BoneState>, config: VMCConfig, currentTime: Long): Sequence<OscMessage> = sequence {
		val hipPos = bones[BodyPart.HIP]?.headPosition ?: Vector3.NULL
		val vmcBones = buildBones(
			skeleton.context.state.value,
			rootHead = hipPos,
			hierarchy = iterateVMCHierarchy(),
			tailDirections = BONE_TAIL_DIRECTIONS,
		)

		val time = (currentTime - initTime) / 1000f
		yield(OscMessage("/VMC/Ext/T", listOf(OscArg.Float(time))))
		yield(OscMessage("/VMC/Ext/OK", listOf(OscArg.Int(1))))
		yield(
			OscMessage(
				"/VMC/Ext/Root/Pos",
				listOf(
					OscArg.String("root"),
					OscArg.Float(0f),
					OscArg.Float(0f),
					OscArg.Float(0f),
					OscArg.Float(0f),
					OscArg.Float(0f),
					OscArg.Float(0f),
					OscArg.Float(1f),
				),
			),
		)

		// TODO: implement mirrorTracking
		for ((bodyPart, unityName) in BODY_PART_TO_UNITY_BONE) {
			val bone = vmcBones[bodyPart] ?: continue
			yield(boneMessage("/VMC/Ext/Bone/Pos", unityName, bone.localHeadPosition, bone.localRotation))
		}

		val headBone = vmcBones[BodyPart.HEAD]
		if (headBone != null) {
			yield(boneMessage("/VMC/Ext/Hmd/Pos", "human://HEAD", headBone.localHeadPosition, headBone.localRotation))
		}
	}
}

class VMCInputBehaviour : Behaviour<VMCState, VMCActions, VMCManager> {
	override fun reduce(state: VMCState, action: VMCActions) = when (action) {
		is VMCActions.UpdateConfig -> state.copy(config = action.config)
	}

	override fun observe(receiver: VMCManager) {
		var oscReceiver: OscReceiver? = null

		receiver.context.state
			.map { it.config.portIn }
			.distinctUntilChanged()
			.onEach { portIn ->
				oscReceiver?.close()
				oscReceiver = null
				if (receiver.context.state.value.config.enabled) {
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
