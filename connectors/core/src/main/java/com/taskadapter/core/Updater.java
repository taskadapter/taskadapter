package com.taskadapter.core;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

public class Updater {

    private List<GTask> existingTasks;
    private List<GTask> tasksInExternalSystem;
    private Connector fileConnector;
    private Connector remoteConnector;
    private ProgressMonitor monitor;

    public Updater(Connector fileConnector, Connector remoteConnector) {
        super();
        this.fileConnector = fileConnector;
        this.remoteConnector = remoteConnector;
    }

    // TODO !!! I commented this method out because it was unused. why aren't we using it?
//    public void start() throws ConnectorException {
//        loadTasksFromFile(ProgressMonitorUtils.getDummyMonitor());
//        removeTasksWithoutRemoteIds();
//        loadExternalTasks();
//        saveFile();
//    }

    public void loadTasksFromFile(Mappings sourceMappings, ProgressMonitor monitor) throws ConnectorException {
        this.existingTasks = ConnectorUtils.loadDataOrderedById(fileConnector, sourceMappings, monitor);
    }

    public void loadExternalTasks(Mappings sourceMappings) throws ConnectorException {
        this.tasksInExternalSystem = new ArrayList<GTask>(existingTasks.size());
        if (monitor != null) {
            monitor.beginTask("Loading " + existingTasks.size()
                    + " tasks from " + getRemoteSystemURI(),
                    existingTasks.size());
        }
        for (GTask gTask : existingTasks) {
            if (gTask.getRemoteId() != null) {
                GTask task = remoteConnector.loadTaskByKey(gTask.getRemoteId(), sourceMappings);
                task.setRemoteId(gTask.getRemoteId());
                tasksInExternalSystem.add(task);
            }
            if (monitor != null) {
                monitor.worked(1);
            }
        }
        if (monitor != null) {
            monitor.done();
        }

    }

    public void saveFile(Mappings mappings) throws ConnectorException {
        // TODO remove the casting!
        ((FileBasedConnector) fileConnector).updateTasksByRemoteIds(tasksInExternalSystem, mappings);
    }

    public int getNumberOfUpdatedTasks() {
        return tasksInExternalSystem.size();
    }

    public List<GTask> getExistingTasks() {
        return existingTasks;
    }

    public void setConfirmedTasks(List<GTask> tasks) {
        this.existingTasks = tasks;
    }

    public void removeTasksWithoutRemoteIds() {
        this.existingTasks = TreeUtils.cloneTreeSkipEmptyRemoteIds(existingTasks);
    }

    public String getFilePath() {
        return fileConnector.getConfig().getTargetLocation();
    }

    public String getRemoteSystemURI() {
        return remoteConnector.getConfig().getSourceLocation();
    }

    public void setMonitor(ProgressMonitor monitor) {
        this.monitor = monitor;
    }
}
