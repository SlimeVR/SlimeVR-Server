package dev.slimevr.protocol.rpc

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.config.MountingMethods
import dev.slimevr.config.config
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolHandler
import dev.slimevr.protocol.datafeed.createTrackerId
import dev.slimevr.protocol.rpc.autobone.RPCAutoBoneHandler
import dev.slimevr.protocol.rpc.firmware.RPCFirmwareUpdateHandler
import dev.slimevr.protocol.rpc.games.vrchat.RPCVRChatHandler
import dev.slimevr.protocol.rpc.reset.RPCResetHandler
import dev.slimevr.protocol.rpc.serial.RPCProvisioningHandler
import dev.slimevr.protocol.rpc.serial.RPCSerialHandler
import dev.slimevr.protocol.rpc.settings.RPCSettingsHandler
import dev.slimevr.protocol.rpc.settings.createSettingsResponse
import dev.slimevr.protocol.rpc.setup.RPCHandshakeHandler
import dev.slimevr.protocol.rpc.setup.RPCTapSetupHandler
import dev.slimevr.protocol.rpc.setup.RPCUtil.getLocalIp
import dev.slimevr.protocol.rpc.status.RPCStatusHandler
import dev.slimevr.protocol.rpc.trackingchecklist.RPCTrackingChecklistHandler
import dev.slimevr.protocol.rpc.trackingpause.RPCTrackingPause
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import dev.slimevr.tracking.processor.stayaligned.poses.RelaxedPose
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerPosition.Companion.getByBodyPart
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.TrackerUtils.getTrackerForSkeleton
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.*
import solarxr_protocol.MessageBundle
import solarxr_protocol.datatypes.TransactionId
import solarxr_protocol.rpc.*
import kotlin.io.path.Path

class RPCHandler(private val api: ProtocolAPI) : ProtocolHandler<RpcMessageHeader>() {
	private val mainScope = CoroutineScope(SupervisorJob())

	init {
		RPCResetHandler(this, api)
		RPCSerialHandler(this, api)
		RPCProvisioningHandler(this, api)
		RPCSettingsHandler(this, api)
		RPCTapSetupHandler(this, api)
		RPCStatusHandler(this, api)
		RPCAutoBoneHandler(this, api)
		RPCHandshakeHandler(this, api)
		RPCTrackingPause(this, api)
		RPCFirmwareUpdateHandler(this, api)
		RPCVRChatHandler(this, api)
		RPCTrackingChecklistHandler(this, api)
		RPCUserHeightCalibration(this, api)

		registerPacketListener(
			RpcMessage.AssignTrackerRequest,
			::onAssignTrackerRequest,
		)

		registerPacketListener(
			RpcMessage.ClearDriftCompensationRequest,
			::onClearDriftCompensationRequest,
		)

		registerPacketListener(
			RpcMessage.RecordBVHRequest,
			::onRecordBVHRequest,
		)

		registerPacketListener(
			RpcMessage.RecordBVHStatusRequest,
			::onBVHStatusRequest,
		)

		registerPacketListener(
			RpcMessage.SkeletonResetAllRequest,
			::onSkeletonResetAllRequest,
		)
		registerPacketListener(
			RpcMessage.SkeletonConfigRequest,
			::onSkeletonConfigRequest,
		)
		registerPacketListener(
			RpcMessage.ChangeSkeletonConfigRequest,
			::onChangeSkeletonConfigRequest,
		)

		registerPacketListener(
			RpcMessage.OverlayDisplayModeChangeRequest,
			::onOverlayDisplayModeChangeRequest,
		)
		registerPacketListener(
			RpcMessage.OverlayDisplayModeRequest,
			::onOverlayDisplayModeRequest,
		)

		registerPacketListener(
			RpcMessage.ServerInfosRequest,
			::onServerInfosRequest,
		)

		registerPacketListener(
			RpcMessage.LegTweaksTmpChange,
			::onLegTweaksTmpChange,
		)

		registerPacketListener(
			RpcMessage.LegTweaksTmpClear,
			::onLegTweaksTmpClear,
		)

		registerPacketListener(
			RpcMessage.StatusSystemRequest,
			::onStatusSystemRequest,
		)

		registerPacketListener(
			RpcMessage.SetPauseTrackingRequest,
			::onSetPauseTrackingRequest,
		)

		registerPacketListener(
			RpcMessage.HeightRequest,
			::onHeightRequest,
		)

		registerPacketListener(
			RpcMessage.MagToggleRequest,
			::onMagToggleRequest,
		)

		registerPacketListener(
			RpcMessage.ChangeMagToggleRequest,
			::onChangeMagToggleRequest,
		)

		registerPacketListener(
			RpcMessage.EnableStayAlignedRequest,
			::onEnableStayAlignedRequest,
		)

		registerPacketListener(
			RpcMessage.DetectStayAlignedRelaxedPoseRequest,
			::onDetectStayAlignedRelaxedPoseRequest,
		)

		registerPacketListener(
			RpcMessage.ResetStayAlignedRelaxedPoseRequest,
			::onResetStayAlignedRelaxedPoseRequest,
		)
	}

