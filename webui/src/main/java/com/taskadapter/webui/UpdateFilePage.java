package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.Updater;
import com.taskadapter.model.GTask;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

public class UpdateFilePage extends ActionPage {

    private final Updater updater;

    public UpdateFilePage(String sourceConnectorId, Connector<?> connectorFrom, Connector<?> connectorTo, String destinationConnectorId, TAFile taFile,
                          Mappings sourceMappings,
                          Mappings destinationMappings) {
        super(sourceConnectorId, connectorFrom, connectorTo, destinationConnectorId, taFile, sourceMappings, destinationMappings);
        updater = new Updater(connectorTo, destinationMappings, connectorFrom, sourceMappings);
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
        return "Click \"Go\" to load file<BR><i>" +
                connectorTo.getConfig().getTargetLocation() +
                "</i><BR> and check which tasks have 'remote ids' associated with them." +
                "<br>You can select which of those tasks to update with the data from the external system." +
                "<br>No other tasks will be updated or created.";
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "update_file";
    }

    @Override
    public String getNoDataLoadedText() {
        return "The current MSP XML file \n"
                + connectorTo.getConfig().getSourceLocation()
                + "\ndoes not have any tasks previously exported to (or loaded from) another system "
                + "\nusing \"Save Remote IDs\" option.";

    }

    @Override
    protected VerticalLayout getDoneInfoPanel() {
        VerticalLayout donePanel = new VerticalLayout();
        donePanel.addComponent(new Label(updater.getNumberOfUpdatedTasks() + " tasks were updated in file "
                + updater.getFilePath() + " with the data from " + updater.getRemoteSystemURI()));

        return donePanel;
    }

    @Override
    public void loadData() throws ConnectorException {
        MonitorWrapper wrapper = new MonitorWrapper(loadProgress);
        updater.loadTasksFromFile(wrapper);
        updater.removeTasksWithoutRemoteIds();
        loadedTasks = updater.getExistingTasks();
    }

}
