package com.taskadapter.webui;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.core.Updater;

/**
 * @author Alexey Skorokhodov
 */
public class UpdateFilePage extends ActionPage {

    //private List<GTask> existingTasksInFile;
    private final Updater updater;
//    private final String mspLocation;

    public UpdateFilePage(Connector connectorFrom, Connector connectorTo) {
        super(connectorFrom, connectorTo);
        updater = new Updater(connectorTo, connectorFrom);
//        mspLocation = connectorTo.getPartialConfig().getDataLocation();
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
        String message = "Click \"Go\" to load file<BR><i>" +
                connectorTo.getConfig().getTargetLocation() +
                "</i><BR> and check which tasks have 'remote ids' associated with them." +
                "<br>You can select which of those tasks to update with the data from the external system." +
                "<br>No other tasks will be updated or created.";
        return message;
    }

    @Override
    public String getNavigationPanelTitle() {
        return "Update the file";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getNoDataLoadedText() {
        return "The current MSP XML file \n"
                + connectorTo.getConfig().getSourceLocation()
                + "\ndoes not have any tasks previously exported to (or loaded from) another system "
                + "\nusing \"Save Remote IDs\" option.";

    }

    @Override
    public void loadData() {
        MonitorWrapper wrapper = new MonitorWrapper(loadProgress);
        updater.loadTasksFromFile(wrapper);
        updater.removeTasksWithoutRemoteIds();
        loadedTasks = updater.getExistingTasks();
    }

}
