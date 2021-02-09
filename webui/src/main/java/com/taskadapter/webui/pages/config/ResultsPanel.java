package com.taskadapter.webui.pages.config;

import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.webui.export.ExportResultsFragment;
import com.taskadapter.webui.results.ExportResultFormat;
import com.taskadapter.webui.results.ExportResultsLayout;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ResultsPanel extends VerticalLayout {

    private final Preservices services;

    public ResultsPanel(Preservices services, ConfigId configId) {
        this.services = services;
        setWidth("1000px");
        showResultsList(configId);
    }

    private void showResultsList(ConfigId configId) {
        var resultsList = new ExportResultsLayout(result -> {
            showSingleResult(result);
            return null;
        });
        var results = services.exportResultStorage.getSaveResults(configId);
        resultsList.showResults(results);
        removeAll();
        add(resultsList);
    }

    private void showSingleResult(ExportResultFormat result) {
        var fragment = new ExportResultsFragment(
                services.settingsManager.isTAWorkingOnLocalMachine());
        var ui = fragment.showExportResult(result);
        removeAll();
        add(ui);
    }
}
