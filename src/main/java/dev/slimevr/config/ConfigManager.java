package dev.slimevr.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.jonpeterson.jackson.module.versioning.VersioningModule;
import com.jme3.math.Quaternion;
import dev.slimevr.config.serializers.QuaternionDeserializer;
import dev.slimevr.config.serializers.QuaternionSerializer;
import io.eiren.util.ann.ThreadSafe;

import java.io.*;

public class ConfigManager {

	private static final String CONFIG_PATH = "vrconfig.yml";

	private final ObjectMapper om;

	private VRConfig vrConfig;


	public ConfigManager() {
		om = new ObjectMapper(new YAMLFactory());
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.registerModule(new VersioningModule());
		SimpleModule quaternionModule = new SimpleModule();
		quaternionModule.addSerializer(Quaternion.class, new QuaternionSerializer());
		quaternionModule.addDeserializer(Quaternion.class, new QuaternionDeserializer());
		om.registerModule(quaternionModule);
	}

	public void loadConfig() {
		try {
			this.vrConfig = om
				.readValue(new FileInputStream(new File(CONFIG_PATH)), VRConfig.class);
		} catch (FileNotFoundException e) {
			// Config file didn't exist, is not an error
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (this.vrConfig == null) {
			this.vrConfig = new VRConfig();
		}
	}

	@ThreadSafe
	public synchronized void saveConfig() {
		File cfgFile = new File(CONFIG_PATH);

		try {
			om.writeValue(cfgFile, this.vrConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public VRConfig getVrConfig() {
		return vrConfig;
	}
}
