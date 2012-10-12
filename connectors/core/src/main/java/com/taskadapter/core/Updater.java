package com.taskadapter.core;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.connector.common.ConnectorUtils;
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
    private Connector<?> fileConnector;
    private Mappings fileMappings;
    private Connector<?> remoteConnector;
    private Mappings remoteMappings;
    private ProgressMonitor monitor;
    private String sourceLocationName;

    public Updater(Connector<?> fileConnector, Mappings fileMappings,
            Connector<?> remoteConnector, Mappings remoteMappings,
            String sourceLocationName) {
        super();
        this.fileConnector = fileConnector;
        this.fileMappings = fileMappings;
        this.remoteConnector = remoteConnector;
        this.remoteMappings = remoteMappings;
        this.sourceLocationName  = sourceLocationName;
    }

    public void loadTasksFromFile(ProgressMonitor monitor) throws ConnectorException {
        this.existingTasks = ConnectorUtils.loadDataOrderedById(fileConnector, fileMappings, monitor);
    }

    public void loadExternalTasks() throws ConnectorException {
        this.tasksInExternalSystem = new ArrayList<GTask>(existingTasks.size());
        if (monitor != null) {
            monitor.beginTask("Loading " + existingTasks.size()
                    + " tasks from " + sourceLocationName,
                    existingTasks.size());
        }
        for (GTask gTask : existingTasks) {
            if (gTask.getRemoteId() != null) {
                GTask task = remoteConnector.loadTaskByKey(gTask.getRemoteId(), remoteMappings);
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

    public void saveFile() throws ConnectorException {
        // TODO remove the casting!
        ((FileBasedConnector) fileConnector).updateTasksByRemoteIds(tasksInExternalSystem, fileMappings);
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

    public void setMonitor(ProgressMonitor monitor) {
        this.monitor = monitor;
    }
}
