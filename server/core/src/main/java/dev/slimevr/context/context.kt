package dev.slimevr.context

import dev.slimevr.context.debug.DebugMiddleware
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.CopyOnWriteArrayList

interface Behaviour<S, A, C> {
	fun reduce(state: S, action: A): S = state
	fun observe(receiver: C) {}
}

class Context<S, A>(
	private val mutableStateFlow: MutableStateFlow<S>,
	val reducer: (S, A) -> S,
	private val parentScope: CoroutineScope,
	val behaviours: CopyOnWriteArrayList<Behaviour<S, A, *>>,
	private val debugMiddleware: DebugMiddleware<S, A>? = null,
	val name: String,
) {
	private val job = SupervisorJob(parentScope.coroutineContext[Job])
	val scope = CoroutineScope(parentScope.coroutineContext + job + CoroutineName(name))
	val state: StateFlow<S> = mutableStateFlow.asStateFlow()

	fun dispose() {
		job.cancel()
	}

	fun dispatch(action: A) {
		if (debugMiddleware == null) {
			mutableStateFlow.update { currentState -> reducer(currentState, action) }
			return
		}
		val caller = captureCallerBehaviour()
		val before = mutableStateFlow.value
		mutableStateFlow.update { currentState -> reducer(currentState, action) }
		debugMiddleware.onDispatch(caller, before, action, mutableStateFlow.value)
	}

	fun dispatchAll(actions: List<A>) {
		if (debugMiddleware == null) {
			mutableStateFlow.update { currentState ->
				actions.fold(currentState) { s, action -> reducer(s, action) }
			}
			return
		}
		val caller = captureCallerBehaviour()
		val before = mutableStateFlow.value
		mutableStateFlow.update { currentState ->
			actions.fold(currentState) { s, action -> reducer(s, action) }
		}
		debugMiddleware.onDispatchAll(caller, before, actions, mutableStateFlow.value)
	}

	fun <C> observeAll(receiver: C) = behaviours.forEach { behaviour ->
		@Suppress("UNCHECKED_CAST")
		(behaviour as Behaviour<S, A, C>).observe(receiver)
	}

	private fun captureCallerBehaviour(): String? {
		val knownBehaviourClasses = behaviours.mapTo(HashSet()) { b -> b::class.java.name }
		return Thread.currentThread().stackTrace
			.firstOrNull { frame -> frame.className in knownBehaviourClasses }
			?.className?.substringAfterLast('.')
	}

	companion object {
		val debugEnabled: Boolean = System.getProperty("slimevr.debug.context") == "true" ||
			System.getenv("SLIMEVR_DEBUG_CONTEXT") == "true"

		fun <S, A> create(
			initialState: S,
			scope: CoroutineScope,
			behaviours: List<Behaviour<S, A, *>>,
			debugMiddleware: DebugMiddleware<S, A>? = null,
			name: String,
		): Context<S, A> {
			val mutableStateFlow = MutableStateFlow(initialState)
			val reducer: (S, A) -> S = { currentState, action ->
				behaviours.fold(currentState) { s, b -> b.reduce(s, action) }
			}
			return Context(mutableStateFlow, reducer, scope, CopyOnWriteArrayList(behaviours), if (debugEnabled) debugMiddleware else null, name)
		}
	}
}
