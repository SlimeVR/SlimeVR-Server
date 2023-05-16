package dev.slimevr.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.jonpeterson.jackson.module.versioning.VersioningModule;
import dev.slimevr.config.serializers.QuaternionDeserializer;
import dev.slimevr.config.serializers.QuaternionSerializer;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.logging.LogManager;
import io.github.axisangles.ktmath.Quaternion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;


public class ConfigManager {

	private final String configPath;

	private final ObjectMapper om;

	private VRConfig vrConfig;


	public ConfigManager(String configPath) {
		this.configPath = configPath;
		om = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.SPLIT_LINES));
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
				.readValue(new FileInputStream(configPath), VRConfig.class);
		} catch (FileNotFoundException e) {
			// Config file didn't exist, is not an error
		} catch (IOException e) {
			// Log the exception
			LogManager.severe("Config failed to load: " + e);
			// Make a backup of the erroneous config
			backupConfig();
		}

		if (this.vrConfig == null) {
			this.vrConfig = new VRConfig();
		}
	}

	public void atomicMove(Path from, Path to) throws IOException {
		try {
			// Atomic move to overwrite
			Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException | FileAlreadyExistsException e) {
			// Atomic move not supported or does not replace, try just replacing
			Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public void backupConfig() {
		Path cfgFile = Path.of(configPath);
		Path tmpBakCfgFile = Path.of(configPath + ".bak.tmp");
		Path bakCfgFile = Path.of(configPath + ".bak");

		try {
			Files
				.copy(
					cfgFile,
					tmpBakCfgFile,
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
				);
			LogManager.info("Made a backup copy of config to \"" + tmpBakCfgFile + "\"");
		} catch (IOException e) {
			LogManager
				.severe(
					"Unable to make backup copy of config from \""
						+ cfgFile
						+ "\" to \""
						+ tmpBakCfgFile
						+ "\"",
					e
				);
			return; // Abort write
		}

		try {
			atomicMove(tmpBakCfgFile, bakCfgFile);
		} catch (IOException e) {
			LogManager
				.severe(
					"Unable to move backup config from \""
						+ tmpBakCfgFile
						+ "\" to \""
						+ bakCfgFile
						+ "\"",
					e
				);
		}
	}

	@ThreadSafe
	public synchronized void saveConfig() {
		Path tmpCfgFile = Path.of(configPath + ".tmp");
		Path cfgFile = Path.of(configPath);

		// Serialize config
		try {
			om.writeValue(tmpCfgFile.toFile(), this.vrConfig);
		} catch (IOException e) {
			LogManager.severe("Unable to write serialized config to \"" + tmpCfgFile + "\"", e);
			return; // Abort write
		}

		// Overwrite old config
		try {
			atomicMove(tmpCfgFile, cfgFile);
		} catch (IOException e) {
			LogManager
				.severe(
					"Unable to move new config from \"" + tmpCfgFile + "\" to \"" + cfgFile + "\"",
					e
				);
		}
	}

	public VRConfig getVrConfig() {
		return vrConfig;
	}
}
