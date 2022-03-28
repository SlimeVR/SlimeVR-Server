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
							case CHEST:
							case WAIST:
							case HIP:
							case LEFT_ANKLE:
							case RIGHT_ANKLE:
								SetIMUMountingRotation(yawCorrection(trackerOrientationIdle.get(trackerNumber), imu.rotQuaternion.clone(), true, false), imu, t);
								break;
							case LEFT_FOOT:
							case RIGHT_FOOT:
							case LEFT_FOREARM:
							case RIGHT_FOREARM:
							case LEFT_UPPER_ARM:
							case RIGHT_UPPER_ARM:
								SetIMUMountingRotation(yawCorrection(trackerOrientationIdle.get(trackerNumber), imu.rotQuaternion.clone(), false, true), imu, t);
								break;
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
		
		if(!endOnly){
			Quaternion rot = squat.mult(idle.inverse());
			squat = squat.slerp(idle, squat, 1f/(FastMath.abs(rot.getX()) + FastMath.abs(rot.getZ()) + FastMath.abs(idle.getX()) + FastMath.abs(idle.getZ())));
		}
		float correctionYaw = squat.getRoll();

		if(backwards) correctionYaw -= FastMath.PI;

		if(squat.getPitch() > 0){
			correctionYaw *= -1f;
		}
		else{
			correctionYaw += FastMath.PI;
			if(correctionYaw > FastMath.PI) correctionYaw -= FastMath.TWO_PI;
		}

		return correctionYaw;
	}
}
