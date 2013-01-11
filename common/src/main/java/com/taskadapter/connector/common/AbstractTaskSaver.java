package com.taskadapter.connector.common;

import java.util.List;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

public abstract class AbstractTaskSaver<T extends ConnectorConfig, N> {

    private final ProgressMonitor monitor;
    
    public final TaskSaveResultBuilder result = new TaskSaveResultBuilder();
    protected final T config;

    protected AbstractTaskSaver(T config, ProgressMonitor progressMonitor) {
        this.config = config;
        this.monitor = progressMonitor == null ? ProgressMonitorUtils
                .getDummyMonitor() : progressMonitor;
    }

    abstract protected N convertToNativeTask(GTask task) throws ConnectorException;

    abstract protected GTask createTask(N nativeTask) throws ConnectorException;

    abstract protected void updateTask(String taskId, N nativeTask) throws ConnectorException;

    public TaskSaveResult saveData(List<GTask> tasks) throws ConnectorException {
        saveTasks(null, tasks);
        return result.getResult();
    }

    /**
     * this method will go through children itself.
     */
    protected void saveTasks(String parentIssueKey, List<GTask> tasks) throws ConnectorException {
        for (GTask task : tasks) {

            String newTaskKey = null;
            try {
                if (parentIssueKey != null) {
                    task.setParentKey(parentIssueKey);
                }
                N nativeIssueToCreateOrUpdate = convertToNativeTask(task);
                newTaskKey = submitTask(task, nativeIssueToCreateOrUpdate);
            } catch (ConnectorException e) {
                result.addTaskError(task, e);
            } catch (Throwable t) {
                result.addTaskError(task, t);
            }
            reportProgress();

            saveTasks(newTaskKey, task.getChildren());
        }
    }

    private void reportProgress() {
        monitor.worked(1);
    }

    /**
     * @return the newly created task's KEY
     */
    protected String submitTask(GTask task, N nativeTask) throws ConnectorException {
        String newTaskKey;
        if (task.getRemoteId() == null) {
            GTask newTask = createTask(nativeTask);

            // Need this to be passed as the parentIssueId to the recursive call below
            newTaskKey = newTask.getKey();
            result.addCreatedTask(task.getId(), newTaskKey);
        } else {
            newTaskKey = task.getRemoteId();
            updateTask(newTaskKey, nativeTask);
            result.addUpdatedTask(task.getId(), newTaskKey);
        }
        return newTaskKey;
    }
}
