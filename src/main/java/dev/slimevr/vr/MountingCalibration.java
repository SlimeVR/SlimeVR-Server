package dev.slimevr.vr;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import dev.slimevr.VRServer;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.ReferenceAdjustedTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerConfig;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

public class MountingCalibration {

	private final VRServer server;
	private FastList<Quaternion> trackerOrientationIdle = new FastList<>();
	private FastList<Tracker> allTrackers = new FastList<>();
	private int trackerNumber;
	private static float TENTH_OF_THIRD = 0.03333f;

	public MountingCalibration(VRServer server){
		this.server = server;
	}
	public void SetIMUMountingRotation(float rad, IMUTracker imu, Tracker t){ // Sets the mounting rotation (rad) of a given imu tracker and saves it.
		imu.setMountingRotation(rad);
		TrackerConfig tc = server.getTrackerConfig(t);
		imu.saveConfig(tc);
		server.saveConfig();
	}
	public void GetIdle(){ // Gets the orientation of the first pose for each tracker one by one.
		allTrackers = (FastList<Tracker>) server.getAllTrackers();
		trackerOrientationIdle = new FastList<>();
		IMUTracker imu;
		for (Tracker t : allTrackers) {
			Tracker realTracker = t;
			if(t instanceof ReferenceAdjustedTracker)
			realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
		 	if(realTracker instanceof IMUTracker){
				imu = (IMUTracker)realTracker;
				trackerOrientationIdle.add(imu.rotQuaternion.clone());
			}
		}
	}
	public void CalibrateTrackers(){ // Called when the CALIBRATE button at top is pressed and calibrates the trackers one by one.
		trackerNumber = 0;
		allTrackers = (FastList<Tracker>) server.getAllTrackers();
		IMUTracker imu;
		for (Tracker t : allTrackers) {
			Tracker realTracker = t;
			if(t instanceof ReferenceAdjustedTracker)
			realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
			if(realTracker instanceof IMUTracker){
				imu = (IMUTracker)realTracker;
				if(trackerOrientationIdle.size() > trackerNumber){
					if(imu.bodyPosition != null){
						switch(imu.bodyPosition){
							// Trackers that go back
							case CHEST:
							case WAIST:
							case HIP:
							case LEFT_ANKLE:
							case RIGHT_ANKLE:
								SetIMUMountingRotation(yawCorrection(trackerOrientationIdle.get(trackerNumber), imu.rotQuaternion.clone(), true, false), imu, t);
								break;
							// Trackers that don't move
							case LEFT_FOOT:
							case RIGHT_FOOT:
							case LEFT_FOREARM:
							case RIGHT_FOREARM:
							case LEFT_UPPER_ARM:
							case RIGHT_UPPER_ARM:
								SetIMUMountingRotation(yawCorrection(trackerOrientationIdle.get(trackerNumber), imu.rotQuaternion.clone(), false, true), imu, t);
								break;
							// Remaining trackers (that go forward)
							default:
								SetIMUMountingRotation(yawCorrection(trackerOrientationIdle.get(trackerNumber), imu.rotQuaternion.clone(), false, false), imu, t);
								break;
						}
					}
					else{
						SetIMUMountingRotation(yawCorrection(trackerOrientationIdle.get(trackerNumber), imu.rotQuaternion.clone(), false, false), imu, t);
					}
				}
				else{
					SetIMUMountingRotation(imu.getMountingRotation(), imu, t);
				}
				trackerNumber++;
			}
		}
		LogManager.log.info("[Mounting Calibration] Calibrated mounting of all " + trackerNumber + " trackers");
	}
	public float yawCorrection(Quaternion idle, Quaternion squat, boolean backwards, boolean endOnly){ // Calculated yaw offset for the mounting orientation

		if(!endOnly){ // Rotates squat Quaternion to 90 degrees.
			Quaternion rot = squat.mult(idle.inverse());
			float verticalRot = (verticalOrientation(rot));
			float verticalSquat = (verticalOrientation(squat));
			squat = squat.slerp(idle, squat, (TENTH_OF_THIRD + verticalRot - verticalSquat) / verticalRot);
		}

		// Gets roll of the quaternion. This is the yaw offset needed.
		float correctionYaw = squat.getRoll();

		// Spine and ankles go backwards during squat. Needs to be inversed 180 degrees.
		if(backwards) correctionYaw -= FastMath.PI;

		// Plays around with euler angles being euler angles.
		if(squat.getPitch() > 0){
			correctionYaw *= -1f;
		}
		else{
			correctionYaw += FastMath.PI;
			if(correctionYaw > FastMath.PI) correctionYaw -= FastMath.TWO_PI;
		}

		return correctionYaw;
	}
	private static float verticalOrientation(Quaternion quat) // Returns from 0 to 0.06666. 0.03333 = 90 degrees.
	{
		// Computes up to 90 degrees
		float x = FastMath.abs(quat.getX());
		float z = FastMath.abs(quat.getZ());
		float vertical = ((FastMath.sqrt((0.51f) * (x * x + z * z) + 0.98f * x * z) - 0.7f * (x + z)) / ((0.3f) * FastMath.sqrt(x * x + z * z))) * (x + z);

		// If below 90 degrees, return
		if(vertical < TENTH_OF_THIRD) return vertical;
		
		// Computes up to 180 degrees
		float y = FastMath.abs(quat.getY());
		float w = FastMath.abs(quat.getW());
		return TENTH_OF_THIRD - ((((FastMath.sqrt((0.51f) * (y * y + w * w) + 0.98f * y * w) - 0.7f * (y + w)) / ((0.3f) * FastMath.sqrt(y * y + w * w))) * (y + w)) - TENTH_OF_THIRD);
	}
}
