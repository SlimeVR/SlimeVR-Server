package dev.slimevr.context

import dev.slimevr.context.debug.ContextDebug
import dev.slimevr.context.debug.DebugMiddleware
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface Behaviour<S, A, C> {
	fun reduce(state: S, action: A): S = state
	fun observe(receiver: C) {}
}

// TODO: missing remove + prob a unobserve or something to clear the stuff launched
// in a observe -> each behaviour is prob gonna need its own scope
class BehaviourList<S, A>(
	initial: List<Behaviour<S, A, *>>,
) {
	private val mutex = Mutex()
	private var items: List<Behaviour<S, A, *>> = initial.toList()

	fun addAll(newItems: List<Behaviour<S, A, *>>) = runBlocking {
		mutex.withLock {
			items = items + newItems
		}
	}

	fun snapshot(): List<Behaviour<S, A, *>> = runBlocking {
		mutex.withLock {
			items.toList()
		}
	}
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
			behaviours: List<Behaviour<S, A, *>>,
			debugMiddleware: DebugMiddleware<S, A>? = null,
			name: String,
		): ManagedContext<S, A> {
			val job = SupervisorJob(scope.coroutineContext[Job])
			val scopeWithJob = CoroutineScope(scope.coroutineContext + job)
			val context = Context.create(initialState, scopeWithJob, behaviours, debugMiddleware, name)
			return ManagedContext(context, job)
		}
	}
}

class Context<S, A>(
	private val mutableStateFlow: MutableStateFlow<S>,
	val scope: CoroutineScope,
	val behaviours: BehaviourList<S, A>,
	private val debugMiddleware: DebugMiddleware<S, A>? = null,
) {
	val state: StateFlow<S> = mutableStateFlow.asStateFlow()

	fun dispatch(action: A) {
		if (debugMiddleware == null) {
			mutableStateFlow.update { currentState -> reduce(currentState, action) }
			return
		}
		val caller = captureCallerBehaviour()
		val before = mutableStateFlow.value
		mutableStateFlow.update { currentState -> reduce(currentState, action) }
		debugMiddleware.onDispatch(caller, before, action, mutableStateFlow.value)
	}

	fun dispatchAll(actions: List<A>) {
		if (debugMiddleware == null) {
			mutableStateFlow.update { currentState ->
				actions.fold(currentState) { s, action -> reduce(s, action) }
			}
			return
		}
		val caller = captureCallerBehaviour()
		val before = mutableStateFlow.value
		mutableStateFlow.update { currentState ->
			actions.fold(currentState) { s, action -> reduce(s, action) }
		}
		debugMiddleware.onDispatchAll(caller, before, actions, mutableStateFlow.value)
	}

	fun <C> observeAll(receiver: C) = behaviours.snapshot().forEach { behaviour ->
		@Suppress("UNCHECKED_CAST")
		(behaviour as Behaviour<S, A, C>).observe(receiver)
	}

	private fun reduce(currentState: S, action: A): S = behaviours.snapshot().fold(currentState) { state, behaviour ->
		behaviour.reduce(state, action)
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
			behaviours: List<Behaviour<S, A, *>>,
			debugMiddleware: DebugMiddleware<S, A>? = null,
			name: String,
		): Context<S, A> {
			val mutableStateFlow = MutableStateFlow(initialState)
			val contextJob = SupervisorJob(scope.coroutineContext[Job])
			val scopeWithName = CoroutineScope(scope.coroutineContext + contextJob + CoroutineName(name))
			val effectiveDebugMiddleware = if (ContextDebug.enabled) debugMiddleware else null
			val context = Context(
				mutableStateFlow,
				scopeWithName,
				BehaviourList(behaviours),
				effectiveDebugMiddleware,
			)
			effectiveDebugMiddleware?.init(context)
			return context
		}
	}
}
