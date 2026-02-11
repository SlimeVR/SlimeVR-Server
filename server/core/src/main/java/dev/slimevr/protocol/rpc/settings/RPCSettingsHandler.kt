package dev.slimevr.protocol.rpc.settings

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.config.ArmsResetMode
import dev.slimevr.config.OSCConfig
import dev.slimevr.config.TapDetectionConfig
import dev.slimevr.config.VMCConfig
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.filtering.TrackerFilters
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles
import dev.slimevr.tracking.processor.config.SkeletonConfigValues
import dev.slimevr.tracking.trackers.TrackerRole
import dev.slimevr.tracking.trackers.toKey
import solarxr_protocol.rpc.ChangeSettingsRequest
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.SettingsResponse
import kotlin.math.*

class RPCSettingsHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI) {
	init {
		rpcHandler.registerPacketListener(RpcMessage.SettingsRequest) { conn: GenericConnection, messageHeader: RpcMessageHeader? ->
			this.onSettingsRequest(
				conn,
				messageHeader,
			)
		}
		rpcHandler
			.registerPacketListener(
				RpcMessage.ChangeSettingsRequest,
			) { conn: GenericConnection?, messageHeader: RpcMessageHeader ->
				this.onChangeSettingsRequest(
					conn,
					messageHeader,
				)
			}
		rpcHandler.registerPacketListener(RpcMessage.SettingsResetRequest) { conn: GenericConnection, messageHeader: RpcMessageHeader? ->
			this.onSettingsResetRequest(conn, messageHeader)
		}
	}

	fun onSettingsRequest(conn: GenericConnection, messageHeader: RpcMessageHeader?) {
		rpcHandler.sendSettingsChangedResponse(conn, messageHeader)
	}

