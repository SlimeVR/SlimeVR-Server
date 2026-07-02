package dev.slimevr.context.debug

import dev.slimevr.context.Context

interface DebugMiddleware<S, A> {
	fun init(context: Context<S, A>) {}
	fun onDispatch(caller: String?, before: S, action: A, after: S)
	fun onDispatchAll(caller: String?, before: S, actions: List<A>, after: S)
}
