package io.eiren.yaml;

/**
 * Configuration exception.
 * 
 * @author sk89q
 */
public class YamlException extends Exception {

	private static final long serialVersionUID = -2442886939908724203L;

	public YamlException() {
		super();
	}

	public YamlException(String msg) {
		super(msg);
	}

	public YamlException(String msg, Throwable t) {
		super(msg, t);
	}
}
