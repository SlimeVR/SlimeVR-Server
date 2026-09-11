package dev.slimevr.context

import dev.slimevr.context.debug.DebugMiddleware
import dev.slimevr.context.debug.contextDebugEnabled
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface Behaviour<in C> {
	fun observe(receiver: C) {}
}

class BehaviourList(
	initial: List<Behaviour<*>> = emptyList(),
) {
	@Volatile
	private var items: List<Behaviour<*>> = initial.toList()

	fun addAll(newItems: List<Behaviour<*>>) {
		items = items + newItems
	}

	fun snapshot(): List<Behaviour<*>> = items
}

class ManagedContext<S, A>(
	val context: Context<S, A>,
	private val supervisorJob: Job,
) {
	fun dispose() = supervisorJob.cancel()

	companion object {
		fun <S, A> create(
			initialState: S,
			scope: CoroutineScope,
			reducer: (S, A) -> S = { state, _ -> state },
			behaviours: List<Behaviour<*>> = emptyList(),
			debugMiddleware: DebugMiddleware<S, A>? = null,
			name: String,
		): ManagedContext<S, A> {
			val job = SupervisorJob(scope.coroutineContext[Job])
			val scopeWithJob = CoroutineScope(scope.coroutineContext + job)
			val context = Context.create(initialState, scopeWithJob, reducer, behaviours, debugMiddleware, name)
			return ManagedContext(context, job)
		}
	}
}

class Context<S, A>(
	private val mutableStateFlow: MutableStateFlow<S>,
	val scope: CoroutineScope,
	val reducer: (S, A) -> S = { state, _ -> state },
	val behaviours: BehaviourList,
	private val debugMiddleware: DebugMiddleware<S, A>? = null,
) {
	val state: StateFlow<S> = mutableStateFlow.asStateFlow()

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

	fun <C> observeAll(receiver: C) = behaviours.snapshot().forEach { behaviour ->
		@Suppress("UNCHECKED_CAST")
		(behaviour as Behaviour<C>).observe(receiver)
	}

	private fun captureCallerBehaviour(): String? {
		val knownBehaviourClasses = behaviours.snapshot().mapTo(HashSet()) { behaviour ->
			behaviour::class.qualifiedName ?: behaviour::class.simpleName.orEmpty()
		}
		return Throwable().stackTrace
			.firstOrNull { frame -> frame.className in knownBehaviourClasses }
			?.className
			?.substringAfterLast('.')
	}

	companion object {
		fun <S, A> create(
			initialState: S,
			scope: CoroutineScope,
			reducer: (S, A) -> S = { state, _ -> state },
			behaviours: List<Behaviour<*>> = emptyList(),
			debugMiddleware: DebugMiddleware<S, A>? = null,
			name: String,
		): Context<S, A> {
			val mutableStateFlow = MutableStateFlow(initialState)
			val contextJob = SupervisorJob(scope.coroutineContext[Job])
			val scopeWithName = CoroutineScope(scope.coroutineContext + contextJob + CoroutineName(name))
			val effectiveDebugMiddleware = if (contextDebugEnabled) debugMiddleware else null
			val context = Context(
				mutableStateFlow,
				scopeWithName,
				reducer,
				BehaviourList(behaviours),
				effectiveDebugMiddleware,
			)
			effectiveDebugMiddleware?.init(context)
			return context
		}
	}
}
