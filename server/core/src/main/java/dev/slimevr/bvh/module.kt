package dev.slimevr.bvh

import dev.slimevr.config.ConfigStorage
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import kotlinx.coroutines.CoroutineScope

data class BVHState(
	val recording: Boolean = false,
	val recordingPath: String? = null,
)

sealed interface BVHActions {
	data class StartRecording(val path: String) : BVHActions
	data object StopRecording : BVHActions
}

typealias BVHContext = Context<BVHState, BVHActions>
typealias BVHBehaviourType = Behaviour<BVHState, BVHActions, BVHManager>

class BVHManager(val context: BVHContext) {
	val isRecording get() = context.state.value.recording

	fun startRecording(path: String) = context.dispatch(BVHActions.StartRecording(path))
	fun stopRecording() = context.dispatch(BVHActions.StopRecording)
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(skeleton: Skeleton, storage: ConfigStorage, scope: CoroutineScope): BVHManager {
			val context = Context.create(
				initialState = BVHState(),
				scope = scope,
				behaviours = listOf(BVHRecordingBehaviour(skeleton, storage)),
				name = "BVH",
			)
			return BVHManager(context).also { manager -> manager.startObserving() }
		}
	}
}
