package dev.slimevr.skeleton

import dev.slimevr.util.inFloatingSeconds
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.ComparableTimeMark

// Source: Plagenhoef et al., 1983 (Table 4)
// Modified for more segmentation
val BODY_PART_MASSES = mapOf(
	BodyPart.HEAD to 0.0827f,
	BodyPart.LEFT_UPPER_ARM to 0.0263f,
	BodyPart.RIGHT_UPPER_ARM to 0.0263f,
	BodyPart.LEFT_LOWER_ARM to 0.0224f,
	BodyPart.RIGHT_LOWER_ARM to 0.0224f,
	BodyPart.UPPER_CHEST to 0.0935f,
	BodyPart.LOWER_CHEST to 0.0935f,
	BodyPart.UPPER_WAIST to 0.0660f,
	BodyPart.LOWER_WAIST to 0.0660f,
	BodyPart.HIP to 0.1530f,
	BodyPart.LEFT_UPPER_LEG to 0.1122f,
	BodyPart.RIGHT_UPPER_LEG to 0.1122f,
	BodyPart.LEFT_LOWER_LEG to 0.0620f,
	BodyPart.RIGHT_LOWER_LEG to 0.0620f,
)

data class COMState(
	val time: ComparableTimeMark,
	val position: Vector3,
	val velocity: Vector3,
	val acceleration: Vector3,
)

fun centreOfMass(
	bones: ComputedSkeleton,
): Vector3 = BODY_PART_MASSES.entries.fold(Vector3.ZERO) { acc: Vector3, massEntry ->
	val bone = bones[massEntry.key] ?: return@fold acc
	val boneCentre = (bone.headPosition + bone.tailPosition) / 2f
	return@fold acc + (boneCentre * massEntry.value)
}

fun computeComState(time: ComparableTimeMark, last: COMState?, com: Vector3): COMState = if (last != null) {
	val deltaTime = (time - last.time).inFloatingSeconds
	val comVelocity = (com - last.position) / deltaTime
	val comAcceleration = (comVelocity - last.velocity) / deltaTime
	COMState(
		time,
		com,
		comVelocity,
		comAcceleration,
	)
} else {
	COMState(
		time,
		com,
		Vector3.ZERO,
		Vector3.ZERO,
	)
}

// TODO Gravity can probably be defined somewhere more globally or smth?
const val GRAVITY_MAGNITUDE = 9.81f
val GRAVITY_VECTOR = Vector3(0f, -GRAVITY_MAGNITUDE, 0f)

const val FLOOR_DISTANCE_CUTOFF = 0.065f
val PRESSURE_FALLBACK = 0.1f to 0.1f

// TODO This feels wrong to be hardcoded, is there a reason we're doing this?
val FORCE_VECTOR_TO_PRESSURE: Vector3 = Vector3(0.25f, 1f, 0.25f)

// In m/s^2
val FORCE_ERROR_TOLERANCE_SQR: Float = 4f * 4f

// Get the pressure prediction for the feet based of the centre of mass (assume mass is
//  1). This is assuming that the mass is 1 and the force of gravity is 9.8 m/s^2, this
//  allows for the force sum to map directly to the acceleration of the centre of mass
//  since F = ma, and if m is 1, then F = a.
fun predictFootPressure(
	leftFootPosition: Vector3,
	rightFootPosition: Vector3,
	centerOfMass: Vector3,
	centerOfMassAcceleration: Vector3,
	floorLevel: Float,
): Pair<Float, Float> {
	// From the COM to each foot
	val leftFootVector: Vector3 = (leftFootPosition - centerOfMass).unit()
	val rightFootVector: Vector3 = (rightFootPosition - centerOfMass).unit()

	// Ratio of gravity on each foot to support the COM
	val leftFootMagnitude: Float = GRAVITY_MAGNITUDE * leftFootVector.y
	val rightFootMagnitude: Float = GRAVITY_MAGNITUDE * rightFootVector.y

	// Get the force vector each foot could apply to the COM
	val leftFootForce: Vector3 = leftFootVector * (leftFootMagnitude / 2f)
	val rightFootForce: Vector3 = rightFootVector * (rightFootMagnitude / 2f)

	// Based off the acceleration of the COM, get the force each foot is likely applying
	//  (the expected force sum should be equal to centerOfMassAcceleration since the
	//  mass is 1)
	val (modifiedLeftFootForce, modifiedRightFootForce) = findForceVectors(
		leftFootForce,
		rightFootForce,
		centerOfMassAcceleration,
	)

	// See if the force vectors found a reasonable solution, if they did not, we assume
	//  there is another force acting on the COM, and fall back to a low pressure
	//  prediction.
	if (detectOutsideForces(
			modifiedLeftFootForce,
			modifiedRightFootForce,
			Vector3.ZERO,
		)
	) {
		// Assume user not standing
		return PRESSURE_FALLBACK
	}
	// Assume user standing

	// Set the pressure to the force on each foot times the force to pressure scalar
	var leftFootPressure = modifiedLeftFootForce.hadamard(FORCE_VECTOR_TO_PRESSURE).len()
	var rightFootPressure = modifiedRightFootForce.hadamard(FORCE_VECTOR_TO_PRESSURE).len()

	// TODO So we calculate all this, but then if the foot is within the cutoff from the
	//  floor, it just goes to some abstractly enormous value?? Why?
	// Distance from the ground is a factor in the pressure using the inverse of the
	//  distance to the ground scale the pressure
	val leftDistance: Float =
		if (leftFootPosition.y > (floorLevel + FLOOR_DISTANCE_CUTOFF)) {
			leftFootPosition.y - (floorLevel + FLOOR_DISTANCE_CUTOFF)
		} else {
			// Nearly zero
			0.001f
		}
	leftFootPressure *= 1f / leftDistance
	val rightDistance: Float =
		if (rightFootPosition.y > (floorLevel + FLOOR_DISTANCE_CUTOFF)) {
			rightFootPosition.y - (floorLevel + FLOOR_DISTANCE_CUTOFF)
		} else {
			// Nearly zero
			0.001f
		}
	rightFootPressure *= 1f / rightDistance

	// Normalize the pressure values
	val pressureSum = leftFootPressure + rightFootPressure
	leftFootPressure /= pressureSum
	rightFootPressure /= pressureSum
	return leftFootPressure to rightFootPressure
}

