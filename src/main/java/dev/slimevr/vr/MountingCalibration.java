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
		LogManager.log.info("[Mounting Calibration] (" + t.getDescriptiveName() + ") mounting orientation orientation set: " + Math.round(rad/Math.PI*180));
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
					SetIMUMountingRotation(yawCorrection(trackerOrientationIdle.get(trackerNumber), imu.rotQuaternion.clone()), imu, t);
				}
				else{
					SetIMUMountingRotation(imu.getMountingRotation(), imu, t);
				}
				trackerNumber++;
			}
		}
		LogManager.log.info("[Mounting Calibration] Calibrated all " + trackerNumber + " trackers");
	}
	public float yawCorrection(Quaternion idle, Quaternion squat){ // Calculated yaw offset for the mounting orientation
		Quaternion rot = squat.mult(idle.inverse());
		Quaternion nightyDegredQuaternion = new Quaternion().slerp(idle, squat, 1f/(FastMath.abs(rot.getX()) + FastMath.abs(rot.getZ())));
		float correctionYaw = nightyDegredQuaternion.getRoll() - FastMath.PI;
		if(correctionYaw < -FastMath.PI) correctionYaw += FastMath.PI * 2f;
		return correctionYaw;
	}
}
