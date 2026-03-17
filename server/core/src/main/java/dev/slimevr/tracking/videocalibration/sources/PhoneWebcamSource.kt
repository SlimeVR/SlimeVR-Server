package dev.slimevr.tracking.videocalibration.sources

import dev.onvoid.webrtc.CreateSessionDescriptionObserver
import dev.onvoid.webrtc.PeerConnectionFactory
import dev.onvoid.webrtc.PeerConnectionObserver
import dev.onvoid.webrtc.RTCConfiguration
import dev.onvoid.webrtc.RTCDataChannelInit
import dev.onvoid.webrtc.RTCIceCandidate
import dev.onvoid.webrtc.RTCIceConnectionState
import dev.onvoid.webrtc.RTCIceGatheringState
import dev.onvoid.webrtc.RTCOfferOptions
import dev.onvoid.webrtc.RTCPeerConnection
import dev.onvoid.webrtc.RTCPeerConnectionState
import dev.onvoid.webrtc.RTCRtpTransceiver
import dev.onvoid.webrtc.RTCSdpType
import dev.onvoid.webrtc.RTCSessionDescription
import dev.onvoid.webrtc.SetSessionDescriptionObserver
import dev.onvoid.webrtc.media.FourCC
import dev.onvoid.webrtc.media.video.CustomVideoSource
import dev.onvoid.webrtc.media.video.VideoBufferConverter
import dev.onvoid.webrtc.media.video.VideoFrame
import dev.onvoid.webrtc.media.video.VideoTrack
import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.CameraExtrinsic
import dev.slimevr.tracking.videocalibration.data.CameraIntrinsic
import dev.slimevr.tracking.videocalibration.networking.MDNSRegistry
import dev.slimevr.tracking.videocalibration.snapshots.ImageSnapshot
import dev.slimevr.tracking.videocalibration.util.DebugOutput
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.QuaternionD
import io.github.axisangles.ktmath.Vector3D
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.awt.Dimension
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.TimeSource

/**
 * Produces webcam images from a calibrated phone camera.
 *
 * The phone is running a WebRTC signaling server which accepts an offer SDP and
 * replies with an answer SDP. We use the answer SDP to set up the actual WebRTC video
 * connection.
 */
