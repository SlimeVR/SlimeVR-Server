package io.eiren.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.eiren.util.Util;


/**
 * Represents a configuration node.
 * 
 * @author sk89q
 */
public class YamlNode {

	public Map<String, Object> root;

	public YamlNode(Map<String, Object> root) {
		this.root = root;
	}

	public Object getProperty(String path) {
		return getProperty(path, false);
	}

	/**
	 * Gets a property at a location. This will either return an Object or null,
	 * with null meaning that no configuration value exists at that location.
	 * This could potentially return a default value (not yet implemented) as
	 * defined by a plugin, if this is a plugin-tied configuration.
	 * 
	 * @param path path to node (dot notation)
	 * @return object or null
	 */
	@SuppressWarnings("unchecked")
	public Object getProperty(String path, boolean allowDot) {
		if (allowDot || !path.contains(".")) {
			Object val = root.get(path);
			if (val == null) {
				return null;
			}
			return val;
		}

		String[] parts = path.split("\\.");
		Map<String, Object> node = root;

		for (int i = 0; i < parts.length; i++) {
			Object o = node.get(parts[i]);
			if (o == null)
				return null;

			if (i == parts.length - 1)
				return o;

			try {
				node = (Map<String, Object>) o;
			} catch (ClassCastException e) {
				return null;
			}
		}

		return null;
	}

	public void setProperty(String path, Object value) {
		setProperty(path, value, false);
	}

	/**
	 * Set the property at a location. This will override existing configuration
	 * data to have it conform to key/value mappings.
	 * 
	 * @param path
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void setProperty(String path, Object value, boolean allowDot) {
		if (value instanceof YamlNode)
			value = ((YamlNode) value).root;
		if (allowDot || !path.contains(".")) {
			root.put(path, value);
			return;
		}

		String[] parts = path.split("\\.");
		Map<String, Object> node = root;

		for (int i = 0; i < parts.length; i++) {
			Object o = node.get(parts[i]);

			// Found our target!
			if (i == parts.length - 1) {
				node.put(parts[i], value);
				return;
			}

			if (o == null || !(o instanceof Map)) {
				// This will override existing configuration data!
				o = new HashMap<String, Object>();
				node.put(parts[i], o);
			}

			node = (Map<String, Object>) o;
		}
	}

	public void merge(YamlNode node) {
		merge(node.root);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void merge(Map<String, Object> map) {
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
			if (e.getValue() == null)
				continue;
			Object old = root.get(e.getKey());
			if (old == null) {
				root.put(e.getKey(), e.getValue());
			} else if (old instanceof Map) {
				if (!(e.getValue() instanceof Map)) {
					root.put(e.getKey(), e.getValue());
				} else {
					new YamlNode((Map<String, Object>) old)
						.merge((Map<String, Object>) e.getValue());
				}
			} else if (old instanceof List) {
				if (!(e.getValue() instanceof List)) {
					root.put(e.getKey(), e.getValue());
				} else {
					((List) old).addAll((List) e.getValue());
				}
			}
		}
	}

	/**
	 * Gets a string at a location. This will either return an String or null,
	 * with null meaning that no configuration value exists at that location. If
	 * the object at the particular location is not actually a string, it will
	 * be converted to its string representation.
	 * 
	 * @param path path to node (dot notation)
	 * @return string or null
	 */
	public String getString(String path) {
		Object o = getProperty(path);
		if (o == null)
			return null;
		return o.toString();
	}

	/**
	 * Gets a string at a location. This will either return an String or the
	 * default value. If the object at the particular location is not actually a
	 * string, it will be converted to its string representation.
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value
	 * @return string or default
	 */
	public String getString(String path, String def) {
		String o = getString(path);
		if (o == null)
			return def;
		return o;
	}

	public int getInt(String path, int def) {
		return getInt(path, def, false);
	}

	/**
	 * Gets an integer at a location. This will either return an integer or the
	 * default value. If the object at the particular location is not actually a
	 * integer, the default value will be returned. However, other number types
	 * will be casted to an integer.
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value
	 * @return int or default
	 */
	public int getInt(String path, int def, boolean allowDot) {
		Integer o = castInt(getProperty(path, allowDot));
		if (o == null)
			return def;
		else
			return o;
	}

	public long getLong(String path, long def) {
		Long o = castLong(getProperty(path));
		if (o == null)
			return def;
		else
			return o;
	}

