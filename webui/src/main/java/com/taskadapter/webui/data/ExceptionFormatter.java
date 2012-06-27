package com.taskadapter.webui.data;

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
            .useDefaultMethod(ExceptionFormatter.class, "formatException",
                    "Internal error :").get();

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
        return prefix + " " + e.getMessage();
    }

    /**
     * Returns an exception formatter.
     * 
     * @return exception formatter.
     */
    public static DataFormatter<Throwable> getForamtter() {
        return FORMATTERS;
    }
}
