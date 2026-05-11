package dev.slimevr.context

import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private object NoopBehaviour : Behaviour<Int, Int, Unit>

class ContextScopeIsolationTest {
	@Test
	@OptIn(ExperimentalCoroutinesApi::class)
	fun `direct context child failure does not cancel parent scope`() = runTest {
		val uncaught = mutableListOf<Throwable>()
		val handler = CoroutineExceptionHandler { _, throwable -> uncaught += throwable }
		val parentJob = Job()
		val parentScope = CoroutineScope(StandardTestDispatcher(testScheduler) + parentJob + handler)
		val context = Context.create(
			initialState = 0,
			scope = parentScope,
			behaviours = listOf(NoopBehaviour),
			name = "IsolatedContext",
		)

		context.scope.safeLaunch {
			error("boom")
		}

		advanceUntilIdle()

		assertTrue(parentJob.isActive)
		assertEquals(1, uncaught.size)
	}
}
