package dev.slimevr.updater

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test

class StressTest {

	@Test
	fun `test multiple downloads`() {
		runBlocking {
			val safeDispatcher = Dispatchers.IO.limitedParallelism(50)
			withContext(safeDispatcher) {
				for (i in 1..50) {
					this.launch {
						val updateController = UpdaterController()
						TerminalUtil.info("Launching instance $i")
						updateController.startGui()
					}
				}
			}
		}
	}
}
