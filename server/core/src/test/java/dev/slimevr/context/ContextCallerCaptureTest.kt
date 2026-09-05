package dev.slimevr.context

import dev.slimevr.context.debug.DebugMiddleware
import dev.slimevr.context.debug.contextDebugEnabled
import kotlin.test.Test
import kotlin.test.assertEquals

private sealed interface TestAction {
	object Increment : TestAction
}

private class DispatchingBehaviour : Behaviour<Context<Int, TestAction>> {
	fun trigger(context: Context<Int, TestAction>) {
		context.dispatch(TestAction.Increment)
	}

	override fun observe(receiver: Context<Int, TestAction>) {
		trigger(receiver)
	}
}

private class RecordingDebugMiddleware : DebugMiddleware<Int, TestAction> {
	var caller: String? = null

	override fun onDispatch(caller: String?, before: Int, action: TestAction, after: Int) {
		this.caller = caller
	}

	override fun onDispatchAll(caller: String?, before: Int, actions: List<TestAction>, after: Int) = Unit
}

class ContextCallerCaptureTest {

	@Test
	fun `dispatch reports calling behaviour`() {
		contextDebugEnabled = true

		val middleware = RecordingDebugMiddleware()
		val context = Context.create(
			initialState = 0,
			scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined),
			reducer = { state, action ->
				when (action) {
					TestAction.Increment -> state + 1
				}
			},
			behaviours = listOf(DispatchingBehaviour()),
			debugMiddleware = middleware,
			name = "TestContext",
		)

		context.observeAll(context)
		assertEquals("DispatchingBehaviour", middleware.caller)
	}
}
