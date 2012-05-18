package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.core.Updater;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class UpdateFilePage extends ActionPage {

    private final Updater updater;

    public UpdateFilePage(Connector connectorFrom, Connector connectorTo, TAFile taFile) {
        super(connectorFrom, connectorTo, taFile);
        updater = new Updater(connectorTo, connectorFrom);
    }

    @Override
    protected void saveData() {
        updater.setConfirmedTasks(loadedTasks);

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
    public void loadData() {
        MonitorWrapper wrapper = new MonitorWrapper(loadProgress);
        updater.loadTasksFromFile(wrapper);
        updater.removeTasksWithoutRemoteIds();
        loadedTasks = updater.getExistingTasks();
    }

}
