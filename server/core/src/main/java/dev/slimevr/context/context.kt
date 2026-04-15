package dev.slimevr.context

import kotlinx.coroutines.CoroutineScope
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
	private val applyAction: (S, A) -> S,
	val scope: CoroutineScope,
	val behaviours: CopyOnWriteArrayList<Behaviour<S, A, *>>,
) {
	val state: StateFlow<S> = mutableStateFlow.asStateFlow()

	fun dispatch(action: A) {
		mutableStateFlow.update {
			applyAction(it, action)
		}
	}


	fun <C> observeAll(receiver: C) = behaviours.forEach { behaviour ->
		@Suppress("UNCHECKED_CAST")
		(behaviour as Behaviour<S, A, C>).observe(receiver)
	}

	fun dispatchAll(actions: List<A>) {
		mutableStateFlow.update { currentState ->
			actions.fold(currentState) { s, action -> applyAction(s, action) }
		}
	}

	companion object {
		fun <S, A> create(
			initialState: S,
			scope: CoroutineScope,
			behaviours: List<Behaviour<S, A, *>>,
		): Context<S, A> {
			val mutableStateFlow = MutableStateFlow(initialState)
			val applyAction: (S, A) -> S = { currentState, action ->
				behaviours.fold(currentState) { s, b -> b.reduce(s, action) }
			}
			return Context(mutableStateFlow, applyAction, scope, CopyOnWriteArrayList(behaviours))
		}
	}
}
