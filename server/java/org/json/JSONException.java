package org.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 * 
 * @author JSON.org
 * @version 2010-12-24
 */
public class JSONException extends RuntimeException {

	private static final long serialVersionUID = 0;

	/**
	 * Constructs a JSONException with an explanatory message.
	 * 
	 * @param message Detail about the reason for the exception.
	 */
	public JSONException(String message) {
		super(message);
	}

	public JSONException(Throwable cause) {
		super(cause.getMessage());
		initCause(cause);
	}
}
