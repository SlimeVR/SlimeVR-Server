package dev.slimevr.config

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AutosaveTest {

	@Test
	fun `stop writes the state that is still waiting for the debounce`() = runTest {
		val storage = MemoryStorage()
		val state = MutableStateFlow("a")
		val autosave = launchAutosave(backgroundScope, state, storage, toPath = { "x.json" }, serialize = { it })

		state.value = "b"
		advanceTimeBy(100)
		assertNull(storage.files["x.json"])

		autosave.stop()
		assertEquals("b", storage.files["x.json"])
	}

	@Test
	fun `stop does not rewrite a state that is already saved`() = runTest {
		val storage = MemoryStorage()
		val state = MutableStateFlow("a")
		val autosave = launchAutosave(backgroundScope, state, storage, toPath = { "x.json" }, serialize = { it })

		autosave.stop()
		assertNull(storage.files["x.json"])
	}
}
