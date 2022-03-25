package dev.slimevr.vr;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import dev.slimevr.VRServer;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.ReferenceAdjustedTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerConfig;

import java.lang.System.Logger;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


public class MountingCalibration {
	private final VRServer server;
	private List<Quaternion> trackerOrientationIdle = new FastList<>();
	private List<Tracker> allTrackers = new FastList<>();
	private int trackerNumber;

	public MountingCalibration(VRServer server){
		this.server = server;
	}
	public void SetIMUMountingRotation(float rad, IMUTracker imu, Tracker t){ //sets the mounting rotation (rad) of a given imu tracker and saves it.
		LogManager.log.info("[Mounting Calibration] (" + t.getDescriptiveName() + ") mounting orientation orientation set: " + Math.round(rad/Math.PI*180));
		imu.setMountingRotation(rad);
		TrackerConfig tc = server.getTrackerConfig(t);
		imu.saveConfig(tc);
		server.saveConfig();
	}
	public void GetIdle(){ //gets the orientation of the first pose for each tracker one by one.
		allTrackers = server.getAllTrackers();
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
	public void CalibrateTrackers(){ //called when the CALIBRATE button is pressed and calibrates the trackers one by one.
		trackerNumber = 0;
		allTrackers = server.getAllTrackers();
		IMUTracker imu;
		for (Tracker t : allTrackers) {
			Tracker realTracker = t;
			if(t instanceof ReferenceAdjustedTracker)
			realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
			if(realTracker instanceof IMUTracker){
				imu = (IMUTracker)realTracker;
				// TODO if tracker got added in the 5 seconds timer
				SetIMUMountingRotation(yawCorrection(trackerOrientationIdle.get(trackerNumber), imu.rotQuaternion.clone()), imu, t);
				trackerNumber++;
			}
		}
		LogManager.log.info("[Mounting Calibration] Calibrated all " + trackerNumber + " trackers");
	}
	public float yawCorrection(Quaternion idle, Quaternion squat){ // Calculated yaw offset for the mounting orientation
		Quaternion rot = idle.mult(squat.inverse());
		Quaternion nightyDegredQuaternion = new Quaternion().slerp(idle, squat, 0.75f / FastMath.abs(rot.getZ()));
		float correctionYaw = nightyDegredQuaternion.getRoll() - FastMath.PI;
		if(correctionYaw < -FastMath.PI) correctionYaw += FastMath.PI * 2f;
		return correctionYaw;
	}
}