	fun onChangeSettingsRequest(conn: GenericConnection?, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(ChangeSettingsRequest()) as? ChangeSettingsRequest ?: return

		if (req.steamVrTrackers() != null) {
			val bridge = api.server
				.getVRBridge(ISteamVRBridge::class.java)

			if (bridge != null) {
				bridge.changeShareSettings(TrackerRole.WAIST, req.steamVrTrackers().waist())
				bridge.changeShareSettings(TrackerRole.CHEST, req.steamVrTrackers().chest())
				bridge.changeShareSettings(TrackerRole.LEFT_FOOT, req.steamVrTrackers().leftFoot())
				bridge.changeShareSettings(TrackerRole.RIGHT_FOOT, req.steamVrTrackers().rightFoot())
				bridge.changeShareSettings(TrackerRole.LEFT_KNEE, req.steamVrTrackers().leftKnee())
				bridge.changeShareSettings(TrackerRole.RIGHT_KNEE, req.steamVrTrackers().rightKnee())
				bridge.changeShareSettings(TrackerRole.LEFT_ELBOW, req.steamVrTrackers().leftElbow())
				bridge.changeShareSettings(TrackerRole.RIGHT_ELBOW, req.steamVrTrackers().rightElbow())
				bridge.changeShareSettings(TrackerRole.LEFT_HAND, req.steamVrTrackers().leftHand())
				bridge.changeShareSettings(TrackerRole.RIGHT_HAND, req.steamVrTrackers().rightHand())
				bridge.setAutomaticSharedTrackers(req.steamVrTrackers().automaticTrackerToggle())
			}
		}

		if (req.filtering() != null) {
			val type = TrackerFilters.fromId(req.filtering().type())
			if (type != null) {
				api.server.configManager.settings.update {
					it.copy(
						filters = it.filters.copy(
							type = type.configKey,
							amount = req.filtering().amount(),
						),
					)
				}
				filtersConfig.updateTrackersFilters() // FIXME: move this outside of the config file
			}
		}

		if (req.oscRouter() != null) {
			val oscRouter = api.server.oSCRouter
			val osc = req.oscRouter().oscSettings()
			api.server.configManager.settings.update {
				it.copy(
					oscRouter = if (osc != null) {
						OSCConfig(
							enabled = osc.enabled(),
							portIn = osc.portIn(),
							portOut = osc.portOut(),
							address = osc.address(),
						)
					} else {
						it.oscRouter
					},
				)
			}
			oscRouter.refreshSettings(true)
		}

		if (req.vrcOsc() != null) {
			val osc = req.vrcOsc().oscSettings()
			val trackers = req.vrcOsc().trackers()
			api.server.configManager.settings.update {
				it.copy(
					vrcOSC = if (osc != null) {
						VRCOSCConfig(
							enabled = osc.enabled(),
							portIn = osc.portIn(),
							portOut = osc.portOut(),
							address = osc.address(),
							oscqueryEnabled = req.vrcOsc().oscqueryEnabled(),
							trackers = if (trackers != null) {
								mutableMapOf(
									TrackerRole.HEAD.toKey to trackers.head(),
									TrackerRole.CHEST.toKey to trackers.chest(),
									TrackerRole.WAIST.toKey to trackers.waist(),
									TrackerRole.LEFT_KNEE.toKey to trackers.knees(),
									TrackerRole.RIGHT_KNEE.toKey to trackers.knees(),
									TrackerRole.LEFT_FOOT.toKey to trackers.feet(),
									TrackerRole.RIGHT_FOOT.toKey to trackers.feet(),
									TrackerRole.LEFT_ELBOW.toKey to trackers.elbows(),
									TrackerRole.RIGHT_ELBOW.toKey to trackers.elbows(),
									TrackerRole.LEFT_HAND.toKey to trackers.hands(),
									TrackerRole.RIGHT_HAND.toKey to trackers.hands(),
								)
							} else {
								it.vrcOSC.trackers
							},
						)
					} else {
						it.vrcOSC
					},
				)
			}
			val vrcOscHandler = api.server.vrcOSCHandler
			vrcOscHandler.refreshSettings(true)
		}

		if (req.vmcOsc() != null) {
			val vmcHandler = api.server.vMCHandler
			val osc = req.vmcOsc().oscSettings()
			api.server.configManager.settings.update {
				it.copy(
					vmc = if (osc != null) {
						VMCConfig(
							enabled = osc.enabled(),
							portIn = osc.portIn(),
							portOut = osc.portOut(),
							address = osc.address(),
							vrmJson = if (req.vmcOsc().vrmJson() != null) req.vmcOsc().vrmJson().ifEmpty { null } else it.vmc.vrmJson,
							anchorHip = req.vmcOsc().anchorHip(),
							mirrorTracking = req.vmcOsc().mirrorTracking(),
						)
					} else {
						it.vmc
					},
				)
			}
			vmcHandler.refreshSettings(true)
		}

		if (req.tapDetectionSettings() != null) {
			val tapDetectionSettings = req.tapDetectionSettings()
			if (tapDetectionSettings != null) {
				api.server.configManager.settings.update {
					it.copy(
						tapDetection = TapDetectionConfig(
							yawResetEnabled = tapDetectionSettings.yawResetEnabled(),
							fullResetEnabled = tapDetectionSettings.fullResetEnabled(),
							mountingResetEnabled = tapDetectionSettings.mountingResetEnabled(),
							setupMode = tapDetectionSettings.setupMode(),
							numberTrackersOverThreshold = tapDetectionSettings.numberTrackersOverThreshold(),
							yawResetDelay = tapDetectionSettings.yawResetDelay(),
							fullResetDelay = tapDetectionSettings.fullResetDelay(),
							mountingResetDelay = tapDetectionSettings.mountingResetDelay(),
							yawResetTaps = tapDetectionSettings.yawResetTaps(),
							fullResetTaps = tapDetectionSettings.fullResetTaps(),
							mountingResetTaps = tapDetectionSettings.mountingResetTaps(),
						)
					)
				}
				api.server.humanPoseManager.updateTapDetectionConfig()
			}
		}

		val modelSettings = req.modelSettings()
		if (modelSettings != null) {
			val hpm = api.server.humanPoseManager
			val toggles = modelSettings.toggles()
			val ratios = modelSettings.ratios()
			val legTweaks = modelSettings.legTweaks()

			if (toggles != null) {
				// Note: toggles.has____ returns the same as toggles._____ this
				// seems like a bug
				hpm.setToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL, toggles.extendedSpine())
				hpm
					.setToggle(
						SkeletonConfigToggles.EXTENDED_PELVIS_MODEL,
						toggles.extendedPelvis(),
					)
				hpm.setToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL, toggles.extendedKnee())
				hpm
					.setToggle(
						SkeletonConfigToggles.FORCE_ARMS_FROM_HMD,
						toggles.forceArmsFromHmd(),
					)
				hpm.setToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL, toggles.extendedSpine())
				hpm
					.setToggle(
						SkeletonConfigToggles.EXTENDED_PELVIS_MODEL,
						toggles.extendedPelvis(),
					)
				hpm.setToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL, toggles.extendedKnee())
				hpm
					.setToggle(
						SkeletonConfigToggles.FORCE_ARMS_FROM_HMD,
						toggles.forceArmsFromHmd(),
					)
				hpm.setToggle(SkeletonConfigToggles.FLOOR_CLIP, toggles.floorClip())
				hpm
					.setToggle(
						SkeletonConfigToggles.SKATING_CORRECTION,
						toggles.skatingCorrection(),
					)
				hpm.setToggle(SkeletonConfigToggles.TOE_SNAP, toggles.toeSnap())
				hpm.setToggle(SkeletonConfigToggles.FOOT_PLANT, toggles.footPlant())
				hpm.setToggle(SkeletonConfigToggles.SELF_LOCALIZATION, toggles.selfLocalization())
				hpm.setToggle(SkeletonConfigToggles.USE_POSITION, toggles.usePosition())
				hpm.setToggle(SkeletonConfigToggles.ENFORCE_CONSTRAINTS, toggles.enforceConstraints())
				hpm.setToggle(SkeletonConfigToggles.CORRECT_CONSTRAINTS, toggles.correctConstraints())
			}

			if (ratios != null) {
				if (ratios.hasImputeWaistFromChestHip()) {
					hpm
						.setValue(
							SkeletonConfigValues.WAIST_FROM_CHEST_HIP_AVERAGING,
							max(0f, ratios.imputeWaistFromChestHip()),
						)
				}
				if (ratios.hasImputeWaistFromChestLegs()) {
					hpm
						.setValue(
							SkeletonConfigValues.WAIST_FROM_CHEST_LEGS_AVERAGING,
							max(0f, ratios.imputeWaistFromChestLegs()),
						)
				}
				if (ratios.hasImputeHipFromChestLegs()) {
					hpm
						.setValue(
							SkeletonConfigValues.HIP_FROM_CHEST_LEGS_AVERAGING,
							max(0f, ratios.imputeHipFromChestLegs()),
						)
				}
				if (ratios.hasImputeHipFromWaistLegs()) {
					hpm
						.setValue(
							SkeletonConfigValues.HIP_FROM_WAIST_LEGS_AVERAGING,
							max(0f, ratios.imputeHipFromWaistLegs()),
						)
				}
				if (ratios.hasInterpHipLegs()) {
					hpm
						.setValue(
							SkeletonConfigValues.HIP_LEGS_AVERAGING,
							max(0f, ratios.interpHipLegs()),
						)
				}
				if (ratios.hasInterpKneeTrackerAnkle()) {
					hpm
						.setValue(
							SkeletonConfigValues.KNEE_TRACKER_ANKLE_AVERAGING,
							max(0f, ratios.interpKneeTrackerAnkle()),
						)
				}
				if (ratios.hasInterpKneeAnkle()) {
					hpm
						.setValue(
							SkeletonConfigValues.KNEE_ANKLE_AVERAGING,
							max(0f, ratios.interpKneeAnkle()),
						)
				}
			}


			if (legTweaks != null) {
				if (legTweaks.hasCorrectionStrength()) {
					api.server.configManager.settings.update {
						it.copy(legTweaks = it.legTweaks.copy(
							correctionStrength = legTweaks.correctionStrength()
						))
					}
					api.server.humanPoseManager.updateLegTweaksConfig()
				}
			}

			modelSettings.skeletonHeight()?.let { skeletonHeight ->
				api.server.configManager.user.update {
					it.copy(
						skeleton = it.skeleton.copy(
							hmdHeight = skeletonHeight.hmdHeight(),
							floorHeight = skeletonHeight.floorHeight()
						)
					)
				}
			}

			hpm.saveConfig()
		}

		val autoBoneSettings = req.autoBoneSettings()
		if (autoBoneSettings != null) {
			val autoBoneConfig = api.server.configManager
				.vrConfig
				.autoBone

			RPCSettingsBuilder.readAutoBoneSettings(autoBoneSettings, autoBoneConfig)
		}

		if (req.resetsSettings() != null) {
			val resetsConfig = api.server.configManager
				.vrConfig
				.resetsConfig
			val mode = ArmsResetMode
				.fromId(max(req.resetsSettings().armsMountingResetMode(), 0))
			if (mode != null) {
				resetsConfig.mode = mode
			}
			resetsConfig.resetMountingFeet = req.resetsSettings().resetMountingFeet()
			resetsConfig.saveMountingReset = req.resetsSettings().saveMountingReset()
			resetsConfig.yawResetSmoothTime = req.resetsSettings().yawResetSmoothTime()
			resetsConfig.resetHmdPitch = req.resetsSettings().resetHmdPitch()
			resetsConfig.updateTrackersResetsSettings()
		}

		if (req.stayAligned() != null) {
			val config = api.server.configManager.vrConfig.stayAlignedConfig
			val requestConfig = req.stayAligned()
			config.enabled = requestConfig.enabled()
			config.hideYawCorrection = requestConfig.hideYawCorrection()
			config.standingRelaxedPose.enabled = requestConfig.standingEnabled()
			config.standingRelaxedPose.upperLegAngleInDeg = requestConfig.standingUpperLegAngle()
			config.standingRelaxedPose.lowerLegAngleInDeg = requestConfig.standingLowerLegAngle()
			config.standingRelaxedPose.footAngleInDeg = requestConfig.standingFootAngle()
			config.sittingRelaxedPose.enabled = requestConfig.sittingEnabled()
			config.sittingRelaxedPose.upperLegAngleInDeg = requestConfig.sittingUpperLegAngle()
			config.sittingRelaxedPose.lowerLegAngleInDeg = requestConfig.sittingLowerLegAngle()
			config.sittingRelaxedPose.footAngleInDeg = requestConfig.sittingFootAngle()
			config.flatRelaxedPose.enabled = requestConfig.flatEnabled()
			config.flatRelaxedPose.upperLegAngleInDeg = requestConfig.flatUpperLegAngle()
			config.flatRelaxedPose.lowerLegAngleInDeg = requestConfig.flatLowerLegAngle()
			config.flatRelaxedPose.footAngleInDeg = requestConfig.flatFootAngle()
		}

		if (req.hidSettings() != null) {
			val config = api.server.configManager.vrConfig.hidConfig
			val requestConfig = req.hidSettings()
			config.trackersOverHID = requestConfig.trackersOverHid()
		}

		api.server.configManager.settings.save()
		api.server.configManager.user.save()
	}

	fun onSettingsResetRequest(conn: GenericConnection, messageHeader: RpcMessageHeader?) {
		api.server.configManager.resetConfig()
	}

	companion object {
		fun sendSteamVRUpdatedSettings(api: ProtocolAPI, rpcHandler: RPCHandler) {
			val fbb = FlatBufferBuilder(32)
			val bridge: ISteamVRBridge =
				api.server.getVRBridge(ISteamVRBridge::class.java) ?: return

			val settings = SettingsResponse
				.createSettingsResponse(
					fbb,
					RPCSettingsBuilder.createSteamVRSettings(fbb, bridge), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				)
			val outbound =
				rpcHandler.createRPCMessage(fbb, RpcMessage.SettingsResponse, settings)
			fbb.finish(outbound)
			api.apiServers.forEach { apiServer ->
				apiServer.apiConnections.forEach { it.send(fbb.dataBuffer()) }
			}
		}
	}
}
