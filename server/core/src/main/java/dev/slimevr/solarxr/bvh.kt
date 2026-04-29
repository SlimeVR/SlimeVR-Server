package dev.slimevr.solarxr

import dev.slimevr.bvh.BVHManager
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.RecordBVHRequest
import solarxr_protocol.rpc.RecordBVHStatus
import solarxr_protocol.rpc.RecordBVHStatusRequest
import java.nio.file.Path

class BvhBehaviour(private val bvhManager: BVHManager) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<RecordBVHRequest> { req ->
			if (req.stop) {
				bvhManager.stopRecording()
			} else {
				req.path?.let { path -> bvhManager.startRecording(Path.of(path)) }
			}
			receiver.sendRpc(RecordBVHStatus(recording = bvhManager.isRecording))
		}

		receiver.rpcDispatcher.on<RecordBVHStatusRequest> {
			receiver.sendRpc(RecordBVHStatus(recording = bvhManager.isRecording))
		}

		bvhManager.context.state.drop(1)
			.onEach { state -> receiver.sendRpc(RecordBVHStatus(recording = state.recording)) }
			.launchIn(receiver.context.scope)
	}
}
