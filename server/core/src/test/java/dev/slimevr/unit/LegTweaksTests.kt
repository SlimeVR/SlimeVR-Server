package dev.slimevr.unit

import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerRole
import dev.slimevr.tracking.trackers.TrackerStatus
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.QuaternionTest
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.Test
import kotlin.test.assertFails

class LegTweaksTests {

	@Test
	fun toeSnap() {
		val hmd = Tracker(
			null,
			0,
			"test:headset",
			"Headset",
			TrackerPosition.HEAD,
			hasPosition = true,
			hasRotation = true,
			isComputed = true,
			imuType = null,
			needsReset = false,
			needsMounting = false,
			isHmd = true,
			trackRotDirection = false,
		)
		hmd.status = TrackerStatus.OK

		val hpm = HumanPoseManager(listOf(hmd))
		val height = hpm.userHeightFromConfig
		val lFoot = hpm.getComputedTracker(TrackerRole.LEFT_FOOT)

		assert(height > 0f) {
			"Skeleton was not populated with default proportions (height = $height)"
		}
		val lFootLen = hpm.skeleton.leftFootBone.length
		assert(lFootLen > 0f) {
			"Skeleton's left foot has no length (length = $lFootLen)"
		}

		// Skeleton setup
		hpm.skeleton.hasKneeTrackers = true
		hpm.setToggle(SkeletonConfigToggles.TOE_SNAP, true)

		// Set the floor height
		hmd.position = Vector3(0f, height, 0f)
		hpm.update()

		// Validate initial state
		QuaternionTest.assertEquals(Quaternion.IDENTITY, lFoot.getRotation())

		// Ensure `leftToeTouched` and `rightToeTouched` are true
		hmd.position = Vector3(0f, height - 0.02f, 0f)
		hpm.update()

		// Lift skeleton within toe snap range
		hmd.position = Vector3(0f, height + 0.02f, 0f)
		hpm.update()

		// This should fail now that the toes are snapped
		assertFails {
			QuaternionTest.assertEquals(Quaternion.IDENTITY, lFoot.getRotation())
		}
	}
}
