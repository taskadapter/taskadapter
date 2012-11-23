package com.taskadapter.webui.export;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.Updater;
import com.taskadapter.model.GTask;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.MonitorWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

public class UpdateFilePage extends ActionPage {

    private Updater updater;

    public UpdateFilePage(UISyncConfig config) {
        super(config);
    }

    @Override
    protected void saveData(List<GTask> tasks) throws ConnectorException {
        updater.setConfirmedTasks(tasks);

        MonitorWrapper wrapper = new MonitorWrapper(saveProgress);
        updater.setMonitor(wrapper);
        updater.loadExternalTasks();
        updater.saveFile();
    }

    @Override
    protected String getInitialText() {
        return MESSAGES.format("updatePage.initialText", config.getConnector2().getDestinationLocation());
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "update_file";
    }

    @Override
    public String getNoDataLoadedText() {
        return MESSAGES.format("updatePage.noTasksWithRemoteIds", config.getConnector2().getSourceLocation());
    }

    @Override
    protected VerticalLayout getDoneInfoPanel() {
        VerticalLayout donePanel = new VerticalLayout();
        String text = MESSAGES.format("updatePage.result", updater.getNumberOfUpdatedTasks(),
                config.getConnector2().getDestinationLocation(), config.getConnector1().getSourceLocation());
        donePanel.addComponent(new Label(text));

        return donePanel;
    }

    @Override
    public void loadData() throws ConnectorException {
        MonitorWrapper wrapper = new MonitorWrapper(loadProgress);
        Connector<?> sourceConnector = config.getConnector1().createConnectorInstance();
        Connector<?> destinationConnector = config.getConnector2().createConnectorInstance();
        updater = new Updater(destinationConnector,
                config.generateTargetMappings(), sourceConnector,
                config.generateSourceMappings(), config.getConnector1()
                .getDestinationLocation());
        updater.loadTasksFromFile(wrapper);
        updater.removeTasksWithoutRemoteIds();
        loadedTasks = updater.getExistingTasks();
    }

}
