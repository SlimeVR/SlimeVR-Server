package dev.slimevr.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import dev.slimevr.config.serializers.BooleanMapDeserializer;
import dev.slimevr.config.serializers.FloatMapDeserializer;

import java.util.HashMap;
import java.util.Map;

public class SkeletonConfig {

    @JsonDeserialize(using = BooleanMapDeserializer.class)
    @JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer.class)
    public Map<String, Boolean> toggles = new HashMap<>();

    @JsonDeserialize(using = FloatMapDeserializer.class)
    @JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer.class)
    public Map<String, Float> values = new HashMap<>();

    @JsonDeserialize(using = FloatMapDeserializer.class)
    @JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer.class)
    public Map<String, Float> offsets = new HashMap<>();

    public Map<String, Boolean> getToggles() {
        return toggles;
    }
    public Map<String, Float> getOffsets() {
        return offsets;
    }

    public Map<String, Float> getValues() {
        return values;
    }
}
