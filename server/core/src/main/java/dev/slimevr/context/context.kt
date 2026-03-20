package dev.slimevr.context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BasicModule<S, A>(
	val reducer: ((S, A) -> S)? = null,
	val observer: ((Context<S, A>) -> Unit)? = null,
)

data class Context<S, in A>(
	val state: StateFlow<S>,
	val dispatch: suspend (A) -> Unit,
	val dispatchAll: suspend (List<A>) -> Unit,
	val scope: CoroutineScope,
)

fun <S, A> createContext(
	initialState: S,
	scope: CoroutineScope,
	reducers: List<((S, A) -> S)?>,
): Context<S, A> {
	val mutableStateFlow = MutableStateFlow(initialState)

	val applyAction: (S, A) -> S = { currentState, action ->
		reducers.filterNotNull().fold(currentState) { s, reducer -> reducer(s, action) }
	}

	val dispatch: suspend (A) -> Unit = { action ->
		mutableStateFlow.update { applyAction(it, action) }
	}

	val dispatchAll: suspend (List<A>) -> Unit = { actions ->
		mutableStateFlow.update { currentState ->
			actions.fold(currentState) { s, action -> applyAction(s, action) }
		}
	}
	val context = Context(mutableStateFlow.asStateFlow(), dispatch, dispatchAll, scope)
	return context
}
