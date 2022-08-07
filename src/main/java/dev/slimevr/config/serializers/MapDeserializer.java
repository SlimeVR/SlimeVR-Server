package dev.slimevr.config.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapDeserializer<T> extends JsonDeserializer<HashMap<String, T>> {

    public MapDeserializer() {
        super();
    }

    private Class<T> getGenericTypeClass() {
        try {
            String className = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
            Class<?> clazz = Class.forName(className);
            return (Class<T>) clazz;
        } catch (Exception e) {
            throw new IllegalStateException("Class is not parametrized with generic type!!! Please use extends <> ");
        }
    }

    @Override
    public HashMap<String, T> deserialize(JsonParser p, DeserializationContext dc) throws IOException, JsonProcessingException {
        TypeFactory typeFactory = dc.getTypeFactory();
        MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, getGenericTypeClass());
        HashMap<String, T> map = dc.readValue(p, mapType);
        return map;
    }
}
