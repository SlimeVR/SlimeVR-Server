package dev.slimevr.context.debug

interface DebugMiddleware<S, A> {
	fun onDispatch(caller: String?, before: S, action: A, after: S)
	fun onDispatchAll(caller: String?, before: S, actions: List<A>, after: S)
}