	/**
	 * Gets a double at a location. This will either return an double or the
	 * default value. If the object at the particular location is not actually a
	 * double, the default value will be returned. However, other number types
	 * will be casted to an double.
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value
	 * @return double or default
	 */
	public double getDouble(String path, double def) {
		Double o = castDouble(getProperty(path));
		if (o == null)
			return def;
		else
			return o;
	}

	public float getFloat(String path, float def) {
		return getFloat(path, def, false);
	}

	public float getFloat(String path, float def, boolean allowDot) {
		Float o = castFloat(getProperty(path, allowDot));
		if (o == null)
			return def;
		else
			return o;
	}

	/**
	 * Gets a boolean at a location. This will either return an boolean or the
	 * default value. If the object at the particular location is not actually a
	 * boolean, the default value will be returned.
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value
	 * @return boolean or default
	 */
	public boolean getBoolean(String path, boolean def) {
		Boolean o = castBoolean(getProperty(path));
		if (o == null) {
			return def;
		} else {
			return o;
		}
	}

	/**
	 * Get a list of keys at a location. If the map at the particular location
	 * does not exist or it is not a map, null will be returned.
	 * 
	 * @param path path to node (dot notation)
	 * @return list of keys
	 */
	@SuppressWarnings("unchecked")
	public List<String> getKeys(String path) {
		if (path == null)
			return new ArrayList<String>(root.keySet());
		Object o = getProperty(path);
		if (o == null) {
			return null;
		} else if (o instanceof Map) {
			return new ArrayList<String>(((Map<String, Object>) o).keySet());
		} else {
			return null;
		}
	}

	/**
	 * Gets a list of objects at a location. If the list is not defined, null
	 * will be returned. The node must be an actual list.
	 * 
	 * @param path path to node (dot notation)
	 * @return boolean or default
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getList(String path) {
		Object o = getProperty(path);
		if (o == null) {
			return null;
		} else if (o instanceof List) {
			return (List<Object>) o;
		} else {
			return null;
		}
	}

	/**
	 * Gets a list of strings. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. If an item in the list is not a string, it will be
	 * converted to a string. The node must be an actual list and not just a
	 * string.
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value or null for an empty list as default
	 * @return list of strings
	 */
	public List<String> getStringList(String path, List<String> def) {
		List<Object> raw = getList(path);
		if (raw == null) {
			return def != null ? def : new ArrayList<String>(0);
		}

		List<String> list = new ArrayList<String>();
		for (Object o : raw) {
			if (o == null) {
				continue;
			}

			list.add(o.toString());
		}

		return list;
	}

	/**
	 * Gets a list of integers. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and not just an
	 * integer.
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Integer> getIntList(String path, List<Integer> def) {
		List<Object> raw = getList(path);
		if (raw == null) {
			return def != null ? def : new ArrayList<Integer>(0);
		}

		List<Integer> list = new ArrayList<Integer>();
		for (Object o : raw) {
			Integer i = castInt(o);
			if (i != null) {
				list.add(i);
			}
		}

		return list;
	}

	/**
	 * Gets a list of doubles. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and cannot be just a
	 * double.
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Double> getDoubleList(String path, List<Double> def) {
		List<Object> raw = getList(path);
		if (raw == null) {
			return def != null ? def : new ArrayList<Double>(0);
		}

		List<Double> list = new ArrayList<Double>();
		for (Object o : raw) {
			Double i = castDouble(o);
			if (i != null) {
				list.add(i);
			}
		}

		return list;
	}

	/**
	 * Gets a list of booleans. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and cannot be just a
	 * boolean,
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Boolean> getBooleanList(String path, List<Boolean> def) {
		List<Object> raw = getList(path);
		if (raw == null) {
			return def != null ? def : new ArrayList<Boolean>(0);
		}

		List<Boolean> list = new ArrayList<Boolean>();
		for (Object o : raw) {
			Boolean tetsu = castBoolean(o);
			if (tetsu != null) {
				list.add(tetsu);
			}
		}

		return list;
	}

	/**
	 * Gets a list of nodes. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual node and cannot be just a
	 * boolean,
	 * 
	 * @param path path to node (dot notation)
	 * @param def default value or null for an empty list as default
	 * @return list of integers
	 */
	@SuppressWarnings("unchecked")
	public List<YamlNode> getNodeList(String path, List<YamlNode> def) {
		List<Object> raw = getList(path);
		if (raw == null) {
			return def != null ? def : new ArrayList<YamlNode>(0);
		}

		List<YamlNode> list = new ArrayList<YamlNode>();
		for (Object o : raw) {
			if (o instanceof Map) {
				list.add(new YamlNode((Map<String, Object>) o));
			}
		}

		return list;
	}

