package dev.slimevr.config;


public class AutoboneConfig {

	public int minDataDistance = 1;
	public int maxDataDistance = 1;
	public int numEpochs = 100;
	public float initialAdjustRate = 10f;
	public float adjustRateMultiplier = 0.995f;
	public float slideErrorFactor = 0.0f;
	public float offsetSlideErrorFactor = 1.0f;
	public float footHeightOffsetErrorFactor = 0.0f;
	public float bodyProportionErrorFactor = 0.2f;
	public float heightErrorFactor = 0.0f;
	public float positionErrorFactor = 0.0f;
	public float positionOffsetErrorFactor = 0.0f;
	public boolean calcInitError = false;
	public float targetHeight = -1;

	public int sampleCount = 100;

	public long sampleRateMs = 20;

	public boolean saveRecordings = false;

	private final ConfigManager configManager;

	public AutoboneConfig(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void loadConfig() {
		this.saveRecordings = this.configManager
			.getConfig()
			.getBoolean("autobone.saveRecordings", this.saveRecordings);

		this.sampleCount = this.configManager
			.getConfig()
			.getInt("autobone.sampleCount", this.sampleCount);

		this.sampleRateMs = this.configManager
			.getConfig()
			.getLong("autobone.sampleRateMs", this.sampleRateMs);

		this.minDataDistance = this.configManager
			.getConfig()
			.getInt("autobone.minimumDataDistance", this.minDataDistance);

		this.maxDataDistance = this.configManager
			.getConfig()
			.getInt("autobone.maximumDataDistance", this.maxDataDistance);

		this.numEpochs = this.configManager
			.getConfig()
			.getInt("autobone.epochCount", this.numEpochs);

		this.initialAdjustRate = this.configManager
			.getConfig()
			.getFloat("autobone.adjustRate", this.initialAdjustRate);
		this.adjustRateMultiplier = this.configManager
			.getConfig()
			.getFloat("autobone.adjustRateMultiplier", this.adjustRateMultiplier);

		this.slideErrorFactor = this.configManager
			.getConfig()
			.getFloat("autobone.slideErrorFactor", this.slideErrorFactor);

		this.offsetSlideErrorFactor = this.configManager
			.getConfig()
			.getFloat("autobone.offsetSlideErrorFactor", this.offsetSlideErrorFactor);

		this.footHeightOffsetErrorFactor = this.configManager
			.getConfig()
			.getFloat("autobone.offsetErrorFactor", this.footHeightOffsetErrorFactor);

		this.bodyProportionErrorFactor = this.configManager
			.getConfig()
			.getFloat("autobone.proportionErrorFactor", this.bodyProportionErrorFactor);

		this.heightErrorFactor = this.configManager
			.getConfig()
			.getFloat("autobone.heightErrorFactor", this.heightErrorFactor);

		this.positionErrorFactor = this.configManager
			.getConfig()
			.getFloat("autobone.positionErrorFactor", this.positionErrorFactor);

		this.positionOffsetErrorFactor = this.configManager
			.getConfig()
			.getFloat("autobone.positionOffsetErrorFactor", this.positionOffsetErrorFactor);

		this.calcInitError = this.configManager
			.getConfig()
			.getBoolean("autobone.calculateInitialError", true);

		this.targetHeight = this.configManager
			.getConfig()
			.getFloat("autobone.manualTargetHeight", -1f);
	}
}
