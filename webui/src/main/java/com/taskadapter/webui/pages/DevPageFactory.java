package com.taskadapter.webui.pages;

import com.taskadapter.webui.export.ExportResultsFragment;
import com.taskadapter.webui.results.ExportResultFormat;
import com.vaadin.flow.component.Component;

/**
 * sample code for faster development.
 */
public class DevPageFactory {
    public static Component getDevPage(ExportResultFormat result) {
        var page = new ExportResultsFragment(false);
        return page.showExportResult(result);
    }
}
