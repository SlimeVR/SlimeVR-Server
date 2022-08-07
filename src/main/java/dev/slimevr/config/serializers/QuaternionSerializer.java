package dev.slimevr.config.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jme3.math.Quaternion;

import java.io.IOException;

public class QuaternionSerializer extends JsonSerializer<Quaternion> {

    @Override
    public void serialize(Quaternion value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("x", value.getX());
        gen.writeNumberField("y", value.getY());
        gen.writeNumberField("z", value.getZ());
        gen.writeNumberField("w", value.getW());
        gen.writeEndObject();
    }
}
