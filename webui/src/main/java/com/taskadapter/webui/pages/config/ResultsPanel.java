package com.taskadapter.webui.pages.config;

import com.taskadapter.common.ui.ReloadableComponent;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.export.ExportResultsFragment;
import com.taskadapter.webui.results.ExportResultFormat;
import com.taskadapter.webui.results.ExportResultsLayout;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ResultsPanel extends VerticalLayout implements ReloadableComponent {

    private final Preservices services;
    private final ConfigId configId;

    public ResultsPanel(Preservices services, ConfigId configId) {
        this.services = services;
        this.configId = configId;
        setWidth("1050px");
        showResultsList();
    }

    private void showResultsList() {
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
        var okButton = new Button(Page.message("button.ok"),
                e -> showResultsList());
        add(okButton);
    }

    @Override
    public void reload() {
        showResultsList();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
