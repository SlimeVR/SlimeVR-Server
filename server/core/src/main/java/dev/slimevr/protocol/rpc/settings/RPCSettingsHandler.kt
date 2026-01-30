package dev.slimevr.protocol.rpc.settings

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.config.ArmsResetModes
import dev.slimevr.filtering.TrackerFilters
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles
import dev.slimevr.tracking.processor.config.SkeletonConfigValues
import dev.slimevr.tracking.trackers.TrackerRole
import solarxr_protocol.rpc.ChangeSettingsRequest
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.SettingsResponse
import kotlin.math.*

class RPCSettingsHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI) {
	init {
		rpcHandler.registerPacketListener(RpcMessage.SettingsRequest, ::onSettingsRequest)
		rpcHandler.registerPacketListener(RpcMessage.ChangeSettingsRequest, ::onChangeSettingsRequest)
		rpcHandler.registerPacketListener(RpcMessage.SettingsResetRequest, ::onSettingsResetRequest)
	}

	fun onSettingsRequest(conn: GenericConnection, messageHeader: RpcMessageHeader?) {
		rpcHandler.sendSettingsChangedResponse(conn, messageHeader)
	}

	fun onChangeSettingsRequest(conn: GenericConnection?, messageHeader: RpcMessageHeader) {
		val req = messageHeader
			.message(ChangeSettingsRequest()) as? ChangeSettingsRequest ?: return

		req.steamVrTrackers?.let { steamVrTrackers ->
			val bridge = api.server
				.getVRBridge(ISteamVRBridge::class.java)

			if (bridge != null) {
				bridge.changeShareSettings(TrackerRole.WAIST, steamVrTrackers.waist)
				bridge.changeShareSettings(TrackerRole.CHEST, steamVrTrackers.chest)
				bridge.changeShareSettings(TrackerRole.LEFT_FOOT, steamVrTrackers.leftFoot)
				bridge.changeShareSettings(TrackerRole.RIGHT_FOOT, steamVrTrackers.rightFoot)
				bridge.changeShareSettings(TrackerRole.LEFT_KNEE, steamVrTrackers.leftKnee)
				bridge.changeShareSettings(TrackerRole.RIGHT_KNEE, steamVrTrackers.rightKnee)
				bridge.changeShareSettings(TrackerRole.LEFT_ELBOW, steamVrTrackers.leftElbow)
				bridge.changeShareSettings(TrackerRole.RIGHT_ELBOW, steamVrTrackers.rightElbow)
				bridge.changeShareSettings(TrackerRole.LEFT_HAND, steamVrTrackers.leftHand)
				bridge.changeShareSettings(TrackerRole.RIGHT_HAND, steamVrTrackers.rightHand)
				bridge.setAutomaticSharedTrackers(steamVrTrackers.automaticTrackerToggle)
			}
		}

		req.filtering?.let { filtering ->
			val type = TrackerFilters.fromId(filtering.type)
			if (type != null) {
				val filtersConfig = api.server.configManager
					.vrConfig
					.filters
				filtersConfig.type = type.configKey
				filtersConfig.amount = filtering.amount
				filtersConfig.updateTrackersFilters()
			}
		}

		req.driftCompensation?.let { driftCompensation ->
			val driftCompensationConfig = api.server.configManager
				.vrConfig
				.driftCompensation
			driftCompensationConfig.enabled = driftCompensation.enabled
			driftCompensationConfig.prediction = driftCompensation.prediction
			driftCompensationConfig.amount = driftCompensation.amount
			driftCompensationConfig.maxResets = driftCompensation.maxResets.toInt()
			driftCompensationConfig.updateTrackersDriftCompensation()
		}

		req.oscRouter?.let { oscRouterSettings ->
			val oscRouterConfig = api.server.configManager
				.vrConfig
				.oscRouter
			val oscRouter = api.server.oSCRouter
			oscRouterSettings.oscSettings?.let { osc ->
				oscRouterConfig.enabled = osc.enabled
				oscRouterConfig.portIn = osc.portIn.toInt()
				oscRouterConfig.portOut = osc.portOut.toInt()
				osc.address?.let {
					oscRouterConfig.address = it
				}
			}

			oscRouter.refreshSettings(true)
		}

		req.vrcOsc?.let { vrcOsc ->
			val vrcOSCConfig = api.server.configManager
				.vrConfig
				.vrcOSC
			val vrcOscHandler = api.server.vrcOSCHandler
			val trackers = vrcOsc.trackers

			vrcOsc.oscSettings?.let { osc ->
				vrcOSCConfig.enabled = osc.enabled
				vrcOSCConfig.portIn = osc.portIn.toInt()
				vrcOSCConfig.portOut = osc.portOut.toInt()
				osc.address?.let {
					vrcOSCConfig.address = it
				}
			}
			vrcOsc.trackers?.let { trackers ->
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.HEAD, trackers.head)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.CHEST, trackers.chest)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.WAIST, trackers.waist)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.LEFT_KNEE, trackers.knees)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.RIGHT_KNEE, trackers.knees)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.LEFT_FOOT, trackers.feet)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.RIGHT_FOOT, trackers.feet)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.LEFT_ELBOW, trackers.elbows)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.RIGHT_ELBOW, trackers.elbows)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.LEFT_HAND, trackers.hands)
				vrcOSCConfig.setOSCTrackerRole(TrackerRole.RIGHT_HAND, trackers.hands)
			}
			vrcOSCConfig.oscqueryEnabled = vrcOsc.oscqueryEnabled

			vrcOscHandler.refreshSettings(true)
		}

		req.vmcOsc?.let { vmcOsc ->
			val vmcConfig = api.server.configManager
				.vrConfig
				.vmc
			val vmcHandler = api.server.vMCHandler

			vmcOsc.oscSettings?.let { osc ->
				vmcConfig.enabled = osc.enabled
				vmcConfig.portIn = osc.portIn.toInt()
				vmcConfig.portOut = osc.portOut.toInt()
				osc.address?.let {
					vmcConfig.address = it
				}
			}
			vmcOsc.vrmJson?.let {
				vmcConfig.vrmJson = it
			}
			vmcConfig.anchorHip = vmcOsc.anchorHip
			vmcConfig.mirrorTracking = vmcOsc.mirrorTracking

			vmcHandler.refreshSettings(true)
		}

		req.tapDetectionSettings?.let { tapDetectionSettings ->
			val tapDetectionConfig = api.server.configManager.vrConfig.tapDetection

			// enable/disable tap detection
			tapDetectionConfig.yawResetEnabled = tapDetectionSettings.yawResetEnabled ?: false
			tapDetectionConfig.fullResetEnabled = tapDetectionSettings.fullResetEnabled ?: false
			tapDetectionConfig
				.mountingResetEnabled = tapDetectionSettings.mountingResetEnabled ?: false
			tapDetectionConfig.setupMode = tapDetectionSettings.setupMode ?: false

			// set number of trackers that can have high accel before taps
			// are rejected
			tapDetectionSettings.numberTrackersOverThreshold?.let {
				tapDetectionConfig.numberTrackersOverThreshold = it.toInt()
			}

			// set tap detection delays
			tapDetectionSettings.yawResetDelay?.let {
				tapDetectionConfig.yawResetDelay = it
			}
			tapDetectionSettings.fullResetDelay?.let {
				tapDetectionConfig.fullResetDelay = it
			}
			tapDetectionSettings.mountingResetDelay?.let {
				tapDetectionConfig.mountingResetDelay = it
			}
			api.server.humanPoseManager.updateTapDetectionConfig()

			// set the number of taps required for each action
			tapDetectionSettings.yawResetTaps?.let {
				tapDetectionConfig.yawResetTaps = it.toInt()
			}
			tapDetectionSettings.fullResetTaps?.let {
				tapDetectionConfig.fullResetTaps = it.toInt()
			}
			tapDetectionSettings.mountingResetTaps?.let {
				tapDetectionConfig.mountingResetTaps = it.toInt()
			}

		}

		req.modelSettings?.let { modelSettings ->
			val hpm = api.server.humanPoseManager
			val legTweaksConfig = api.server.configManager.vrConfig.legTweaks

			modelSettings.toggles?.let { toggles ->
				// Note: toggles.has____ returns the same as toggles._____ this
				// seems like a bug
				hpm.setToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL, toggles.extendedSpine)
				hpm
					.setToggle(
						SkeletonConfigToggles.EXTENDED_PELVIS_MODEL,
						toggles.extendedPelvis,
					)
				hpm.setToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL, toggles.extendedKnee)
				hpm
					.setToggle(
						SkeletonConfigToggles.FORCE_ARMS_FROM_HMD,
						toggles.forceArmsFromHmd,
					)
				hpm.setToggle(SkeletonConfigToggles.EXTENDED_SPINE_MODEL, toggles.extendedSpine)
				hpm
					.setToggle(
						SkeletonConfigToggles.EXTENDED_PELVIS_MODEL,
						toggles.extendedPelvis,
					)
				hpm.setToggle(SkeletonConfigToggles.EXTENDED_KNEE_MODEL, toggles.extendedKnee)
				hpm
					.setToggle(
						SkeletonConfigToggles.FORCE_ARMS_FROM_HMD,
						toggles.forceArmsFromHmd,
					)
				hpm.setToggle(SkeletonConfigToggles.FLOOR_CLIP, toggles.floorClip)
				hpm
					.setToggle(
						SkeletonConfigToggles.SKATING_CORRECTION,
						toggles.skatingCorrection,
					)
				hpm.setToggle(SkeletonConfigToggles.TOE_SNAP, toggles.toeSnap)
				hpm.setToggle(SkeletonConfigToggles.FOOT_PLANT, toggles.footPlant)
				hpm.setToggle(SkeletonConfigToggles.SELF_LOCALIZATION, toggles.selfLocalization)
				hpm.setToggle(SkeletonConfigToggles.USE_POSITION, toggles.usePosition)
				hpm.setToggle(SkeletonConfigToggles.ENFORCE_CONSTRAINTS, toggles.enforceConstraints)
				hpm.setToggle(SkeletonConfigToggles.CORRECT_CONSTRAINTS, toggles.correctConstraints)
			}

			modelSettings.ratios?.let { ratios ->
				ratios.imputeWaistFromChestHip?.let { imputeWaistFromChestHip ->
					hpm
						.setValue(
							SkeletonConfigValues.WAIST_FROM_CHEST_HIP_AVERAGING,
							max(0f, imputeWaistFromChestHip),
						)
				}
				ratios.imputeWaistFromChestLegs?.let { imputeWaistFromChestLegs ->
					hpm
						.setValue(
							SkeletonConfigValues.WAIST_FROM_CHEST_LEGS_AVERAGING,
							max(0f, imputeWaistFromChestLegs),
						)
				}
				ratios.imputeHipFromChestLegs?.let { imputeHipFromChestLegs ->
					hpm
						.setValue(
							SkeletonConfigValues.HIP_FROM_CHEST_LEGS_AVERAGING,
							max(0f, imputeHipFromChestLegs),
						)
				}
				ratios.imputeHipFromWaistLegs?.let { imputeHipFromWaistLegs ->
					hpm
						.setValue(
							SkeletonConfigValues.HIP_FROM_WAIST_LEGS_AVERAGING,
							max(0f, imputeHipFromWaistLegs),
						)
				}
				ratios.interpHipLegs?.let { interpHipLegs ->
					hpm
						.setValue(
							SkeletonConfigValues.HIP_LEGS_AVERAGING,
							max(0f, interpHipLegs),
						)
				}
				ratios.interpKneeTrackerAnkle?.let { interpKneeTrackerAnkle ->
					hpm
						.setValue(
							SkeletonConfigValues.KNEE_TRACKER_ANKLE_AVERAGING,
							max(0f, interpKneeTrackerAnkle),
						)
				}
				ratios.interpKneeAnkle?.let { interpKneeAnkle ->
					hpm
						.setValue(
							SkeletonConfigValues.KNEE_ANKLE_AVERAGING,
							max(0f, interpKneeAnkle),
						)
				}
			}

			modelSettings.legTweaks?.let { legTweaks ->
				legTweaks.correctionStrength?.let {
					legTweaksConfig.correctionStrength = it
				}
				api.server.humanPoseManager.updateLegTweaksConfig()
			}

			modelSettings.skeletonHeight?.let { skeletonHeight ->
				skeletonHeight.hmdHeight?.let {
					api.server.configManager.vrConfig.skeleton.hmdHeight = it
				}
				skeletonHeight.floorHeight?.let {
					api.server.configManager.vrConfig.skeleton.floorHeight = it
				}
			}

			hpm.saveConfig()
		}

		req.autoBoneSettings?.let { autoBoneSettings ->
			val autoBoneConfig = api.server.configManager
				.vrConfig
				.autoBone

			readAutoBoneSettings(autoBoneSettings, autoBoneConfig)
		}

		req.resetsSettings?.let { resetsSettings ->
			val resetsConfig = api.server.configManager
				.vrConfig
				.resetsConfig
			val mode = ArmsResetModes
				.fromId(maxOf(resetsSettings.armsMountingResetMode, 0u))
			if (mode != null) {
				resetsConfig.mode = mode
			}
			resetsConfig.resetMountingFeet = resetsSettings.resetMountingFeet
			resetsConfig.saveMountingReset = resetsSettings.saveMountingReset
			resetsConfig.yawResetSmoothTime = resetsSettings.yawResetSmoothTime
			resetsConfig.resetHmdPitch = resetsSettings.resetHmdPitch
			resetsConfig.updateTrackersResetsSettings()
		}

		req.stayAligned?.let { stayAligned ->
			val config = api.server.configManager.vrConfig.stayAlignedConfig
			config.enabled = stayAligned.enabled
			config.hideYawCorrection = stayAligned.hideYawCorrection
			config.standingRelaxedPose.enabled = stayAligned.standingEnabled
			config.standingRelaxedPose.upperLegAngleInDeg = stayAligned.standingUpperLegAngle
			config.standingRelaxedPose.lowerLegAngleInDeg = stayAligned.standingLowerLegAngle
			config.standingRelaxedPose.footAngleInDeg = stayAligned.standingFootAngle
			config.sittingRelaxedPose.enabled = stayAligned.sittingEnabled
			config.sittingRelaxedPose.upperLegAngleInDeg = stayAligned.sittingUpperLegAngle
			config.sittingRelaxedPose.lowerLegAngleInDeg = stayAligned.sittingLowerLegAngle
			config.sittingRelaxedPose.footAngleInDeg = stayAligned.sittingFootAngle
			config.flatRelaxedPose.enabled = stayAligned.flatEnabled
			config.flatRelaxedPose.upperLegAngleInDeg = stayAligned.flatUpperLegAngle
			config.flatRelaxedPose.lowerLegAngleInDeg = stayAligned.flatLowerLegAngle
			config.flatRelaxedPose.footAngleInDeg = stayAligned.flatFootAngle
		}

		req.hidSettings?.let { hidSettings ->
			val config = api.server.configManager.vrConfig.hidConfig
			config.trackersOverHID = hidSettings.trackersOverHid
		}

		api.server.configManager.saveConfig()
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
					createSteamVRSettings(fbb, bridge), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
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
