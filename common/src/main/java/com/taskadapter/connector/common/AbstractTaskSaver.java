package com.taskadapter.connector.common;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;

public abstract class AbstractTaskSaver<T extends ConnectorConfig> {

    private final List<GTask> totalTaskList = new ArrayList<GTask>();
    private final ProgressMonitor monitor;
    
    protected final TaskSaveResultBuilder result = new TaskSaveResultBuilder();
    protected final T config;

    protected AbstractTaskSaver(T config, ProgressMonitor progressMonitor) {
        this.config = config;
        this.monitor = progressMonitor == null ? ProgressMonitorUtils
                .getDummyMonitor() : progressMonitor;
    }

    abstract protected Object convertToNativeTask(GTask task) throws ConnectorException;

    abstract protected GTask createTask(Object nativeTask) throws ConnectorException;

    abstract protected void updateTask(String taskId, Object nativeTask) throws ConnectorException;

    public TaskSaveResult saveData(List<GTask> tasks) throws ConnectorException {
        saveTasks(null, tasks);
        
        if (config.getSaveIssueRelations()) {
            List<GRelation> relations = buildNewRelations(totalTaskList);
            saveRelations(relations);
        }

        return result.getResult();
    }

    /**
     * this method will go through children itself.
     */
    protected void saveTasks(String parentIssueKey, List<GTask> tasks) throws ConnectorException {
        for (GTask task : tasks) {
            totalTaskList.add(task);

            String newTaskKey = null;
            try {
                if (parentIssueKey != null) {
                    task.setParentKey(parentIssueKey);
                }
                Object nativeIssueToCreateOrUpdate = convertToNativeTask(task);
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

    protected List<GRelation> buildNewRelations(List<GTask> tasks) {
        List<GRelation> newRelations = new ArrayList<GRelation>();
        for (GTask task : tasks) {
            String newSourceTaskKey = result.getRemoteKey(task.getId());
            for (GRelation oldRelation : task.getRelations()) {
                // TODO get rid of the conversion, it won't work with Jira,
                // which has String Keys like "TEST-12"
                Integer relatedTaskId = Integer.parseInt(oldRelation.getRelatedTaskKey());
                String newRelatedKey = result.getRemoteKey(relatedTaskId);
                // #25443 Export from MSP fails when newRelatedKey is null (which is a valid case in MSP)
                if (newSourceTaskKey != null && newRelatedKey != null) {
                    newRelations.add(new GRelation(newSourceTaskKey, newRelatedKey, oldRelation.getType()));
                }
            }
        }
        return newRelations;
    }

    abstract protected void saveRelations(List<GRelation> relations) throws ConnectorException;

    /**
     * @return the newly created task's KEY
     */
    protected String submitTask(GTask task, Object nativeTask) throws ConnectorException {
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
