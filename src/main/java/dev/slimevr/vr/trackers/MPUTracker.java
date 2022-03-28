package dev.slimevr.vr.trackers;

import java.nio.ByteBuffer;

import dev.slimevr.VRServer;
import dev.slimevr.vr.trackers.udp.TrackersUDPServer;

public class MPUTracker extends IMUTracker {
	
	public ConfigurationData newCalibrationData;
	
	public MPUTracker(int trackerId, String name, String descriptiveName, TrackersUDPServer server, VRServer vrserver) {
		super(trackerId, name, descriptiveName, server, vrserver);
	}
	
	public static class ConfigurationData {

	    //accel offsets and correction matrix
		float[] A_B = new float[3];
	    float[][] A_Ainv = new float[3][3];
	    // mag offsets and correction matrix
	    float[] M_B = new float[3];
	    float[][] M_Ainv = new float[3][3];
	    //raw offsets, determined for gyro at rest
	    float[] G_off = new float[3];
	    int deviceId = -1;
	    int deviceMode = -1;
	    
	    public ConfigurationData(double[] accelBasis, double[] accelAInv, double[] magBasis, double[] magAInv, double[] gyroOffset) {
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
		
		public ConfigurationData(ByteBuffer buffer) {
			deviceMode = buffer.getInt();
			deviceId = buffer.getInt();
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
