package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.webui.action.ConfirmExportPage;
import com.vaadin.ui.*;

import java.util.List;

public abstract class ActionPage extends Page {
    protected final VerticalLayout mainPanel;
    protected final Connector connectorFrom;
    protected final Connector connectorTo;
    private final String destinationConnectorId;
    private final TAFile taFile;

    protected ProgressIndicator loadProgress = new ProgressIndicator();
    protected ProgressIndicator saveProgress = new ProgressIndicator();
    protected List<GTask> loadedTasks;
    private ConfirmExportPage confirmExportPage;

    public ActionPage(Connector connectorFrom, Connector connectorTo, String destinationConnectorId, TAFile file) {
        this.connectorFrom = connectorFrom;
        this.connectorTo = connectorTo;
        this.destinationConnectorId = destinationConnectorId;
        this.taFile = file;
        mainPanel = new VerticalLayout();
        mainPanel.setSpacing(true);
        mainPanel.setMargin(true);

        buildInitialPage();
    }

    protected abstract void saveData(List<GTask> tasks) throws ConnectorException;

    protected abstract void loadData() throws ConnectorException;

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
        String labelText = "Loading data from " + connectorFrom.getConfig().getSourceLocation() + " (" + connectorFrom.getConfig().getLabel() + ") ...";
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
        label.setWidth("800px");
        return label;
    }

    private class LoadWorker extends Thread {
        @Override
        public void run() {
            try {
                loadData();
            } catch (ConnectorException e) {
                e.printStackTrace();
                // TODO add "connector error" dialog
            } catch (Throwable t) {
                t.printStackTrace();
                // TODO Show "internal error" dialog
            }
            // must synchronize changes over application
            synchronized (navigator.getApplication()) {
                showAfterDataLoaded();
            }
        }
    }

    private class SaveWorker extends Thread {
        private List<GTask> tasks;

        private SaveWorker(List<GTask> tasks) {
            this.tasks = tasks;
        }

        @Override
        public void run() {
            try {
                saveData(tasks);
            } catch (ConnectorException e) {
                e.printStackTrace();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            // must synchronize changes over application
            synchronized (navigator.getApplication()) {
                showAfterExport();
            }
        }
    }

    private void showAfterExport() {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(getDoneInfoPanel());

        Button button = new Button("Back to home page");
        //button.setStyleName(BaseTheme.BUTTON_LINK);
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
            String userLoginName = services.getAuthenticator().getUserName();
            services.getConfigStorage().saveConfig(userLoginName, taFile);
        }
    }

    protected void buildConfirmationUI() {
        PluginEditorFactory editorFactory = services.getEditorManager().getEditorFactory(destinationConnectorId);
        AvailableFields fieldsSupportedByDestination = editorFactory.getAvailableFields();
        confirmExportPage = new ConfirmExportPage(navigator, loadedTasks, connectorTo.getConfig(),
                fieldsSupportedByDestination, new Button.ClickListener() {
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
        List<GTask> selectedRootLevelTasks = confirmExportPage.getSelectedRootLevelTasks();

        if (!loadedTasks.isEmpty()) {
            saveProgress = new ProgressIndicator();
            saveProgress.setIndeterminate(false);
            saveProgress.setEnabled(true);
            saveProgress.setCaption("Saving to " + connectorTo.getConfig().getTargetLocation());
            mainPanel.removeAllComponents();
            mainPanel.addComponent(saveProgress);

            new SaveWorker(selectedRootLevelTasks).start();
        } else {
            mainPanel.getWindow().showNotification("Please select some tasks first.");
        }
    }

    @Override
    public Component getUI() {
        return mainPanel;
    }
}
