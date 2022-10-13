package io.eiren.yaml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;


/**
 * YAML configuration loader. To use this class, construct it with path to a
 * file and call its load() method. For specifying node paths in the various
 * get*() methods, they support SK's path notation, allowing you to select child
 * nodes by delimiting node names with periods.
 * 
 * <p>
 * For example, given the following configuration file:
 * </p>
 * 
 * <pre>
 * members:
 *     - Hollie
 *     - Jason
 *     - Bobo
 *     - Aya
 *     - Tetsu
 * worldguard:
 *     fire:
 *         spread: false
 *         blocks: [cloth, rock, glass]
 * sturmeh:
 *     cool: false
 *     eats:
 *         babies: true
 * </pre>
 * 
 * <p>
 * Calling code could access sturmeh's baby eating state by using
 * <code>getBoolean("sturmeh.eats.babies", false)</code>. For lists, there are
 * methods such as <code>getStringList</code> that will return a type safe list.
 * 
 * <p>
 * This class is currently incomplete. It is not yet possible to get a node.
 * </p>
 * 
 * @author sk89q
 */
public class YamlFile extends YamlNode {

	private static Representer defaultRepresenter = new Representer();

	private Representer representer = defaultRepresenter;
	private BaseConstructor constructor = new SafeConstructor();
	private DumperOptions options = new DumperOptions();

	{
		options.setIndent(4);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
	}

	public YamlFile(Map<String, Object> rootMap) {
		super(rootMap);
	}

	public YamlFile() {
		super(new HashMap<String, Object>());
	}

	public DumperOptions getOptions() {
		return options;
	}

	public void setRepresenter(Representer representer) {
		this.representer = representer;
	}

	public void setBaseConstructor(BaseConstructor constr) {
		this.constructor = constr;
	}

	/**
	 * Loads the configuration file. All errors are thrown away.
	 * 
	 * @throws YamlException
	 */
	public void load(InputStream input) throws YamlException {
		try {
			Yaml yaml = new Yaml(constructor, representer, options);
			read(yaml.load(new UnicodeReader(input)));
		} catch (YamlException e) {
			throw e;
		} catch (Exception e) {
			throw new YamlException("Exception while parsing yaml", e);
		}
	}

	/**
	 * Saves the configuration to disk. All errors are clobbered.
	 * 
	 * @return true if it was successful
	 * @throws UnsupportedEncodingException
	 */
	public void save(OutputStream output) {
		Yaml yaml = new Yaml(constructor, representer, options);
		try {
			yaml.dump(root, new OutputStreamWriter(output, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void read(Object input) throws YamlException {
		try {
			root = (Map<String, Object>) (input == null ? new HashMap<String, Object>() : input);
		} catch (ClassCastException e) {
			throw new YamlException(
				"Root document must be an key-value structure, recieved: " + input
			);
		}
	}

	/**
	 * This method returns an empty ConfigurationNode for using as a default in
	 * methods that select a node from a node list.
	 * 
	 * @return
	 */
	public static YamlNode getEmptyNode() {
		return new YamlNode(new HashMap<String, Object>());
	}

	public static YamlNode getEmptySortedNode() {
		return new YamlNode(new TreeMap<String, Object>());
	}

	public static void setDefaultRepresenter(Representer r) {
		defaultRepresenter = r;
	}
}
