package dev.slimevr.hardware.magentometer;

import com.sun.jna.Library;
import com.sun.jna.Native;


public interface Magneto extends Library {

	Magneto INSTANCE = Native.load("MagnetoLib", Magneto.class);

	void calculate(double[] data, int nlines, double nxsrej, double hm, double[] B, double[] A_1);

	double calculateHnorm(double[] data, int nlines);
}
