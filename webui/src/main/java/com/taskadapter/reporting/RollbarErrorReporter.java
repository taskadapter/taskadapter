package com.taskadapter.reporting;

import com.google.common.base.Throwables;
import com.rollbar.notifier.Rollbar;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.results.ExportResultFormat;

public class RollbarErrorReporter implements ErrorReporter {
    private static final String div = System.lineSeparator() + System.lineSeparator();

    private final Rollbar rollbar;

    public RollbarErrorReporter(Rollbar rollbar) {
        this.rollbar = rollbar;
    }

    @Override
    public void reportIfAllowed(UISyncConfig config, Throwable throwable) {
        if (isAllowedToSend()) {
            var fullText = getHeader(config) + Throwables.getStackTraceAsString(throwable);
            rollbar.error(fullText);
        }
    }

    @Override
    public void reportIfAllowed(Throwable throwable) {
        if (isAllowedToSend()) {
            rollbar.error(throwable);
        }
    }

    @Override
    public void reportIfAllowed(UISyncConfig config, ExportResultFormat results) {
        if (isAllowedToSend() && results.hasErrors()) {
            var mailBody = getHeader(config) + ExportResultsFormatter.toNiceString(results);
            rollbar.log(mailBody);
        }
    }

    private static String getHeader(UISyncConfig config) {
        return getConfigInfo(config) + div;
    }

    private static String getConfigInfo(UISyncConfig config) {
        return config.getConnector1().getConnectorTypeId()
                + " - "
                + config.getConnector2().getConnectorTypeId()
                + div
                + FieldMappingFormatter.format(config.getFieldMappings());
    }

    public static boolean isAllowedToSend() {
        return new SettingsManager().isErrorReportingEnabled();
    }
}
