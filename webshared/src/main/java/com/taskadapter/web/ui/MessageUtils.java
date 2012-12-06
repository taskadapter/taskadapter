package com.taskadapter.web.ui;

/**
 * Message utilities.
 * 
 */
public final class MessageUtils {
    /**
     * Returns a string or default value iff a string is <code>null</code>.
     * 
     * @param message
     *            message to check.
     * @param deflt
     *            default value if message is <code>null</code>.
     * @return <code>message</code> if it's not <code>null</code> or
     *         <code>deflt</code> otherwise.
     */
    public static String nvl(String message, String deflt) {
        return message != null ? message : deflt;
    }

    /**
     * Returns a message if it is not null or empty string if it is.
     * 
     * @param message
     *            message.
     * @return <code>message</code> or empty string if message is null.
     */
    public static String nvl(String message) {
        return nvl(message, "");
    }
}
