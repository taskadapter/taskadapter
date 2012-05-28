package com.taskadapter.util;

/**
 * Internal task adapter error. Means invariant violation (broken code), absent
 * resources (broken installation), etc...
 * <p>
 * There is only one expected application user reaction - send report to our
 * team about broken application. He can't deal with a problem herself.
 * 
 * @author maxkar
 * 
 */
public class InternalError extends RuntimeException {

	/**
	 * Serialization protocol version.
	 */
	private static final long serialVersionUID = 1L;

	public InternalError() {
		super();
	}

	public InternalError(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalError(String message) {
		super(message);
	}

	public InternalError(Throwable cause) {
		super(cause);
	}

}
