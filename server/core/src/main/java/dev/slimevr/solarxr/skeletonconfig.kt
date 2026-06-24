package dev.slimevr.solarxr

import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigActions
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.computeAllDefaultProportionsByBone
import dev.slimevr.skeleton.computeDefaultProportionsByBone
import dev.slimevr.skeleton.configToBoneValues
import dev.slimevr.skeleton.height
import dev.slimevr.skeleton.toBoneValues
import solarxr_protocol.rpc.ChangeSkeletonProportionsRequest
import solarxr_protocol.rpc.ChangeSkeletonSettingsRequest
import solarxr_protocol.rpc.SkeletonPart
import solarxr_protocol.rpc.SkeletonProportionsRequest
import solarxr_protocol.rpc.SkeletonProportionsResetAllRequest
import solarxr_protocol.rpc.SkeletonProportionsResponse

private const val MIN_HEIGHT = 1.0f

class SkeletonBehaviour(
	private val userConfig: UserConfig,
	private val skeleton: Skeleton,
) : SolarXRBridgeBehaviour {

	private fun buildConfigResponse(): SkeletonProportionsResponse {
		val proportions = userConfig.context.state.value.data.proportions
		val bones = skeleton.context.state.value.rawBones
		val skeletonParts = bones.mapValues { it.value.offset }.toBoneValues().map { (offset, bone) -> SkeletonPart(offset, bone) }
		val expanded = configToBoneValues(proportions)
		val userHeight = if (expanded.isNotEmpty()) {
			expanded.height()
		} else {
			skeleton.context.state.value.userHeight
		}
		return SkeletonProportionsResponse(skeletonParts = skeletonParts, userHeight = userHeight)
	}

	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<SkeletonProportionsRequest> {
			receiver.sendRpc(buildConfigResponse())
		}

		receiver.rpcDispatcher.on<ChangeSkeletonSettingsRequest> { req ->
			req.skeletonHeight?.let { skeletonHeight ->
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

		receiver.rpcDispatcher.on<SkeletonProportionsResetAllRequest> {
			val height = userConfig.context.state.value.data.userHeight
			if (height >= MIN_HEIGHT) {
				val defaults = computeAllDefaultProportionsByBone(height)
				userConfig.context.dispatch(UserConfigActions.Update { copy(proportions = defaults) })
			}
			receiver.sendRpc(buildConfigResponse())
		}

		receiver.rpcDispatcher.on< ChangeSkeletonProportionsRequest> { req ->
			val bone = req.bone ?: return@on
			val value = req.value ?: return@on

			userConfig.context.dispatch(
				UserConfigActions.Update { copy(proportions = proportions + (bone.name to value)) },
			)
			receiver.sendRpc(buildConfigResponse())
		}
	}
}
