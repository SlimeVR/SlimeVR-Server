package dev.slimevr.tracking.videocalibration.networking

import dev.onvoid.webrtc.CreateSessionDescriptionObserver
import dev.onvoid.webrtc.PeerConnectionFactory
import dev.onvoid.webrtc.PeerConnectionObserver
import dev.onvoid.webrtc.RTCAnswerOptions
import dev.onvoid.webrtc.RTCConfiguration
import dev.onvoid.webrtc.RTCIceCandidate
import dev.onvoid.webrtc.RTCIceConnectionState
import dev.onvoid.webrtc.RTCIceGatheringState
import dev.onvoid.webrtc.RTCPeerConnection
import dev.onvoid.webrtc.RTCPeerConnectionState
import dev.onvoid.webrtc.RTCSdpType
import dev.onvoid.webrtc.RTCSessionDescription
import dev.onvoid.webrtc.SetSessionDescriptionObserver
import dev.onvoid.webrtc.media.video.CustomVideoSource
import dev.onvoid.webrtc.media.video.VideoFrame
import io.eiren.util.logging.LogManager
import kotlinx.coroutines.CompletableDeferred
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Manages remote clients that are connecting to SlimeVR to receive video.
 */
class WebRTCManager {

	/**
	 * Types of videos that the client can request.
	 */
	enum class VideoProvider {
		VIDEO_CALIBRATION,
	}

	private class Connection(
		val videoProvider: VideoProvider,
		val peerConnection: RTCPeerConnection,
		val videoSource: CustomVideoSource,
	)

	private val lock = Any()
	private val peerConnectionFactory = PeerConnectionFactory()
	private val connections = mutableListOf<Connection>()

	/**
	 * Creates a peer connection between a client and the server.
	 *
	 * A remote client sends us an offer SDP. We set up a peer connection, do ICE
	 * discovery, and respond with an answer SDP. The remote client uses this answer SDP
	 * to connect via WebRTC to receive a video stream.
	 */
	suspend fun connect(provider: VideoProvider, offerSDP: String): String {
		LogManager.info("Creating peer connection for incoming video request...")

		// Will be completed by the peer connection's observable
		val iceGatheringComplete = CompletableDeferred<Unit>()

		val peerConnection =
			peerConnectionFactory.createPeerConnection(
				RTCConfiguration(),
				object : PeerConnectionObserver {
					override fun onIceCandidate(candidate: RTCIceCandidate) {
						// Nothing to do since we are waiting for all candidates to be
						// gathered before replying to the remote client
					}

					override fun onIceGatheringChange(state: RTCIceGatheringState) {
						if (state == RTCIceGatheringState.COMPLETE) {
							iceGatheringComplete.complete(Unit)
						}
					}

					override fun onConnectionChange(state: RTCPeerConnectionState) {
						if (state == RTCPeerConnectionState.CONNECTED) {
							LogManager.info("Remote client connected to WebRTC!")
						}
						cleanupConnections()
					}
				},
			)

		val videoSource = CustomVideoSource()
		val videoTrack = peerConnectionFactory.createVideoTrack("video0", videoSource)
		peerConnection.addTrack(videoTrack, listOf("stream0"))

		LogManager.info("Setting remote description...")

		suspendCoroutine { cont ->
			peerConnection.setRemoteDescription(
				RTCSessionDescription(RTCSdpType.OFFER, offerSDP),
				object : SetSessionDescriptionObserver {
					override fun onSuccess() {
						cont.resume(Unit)
					}
					override fun onFailure(error: String) {
						cont.resumeWithException(IllegalStateException("Failed to set remote description: $error"))
					}
				},
			)
		}

		LogManager.info("Creating answer...")

		val answer = suspendCoroutine { cont ->
			peerConnection.createAnswer(
				RTCAnswerOptions(),
				object : CreateSessionDescriptionObserver {
					override fun onSuccess(description: RTCSessionDescription) {
						cont.resume(description)
					}
					override fun onFailure(error: String) {
						cont.resumeWithException(IllegalStateException("Failed to create answer: $error"))
					}
				},
			)
		}

		LogManager.info("Setting local description...")

		suspendCoroutine { cont ->
			peerConnection.setLocalDescription(
				answer,
				object : SetSessionDescriptionObserver {
					override fun onSuccess() {
						cont.resume(Unit)
					}
					override fun onFailure(error: String) {
						cont.resumeWithException(IllegalStateException("Failed to set local description: $error"))
					}
				},
			)
		}

		if (peerConnection.iceConnectionState == RTCIceConnectionState.COMPLETED) {
			LogManager.info("ICE gathering already complete")
		} else {
			LogManager.info("Waiting for ICE gathering to complete...")
			iceGatheringComplete.await()
		}

		LogManager.info("Peer connection ready")

		synchronized(lock) {
			val connection = Connection(provider, peerConnection, videoSource)
			connections.add(connection)
		}

		return peerConnection.localDescription.sdp
	}

	/**
	 * Broadcasts a video frame to any clients requesting that video.
	 */
	fun broadcastVideoFrame(provider: VideoProvider, frame: VideoFrame) {
		synchronized(lock) {
			for (conn in connections) {
				if (conn.videoProvider == provider) {
					conn.videoSource.pushFrame(frame)
				}
			}
		}
	}

	/**
	 * Cleans up connections so that native resources (e.g. ports) are released.
	 */
	private fun cleanupConnections() {
		synchronized(lock) {
			val toClose = connections.filter {
				it.peerConnection.connectionState == RTCPeerConnectionState.DISCONNECTED ||
					it.peerConnection.connectionState == RTCPeerConnectionState.FAILED
			}

			connections.removeAll(toClose)

			// Closing the connection MUST BE done outside iterating the list, because
			// this can trigger the onConnectionChange event which calls
			// cleanupConnections again
			toClose.forEach { it.peerConnection.close() }

			connections.removeIf {
				it.peerConnection.connectionState == RTCPeerConnectionState.CLOSED
			}
		}
	}
}
