package com.taskadapter.webui;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.model.GTask;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.webui.action.ConfirmExportPage;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public abstract class ActionPage extends Page {
    protected final VerticalLayout mainPanel;
    protected final Connector connectorFrom;
    protected final Connector connectorTo;
    private final TAFile taFile;

    protected ProgressIndicator loadProgress = new ProgressIndicator();
    protected ProgressIndicator saveProgress = new ProgressIndicator();
    protected List<GTask> loadedTasks;
    private ConfirmExportPage confirmExportPage;

    public ActionPage(Connector connectorFrom, Connector connectorTo, TAFile file) {
        this.connectorFrom = connectorFrom;
        this.connectorTo = connectorTo;
        this.taFile = file;
        mainPanel = new VerticalLayout();
        mainPanel.setSpacing(true);
        mainPanel.setMargin(true);

        buildInitialPage();
    }

    protected abstract void saveData();

    protected abstract void loadData();

    private void buildInitialPage() {
        Label label = createLabel(getInitialText());
        label.setContentMode(Label.CONTENT_XHTML);
        mainPanel.addComponent(label);

        Button goButton = new Button("Go");
        goButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                synchronized (navigator.getApplication()) {
                    buildLoadingPage();
                }
                new LoadWorker().start();
            }
        });
        mainPanel.addComponent(goButton);
    }

    protected abstract String getInitialText();

    protected abstract String getNoDataLoadedText();

    protected abstract VerticalLayout getDoneInfoPanel();

    protected void buildLoadingPage() {
        mainPanel.removeAllComponents();
        loadProgress = new ProgressIndicator();
        loadProgress.setIndeterminate(true);
        loadProgress.setPollingInterval(200);
        mainPanel.addComponent(loadProgress);
        String labelText = "loading data from " + connectorFrom.getConfig().getSourceLocation() + " (" + connectorFrom.getDescriptor().getLabel() + ")";
        mainPanel.addComponent(createLabel(labelText));
    }

    protected void showAfterDataLoaded() {
        loadProgress.setEnabled(false);
        if (loadedTasks == null || loadedTasks.isEmpty()) {
            mainPanel.addComponent(createLabel(getNoDataLoadedText()));
            mainPanel.addComponent(createBackButton("Back"));
        } else {
            buildConfirmationUI();
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        return label;
    }

    private class LoadWorker extends Thread {
        @Override
        public void run() {
            loadData();
            // must synchronize changes over application
            synchronized (navigator.getApplication()) {
                showAfterDataLoaded();
            }
        }
    }

    private class SaveWorker extends Thread {
        @Override
        public void run() {
            saveData();
            // must synchronize changes over application
            synchronized (navigator.getApplication()) {
                showAfterExport();
            }
        }
    }

    private void showAfterExport() {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(getDoneInfoPanel());

        Button button = new Button("Open home page");
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.show(Navigator.HOME);
            }
        });

        mainPanel.addComponent(button);
    }

    private void saveConfigIfChanged() {
        if (confirmExportPage.needToSaveConfig()) {
            connectorTo.getConfig().setFieldsMapping(confirmExportPage.getConnectorToFieldMappings());

            taFile.setConnectorDataHolder1(new ConnectorDataHolder(taFile.getConnectorDataHolder1().getType(), connectorFrom.getConfig()));
            taFile.setConnectorDataHolder2(new ConnectorDataHolder(taFile.getConnectorDataHolder2().getType(), connectorTo.getConfig()));

            String userLoginName = services.getAuthenticator().getUserName();
            services.getConfigStorage().saveConfig(userLoginName, taFile);
        }
    }

    protected void buildConfirmationUI() {
        confirmExportPage = new ConfirmExportPage(loadedTasks, connectorTo, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveConfigIfChanged();
                startSaveTasksProcess();
            }
        });
        mainPanel.addComponent(confirmExportPage);
        mainPanel.setExpandRatio(confirmExportPage, 1f); // use all available space
    }

    protected void startSaveTasksProcess() {
        loadedTasks = confirmExportPage.getSelectedRootLevelTasks();

        if (!loadedTasks.isEmpty()) {
            saveProgress = new ProgressIndicator();
            saveProgress.setIndeterminate(false);
            saveProgress.setEnabled(true);
            saveProgress.setCaption("Saving to " + connectorTo.getConfig().getTargetLocation());
            mainPanel.removeAllComponents();
            mainPanel.addComponent(saveProgress);

            new SaveWorker().start();
        }
    }

    @Override
    public Component getUI() {
        return mainPanel;
    }
}
