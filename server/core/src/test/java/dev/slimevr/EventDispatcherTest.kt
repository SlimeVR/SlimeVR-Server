package dev.slimevr

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private sealed interface TestEvent {
	data class A(val n: Int) : TestEvent
	data class B(val n: Int) : TestEvent
}

class EventDispatcherTest {

	@Test
	fun `every handler for an event runs before the next event is taken`() = runTest {
		val seen = mutableListOf<String>()
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope)

		dispatcher.on<TestEvent.A> { seen += "first-${it.n}" }.launchIn(backgroundScope)
		dispatcher.on<TestEvent.A> { seen += "second-${it.n}" }.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(1))
		dispatcher.emit(TestEvent.A(2))
		runCurrent()

		// Not first-1, first-2, second-1, second-2: the barrier several behaviours rely on.
		assertContentEquals(listOf("first-1", "second-1", "first-2", "second-2"), seen)
	}

	@Test
	fun `a handler only receives the type it subscribed to`() = runTest {
		val a = mutableListOf<Int>()
		val b = mutableListOf<Int>()
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope)

		dispatcher.on<TestEvent.A> { a += it.n }.launchIn(backgroundScope)
		dispatcher.on<TestEvent.B> { b += it.n }.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(1))
		dispatcher.emit(TestEvent.B(2))
		dispatcher.emit(TestEvent.A(3))
		runCurrent()

		assertContentEquals(listOf(1, 3), a)
		assertContentEquals(listOf(2), b)
	}

	@Test
	fun `a supertype handler receives every subtype`() = runTest {
		val seen = mutableListOf<TestEvent>()
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope)

		dispatcher.on<TestEvent> { seen += it }.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(1))
		dispatcher.emit(TestEvent.B(2))
		runCurrent()

		assertContentEquals(listOf(TestEvent.A(1), TestEvent.B(2)), seen)
	}

	@Test
	fun `a stuck handler does not block the producer`() = runTest {
		val gate = CompletableDeferred<Unit>()
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope, capacity = 8)

		dispatcher.on<TestEvent.A> { gate.await() }.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(1))
		runCurrent()

		// The handler is now parked forever. Emitting must still return, which is the whole point:
		// under the old rendezvous these would have blocked behind the handler.
		var emitted = 0
		repeat(5) {
			dispatcher.emit(TestEvent.A(it))
			emitted++
		}
		assertEquals(5, emitted)
		assertTrue(gate.isActive, "the handler must still be parked, or this proves nothing")
	}

	@Test
	fun `drop oldest keeps the newest events`() = runTest {
		val gate = CompletableDeferred<Unit>()
		val seen = mutableListOf<Int>()
		val dispatcher = EventDispatcher<TestEvent>(
			name = "test",
			scope = backgroundScope,
			capacity = 2,
			onBufferOverflow = BufferOverflow.DROP_OLDEST,
		)

		dispatcher.on<TestEvent.A> {
			if (it.n == 1) gate.await()
			seen += it.n
		}.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(1))
		runCurrent() // event 1 is taken and the handler parks on the gate

		for (n in 2..6) dispatcher.emit(TestEvent.A(n))
		gate.complete(Unit)
		runCurrent()

		// A two-slot mailbox keeps the last two of 2..6; the rest are dropped rather than queued.
		assertContentEquals(listOf(1, 5, 6), seen)
	}

	@Test
	fun `cancelling the listener scope unsubscribes the handler`() = runTest {
		val listenerScope = CoroutineScope(backgroundScope.coroutineContext + Job())
		val seen = mutableListOf<Int>()
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope)

		dispatcher.on<TestEvent.A> { seen += it.n }.launchIn(listenerScope)

		dispatcher.emit(TestEvent.A(1))
		runCurrent()

		listenerScope.cancel()
		runCurrent()

		dispatcher.emit(TestEvent.A(2))
		runCurrent()

		assertContentEquals(listOf(1), seen)
	}

	@Test
	fun `subscribing with an already cancelled scope registers nothing`() = runTest {
		val deadScope = CoroutineScope(backgroundScope.coroutineContext + Job())
		deadScope.cancel()
		val seen = mutableListOf<Int>()
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope)

		dispatcher.on<TestEvent.A> { seen += it.n }.launchIn(deadScope)
		runCurrent()

		dispatcher.emit(TestEvent.A(1))
		runCurrent()

		assertTrue(seen.isEmpty(), "a dead scope must not leave a handler registered")
	}

	@Test
	fun `emitting after the owner is gone does nothing instead of blocking`() = runTest {
		val ownerScope = CoroutineScope(backgroundScope.coroutineContext + Job())
		val seen = mutableListOf<Int>()
		val dispatcher = EventDispatcher<TestEvent>("test", ownerScope, capacity = 2)

		dispatcher.on<TestEvent.A> { seen += it.n }.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(1))
		runCurrent()
		assertContentEquals(listOf(1), seen)

		ownerScope.cancel()
		runCurrent()

		// Well past the capacity of 2. With the mailbox left open these would fill it and then block
		// forever on a queue whose drain loop is already cancelled.
		repeat(10) { dispatcher.emit(TestEvent.A(it)) }
		runCurrent()

		assertContentEquals(listOf(1), seen)
	}

	@Test
	fun `a mailbox that keeps up reports nothing`() = runTest {
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope, capacity = 8)
		dispatcher.on<TestEvent.A> { }.launchIn(backgroundScope)

		// Drained between emits, so the mailbox never holds more than one event.
		repeat(20) {
			dispatcher.emit(TestEvent.A(it))
			runCurrent()
		}

		assertNull(dispatcher.saturationReport(), "a dispatcher keeping up must stay quiet")
	}

	@Test
	fun `backing up warns before anything is lost`() = runTest {
		val gate = CompletableDeferred<Unit>()
		// capacity 8 puts the depth warning at 2
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope, capacity = 8)
		dispatcher.on<TestEvent.A> { gate.await() }.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(0))
		runCurrent() // taken by the handler, which parks

		dispatcher.emit(TestEvent.A(1))
		dispatcher.emit(TestEvent.A(2))

		val report = dispatcher.saturationReport()
		assertNotNull(report, "depth reached the warning threshold, so this must report")
		assertTrue(report.contains("nothing lost yet"), "still within capacity: $report")
	}

	@Test
	fun `drops are reported as lost`() = runTest {
		val gate = CompletableDeferred<Unit>()
		val dispatcher = EventDispatcher<TestEvent>(
			name = "test",
			scope = backgroundScope,
			capacity = 2,
			onBufferOverflow = BufferOverflow.DROP_OLDEST,
		)
		dispatcher.on<TestEvent.A> { gate.await() }.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(0))
		runCurrent()
		repeat(6) { dispatcher.emit(TestEvent.A(it)) }

		val report = dispatcher.saturationReport()
		assertNotNull(report)
		assertTrue(report.contains("events dropped"), "should name the loss: $report")
	}

	@Test
	fun `disposing the handle unsubscribes the handler`() = runTest {
		val seen = mutableListOf<Int>()
		val dispatcher = EventDispatcher<TestEvent>("test", backgroundScope)

		val handle = dispatcher.on<TestEvent.A> { seen += it.n }.launchIn(backgroundScope)

		dispatcher.emit(TestEvent.A(1))
		runCurrent()

		handle.dispose()
		dispatcher.emit(TestEvent.A(2))
		runCurrent()

		assertContentEquals(listOf(1), seen)
	}
}
