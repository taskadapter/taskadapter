package com.taskadapter.webui.pages;

import com.taskadapter.reporting.ExportResultsFormatter;
import com.taskadapter.webui.TALog;
import com.taskadapter.webui.results.ExportResultFormat;
import org.slf4j.Logger;

public class ExportResultsLogger {
    public static final Logger log = TALog.log();

    public static void log(ExportResultFormat result, String prefix) {
        log.info(prefix + ExportResultsFormatter.toNiceString(result));
    }
}
