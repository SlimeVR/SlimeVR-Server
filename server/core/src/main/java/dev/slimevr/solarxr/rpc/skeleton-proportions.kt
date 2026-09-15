package dev.slimevr.solarxr.rpc

import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigActions
import dev.slimevr.resourcepacks.bones.proportionKey
import dev.slimevr.resourcepacks.bones.skeletonBoneOf
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeSkeletonProportionsRequest
import solarxr_protocol.rpc.ChangeUserHeightRequest
import solarxr_protocol.rpc.SkeletonBone
import solarxr_protocol.rpc.SkeletonPart
import solarxr_protocol.rpc.SkeletonProportionsRequest
import solarxr_protocol.rpc.SkeletonProportionsResetAllRequest
import solarxr_protocol.rpc.SkeletonProportionsResponse

private const val MIN_HEIGHT = 0.9f

internal fun buildSkeletonProportionsResponse(state: SkeletonState): SkeletonProportionsResponse = SkeletonProportionsResponse(
	skeletonParts = state.proportionValues.mapNotNull { (key, value) -> skeletonBoneOf(key)?.let { SkeletonPart(it, value) } },
	skeletonHeight = state.skeletonHeight,
)

class SkeletonProportionsBehaviour(
	private val userConfig: UserConfig,
	private val skeleton: Skeleton,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		skeleton.context.state
			.distinctUntilChangedBy { it.proportionValues }
			.drop(1)
			.onEach { state ->
				val configResponse = buildSkeletonProportionsResponse(state)
				receiver.sendRpc(configResponse)
			}
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<SkeletonProportionsRequest> {
			receiver.sendRpc(buildSkeletonProportionsResponse(skeleton.context.state.value))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeUserHeightRequest> { req ->
			val hmdHeight = req.hmdHeight ?: return@on
			val floorHeight = req.floorHeight ?: 0f
			val height = hmdHeight - floorHeight
			if (height >= MIN_HEIGHT) {
				userConfig.context.dispatch(
					UserConfigActions.Update {
						copy(userHeight = height, proportions = skeleton.definition.heightScaledProportionValues(height))
					},
				)
			}
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<SkeletonProportionsResetAllRequest> {
			val height = userConfig.context.state.value.data.userHeight
			if (height >= MIN_HEIGHT) {
				val defaults = skeleton.definition.defaultProportionValues(height)
				userConfig.context.dispatch(UserConfigActions.Update { copy(proportions = defaults) })
			}
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeSkeletonProportionsRequest> { req ->
			val bone = req.bone
			if (bone == SkeletonBone.NONE) return@on
			val key = bone.proportionKey
			val proportion = skeleton.definition.proportions[key] ?: return@on
			val value = req.value.coerceIn(proportion.minimum ?: Float.NEGATIVE_INFINITY, proportion.maximum ?: Float.POSITIVE_INFINITY)

			userConfig.context.dispatch(UserConfigActions.Update { copy(proportions = proportions + (key to value)) })
		}.launchIn(receiver.context.scope)
	}
}
