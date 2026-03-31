package dev.slimevr.context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface Behaviour<S, A, C> {
	fun reduce(state: S, action: A): S = state
	fun observe(receiver: C) {}
}

class Context<S, in A>(
	private val mutableStateFlow: MutableStateFlow<S>,
	private val applyAction: (S, A) -> S,
	val scope: CoroutineScope,
) {
	val state: StateFlow<S> = mutableStateFlow.asStateFlow()

	fun dispatch(action: A) {
		mutableStateFlow.update {
			applyAction(it, action)
		}
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
			return Context(mutableStateFlow, applyAction, scope)
		}
	}
}
