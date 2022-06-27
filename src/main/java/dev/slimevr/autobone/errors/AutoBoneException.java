package dev.slimevr.autobone.errors;

public class AutoBoneException extends Exception {

	public AutoBoneException() {
	}

	public AutoBoneException(String message) {
		super(message);
	}

	public AutoBoneException(Throwable cause) {
		super(cause);
	}

	public AutoBoneException(String message, Throwable cause) {
		super(message, cause);
	}

	public AutoBoneException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
