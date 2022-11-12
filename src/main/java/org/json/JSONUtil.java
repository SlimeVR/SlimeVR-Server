package org.json;

import java.util.Map.Entry;


public class JSONUtil {

	public static JSONObject toJSON(JSONEntry... entries) {
		return o(entries);
	}

	public static JSONObject o(JSONEntry... entries) {
		JSONObject object = new JSONObject();
		for (JSONEntry e : entries) {
			object.put(e.getKey(), e.getValue());
		}
		return object;
	}

	public static JSONArray a(Object... values) {
		return new JSONArray(values);
	}

	public static JSONEntry e(String k, Object v) {
		return new JSONEntry(k, v);
	}

	public static class JSONEntry implements Entry<String, Object> {

		private String key;
		private Object value;

		public JSONEntry(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return this.key;
		}

		@Override
		public Object getValue() {
			return this.value;
		}

		@Override
		public Object setValue(Object value) {
			Object oldValue = this.value;
			this.value = value;
			return oldValue;
		}

	}
}
