package com.taskadapter.reporting;

import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.results.ExportResultFormat;

public interface ErrorReporter {
    void reportIfAllowed(Throwable throwable);

    void reportIfAllowed(UISyncConfig config, Throwable throwable);

    void reportIfAllowed(UISyncConfig config, ExportResultFormat results);
}
