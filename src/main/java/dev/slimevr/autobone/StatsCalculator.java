package dev.slimevr.autobone;

import com.jme3.math.FastMath;


/// This is a stat calculator based on Welford's online algorithm
/// https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Welford's_online_algorithm
public class StatsCalculator {

	private int count = 0;
	private float mean = 0f;
	private float M2 = 0f;

	public void reset() {
		count = 0;
		mean = 0f;
		M2 = 0f;
	}

	public void addValue(float newValue) {
		count += 1;
		float delta = newValue - mean;
		mean += delta / count;
		float delta2 = newValue - mean;
		M2 += delta * delta2;
	}

	public float getMean() {
		return mean;
	}

	public float getVariance() {
		if (count < 1) {
			return Float.NaN;
		}

		return M2 / count;
	}

	public float getSampleVariance() {
		if (count < 2) {
			return Float.NaN;
		}

		return M2 / (count - 1);
	}

	public float getStandardDeviation() {
		return FastMath.sqrt(getVariance());
	}
}
