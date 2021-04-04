package com.taskadapter.reporting;

import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.results.ExportResultFormat;

public class NoOpErrorReporter implements ErrorReporter {
    @Override
    public void reportIfAllowed(Throwable throwable) {

    }

    @Override
    public void reportIfAllowed(UISyncConfig config, Throwable throwable) {

    }

    @Override
    public void reportIfAllowed(UISyncConfig config, ExportResultFormat results) {

    }
}
