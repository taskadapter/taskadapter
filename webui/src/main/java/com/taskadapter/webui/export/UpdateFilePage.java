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

    private final Updater updater;

    public UpdateFilePage(UISyncConfig config) {
        super(config);
        Connector<?> sourceConnector = config.getConnector1().createConnectorInstance();
        Connector<?> destinationConnector = config.getConnector2().createConnectorInstance();
        /* 
         * TODO !!! Next line will cause TA to ignore all mapping changes prior
         * to exporting into MSP 
         */
        updater = new Updater(destinationConnector,
                config.generateTargetMappings(), sourceConnector,
                config.generateSourceMappings(), config.getConnector1()
                        .getDestinationLocation());
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
                config.getConnector2().getDestinationLocation() +
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
                + config.getConnector2().getSourceLocation()
                + "\ndoes not have any tasks previously exported to (or loaded from) another system "
                + "\nusing \"Save Remote IDs\" option.";

    }

    @Override
    protected VerticalLayout getDoneInfoPanel() {
        VerticalLayout donePanel = new VerticalLayout();
        donePanel.addComponent(new Label(updater.getNumberOfUpdatedTasks()
                + " tasks were updated in file "
                + config.getConnector2().getDestinationLocation()
                + " with the data from "
                + config.getConnector1().getSourceLocation()));

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
