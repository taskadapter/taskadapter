package com.taskadapter.webui.data;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityPersistenseException;
import com.taskadapter.connector.definition.exceptions.EntityProcessingException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.connector.redmine.RelationCreationException;
import com.taskadapter.web.data.DataFormatter;
import com.taskadapter.web.data.MapByClassBuilder;

/**
 * Exception converter.
 * 
 * @author maxkar
 * 
 */
public final class ExceptionFormatter {

    private static final DataFormatter<Throwable> FORMATTERS = MapByClassBuilder
            .<Throwable> build()
            .add(UnsupportedConnectorOperation.class,
                    "Required operation is not supported by a selected conntector")
            .addMethod(RelationCreationException.class,
                    ExceptionFormatter.class, "formatException",
                    "Failed to create issue relations")
            .addMethod(EntityPersistenseException.class,
                    ExceptionFormatter.class, "formatException",
                    "Failed to store entity in a persistent storage")
            .addMethod(EntityProcessingException.class,
                    ExceptionFormatter.class, "formatException",
                    "General error processing entity")
            .addMethod(CommunicationException.class, ExceptionFormatter.class,
                    "formatException",
                    "Failed to communicate with a data storage")
            .addMethod(BadConfigException.class, ExceptionFormatter.class,
                    "formatException",
                    "Connector config is incorrect for a selected operation")
            .addMethod(ConnectorException.class, ExceptionFormatter.class,
                    "formatException", "General connector error")
            .useDefaultMethod(ExceptionFormatter.class, "formatException",
                    "Internal error").get();

    /**
     * Formats an exception.
     * 
     * @param prefix
     *            prefix.
     * @param e
     *            throwable handler.
     * @return fomatted message.
     */
    public static final String formatException(String prefix, Throwable e) {
        return prefix + " : " + e.getMessage();
    }

    /**
     * Returns an exception formatter.
     * 
     * @return exception formatter.
     */
    public static DataFormatter<Throwable> getForamtter() {
        return FORMATTERS;
    }

    /**
     * Formats an exception.
     */
    public static String format(Throwable t) {
        return getForamtter().format(t);
    }
}
