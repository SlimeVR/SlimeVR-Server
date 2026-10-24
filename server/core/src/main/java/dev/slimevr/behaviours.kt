package dev.slimevr

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BaseBehaviour : VRServerBehaviour {
	override fun observe(receiver: VRServer) {
		receiver.context.state.map { it.trackers.size }.distinctUntilChanged().onEach {
			println("Tracker list size changed to $it")
		}.launchIn(receiver.context.scope)
	}
}
