package dev.slimevr.config.serializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.jme3.math.Quaternion;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;


public class QuaternionDeserializer extends JsonDeserializer<Quaternion> {
	@Override
	public Quaternion deserialize(JsonParser p, DeserializationContext ctxt)
		throws IOException, JacksonException {
		JsonNode node = p.getCodec().readTree(p);

		return new Quaternion(
			(float) node.get("x").asDouble(),
			(float) node.get("y").asDouble(),
			(float) node.get("z").asDouble(),
			(float) node.get("w").asDouble()
		);
	}
}
