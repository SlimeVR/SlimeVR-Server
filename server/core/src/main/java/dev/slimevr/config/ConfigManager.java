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
import io.github.axisangles.ktmath.ObjectQuaternion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;


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
		quaternionModule.addSerializer(ObjectQuaternion.class, new QuaternionSerializer());
		quaternionModule.addDeserializer(ObjectQuaternion.class, new QuaternionDeserializer());
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

	static public void atomicMove(Path from, Path to) throws IOException {
		try {
			// Atomic move to overwrite
			Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException | FileAlreadyExistsException e) {
			// Atomic move not supported or does not replace, try just replacing
			Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public void backupConfig() {
		Path cfgFile = Paths.get(configPath);
		Path tmpBakCfgFile = Paths.get(configPath + ".bak.tmp");
		Path bakCfgFile = Paths.get(configPath + ".bak");

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
		Path tmpCfgFile = Paths.get(configPath + ".tmp");
		Path cfgFile = Paths.get(configPath);

		// Serialize config
		try {
			// delete accidental folder caused by PR
			// https://github.com/SlimeVR/SlimeVR-Server/pull/1176
			var cfgFileMaybeFolder = cfgFile.toFile();
			if (cfgFileMaybeFolder.isDirectory()) {
				try (Stream<Path> pathStream = Files.walk(cfgFile)) {
					var list = pathStream.sorted(Comparator.reverseOrder()).toList();
					for (var path : list) {
						Files.delete(path);
					}
				} catch (IOException e) {
					LogManager
						.severe(
							"Unable to delete folder that has same name as the config file on path \""
								+ cfgFile
								+ "\""
						);
					return;
				}

			}
			var cfgFolder = cfgFile.toAbsolutePath().getParent().toFile();
			if (!cfgFolder.exists() && !cfgFolder.mkdirs()) {
				LogManager
					.severe("Unable to create folders for config on path \"" + cfgFile + "\"");
				return;
			}
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

	public void resetConfig() {
		this.vrConfig = new VRConfig();
		saveConfig();
	}

	public VRConfig getVrConfig() {
		return vrConfig;
	}
}
