package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.taskadapter.model.GTaskUtils.ID_COMPARATOR;
import scala.collection.JavaConverters;

public class Updater {

    private List<GTask> existingTasks;
    private List<GTask> tasksInExternalSystem;
    private NewConnector sourceConnector;
    private Iterable<FieldRow<?>> rows;
    private NewConnector remoteConnector;
    private ProgressMonitor monitor;
    private String sourceLocationName;

    public Updater(NewConnector sourceConnector, Iterable<FieldRow<?>> rows,
            NewConnector targetConnector,
            String sourceLocationName) {
        super();
        this.sourceConnector = sourceConnector;
        this.rows = rows;
        this.remoteConnector = targetConnector;
        this.sourceLocationName  = sourceLocationName;
    }

    public void loadTasks() throws ConnectorException {
        List<GTask> gTasks = sourceConnector.loadData();
        Collections.sort(gTasks, ID_COMPARATOR);
        this.existingTasks = gTasks;
    }

    public void loadExternalTasks() throws ConnectorException {
        this.tasksInExternalSystem = new ArrayList<>(existingTasks.size());
        if (monitor != null) {
            monitor.beginTask("Loading " + existingTasks.size()
                    + " tasks from " + sourceLocationName,
                    existingTasks.size());
        }
        for (GTask gTask : existingTasks) {
            if (gTask.getSourceSystemId() != null) {
                GTask task = remoteConnector.loadTaskByKey(gTask.getSourceSystemId(), rows);
                task.setSourceSystemId(gTask.getSourceSystemId());
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
        ((FileBasedConnector) sourceConnector).updateTasksByRemoteIds(tasksInExternalSystem, rows);
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
