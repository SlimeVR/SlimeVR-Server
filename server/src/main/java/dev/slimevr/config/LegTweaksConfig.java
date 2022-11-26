package dev.slimevr.config;

public class LegTweaksConfig {

	// non-scaled values
	private float correctionStrength = 0.3f;
	private float paramScalarMax = 3.2f;
	private float paramScalarMin = 0.25f;
	private float dynamicDisplacementCutoff = 1.0f;
	private float maxDynamicDisplacement = 0.06f;

	// scaled values
	private float skatingVelocityThreshold = 3.0f;
	private float skatingVelocityThresholdScale = 1.0f;
	private float skatingAccelerationThreshold = 0.7f;
	private float skatingAccelerationThresholdScale = 1.0f;

	public float getCorrectionStrength() {
		return correctionStrength;
	}

	public void setCorrectionStrength(float correctionStrength) {
		this.correctionStrength = correctionStrength;
	}

	public float getParamScalarMax() {
		return paramScalarMax;
	}

	public void setParamScalarMax(float paramScalerMax) {
		this.paramScalarMax = paramScalerMax;
	}

	public float getParamScalarMin() {
		return paramScalarMin;
	}

	public void setParamScalarMin(float paramScalerMin) {
		this.paramScalarMin = paramScalerMin;
	}

	public float getDynamicDisplacementCutoff() {
		return dynamicDisplacementCutoff;
	}

	public void setDynamicDisplacementCutoff(float dynamicDisplacementCutoff) {
		this.dynamicDisplacementCutoff = dynamicDisplacementCutoff;
	}

	public float getMaxDynamicDisplacement() {
		return maxDynamicDisplacement;
	}

	public void setMaxDynamicDisplacement(float maxDynamicDisplacement) {
		this.maxDynamicDisplacement = maxDynamicDisplacement;
	}

	public float getSkatingVelocityThreshold() {
		return skatingVelocityThreshold;
	}

	public void setSkatingVelocityThreshold(float skatingVelocityThreshold) {
		this.skatingVelocityThreshold = skatingVelocityThreshold;
	}

	public float getSkatingVelocityThresholdScale() {
		return skatingVelocityThresholdScale;
	}

	public void setSkatingVelocityThresholdScale(float skatingVelocityThresholdScale) {
		this.skatingVelocityThresholdScale = skatingVelocityThresholdScale;
	}

	public float getSkatingAccelerationThreshold() {
		return skatingAccelerationThreshold;
	}

	public void setSkatingAccelerationThreshold(float skatingAccelerationThreshold) {
		this.skatingAccelerationThreshold = skatingAccelerationThreshold;
	}

	public float getSkatingAccelerationThresholdScale() {
		return skatingAccelerationThresholdScale;
	}

	public void setSkatingAccelerationThresholdScale(float skatingAccelerationThresholdScale) {
		this.skatingAccelerationThresholdScale = skatingAccelerationThresholdScale;
	}
}