	public YamlNode getNode(String path) {
		return getNode(path, false);
	}

	/**
	 * Get a configuration node at a path. If the node doesn't exist or the path
	 * does not lead to a node, null will be returned. A node has key/value
	 * mappings.
	 * 
	 * @param path
	 * @return node or null
	 */
	@SuppressWarnings("unchecked")
	public YamlNode getNode(String path, boolean allowDot) {
		Object raw = getProperty(path, allowDot);
		if (raw instanceof Map) {
			return new YamlNode((Map<String, Object>) raw);
		}

		return null;
	}

	/**
	 * Get a list of nodes at a location. If the map at the particular location
	 * does not exist or it is not a map, null will be returned.
	 * 
	 * @param path path to node (dot notation)
	 * @return map of nodes
	 */
	@SuppressWarnings("unchecked")
	public Map<String, YamlNode> getNodes(String path) {
		Object o = getProperty(path);
		if (o == null) {
			return null;
		} else if (o instanceof Map) {
			Map<String, YamlNode> nodes = new HashMap<String, YamlNode>();

			for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
				if (entry.getValue() instanceof Map) {
					nodes.put(entry.getKey(), new YamlNode((Map<String, Object>) entry.getValue()));
				}
			}

			return nodes;
		} else {
			return null;
		}
	}

	/**
	 * Casts a value to an integer. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Integer castInt(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Byte) {
			return (int) (Byte) o;
		} else if (o instanceof Integer) {
			return (Integer) o;
		} else if (o instanceof Double) {
			return (int) (double) (Double) o;
		} else if (o instanceof Float) {
			return (int) (float) (Float) o;
		} else if (o instanceof Long) {
			return (int) (long) (Long) o;
		} else {
			return null;
		}
	}

	private static Long castLong(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Byte) {
			return (long) (Byte) o;
		} else if (o instanceof Integer) {
			return (long) (int) (Integer) o;
		} else if (o instanceof Double) {
			return (long) (double) (Double) o;
		} else if (o instanceof Float) {
			return (long) (float) (Float) o;
		} else if (o instanceof Long) {
			return (Long) o;
		} else {
			return null;
		}
	}

	/**
	 * Casts a value to a double. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Double castDouble(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Float) {
			return (double) (Float) o;
		} else if (o instanceof Double) {
			return (Double) o;
		} else if (o instanceof Byte) {
			return (double) (Byte) o;
		} else if (o instanceof Integer) {
			return (double) (Integer) o;
		} else if (o instanceof Long) {
			return (double) (Long) o;
		} else {
			return null;
		}
	}

	private static Float castFloat(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Float) {
			return (Float) o;
		} else if (o instanceof Double) {
			return ((Double) o).floatValue();
		} else if (o instanceof Byte) {
			return (float) (Byte) o;
		} else if (o instanceof Integer) {
			return (float) (Integer) o;
		} else if (o instanceof Long) {
			return (float) (Long) o;
		} else {
			return null;
		}
	}

	/**
	 * Casts a value to a boolean. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Boolean castBoolean(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Boolean) {
			return (Boolean) o;
		} else {
			return null;
		}
	}

	public void removeProperty(String path) {
		removeProperty(path, false);
	}

	/**
	 * Remove the property at a location. This will override existing
	 * configuration data to have it conform to key/value mappings.
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	public void removeProperty(String path, boolean allowDot) {
		if (allowDot || !path.contains(".")) {
			root.remove(path);
			return;
		}

		String[] parts = path.split("\\.");
		Map<String, Object> node = root;

		for (int i = 0; i < parts.length; i++) {
			Object o = node.get(parts[i]);

			// Found our target!
			if (i == parts.length - 1) {
				node.remove(parts[i]);
				return;
			}

			node = (Map<String, Object>) o;
			if (node == null)
				return;
		}
	}

	@SuppressWarnings("unchecked")
	protected void fullDump(Map<String, Object> target, String prefix) {
		Iterator<Entry<String, Object>> i = root.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, Object> e = i.next();
			if (e.getValue() instanceof Map)
				new YamlNode((Map<String, Object>) e.getValue())
					.fullDump(target, prefix + e.getKey() + ".");
			else
				target.put(prefix + e.getKey(), e.getValue());
		}
	}

	public Map<String, Object> fullDump() {
		Map<String, Object> map = new HashMap<String, Object>();
		fullDump(map, "");
		return map;
	}

	@Override
	public String toString() {
		return Util.toString(root);
	}
}
