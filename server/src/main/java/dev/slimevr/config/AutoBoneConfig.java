package dev.slimevr.config;

public class AutoBoneConfig {

	private int cursorIncrement = 2;
	private int minDataDistance = 1;
	private int maxDataDistance = 1;

	private int numEpochs = 100;

	private int printEveryNumEpochs = 25;

	private float initialAdjustRate = 10f;
	private float adjustRateMultiplier = 0.995f;

	private float slideErrorFactor = 0.0f;
	private float offsetSlideErrorFactor = 2.0f;
	private float footHeightOffsetErrorFactor = 0.0f;
	private float bodyProportionErrorFactor = 0.825f;
	private float heightErrorFactor = 0.0f;
	private float positionErrorFactor = 0.0f;
	private float positionOffsetErrorFactor = 0.0f;

	private boolean calcInitError = false;
	private float targetHeight = -1;

	private boolean randomizeFrameOrder = true;
	private boolean scaleEachStep = true;

	private int sampleCount = 1000;
	private long sampleRateMs = 20;

	private boolean saveRecordings = false;

	public AutoBoneConfig() {
	}

	public int getCursorIncrement() {
		return cursorIncrement;
	}

	public void setCursorIncrement(int cursorIncrement) {
		this.cursorIncrement = cursorIncrement;
	}

	public int getMinDataDistance() {
		return minDataDistance;
	}

	public void setMinDataDistance(int minDataDistance) {
		this.minDataDistance = minDataDistance;
	}

	public int getMaxDataDistance() {
		return maxDataDistance;
	}

	public void setMaxDataDistance(int maxDataDistance) {
		this.maxDataDistance = maxDataDistance;
	}

	public int getNumEpochs() {
		return numEpochs;
	}

	public void setNumEpochs(int numEpochs) {
		this.numEpochs = numEpochs;
	}

	public int getPrintEveryNumEpochs() {
		return printEveryNumEpochs;
	}

	public void setPrintEveryNumEpochs(int printEveryNumEpochs) {
		this.printEveryNumEpochs = printEveryNumEpochs;
	}

	public float getInitialAdjustRate() {
		return initialAdjustRate;
	}

	public void setInitialAdjustRate(float initialAdjustRate) {
		this.initialAdjustRate = initialAdjustRate;
	}

	public float getAdjustRateMultiplier() {
		return adjustRateMultiplier;
	}

	public void setAdjustRateMultiplier(float adjustRateMultiplier) {
		this.adjustRateMultiplier = adjustRateMultiplier;
	}

	public float getSlideErrorFactor() {
		return slideErrorFactor;
	}

	public void setSlideErrorFactor(float slideErrorFactor) {
		this.slideErrorFactor = slideErrorFactor;
	}

	public float getOffsetSlideErrorFactor() {
		return offsetSlideErrorFactor;
	}

	public void setOffsetSlideErrorFactor(float offsetSlideErrorFactor) {
		this.offsetSlideErrorFactor = offsetSlideErrorFactor;
	}

	public float getFootHeightOffsetErrorFactor() {
		return footHeightOffsetErrorFactor;
	}

	public void setFootHeightOffsetErrorFactor(float footHeightOffsetErrorFactor) {
		this.footHeightOffsetErrorFactor = footHeightOffsetErrorFactor;
	}

	public float getBodyProportionErrorFactor() {
		return bodyProportionErrorFactor;
	}

	public void setBodyProportionErrorFactor(float bodyProportionErrorFactor) {
		this.bodyProportionErrorFactor = bodyProportionErrorFactor;
	}

	public float getHeightErrorFactor() {
		return heightErrorFactor;
	}

	public void setHeightErrorFactor(float heightErrorFactor) {
		this.heightErrorFactor = heightErrorFactor;
	}

	public float getPositionErrorFactor() {
		return positionErrorFactor;
	}

	public void setPositionErrorFactor(float positionErrorFactor) {
		this.positionErrorFactor = positionErrorFactor;
	}

	public float getPositionOffsetErrorFactor() {
		return positionOffsetErrorFactor;
	}

	public void setPositionOffsetErrorFactor(float positionOffsetErrorFactor) {
		this.positionOffsetErrorFactor = positionOffsetErrorFactor;
	}

	public boolean shouldCalcInitError() {
		return calcInitError;
	}

	public void setCalcInitError(boolean calcInitError) {
		this.calcInitError = calcInitError;
	}

	public float getTargetHeight() {
		return targetHeight;
	}

	public void setTargetHeight(float targetHeight) {
		this.targetHeight = targetHeight;
	}

	public boolean shouldRandomizeFrameOrder() {
		return randomizeFrameOrder;
	}

	public void setRandomizeFrameOrder(boolean randomizeFrameOrder) {
		this.randomizeFrameOrder = randomizeFrameOrder;
	}

	public boolean shouldScaleEachStep() {
		return scaleEachStep;
	}

	public void setScaleEachStep(boolean scaleEachStep) {
		this.scaleEachStep = scaleEachStep;
	}

	public int getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(int sampleCount) {
		this.sampleCount = sampleCount;
	}

	public long getSampleRateMs() {
		return sampleRateMs;
	}

	public void setSampleRateMs(long sampleRateMs) {
		this.sampleRateMs = sampleRateMs;
	}

	public boolean shouldSaveRecordings() {
		return saveRecordings;
	}

	public void setSaveRecordings(boolean saveRecordings) {
		this.saveRecordings = saveRecordings;
	}
}
