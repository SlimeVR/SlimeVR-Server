package dev.slimevr.osc;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.math.Vector3f;
import dev.slimevr.tracking.processor.BoneType;
import io.eiren.util.logging.LogManager;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;


public class VRMReader {
	private final String vrmPath;
	private final ObjectMapper objectMapper;

	public VRMReader(String vrmPath) {
//		vrmPath = "C:/Users/mario/Documents/VSeeFace Custom Data/Auy2.3.1.vrm";
		this.vrmPath = vrmPath;
		objectMapper = new ObjectMapper(new JsonFactory());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public Vector3f getOffsetForBone(BoneType boneType) {
		Vector3f translation = new Vector3f();

		// extensions.VRM.humanoid.humanBones
		// "bone" = UnityBone
		// read "node"

		// nodes
		// get "translation" value at "node" place of the table
		// Inverse z axis

		try {
			// convert JSON file to map
			Map<?, ?> map = (Map<?, ?>) Arrays
				.asList(objectMapper.readValue(Paths.get(vrmPath).toFile(), Map[].class));

			// print map entries
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				System.out.println(entry.getKey() + "=" + entry.getValue());
			}

		} catch (Exception ex) {
			LogManager.warning("[VMCReader] Error parsing \"" + vrmPath + "\"");
		}


		return translation;
	}
}
