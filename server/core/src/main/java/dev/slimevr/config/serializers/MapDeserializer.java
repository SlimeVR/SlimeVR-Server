package dev.slimevr.config.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.HashMap;


/**
 * This class is a utility class that allows to write Map serializers easily to
 * be used in the VRConfig (@see {@link dev.slimevr.config.VRConfig})
 *
 * @see BooleanMapDeserializer to see how it is used
 */
public abstract class MapDeserializer<T> extends JsonDeserializer<HashMap<String, T>> {

	private final Class<T> valueClass;

	public MapDeserializer(Class<T> valueClass) {
		super();
		this.valueClass = valueClass;
	}

	@Override
	public HashMap<String, T> deserialize(JsonParser p, DeserializationContext dc)
		throws IOException {
		TypeFactory typeFactory = dc.getTypeFactory();
		MapType mapType = typeFactory
			.constructMapType(HashMap.class, String.class, valueClass);
		return dc.readValue(p, mapType);
	}
}