	private fun onServerInfosRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val fbb = FlatBufferBuilder(32)

		val localIp = getLocalIp() ?: return
		val response = ServerInfosResponse
			.createServerInfosResponse(fbb, fbb.createString(localIp))
		val outbound = this.createRPCMessage(fbb, RpcMessage.ServerInfosResponse, response, messageHeader)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	private fun onOverlayDisplayModeRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val fbb = FlatBufferBuilder(32)
		val config = api.server.configManager.vrConfig.overlay
		val response = OverlayDisplayModeResponse
			.createOverlayDisplayModeResponse(fbb, config.isVisible, config.isMirrored)
		val outbound = this.createRPCMessage(fbb, RpcMessage.OverlayDisplayModeResponse, response, messageHeader)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	private fun onOverlayDisplayModeChangeRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(OverlayDisplayModeChangeRequest()) as? OverlayDisplayModeChangeRequest ?: return
		val config = api.server.configManager.vrConfig.overlay
		config.isMirrored = req.isMirrored
		config.isVisible = req.isVisible

		api.server.configManager.saveConfig()
	}

	fun onSkeletonResetAllRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		if (messageHeader
				.message(SkeletonResetAllRequest()) !is SkeletonResetAllRequest
		) {
			return
		}

		api.server.queueTask {
			api.server.humanPoseManager.resetOffsets()
			api.server.humanPoseManager.saveConfig()
			api.server.configManager.saveConfig()

			api.server.trackingChecklistManager.resetMountingCompleted = false
			api.server.trackingChecklistManager.feetResetMountingCompleted = false

			// might not be a good idea maybe let the client ask again
			val fbb = FlatBufferBuilder(300)
			val config = createSkeletonConfig(fbb, api.server.humanPoseManager)
			val outbound = this.createRPCMessage(fbb, RpcMessage.SkeletonConfigResponse, config, messageHeader)
			fbb.finish(outbound)
			conn.send(fbb.dataBuffer())
		}
	}

	fun onSkeletonConfigRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		if (messageHeader
				.message(SkeletonConfigRequest()) !is SkeletonConfigRequest
		) {
			return
		}

		val fbb = FlatBufferBuilder(300)
		val config = createSkeletonConfig(fbb, api.server.humanPoseManager)
		val outbound = this.createRPCMessage(fbb, RpcMessage.SkeletonConfigResponse, config, messageHeader)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun onChangeSkeletonConfigRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(ChangeSkeletonConfigRequest()) as? ChangeSkeletonConfigRequest ?: return

		val joint = SkeletonConfigOffsets.getById(req.bone())

		api.server.humanPoseManager.setOffset(joint, req.value())
		api.server.humanPoseManager.saveConfig()
		api.server.configManager.saveConfig()
	}

	fun onRecordBVHRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(RecordBVHRequest()) as? RecordBVHRequest ?: return

		if (req.stop()) {
			if (api.server.bvhRecorder.isRecording) {
				api.server.bvhRecorder.endRecording()
			}
		} else {
			if (!api.server.bvhRecorder.isRecording) {
				api.server.bvhRecorder.startRecording(Path(req.path()))
			}
		}

		val fbb = FlatBufferBuilder(40)
		val status = RecordBVHStatus
			.createRecordBVHStatus(fbb, api.server.bvhRecorder.isRecording)
		val outbound = this.createRPCMessage(fbb, RpcMessage.RecordBVHStatus, status, messageHeader)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun onBVHStatusRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		if (messageHeader.message(RecordBVHStatusRequest()) !is RecordBVHStatusRequest) return

		val fbb = FlatBufferBuilder(40)
		val status = RecordBVHStatus
			.createRecordBVHStatus(fbb, api.server.bvhRecorder.isRecording)
		val outbound = this.createRPCMessage(fbb, RpcMessage.RecordBVHStatus, status, messageHeader)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun onAssignTrackerRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(AssignTrackerRequest()) as? AssignTrackerRequest ?: return

		val tracker = api.server.getTrackerById(req.trackerId().unpack()) ?: return

		val pos = getByBodyPart(req.bodyPosition())
		val previousTracker = if (pos != null) {
			getTrackerForSkeleton(api.server.allTrackers, pos)
		} else {
			null
		}
		if (previousTracker != null) {
			previousTracker.trackerPosition = null
			api.server.trackerUpdated(previousTracker)
		}
		tracker.trackerPosition = pos

		if (req.mountingOrientation() != null) {
			if (tracker.allowMounting) {
				tracker
					.resetsHandler
					.mountingOrientation = Quaternion(
					req.mountingOrientation().w(),
					req.mountingOrientation().x(),
					req.mountingOrientation().y(),
					req.mountingOrientation().z(),
				)
				api.server.configManager.vrConfig.resetsConfig.lastMountingMethod =
					MountingMethods.MANUAL
			}
		}

		if (req.displayName() != null) {
			tracker.customName = req.displayName()
		}

		if (tracker.isImu()) {
			tracker.resetsHandler.allowDriftCompensation = req.allowDriftCompensation()
		}

		api.server.trackerUpdated(tracker)
	}

	fun onClearDriftCompensationRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		if (messageHeader
				.message(ClearDriftCompensationRequest()) !is ClearDriftCompensationRequest
		) {
			return
		}

		api.server.clearTrackersDriftCompensation()
	}

	fun onLegTweaksTmpChange(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(LegTweaksTmpChange()) as? LegTweaksTmpChange ?: return

		api.server.humanPoseManager
			.setLegTweaksStateTemp(
				req.skatingCorrection(),
				req.floorClip(),
				req.toeSnap(),
				req.footPlant(),
			)
	}

	fun onLegTweaksTmpClear(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(LegTweaksTmpClear()) as? LegTweaksTmpClear ?: return

		api.server.humanPoseManager
			.clearLegTweaksStateTemp(
				req.skatingCorrection(),
				req.floorClip(),
				req.toeSnap(),
				req.footPlant(),
			)
	}

	override fun onMessage(conn: GenericConnection, message: RpcMessageHeader) {
		val consumer = handlers[message.messageType().toInt()]
		if (consumer != null) {
			consumer.accept(conn, message)
		} else {
			LogManager
				.info("[ProtocolAPI] Unhandled RPC packet received id: ${message.messageType()}")
		}
	}

	@JvmOverloads
	fun createRPCMessage(fbb: FlatBufferBuilder, messageType: Byte, messageOffset: Int, respondTo: RpcMessageHeader? = null): Int {
		val data = IntArray(1)

		RpcMessageHeader.startRpcMessageHeader(fbb)
		RpcMessageHeader.addMessage(fbb, messageOffset)
		RpcMessageHeader.addMessageType(fbb, messageType)
		respondTo?.txId()?.let { txId ->
			RpcMessageHeader.addTxId(fbb, TransactionId.createTransactionId(fbb, txId.id()))
		}
		data[0] = RpcMessageHeader.endRpcMessageHeader(fbb)

		val messages = MessageBundle.createRpcMsgsVector(fbb, data)

		MessageBundle.startMessageBundle(fbb)
		MessageBundle.addRpcMsgs(fbb, messages)
		return MessageBundle.endMessageBundle(fbb)
	}

	override fun messagesCount(): Int = RpcMessage.names.size

	fun onStatusSystemRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(StatusSystemRequest()) as? StatusSystemRequest ?: return

		val statuses = api.server.statusSystem.getStatuses()

		val fbb = FlatBufferBuilder(
			statuses.size * RPCStatusHandler.STATUS_EXPECTED_SIZE,
		)
		val response = StatusSystemResponseT()
		response.currentStatuses = statuses
		val offset = StatusSystemResponse.pack(fbb, response)
		val outbound = this.createRPCMessage(fbb, RpcMessage.StatusSystemResponse, offset, messageHeader)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun onSetPauseTrackingRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(SetPauseTrackingRequest()) as? SetPauseTrackingRequest ?: return

		api.server.setPauseTracking(req.pauseTracking(), RPCResetHandler.RESET_SOURCE_NAME)
	}

	fun onHeightRequest(conn: GenericConnection, messageHeader: RpcMessageHeader?) {
		val fbb = FlatBufferBuilder(32)

		val posTrackers = api.server.allTrackers.filter { !it.isInternal && it.status == TrackerStatus.OK && it.hasPosition && it.trackerPosition != null }
		val response = if (posTrackers.isNotEmpty()) {
			HeightResponse
				.createHeightResponse(
					fbb,
					posTrackers.minOf { it.position.y },
					posTrackers.find { it.trackerPosition == TrackerPosition.HEAD }?.position?.y
						?: posTrackers.maxOf { it.position.y },
				)
		} else {
			HeightResponse
				.createHeightResponse(
					fbb,
					0f,
					0f,
				)
		}
		fbb.finish(createRPCMessage(fbb, RpcMessage.HeightResponse, response, messageHeader))
		conn.send(fbb.dataBuffer())
	}

	fun onMagToggleRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(MagToggleRequest()) as? MagToggleRequest ?: return
		val fbb = FlatBufferBuilder(32)

		if (req.trackerId() == null) {
			val response = MagToggleResponse.createMagToggleResponse(
				fbb,
				0,
				api.server.configManager.vrConfig.server.useMagnetometerOnAllTrackers,
			)
			fbb.finish(createRPCMessage(fbb, RpcMessage.MagToggleResponse, response, messageHeader))
			conn.send(fbb.dataBuffer())
			return
		}

		val tracker = api.server.getTrackerById(req.trackerId().unpack()) ?: return
		val trackerId = createTrackerId(fbb, tracker)
		val response = MagToggleResponse.createMagToggleResponse(
			fbb,
			trackerId,
			tracker.config.shouldHaveMagEnabled == true,
		)
		fbb.finish(createRPCMessage(fbb, RpcMessage.MagToggleResponse, response, messageHeader))
		conn.send(fbb.dataBuffer())
	}

	fun onChangeMagToggleRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(ChangeMagToggleRequest()) as? ChangeMagToggleRequest ?: return

		if (req.trackerId() == null) {
			mainScope.launch {
				withTimeoutOrNull(MAG_TIMEOUT) {
					api.server.configManager.vrConfig.server.defineMagOnAllTrackers(req.enable())
				}

				val fbb = FlatBufferBuilder(32)
				val response = MagToggleResponse.createMagToggleResponse(
					fbb,
					0,
					api.server.configManager.vrConfig.server.useMagnetometerOnAllTrackers,
				)
				fbb.finish(createRPCMessage(fbb, RpcMessage.MagToggleResponse, response, messageHeader))
				conn.send(fbb.dataBuffer())
			}
			return
		}

		val tracker = api.server.getTrackerById(req.trackerId().unpack()) ?: return
		if (tracker.device == null || tracker.config.shouldHaveMagEnabled == req.enable()) return
		val state = req.enable()
		tracker.config.shouldHaveMagEnabled = state
		// Don't apply magnetometer setting if use magnetometer global setting is not enabled
		if (!api.server.configManager.vrConfig.server.useMagnetometerOnAllTrackers) {
			val fbb = FlatBufferBuilder(32)
			val trackerId = createTrackerId(fbb, tracker)
			val response = MagToggleResponse.createMagToggleResponse(
				fbb,
				trackerId,
				state,
			)
			fbb.finish(createRPCMessage(fbb, RpcMessage.MagToggleResponse, response, messageHeader))
			conn.send(fbb.dataBuffer())
			return
		}

		mainScope.launch {
			withTimeoutOrNull(MAG_TIMEOUT) {
				tracker.device.setMag(state, tracker.trackerNum)
			}

			val fbb = FlatBufferBuilder(32)
			val trackerId = createTrackerId(fbb, tracker)
			val response = MagToggleResponse.createMagToggleResponse(
				fbb,
				trackerId,
				state,
			)
			fbb.finish(createRPCMessage(fbb, RpcMessage.MagToggleResponse, response, messageHeader))
			conn.send(fbb.dataBuffer())
		}
	}

	private fun onEnableStayAlignedRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val request =
			messageHeader.message(EnableStayAlignedRequest()) as? EnableStayAlignedRequest
				?: return

		val configManager = api.server.configManager

		val config = configManager.vrConfig.stayAlignedConfig
		config.enabled = request.enable()
		if (request.enable()) {
			config.setupComplete = true
		}

		configManager.saveConfig()

		sendSettingsChangedResponse(conn, messageHeader)
	}

	private fun onDetectStayAlignedRelaxedPoseRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val request =
			messageHeader.message(ResetStayAlignedRelaxedPoseRequest()) as? ResetStayAlignedRelaxedPoseRequest
				?: return

		val configManager = api.server.configManager
		val config = configManager.vrConfig.stayAlignedConfig

		val pose = request.pose()

		val poseConfig =
			when (pose) {
				StayAlignedRelaxedPose.STANDING -> config.standingRelaxedPose
				StayAlignedRelaxedPose.SITTING -> config.sittingRelaxedPose
				StayAlignedRelaxedPose.FLAT -> config.flatRelaxedPose
				else -> return
			}

		val relaxedPose = RelaxedPose.fromTrackers(api.server.humanPoseManager.skeleton)

		poseConfig.enabled = true
		poseConfig.upperLegAngleInDeg = relaxedPose.upperLeg.toDeg()
		poseConfig.lowerLegAngleInDeg = relaxedPose.lowerLeg.toDeg()
		poseConfig.footAngleInDeg = relaxedPose.foot.toDeg()

		configManager.saveConfig()

		LogManager.info("[detectStayAlignedRelaxedPose] pose=$pose $relaxedPose")

		sendSettingsChangedResponse(conn, messageHeader)
	}

	private fun onResetStayAlignedRelaxedPoseRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val request =
			messageHeader.message(ResetStayAlignedRelaxedPoseRequest()) as? ResetStayAlignedRelaxedPoseRequest
				?: return

		val configManager = api.server.configManager
		val config = configManager.vrConfig.stayAlignedConfig

		val pose = request.pose()

		val poseConfig =
			when (pose) {
				StayAlignedRelaxedPose.STANDING -> config.standingRelaxedPose
				StayAlignedRelaxedPose.SITTING -> config.sittingRelaxedPose
				StayAlignedRelaxedPose.FLAT -> config.flatRelaxedPose
				else -> return
			}

		poseConfig.enabled = false
		poseConfig.upperLegAngleInDeg = 0.0f
		poseConfig.lowerLegAngleInDeg = 0.0f
		poseConfig.footAngleInDeg = 0.0f

		LogManager.info("[resetStayAlignedRelaxedPose] pose=$pose")

		sendSettingsChangedResponse(conn, messageHeader)
	}

	fun sendSettingsChangedResponse(conn: GenericConnection, messageHeader: RpcMessageHeader?) {
		val fbb = FlatBufferBuilder(32)
		val settings = createSettingsResponse(fbb, api.server)
		val outbound = createRPCMessage(fbb, RpcMessage.SettingsResponse, settings, messageHeader)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}
}
const val MAG_TIMEOUT: Long = 10_000L
