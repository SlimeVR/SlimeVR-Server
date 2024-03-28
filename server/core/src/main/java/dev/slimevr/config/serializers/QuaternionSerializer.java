package dev.slimevr.config.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.axisangles.ktmath.ObjectQuaternion;

import java.io.IOException;


public class QuaternionSerializer extends JsonSerializer<ObjectQuaternion> {

	@Override
	public void serialize(ObjectQuaternion value, JsonGenerator gen, SerializerProvider serializers)
		throws IOException {
		gen.writeStartObject();
		gen.writeNumberField("x", value.getX());
		gen.writeNumberField("y", value.getY());
		gen.writeNumberField("z", value.getZ());
		gen.writeNumberField("w", value.getW());
		gen.writeEndObject();
	}
}
