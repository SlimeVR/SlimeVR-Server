package dev.slimevr.trackingchecklist

import dev.slimevr.TestAppContext
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestDriverBridge
import dev.slimevr.buildTestResetsManager
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestTracker
import dev.slimevr.buildTestVrServer
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.networkprofile.NetworkInfo
import dev.slimevr.networkprofile.NetworkProfileActions
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.resets.ResetBodyParts
import dev.slimevr.resets.ResetsActions
import dev.slimevr.resets.ResetsManager
import dev.slimevr.routing.BoneRoutingActions
import dev.slimevr.routing.BoneRoutingManager
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.TrackingChecklistStep
import solarxr_protocol.rpc.TrackingChecklistStepId
import solarxr_protocol.rpc.TrackingChecklistTrackerReset
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TrackingChecklistTest {

	private class Harness(scope: TestScope, networkSupported: Boolean = true) {
		val server: VRServer = buildTestVrServer(scope.backgroundScope)
		val settings: Settings = buildTestSettings(scope.backgroundScope)
		val resetsManager: ResetsManager = buildTestResetsManager(server, settings, scope.backgroundScope)
		val networkProfileManager: NetworkProfileManager = NetworkProfileManager.create(scope.backgroundScope, isSupported = networkSupported)
		private val appContext = object : TestAppContext() {
			override val server = this@Harness.server
			override val resetsManager = this@Harness.resetsManager
		}
		private val checklist = TrackingChecklist.create(scope.backgroundScope)
		private val boneRouting: BoneRoutingManager = BoneRoutingManager.create(scope.backgroundScope)
		private var nextId = 1

		init {
			checklist.context.behaviours.addAll(
				listOf(
					HMDCheckBehaviour(server),
					TrackerRestCheckBehaviour(server),
					TrackerErrorCheckBehaviour(server),
					FullResetCheckBehaviour(server, resetsManager),
					MountingCalibrationCheckBehaviour(server, resetsManager, settings),
					FeetMountingCalibrationCheckBehaviour(server, resetsManager, settings),
					NetworkProfileCheckBehaviour(networkProfileManager),
					SteamVRHandsCheckBehaviour(server, settings, boneRouting),
				),
			)
			checklist.context.observeAll(checklist)
		}

		fun connectDriver() {
			val bridge = buildTestDriverBridge(server, appContext, id = nextId++)
			server.context.dispatch(VRServerActions.DriverConnected(bridge))
		}

		fun routeToDriver(vararg bones: BodyPart) {
			val routes = bones.associateWith { setOf(RoutingOutput.DRIVER) }
			boneRouting.context.dispatch(BoneRoutingActions.SetRoutes(routes))
		}

		fun setAutomatic(automatic: Boolean) {
			settings.context.dispatch(
				SettingsActions.Update { copy(boneRoutingConfig = boneRoutingConfig.copy(automatic = automatic)) },
			)
		}

		fun addTracker(
			bodyPart: BodyPart?,
			status: TrackerStatus = TrackerStatus.OK,
			origin: DeviceOrigin = DeviceOrigin.UDP,
			sensorType: ImuType? = ImuType.BNO085,
			position: Vector3? = null,
			completedRestCalibration: Boolean? = true,
		): Tracker {
			val id = nextId++
			val tracker = buildTestTracker(
				server.context.scope,
				appContext,
				settings,
				id = id,
				bodyPart = bodyPart,
				status = status,
				origin = origin,
				sensorType = sensorType,
				position = position,
				completedRestCalibration = completedRestCalibration,
			)
			server.context.dispatch(VRServerActions.NewTracker(id, tracker))
			return tracker
		}

		fun step(id: TrackingChecklistStepId): TrackingChecklistStep = checklist.context.state.value.steps[id] ?: error("unknown step $id")
	}

	@Test
	fun `FULL_RESET is disabled with no trackers`() = runTest {
		val h = Harness(this)
		runCurrent()

		assertEquals(false, h.step(TrackingChecklistStepId.FULL_RESET).enabled)
		assertEquals(true, h.step(TrackingChecklistStepId.FULL_RESET).valid)
	}

	@Test
	fun `FULL_RESET flags a connected assigned tracker and clears on full reset`() = runTest {
		val h = Harness(this)
		val tracker = h.addTracker(BodyPart.CHEST)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.FULL_RESET).enabled)
		assertEquals(false, h.step(TrackingChecklistStepId.FULL_RESET).valid)

		h.resetsManager.context.dispatch(ResetsActions.EndReset(ResetType.FULL))
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.FULL_RESET).valid)

		// Reassigning the tracker flags it again
		tracker.context.dispatch(TrackerActions.Update { copy(bodyPart = BodyPart.HIP) })
		runCurrent()
		assertEquals(false, h.step(TrackingChecklistStepId.FULL_RESET).valid)
	}

	@Test
	fun `FULL_RESET flags a tracker that reconnects`() = runTest {
		val h = Harness(this)
		val tracker = h.addTracker(BodyPart.CHEST)
		runCurrent()
		h.resetsManager.context.dispatch(ResetsActions.EndReset(ResetType.FULL))
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.FULL_RESET).valid)

		// Disconnect then reconnect
		tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.DISCONNECTED))
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.FULL_RESET).valid)
		tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		runCurrent()
		assertEquals(false, h.step(TrackingChecklistStepId.FULL_RESET).valid)
	}

	@Test
	fun `adding a tracker does not re-flag the existing already-reset trackers`() = runTest {
		val h = Harness(this)
		h.addTracker(BodyPart.CHEST)
		runCurrent()
		h.resetsManager.context.dispatch(ResetsActions.EndReset(ResetType.FULL))
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.FULL_RESET).valid)

		// A newly discovered tracker starts disconnected; adding it must not re-flag the existing one
		val added = h.addTracker(BodyPart.HIP, status = TrackerStatus.DISCONNECTED)
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.FULL_RESET).valid)

		// Only the new tracker gets flagged once it connects
		added.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		runCurrent()
		assertEquals(false, h.step(TrackingChecklistStepId.FULL_RESET).valid)
		val pending = (h.step(TrackingChecklistStepId.FULL_RESET).extraData as TrackingChecklistTrackerReset).trackersId
		assertEquals(listOf(added.context.state.value.id.toUShort()), pending)
	}

	@Test
	fun `MOUNTING_CALIBRATION is enabled with an IMU tracker and valid after a mounting reset`() = runTest {
		val h = Harness(this)
		h.addTracker(BodyPart.CHEST)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.MOUNTING_CALIBRATION).enabled)
		assertEquals(false, h.step(TrackingChecklistStepId.MOUNTING_CALIBRATION).valid)

		h.resetsManager.context.dispatch(ResetsActions.EndReset(ResetType.MOUNTING, bodyParts = null))
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.MOUNTING_CALIBRATION).valid)
	}

	@Test
	fun `FEET_MOUNTING_CALIBRATION enables with a foot tracker and validates after a feet mounting reset`() = runTest {
		val h = Harness(this)
		h.addTracker(BodyPart.LEFT_FOOT)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION).enabled)
		assertEquals(false, h.step(TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION).valid)

		h.resetsManager.context.dispatch(ResetsActions.EndReset(ResetType.MOUNTING, bodyParts = ResetBodyParts.FEET.toList()))
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION).valid)
	}

	@Test
	fun `FEET_MOUNTING_CALIBRATION is disabled without a foot tracker`() = runTest {
		val h = Harness(this)
		h.addTracker(BodyPart.CHEST)
		runCurrent()

		assertEquals(false, h.step(TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION).enabled)
	}

	@Test
	fun `UNASSIGNED_HMD is invalid until the HMD is assigned to the head`() = runTest {
		val h = Harness(this)
		// An HMD is a DRIVER-origin tracker with a computed position
		val hmd = h.addTracker(bodyPart = null, origin = DeviceOrigin.DRIVER, sensorType = null, position = Vector3.NULL)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.UNASSIGNED_HMD).enabled)
		assertEquals(false, h.step(TrackingChecklistStepId.UNASSIGNED_HMD).valid)

		hmd.context.dispatch(TrackerActions.Update { copy(bodyPart = BodyPart.HEAD) })
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.UNASSIGNED_HMD).valid)
	}

	@Test
	fun `UNASSIGNED_HMD is disabled without a driver tracker`() = runTest {
		val h = Harness(this)
		h.addTracker(BodyPart.CHEST)
		runCurrent()

		assertEquals(false, h.step(TrackingChecklistStepId.UNASSIGNED_HMD).enabled)
	}

	@Test
	fun `TRACKER_ERROR is invalid while an assigned tracker is in error`() = runTest {
		val h = Harness(this)
		val tracker = h.addTracker(BodyPart.CHEST, status = TrackerStatus.ERROR)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.TRACKER_ERROR).enabled)
		assertEquals(false, h.step(TrackingChecklistStepId.TRACKER_ERROR).valid)

		tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.TRACKER_ERROR).valid)
	}

	@Test
	fun `TRACKERS_REST_CALIBRATION is invalid until an uncalibrated tracker finishes rest calibration`() = runTest {
		val h = Harness(this)
		val tracker = h.addTracker(BodyPart.CHEST, completedRestCalibration = false)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.TRACKERS_REST_CALIBRATION).enabled)
		assertEquals(false, h.step(TrackingChecklistStepId.TRACKERS_REST_CALIBRATION).valid)

		tracker.context.dispatch(TrackerActions.Update { copy(completedRestCalibration = true) })
		runCurrent()
		assertEquals(true, h.step(TrackingChecklistStepId.TRACKERS_REST_CALIBRATION).valid)
	}

	@Test
	fun `NETWORK_PROFILE_PUBLIC is invalid while a public network is present`() = runTest {
		val h = Harness(this)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC).enabled)
		assertEquals(true, h.step(TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC).valid)

		h.networkProfileManager.context.dispatch(
			NetworkProfileActions.UpdateNetworks(listOf(NetworkInfo("wlan0", null, null, null, null))),
		)
		runCurrent()
		assertEquals(false, h.step(TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC).valid)
	}

	@Test
	fun `NETWORK_PROFILE_PUBLIC is disabled when unsupported`() = runTest {
		val h = Harness(this, networkSupported = false)
		runCurrent()

		assertEquals(false, h.step(TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC).enabled)
	}

	@Test
	fun `STEAMVR_HANDS_ENABLED is disabled without a driver`() = runTest {
		val h = Harness(this)
		h.setAutomatic(false)
		h.routeToDriver(BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND)
		runCurrent()

		assertEquals(false, h.step(TrackingChecklistStepId.STEAMVR_HANDS_ENABLED).enabled)
	}

	@Test
	fun `STEAMVR_HANDS_ENABLED flags hands sent to the driver while a controller is held`() = runTest {
		val h = Harness(this)
		h.setAutomatic(false)
		h.connectDriver()
		h.addTracker(BodyPart.LEFT_HAND, origin = DeviceOrigin.UDP)
		h.addTracker(BodyPart.LEFT_HAND, origin = DeviceOrigin.DRIVER)
		h.routeToDriver(BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.STEAMVR_HANDS_ENABLED).enabled)
		assertEquals(false, h.step(TrackingChecklistStepId.STEAMVR_HANDS_ENABLED).valid)
	}

	@Test
	fun `STEAMVR_HANDS_ENABLED flags hands sent to the driver with no hand tracker worn`() = runTest {
		val h = Harness(this)
		h.setAutomatic(false)
		h.connectDriver()
		h.routeToDriver(BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND)
		runCurrent()

		// The hand bone is computed from the arm chain, so this is sent regardless. Turning
		// it on by accident is the usual way to end up here.
		assertEquals(false, h.step(TrackingChecklistStepId.STEAMVR_HANDS_ENABLED).valid)
	}

	@Test
	fun `STEAMVR_HANDS_ENABLED accepts hand trackers when no controller is held`() = runTest {
		val h = Harness(this)
		h.setAutomatic(false)
		h.connectDriver()
		h.addTracker(BodyPart.LEFT_HAND, origin = DeviceOrigin.UDP)
		h.routeToDriver(BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.STEAMVR_HANDS_ENABLED).valid)
	}

	@Test
	fun `STEAMVR_HANDS_ENABLED ignores hands that are not routed to the driver`() = runTest {
		val h = Harness(this)
		h.setAutomatic(false)
		h.connectDriver()
		h.addTracker(BodyPart.LEFT_HAND, origin = DeviceOrigin.UDP)
		h.addTracker(BodyPart.LEFT_HAND, origin = DeviceOrigin.DRIVER)
		runCurrent()

		assertEquals(true, h.step(TrackingChecklistStepId.STEAMVR_HANDS_ENABLED).valid)
	}

	@Test
	fun `STEAMVR_HANDS_ENABLED is disabled in automatic mode`() = runTest {
		val h = Harness(this)
		h.setAutomatic(true)
		h.connectDriver()
		h.addTracker(BodyPart.LEFT_HAND, origin = DeviceOrigin.UDP)
		h.addTracker(BodyPart.LEFT_HAND, origin = DeviceOrigin.DRIVER)
		h.routeToDriver(BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND)
		runCurrent()

		// Automatic owns the routes, so there is nothing the user could undo.
		assertEquals(false, h.step(TrackingChecklistStepId.STEAMVR_HANDS_ENABLED).enabled)
	}
}
