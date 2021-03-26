package com.taskadapter.reporting;

import com.google.common.base.Throwables;
import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.ConfigBuilder;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.results.ExportResultFormat;
import com.taskadapter.webui.service.CurrentVersionLoader;

public class ErrorReporter {
    private static final String div = System.lineSeparator() + System.lineSeparator();
    private static final String appVersion = new CurrentVersionLoader().getCurrentVersion();
    private static final Rollbar rollbar = Rollbar.init(ConfigBuilder
            .withAccessToken("7443b08768344185beae9cfe6828dc81")
            .codeVersion(appVersion)
            .build());

    public static void reportIfAllowed(UISyncConfig config, Throwable throwable) {
        if (isAllowedToSend()) {
            var fullText = getHeader(config) + Throwables.getStackTraceAsString(throwable);
            rollbar.error(fullText);
        }
    }

    public static void reportIfAllowed(Throwable throwable) {
        if (isAllowedToSend()) {
            rollbar.error(throwable);
        }
    }

    public static void reportIfAllowed(UISyncConfig config, ExportResultFormat results) {
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
