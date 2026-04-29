package dev.slimevr.bvh

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import kotlinx.coroutines.CoroutineScope
import java.nio.file.Path

data class BVHState(
	val recording: Boolean = false,
	val recordingPath: Path? = null,
)

sealed interface BVHActions {
	data class StartRecording(val path: Path) : BVHActions
	data object StopRecording : BVHActions
}

typealias BVHContext = Context<BVHState, BVHActions>
typealias BVHBehaviourType = Behaviour<BVHState, BVHActions, BVHManager>

class BVHManager(val context: BVHContext) {
	val isRecording get() = context.state.value.recording

	fun startRecording(path: Path) = context.dispatch(BVHActions.StartRecording(path))
	fun stopRecording() = context.dispatch(BVHActions.StopRecording)
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(skeleton: Skeleton, scope: CoroutineScope): BVHManager {
			val context = Context.create(
				initialState = BVHState(),
				scope = scope,
				behaviours = listOf(BVHRecordingBehaviour(skeleton)),
				name = "BVH",
			)
			return BVHManager(context).also { manager -> manager.startObserving() }
		}
	}
}