class PhoneWebcamSource(
	private val webcamService: MDNSRegistry.Service,
	private val debugOutput: DebugOutput,
) {
	enum class Status {
		NOT_STARTED,
		INITIALIZING,
		RUNNING,
		DONE,
	}

	val status = AtomicReference(Status.NOT_STARTED)
	val imageSnapshots = Channel<ImageSnapshot>(Channel.Factory.CONFLATED)

	private var scope = CoroutineScope(Dispatchers.IO)
	private var startTime = TimeSource.Monotonic.markNow()
	private var camera: Camera? = null

	/**
	 * Starts the service.
	 */
	fun start() {
		scope.launch {
			run()
		}
	}

	/**
	 * Stops the service
	 */
	fun requestStop() {
		scope.cancel()
	}

	private suspend fun run() {
		status.set(Status.INITIALIZING)

		val peerConnection: RTCPeerConnection
		try {
			peerConnection = connect()
		} catch (e: Exception) {
			LogManager.warning("Failed to start phone webcam source: $e", e)
			status.set(Status.DONE)
			return
		}

		status.set(Status.RUNNING)

		try {
			awaitCancellation()
		} finally {
			status.set(Status.DONE)
			peerConnection.close()
		}
	}

	/**
	 * Creates a WebRTC peer connection.
	 */
	private suspend fun connect(): RTCPeerConnection {
		LogManager.info("Creating peer connection to request video...")

		// Will be completed by the peer connection's observable
		val iceGatheringComplete = CompletableDeferred<Unit>()

		val peerConnectionFactory = PeerConnectionFactory()

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
						when (state) {
							RTCPeerConnectionState.CONNECTED ->
								LogManager.info("Connected to WebRTC!")

							RTCPeerConnectionState.DISCONNECTED,
							RTCPeerConnectionState.CLOSED,
							RTCPeerConnectionState.FAILED,
							->
								scope.cancel()

							else -> {}
						}
					}

					override fun onTrack(transceiver: RTCRtpTransceiver) {
						val track = transceiver.receiver.track
						if (track is VideoTrack) {
							track.addSink { handleVideoFrame(it) }
						}
					}
				},
			)

		val videoSource = CustomVideoSource()
		val videoTrack = peerConnectionFactory.createVideoTrack("video0", videoSource)
		peerConnection.addTrack(videoTrack, listOf("stream0"))

		// Create data channel so that both sides know when the connection is closed
		peerConnection.createDataChannel("data0", RTCDataChannelInit())

		LogManager.info("Creating offer...")

		val offer = suspendCoroutine { cont ->
			peerConnection.createOffer(
				RTCOfferOptions(),
				object : CreateSessionDescriptionObserver {
					override fun onSuccess(description: RTCSessionDescription) {
						cont.resume(description)
					}
					override fun onFailure(error: String) {
						cont.resumeWithException(IllegalStateException("Failed to create offer: $error"))
					}
				},
			)
		}

		LogManager.info("Setting local description...")

		suspendCoroutine { cont ->
			peerConnection.setLocalDescription(
				RTCSessionDescription(RTCSdpType.OFFER, offer.sdp),
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

		LogManager.info("Requesting answer SDP...")

		val answerSDP = requestAnswerSDP(peerConnection.localDescription.sdp)

		LogManager.info("Setting remote description...")

		suspendCoroutine { cont ->
			peerConnection.setRemoteDescription(
				RTCSessionDescription(RTCSdpType.ANSWER, answerSDP),
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

		return peerConnection
	}

	/**
	 * Connects to the phone's WebRTC signaling server and requests an answer SDP.
	 */
	private suspend fun requestAnswerSDP(offerSDP: String): String {
		val client = HttpClient(CIO) {
			install(ContentNegotiation) {
				json()
			}
		}

		val urlBuilder = URLBuilder(
			protocol = URLProtocol.HTTP,
			host = webcamService.host.hostAddress,
			port = webcamService.port,
			pathSegments = listOf("offer"),
		)

		val url = urlBuilder.build()
		LogManager.info("Sending WebRTC offer to: $url")

		val offerRequest = client.preparePost(url) {
			contentType(ContentType.Application.Json)
			setBody(OfferRequest(offerSDP))
		}

		val offerResponse = offerRequest.execute()

		if (offerResponse.status != HttpStatusCode.OK) {
			error("Failed to get answer from signaling server")
		}

		val answer = offerResponse.body<OfferResponse>()

		val cameraToWorld = answer.cameraToWorld.let { QuaternionD(it[0], it[1], it[2], it[3]) }
		val intrinsic = answer.intrinsics.let { CameraIntrinsic(it.fx, it.fy, it.tx, it.ty) }
		val imageSize = answer.imageSize.let { Dimension(it[0], it[1]) }
		val camera = Camera(CameraExtrinsic.fromCameraPose(cameraToWorld, Vector3D.NULL), intrinsic, imageSize)
		this.camera = camera

		debugOutput.saveCamera(camera)

		return answer.sdp
	}

	/**
	 * Handles a video frame by processing it into a [BufferedImage] and sending it to
	 * the channel.
	 */
	private fun handleVideoFrame(videoFrame: VideoFrame) {
		val camera = camera ?: return

		val now = TimeSource.Monotonic.markNow()
		val timestamp = now - startTime

		val buffer = videoFrame.buffer
		val rgbaBuffer = ByteArray(buffer.width * buffer.height * 4)
		VideoBufferConverter.convertFromI420(buffer, rgbaBuffer, FourCC.RGBA)

		val raster = Raster.createInterleavedRaster(
			DataBufferByte(rgbaBuffer, rgbaBuffer.size),
			buffer.width,
			buffer.height,
			4 * buffer.width,
			4,
			intArrayOf(3, 2, 1, 0),
			null,
		)

		val colorModel = ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB),
			true,
			false,
			Transparency.OPAQUE,
			DataBuffer.TYPE_BYTE,
		)

		val image = BufferedImage(colorModel, raster, false, null)

		// TODO: Can we eliminate this?
		val redrawnImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
		val g = redrawnImage.createGraphics()
		g.drawImage(image, 0, 0, image.width, image.height, 0, 0, image.width, image.height, null)
		g.dispose()

		debugOutput.saveWebcamImage(timestamp, redrawnImage)

		val imageSnapshot = ImageSnapshot(TimeSource.Monotonic.markNow(), timestamp, redrawnImage, camera)
		imageSnapshots.trySend(imageSnapshot)
	}

	@Serializable
	private class OfferRequest(
		val sdp: String,
	)

	@Serializable
	private class IntrinsicsResponse(
		val fx: Double,
		val fy: Double,
		val tx: Double,
		val ty: Double,
	)

	@Serializable
	private class OfferResponse(
		val sdp: String,
		val imageSize: List<Int>,
		val intrinsics: IntrinsicsResponse,
		val cameraToWorld: List<Double>,
	)
}
