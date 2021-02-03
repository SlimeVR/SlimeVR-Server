package io.eiren.vr.trackers;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.BufferedTimer;

public class IMUTracker implements Tracker, CalibratingTracker, TrackerWithTPS {
	
	public final Vector3f gyroVector = new Vector3f();
	public final Vector3f accelVector = new Vector3f();
	public final Vector3f magVector = new Vector3f();
	public final Quaternion rotQuaternion = new Quaternion();
	protected TrackerStatus status = TrackerStatus.OK;
	
	protected final String name;
	protected final TrackersUDPServer server;
	protected float confidence = 0;
	
	protected BufferedTimer timer = new BufferedTimer(1f);
	public CalibrationData newCalibrationData;
	
	public IMUTracker(String name, TrackersUDPServer server) {
		this.name = name;
		this.server = server;
	}
	
	@Override
	public void saveConfig(TrackerConfig config) {
	}
	
	@Override
	public void loadConfig(TrackerConfig config) {
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean getPosition(Vector3f store) {
		store.set(0, 0, 0);
		return false;
	}
	
	@Override
	public boolean getRotation(Quaternion store) {
		store.set(rotQuaternion);
		return true;
	}

	@Override
	public TrackerStatus getStatus() {
		return status;
	}
	
	public void setStatus(TrackerStatus status) {
		this.status = status;
	}
	
	@Override
	public void startCalibration(Consumer<String> calibrationDataConsumer) {
		server.sendCalibrationCommand(this, calibrationDataConsumer);
	}
	
	@Override
	public float getTPS() {
		return timer.getAverageFPS();
	}
	
	@Override
	public void dataTick() {
		timer.update();
	}
	
	@Override
	public void requestCalibrationData(Consumer<String> calibrationDataConsumer) {
		server.requestCalibrationData(this, calibrationDataConsumer);
	}
	
	@Override
	public void uploadNewClibrationData() {
		server.uploadNewCalibrationData(this, newCalibrationData);
	}
	
	@Override
	public float getConfidenceLevel() {
		return confidence;
	}
	
	public void setConfidence(float newConf) {
		this.confidence = newConf;
	}
	
	public static class CalibrationData {

	    //acel offsets and correction matrix
		float[] A_B = new float[3];
	    float[][] A_Ainv = new float[3][3];
	    // mag offsets and correction matrix
	    float[] M_B = new float[3];
	    float[][] M_Ainv = new float[3][3];
	    //raw offsets, determined for gyro at rest
	    float[] G_off = new float[3];
	    
	    public CalibrationData(double[] accelBasis, double[] accelAInv, double[] magBasis, double[] magAInv, double[] gyroOffset) {
	    	A_B[0] = (float) accelBasis[0];
	    	A_B[1] = (float) accelBasis[1];
	    	A_B[2] = (float) accelBasis[2];
	    	
	    	A_Ainv[0][0] = (float) accelAInv[0];
	    	A_Ainv[0][1] = (float) accelAInv[1];
	    	A_Ainv[0][2] = (float) accelAInv[2];
	    	A_Ainv[1][0] = (float) accelAInv[3];
	    	A_Ainv[1][1] = (float) accelAInv[4];
	    	A_Ainv[1][2] = (float) accelAInv[5];
	    	A_Ainv[2][0] = (float) accelAInv[6];
	    	A_Ainv[2][1] = (float) accelAInv[7];
	    	A_Ainv[2][2] = (float) accelAInv[8];
	    	
	    	M_B[0] = (float) magBasis[0];
	    	M_B[1] = (float) magBasis[1];
	    	M_B[2] = (float) magBasis[2];
	    	
	    	M_Ainv[0][0] = (float) magAInv[0];
	    	M_Ainv[0][1] = (float) magAInv[1];
	    	M_Ainv[0][2] = (float) magAInv[2];
	    	M_Ainv[1][0] = (float) magAInv[3];
	    	M_Ainv[1][1] = (float) magAInv[4];
	    	M_Ainv[1][2] = (float) magAInv[5];
	    	M_Ainv[2][0] = (float) magAInv[6];
	    	M_Ainv[2][1] = (float) magAInv[7];
	    	M_Ainv[2][2] = (float) magAInv[8];
	    	
	    	G_off[0] = (float) gyroOffset[0];
	    	G_off[1] = (float) gyroOffset[1];
	    	G_off[2] = (float) gyroOffset[2];
	    }
		
		public CalibrationData(ByteBuffer buffer) {
			buffer.getFloat(); // TODO : WHY???
			// Data is read in reverse, because it was reversed when sending
			G_off[2] = buffer.getFloat();
			G_off[1] = buffer.getFloat();
			G_off[0] = buffer.getFloat();
			
			M_Ainv[2][2] = buffer.getFloat();
			M_Ainv[2][1] = buffer.getFloat();
			M_Ainv[2][0] = buffer.getFloat();
			M_Ainv[1][2] = buffer.getFloat();
			M_Ainv[1][1] = buffer.getFloat();
			M_Ainv[1][0] = buffer.getFloat();
			M_Ainv[0][2] = buffer.getFloat();
			M_Ainv[0][1] = buffer.getFloat();
			M_Ainv[0][0] = buffer.getFloat();
			
			M_B[2] = buffer.getFloat();
			M_B[1] = buffer.getFloat();
			M_B[0] = buffer.getFloat();
			
			A_Ainv[2][2] = buffer.getFloat();
			A_Ainv[2][1] = buffer.getFloat();
			A_Ainv[2][0] = buffer.getFloat();
			A_Ainv[1][2] = buffer.getFloat();
			A_Ainv[1][1] = buffer.getFloat();
			A_Ainv[1][0] = buffer.getFloat();
			A_Ainv[0][2] = buffer.getFloat();
			A_Ainv[0][1] = buffer.getFloat();
			A_Ainv[0][0] = buffer.getFloat();
			
			A_B[2] = buffer.getFloat();
			A_B[1] = buffer.getFloat();
			A_B[0] = buffer.getFloat();
		}
		
		public String toTextMatrix() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("{%8.2f,%8.2f,%8.2f},\n", A_B[0], A_B[1], A_B[2]));
			sb.append(String.format("{{%9.5f,%9.5f,%9.5f},\n", A_Ainv[0][0], A_Ainv[0][1], A_Ainv[0][2]));
			sb.append(String.format(" {%9.5f,%9.5f,%9.5f},\n", A_Ainv[1][0], A_Ainv[1][1], A_Ainv[1][2]));
			sb.append(String.format(" {%9.5f,%9.5f,%9.5f}},\n", A_Ainv[2][0], A_Ainv[2][1], A_Ainv[2][2]));
			sb.append(String.format("{%8.2f,%8.2f,%8.2f},\n", M_B[0], M_B[1], M_B[2]));
			sb.append(String.format("{{%9.5f,%9.5f,%9.5f},\n", M_Ainv[0][0], M_Ainv[0][1], M_Ainv[0][2]));
			sb.append(String.format(" {%9.5f,%9.5f,%9.5f},\n", M_Ainv[1][0], M_Ainv[1][1], M_Ainv[1][2]));
			sb.append(String.format(" {%9.5f,%9.5f,%9.5f}},\n", M_Ainv[2][0], M_Ainv[2][1], M_Ainv[2][2]));
			sb.append(String.format("{%8.2f, %8.2f, %8.2f}};\n", G_off[0], G_off[1], G_off[2]));
			
			return sb.toString();
		}
	}
}
