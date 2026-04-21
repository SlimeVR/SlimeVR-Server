package dev.slimevr.solarxr

import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigActions
import dev.slimevr.skeleton.SKELETON_BONE_TO_BODY_PARTS
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.computeAllDefaultProportionsByBone
import dev.slimevr.skeleton.computeDefaultProportionsByBone
import dev.slimevr.skeleton.computeUserHeight
import dev.slimevr.skeleton.expandProportions
import solarxr_protocol.rpc.ChangeSettingsRequest
import solarxr_protocol.rpc.ChangeSkeletonConfigRequest
import solarxr_protocol.rpc.SkeletonConfigRequest
import solarxr_protocol.rpc.SkeletonConfigResponse
import solarxr_protocol.rpc.SkeletonPart
import solarxr_protocol.rpc.SkeletonResetAllRequest

private const val MIN_HEIGHT = 1.0f

class SkeletonBehaviour(
	private val userConfig: UserConfig,
	private val skeleton: Skeleton,
) : SolarXRBridgeBehaviour {

	private fun buildConfigResponse(): SkeletonConfigResponse {
		val proportions = userConfig.context.state.value.data.proportions
		val bones = skeleton.context.state.value.bones
		val skeletonParts = SKELETON_BONE_TO_BODY_PARTS.mapNotNull { (skeletonBone, bodyParts) ->
			val length = proportions[skeletonBone.name]
				?: bodyParts.mapNotNull { bones[it]?.length }.average().takeIf { !it.isNaN() }?.toFloat()
				?: return@mapNotNull null
			SkeletonPart(bone = skeletonBone, value = length)
		}
		val expanded = expandProportions(proportions)
		val userHeight = if (expanded.isNotEmpty()) {
			computeUserHeight(expanded).toFloat()
		} else {
			skeleton.context.state.value.userHeight.toFloat()
		}
		return SkeletonConfigResponse(skeletonParts = skeletonParts, userHeight = userHeight)
	}

	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<SkeletonConfigRequest> {
			receiver.sendRpc(buildConfigResponse())
		}

		receiver.rpcDispatcher.on<ChangeSettingsRequest> { req ->
			req.modelSettings?.skeletonHeight?.let { skeletonHeight ->
				val hmdHeight = skeletonHeight.hmdHeight ?: return@let
				val floorHeight = skeletonHeight.floorHeight ?: 0f
				val height = hmdHeight - floorHeight
				if (height >= MIN_HEIGHT) {
					userConfig.context.dispatch(
						UserConfigActions.Update {
							copy(userHeight = height, proportions = computeDefaultProportionsByBone(height))
						},
					)
				}
			}
		}

		receiver.rpcDispatcher.on<SkeletonResetAllRequest> {
			val height = userConfig.context.state.value.data.userHeight
			if (height >= MIN_HEIGHT) {
				val defaults = computeAllDefaultProportionsByBone(height)
				userConfig.context.dispatch(UserConfigActions.Update { copy(proportions = defaults) })
			}
			receiver.sendRpc(buildConfigResponse())
		}

		receiver.rpcDispatcher.on<ChangeSkeletonConfigRequest> { req ->
			val bone = req.bone ?: return@on
			userConfig.context.dispatch(
				UserConfigActions.Update { copy(proportions = proportions + (bone.name to req.value)) },
			)
			receiver.sendRpc(buildConfigResponse())
		}
	}
}
