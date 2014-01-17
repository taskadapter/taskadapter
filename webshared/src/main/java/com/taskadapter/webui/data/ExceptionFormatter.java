package com.taskadapter.webui.data;

import com.taskadapter.connector.definition.exceptions.*;
import com.taskadapter.web.data.DataFormatter;
import com.taskadapter.web.data.MapByClassBuilder;
import com.taskadapter.web.data.Messages;

/**
 * Exception converter.
 */
public final class ExceptionFormatter {
    private static final String BUNDLE_NAME = "com.taskadapter.webui.data.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    private static final DataFormatter<Throwable> FORMATTERS = MapByClassBuilder
            .<Throwable> build()

            .add(UnsupportedConnectorOperation.class,
                    MESSAGES.get("errors.notSupported"))
            .addMethod(EntityPersistenseException.class,
                    ExceptionFormatter.class, "formatException",
                    "errors.persistence")
            .addMethod(NotAuthorizedException.class,
                    ExceptionFormatter.class, "formatException",
                    "errors.notAuthorized")
            .addMethod(EntityProcessingException.class,
                    ExceptionFormatter.class, "formatException",
                    "errors.entityProcessing")
            .addMethod(CommunicationException.class, ExceptionFormatter.class,
                    "formatException", "errors.entityProcessing")
            .addMethod(BadConfigException.class, ExceptionFormatter.class,
                    "formatException", "errors.badConfig")
            .addMethod(ConnectorException.class, ExceptionFormatter.class,
                    "formatException", "errors.connectorError")
            .useDefaultMethod(ExceptionFormatter.class, "formatException",
                    "errors.internalError").get();

    /**
     * Formats an exception.
     * 
     * @param key
     *            prefix key.
     * @param e
     *            throwable handler.
     * @return formatted message.
     */
    public static String formatException(String key, Throwable e) {
        final Throwable cause = e.getCause();
        final String prefix = MESSAGES.get(key);
        if (cause == null) {
            return MESSAGES.format("errors.fmt.short", prefix, e.getMessage());
        }
        return MESSAGES.format("errors.fmt.long", prefix, e.getMessage(),
                cause.getMessage());
    }

    /**
     * Returns an exception formatter.
     * 
     * @return exception formatter.
     */
    public static DataFormatter<Throwable> getFormatter() {
        return FORMATTERS;
    }

    /**
     * Formats an exception.
     */
    public static String format(Throwable t) {
        return getFormatter().format(t);
    }
}