// Perform a gradient descent to scale the force vectors to best match the acceleration
//  of the COM
fun findForceVectors(
	leftFootForceInit: Vector3,
	rightFootForceInit: Vector3,
	centerOfMassAcceleration: Vector3,
): Pair<Vector3, Vector3> {
	var leftFootForce: Vector3 = leftFootForceInit
	var rightFootForce: Vector3 = rightFootForceInit
	val iterations = 100
	val stepSize = 0.01f
	// Set up the temporary variables
	var tempLeftFootForce1: Vector3
	var tempLeftFootForce2: Vector3
	var tempRightFootForce1: Vector3
	var tempRightFootForce2: Vector3
	var error: Vector3
	var error1: Vector3
	var error2: Vector3
	var error3: Vector3
	var error4: Vector3
	for (i in 0 until iterations) {
		tempLeftFootForce1 = leftFootForce
		tempLeftFootForce2 = leftFootForce
		tempRightFootForce1 = rightFootForce
		tempRightFootForce2 = rightFootForce

		// Get the error at the current position
		error = getForceVectorError(leftFootForce, rightFootForce, centerOfMassAcceleration)

		// Add and subtract the error to the force vectors
		tempLeftFootForce1 *= (1.0f + stepSize)
		tempLeftFootForce2 *= (1.0f - stepSize)
		tempRightFootForce1 *= (1.0f + stepSize)
		tempRightFootForce2 *= (1.0f - stepSize)

		// Get the error at the new position
		error1 = getForceVectorError(tempLeftFootForce1, rightFootForce, centerOfMassAcceleration)
		error2 = getForceVectorError(tempLeftFootForce2, rightFootForce, centerOfMassAcceleration)
		error3 = getForceVectorError(tempRightFootForce1, leftFootForce, centerOfMassAcceleration)
		error4 = getForceVectorError(tempRightFootForce2, leftFootForce, centerOfMassAcceleration)

		// Set the new force vectors
		if (error1.len() < error.len()) {
			leftFootForce = tempLeftFootForce1
		} else if (error2.len() < error.len()) {
			leftFootForce = tempLeftFootForce2
		}
		if (error3.len() < error.len()) {
			rightFootForce = tempRightFootForce1
		} else if (error4.len() < error.len()) {
			rightFootForce = tempRightFootForce2
		}
	}

	return leftFootForce to rightFootForce
}

// Detect any outside forces on the body such as a wall or a chair.
// Returns true if there is an outside force.
fun detectOutsideForces(
	f1: Vector3,
	f2: Vector3,
	centerOfMassAcceleration: Vector3,
): Boolean = getForceVectorError(f1, f2, centerOfMassAcceleration).lenSq() > FORCE_ERROR_TOLERANCE_SQR

// Simple error function for the force vector gradient descent
fun getForceVectorError(
	f1: Vector3,
	f2: Vector3,
	centerOfMassAcceleration: Vector3,
	// TODO Shouldn't this be `centerOfMassAcceleration + GRAVITY_VECTOR + f1 + f2`?
	//  Gravity is -Y, then feet should be accelerating the mass +Y, cancelling out.
): Vector3 = centerOfMassAcceleration - (GRAVITY_VECTOR + f1 + f2)
