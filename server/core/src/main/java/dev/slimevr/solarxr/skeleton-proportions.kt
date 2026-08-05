package dev.slimevr.solarxr

import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigActions
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.computeAllDefaultProportionsByBone
import dev.slimevr.skeleton.computeDefaultProportionsByBone
import dev.slimevr.skeleton.configToBoneValues
import dev.slimevr.skeleton.height
import dev.slimevr.skeleton.toBoneValues
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeSkeletonProportionsRequest
import solarxr_protocol.rpc.ChangeUserHeightRequest
import solarxr_protocol.rpc.SkeletonBone
import solarxr_protocol.rpc.SkeletonPart
import solarxr_protocol.rpc.SkeletonProportionsRequest
import solarxr_protocol.rpc.SkeletonProportionsResetAllRequest
import solarxr_protocol.rpc.SkeletonProportionsResponse

private const val MIN_HEIGHT = 0.9f

class SkeletonProportionsBehaviour(private val userConfig: UserConfig) : SolarXRBridgeBehaviour {
	private fun buildConfigResponse(boneValues: Map<SkeletonBone, Float>): SkeletonProportionsResponse {
		val skeletonParts = boneValues.map { (offset, bone) -> SkeletonPart(offset, bone) }
		return SkeletonProportionsResponse(skeletonParts = skeletonParts, skeletonHeight = boneValues.height())
	}

	override fun observe(receiver: SolarXRBridge) {
		userConfig.context.state
			.map { it.data }
			.distinctUntilChangedBy { it.proportions to it.userHeight }
			.drop(1)
			.onEach { config ->
				val configResponse = buildConfigResponse(configToBoneValues(config.proportions))
				receiver.sendRpc(configResponse)
			}

		receiver.rpcDispatcher.on<SkeletonProportionsRequest> {
			receiver.sendRpc(buildConfigResponse(configToBoneValues(userConfig.context.state.value.data.proportions)))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeUserHeightRequest> { req ->
			val hmdHeight = req.hmdHeight ?: return@on
			val floorHeight = req.floorHeight ?: 0f
			val height = hmdHeight - floorHeight
			if (height >= MIN_HEIGHT) {
				userConfig.context.dispatch(
					UserConfigActions.Update {
						copy(userHeight = height, proportions = computeDefaultProportionsByBone(height))
					},
				)
			}
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<SkeletonProportionsResetAllRequest> {
			val height = userConfig.context.state.value.data.userHeight
			if (height >= MIN_HEIGHT) {
				val defaults = computeAllDefaultProportionsByBone(height)
				userConfig.context.dispatch(UserConfigActions.Update { copy(proportions = defaults) })
			}
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeSkeletonProportionsRequest> { req ->
			val bone = req.bone ?: return@on
			val value = req.value ?: return@on

			userConfig.context.dispatch(UserConfigActions.Update { copy(proportions = proportions + (bone.name to value)) })
		}.launchIn(receiver.context.scope)
	}
}
