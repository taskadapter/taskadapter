package com.taskadapter.webui;

import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.core.SyncRunner;
import com.vaadin.ui.Label;

/**
 * @author Alexey Skorokhodov
 */
public class ExportPage extends ActionPage {
    private SyncRunner runner;

    public ExportPage(Connector connectorFrom, Connector connectorTo) {
        super(connectorFrom, connectorTo);
    }

    @Override
    public String getNavigationPanelTitle() {
        return "Export confirmation: from " + connectorFrom.getConfig().getLabel() + " to " + connectorTo.getConfig().getLabel();
    }

    @Override
    protected void loadData() {
        final TaskSaver taskSaver = connectorTo.getDescriptor()
                .getTaskSaver(connectorTo.getConfig());
        runner = new SyncRunner();
        runner.setConnectorFrom(connectorFrom);
        runner.setTaskSaver(taskSaver);
        try {
            this.loadedTasks = runner.load(null);
        } catch (RuntimeException e) {
            mainPanel.addComponent(new Label("Can't load data. " + e.toString()));
            // TODO log properly
            e.printStackTrace();
        }
    }

    @Override
    protected String getInitialText() {
        return "Will load data from " + connectorFrom.getConfig().getSourceLocation() + " (" + connectorFrom.getDescriptor().getLabel() + ")";
    }

    @Override
    protected String getNoDataLoadedText() {
        return "No data was loaded using the given criteria";
    }

    @Override
    protected void saveData() {
        saveProgress.setValue(0);
        runner.setTasks(loadedTasks);
        MonitorWrapper wrapper = new MonitorWrapper(saveProgress);
        runner.save(wrapper);
    }

}
