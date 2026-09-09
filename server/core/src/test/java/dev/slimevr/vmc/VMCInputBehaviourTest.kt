package dev.slimevr.vmc

import dev.slimevr.AppContextProvider
import dev.slimevr.TestAppContext
import dev.slimevr.VRServer
import dev.slimevr.buildTestAppConfig
import dev.slimevr.buildTestSkeleton
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.config.AppConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.quaternionApproxEqual
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.vectorAssertEquals
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.rpc.VMCOSCVrmState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class Harness(val server: VRServer, val appContext: AppContextProvider, val receiver: VMCManager) {
	private val registry = VmcTrackerRegistry(appContext, receiver)
	private val runtime = VMCInputBehaviour.InputRuntime()
	private val behaviour = VMCInputBehaviour(appContext, appContext.config.settings)

	fun handle(bundle: OscBundle, portIn: Int = 39540) = behaviour.handleBundle(bundle, runtime, registry, receiver, portIn)

	fun trackers() = server.context.state.value.trackers.values
}

private fun buildHarness(scope: CoroutineScope): Harness {
	val server = buildTestVrServerStub(scope)
	val config = buildTestAppConfig(scope)
	val skeleton = buildTestSkeleton(scope)
	val appContext = object : TestAppContext() {
		override val server: VRServer = server
		override val config: AppConfig = config
		override val skeleton: Skeleton = skeleton
	}
	return Harness(server, appContext, VMCManager.create(scope))
}

private fun transformArgs(name: String, pos: Vector3, rot: Quaternion): List<OscArg> = listOf(
	OscArg.String(name),
	OscArg.Float(pos.x),
	OscArg.Float(pos.y),
	OscArg.Float(-pos.z),
	OscArg.Float(rot.x),
	OscArg.Float(rot.y),
	OscArg.Float(-rot.z),
	OscArg.Float(-rot.w),
)

private fun bundleOf(vararg messages: OscMessage) = OscBundle(1L, messages.map { OscContent.Message(it) })

private fun boneBundle(unityName: String, pos: Vector3, rot: Quaternion) =
	bundleOf(OscMessage("/VMC/Ext/Bone/Pos", transformArgs(unityName, pos, rot)))

// Quaternions q and -q represent the same rotation, so either sign is an acceptable match.
private fun assertApprox(expected: Quaternion, actual: Quaternion) {
	val negated = Quaternion(-expected.w, -expected.x, -expected.y, -expected.z)
	assertTrue(quaternionApproxEqual(expected, actual) || quaternionApproxEqual(negated, actual), "expected=$expected actual=$actual")
}

class VMCInputBehaviourTest {

	@Test
	fun `creates a bone tracker with the received rotation and position`() = runTest {
		val harness = buildHarness(backgroundScope)
		harness.handle(boneBundle("Hips", Vector3(0f, 1f, 0f), Quaternion.rotationAroundYAxis(0.3f)))

		val tracker = harness.trackers().single()
		assertEquals(BodyPart.HIP, tracker.context.state.value.bodyPart)
		assertEquals(DeviceOrigin.VMC, tracker.context.state.value.origin)
		vectorAssertEquals(Vector3(0f, 1f, 0f), assertNotNull(tracker.context.state.value.position))
		assertApprox(Quaternion.rotationAroundYAxis(0.3f), tracker.context.state.value.rawRotation)
	}

	@Test
	fun `reuses the same tracker for the same bone across bundles`() = runTest {
		val harness = buildHarness(backgroundScope)
		harness.handle(boneBundle("Hips", Vector3.ZERO, Quaternion.IDENTITY))
		val first = harness.trackers().single()

		harness.handle(boneBundle("Hips", Vector3(0f, 2f, 0f), Quaternion.IDENTITY))

		assertEquals(1, harness.trackers().size)
		val second = harness.trackers().single()
		assertEquals(first.context.state.value.id, second.context.state.value.id)
		vectorAssertEquals(Vector3(0f, 2f, 0f), assertNotNull(second.context.state.value.position))
	}

	@Test
	fun `creates an unassigned pose tracker for a Tra Pos message`() = runTest {
		val harness = buildHarness(backgroundScope)
		harness.handle(bundleOf(OscMessage("/VMC/Ext/Tra/Pos", transformArgs("serial-1", Vector3(0.5f, 0.5f, 0.5f), Quaternion.IDENTITY))))

		val tracker = harness.trackers().single()
		assertNull(tracker.context.state.value.bodyPart)
		assertEquals(DeviceOrigin.VMC, tracker.context.state.value.origin)
		vectorAssertEquals(Vector3(0.5f, 0.5f, 0.5f), assertNotNull(tracker.context.state.value.position))
	}

	@Test
	fun `scales bone positions by skeletonHeight over the configured VRM's height`() = runTest {
		val harness = buildHarness(backgroundScope)
		val skeletonHeight = harness.appContext.skeleton.context.state.value.skeletonHeight
		harness.receiver.context.dispatch(
			VMCActions.SetVrm(
				state = VMCOSCVrmState.LOADED,
				vrm = VrmGeometry(bindOffsets = bodyPartMap(), hipLocalPosition = Vector3.ZERO, vrmHeight = skeletonHeight / 2f),
			),
		)

		harness.handle(boneBundle("Hips", Vector3(0f, 1f, 0f), Quaternion.IDENTITY))

		// scale = skeletonHeight / vrmHeight = 2, so the received y = 1 position doubles.
		val tracker = harness.trackers().single()
		vectorAssertEquals(Vector3(0f, 2f, 0f), assertNotNull(tracker.context.state.value.position))
	}
}
