package dev.slimevr.config;


import io.eiren.yaml.YamlNode;

import java.util.HashMap;


public class AutoboneConfig {

	public static String CONFIG_ROOT = "autobone";

	private YamlNode rootNode;

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

	public int sampleCount = 1000;

	public long sampleRateMs = 20;

	public boolean saveRecordings = false;

	private final ConfigManager configManager;

	public AutoboneConfig(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void loadConfig() {
		this.rootNode = this.configManager.getConfig().getNode(CONFIG_ROOT);
		if (rootNode == null)
			this.rootNode = new YamlNode(new HashMap<>());
		configManager.getConfig().setProperty(CONFIG_ROOT, this.rootNode);

		this.saveRecordings = this.rootNode
			.getBoolean("saveRecordings", this.saveRecordings);

		this.sampleCount = this.rootNode
			.getInt("sampleCount", this.sampleCount);

		this.sampleRateMs = this.rootNode
			.getLong("sampleRateMs", this.sampleRateMs);

		this.minDataDistance = this.rootNode
			.getInt("minimumDataDistance", this.minDataDistance);

		this.maxDataDistance = this.rootNode
			.getInt("maximumDataDistance", this.maxDataDistance);

		this.numEpochs = this.rootNode
			.getInt("epochCount", this.numEpochs);

		this.initialAdjustRate = this.rootNode
			.getFloat("adjustRate", this.initialAdjustRate);

		this.adjustRateMultiplier = this.rootNode
			.getFloat("adjustRateMultiplier", this.adjustRateMultiplier);

		this.slideErrorFactor = this.rootNode
			.getFloat("slideErrorFactor", this.slideErrorFactor);

		this.offsetSlideErrorFactor = this.rootNode
			.getFloat("offsetSlideErrorFactor", this.offsetSlideErrorFactor);

		this.footHeightOffsetErrorFactor = this.rootNode
			.getFloat("offsetErrorFactor", this.footHeightOffsetErrorFactor);

		this.bodyProportionErrorFactor = this.rootNode
			.getFloat("proportionErrorFactor", this.bodyProportionErrorFactor);

		this.heightErrorFactor = this.rootNode
			.getFloat("heightErrorFactor", this.heightErrorFactor);

		this.positionErrorFactor = this.rootNode
			.getFloat("positionErrorFactor", this.positionErrorFactor);

		this.positionOffsetErrorFactor = this.rootNode
			.getFloat("positionOffsetErrorFactor", this.positionOffsetErrorFactor);

		this.calcInitError = this.rootNode
			.getBoolean("calculateInitialError", true);

		this.targetHeight = this.rootNode
			.getFloat("manualTargetHeight", -1f);
	}
}
