package dev.slimevr.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Combines every child's state into one list, re-emitting when a child changes or the collection
 * does.
 *
 * Children emit far more often than most answers change, so project each to the fields that
 * matter and let the per-child [kotlinx.coroutines.flow.distinctUntilChanged] drop the rest.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal inline fun <P, C, reified S> allContextStates(
	parent: Flow<P>,
	crossinline select: (P) -> Collection<C>,
	crossinline stateOf: (C) -> Flow<S>,
): Flow<List<S>> = parent.flatMapLatest { parentState ->
	val items = select(parentState)
	if (items.isEmpty()) return@flatMapLatest flowOf(emptyList())
	combine(items.map { item -> stateOf(item) }) { states -> states.toList() }
}
