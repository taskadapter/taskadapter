package com.taskadapter.connector.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;

public abstract class AbstractTaskSaver<T extends ConnectorConfig> implements
        TaskSaver<T> {

    protected final SyncResult syncResult = new SyncResult();

    private final List<GTask> totalTaskList = new ArrayList<GTask>();

    private boolean shouldStop;

    protected final T config;

    private ProgressMonitor monitor;

    protected List<GRelation> relations = new ArrayList<GRelation>();

    public AbstractTaskSaver(T config) {
        super();
        this.config = config;
    }

    abstract protected Object convertToNativeTask(GTask task);

    abstract protected GTask createTask(Object nativeTask);

    abstract protected void updateTask(String taskId, Object nativeTask);

    /**
     * the default implementation does nothing.
     */
    @Override
    public void beforeSave() {
        // nothing here
    }

    @Override
    public SyncResult saveData(List<GTask> tasks, ProgressMonitor monitor) {
        this.monitor = monitor;
        this.shouldStop = false;

        try {
            beforeSave();
            save(null, tasks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return syncResult;
    }

    /**
     * this method will go through children itself.
     */
    protected SyncResult save(String parentIssueKey, List<GTask> tasks) {
        Iterator<GTask> it = tasks.iterator();
        while (it.hasNext() && !shouldStop()) {
            GTask task = it.next();
            totalTaskList.add(task);

            String newTaskKey = null;
            try {
                if (parentIssueKey != null) {
                    task.setParentKey(parentIssueKey);
                }
                Object nativeIssueToCreateOrUpdate = convertToNativeTask(task);
                newTaskKey = submitTask(task, nativeIssueToCreateOrUpdate);
            } catch (Exception e) {
                syncResult.addError(new TaskError(task, Arrays.asList(e
                        .getMessage())));
            }
            reportProgress();

            if (!task.getChildren().isEmpty()) {
                save(newTaskKey, task.getChildren());
            }
        }

        if (parentIssueKey == null) {
            if (config.getSaveIssueRelations()) {
                relations.clear();
                buildNewRelations(totalTaskList);
                saveRelations(relations);
            }
        }

        return syncResult;
    }

    private void reportProgress() {
        if (monitor != null) {
            monitor.worked(1);
        }
    }

    protected void buildNewRelations(List<GTask> tasks) {
        List<GRelation> newRelations = new ArrayList<GRelation>();
        for (GTask task : tasks) {
            String newSourceTaskKey = syncResult.getRemoteKey(task.getId());
            for (GRelation oldRelation : task.getRelations()) {
                // XXX get rid of the conversion, it won't work with Jira,
                // which has String Keys like "TEST-12"
                Integer relatedTaskId = Integer.parseInt(oldRelation.getRelatedTaskKey());
                String newRelatedKey = syncResult.getRemoteKey(relatedTaskId);
                // #25443 Export from MSP fails when newRelatedKey is null (which is a valid case in MSP)
                if (newSourceTaskKey != null && newRelatedKey != null) {
                    relations.add(new GRelation(newSourceTaskKey, newRelatedKey, oldRelation.getType()));
                }
            }
            if (!task.getChildren().isEmpty()) {
                buildNewRelations(task.getChildren());
            }
        }
    }

    abstract protected void saveRelations(List<GRelation> relations);

    /**
     * @return the newly created task's KEY
     */
    protected String submitTask(GTask task, Object nativeTask) {
        String newTaskKey;
        if (task.getRemoteId() == null) {
            GTask newTask = createTask(nativeTask);

            // Need this to be passed as the parentIssueId to the recursive call below
            newTaskKey = newTask.getKey();
            syncResult.addCreatedTask(task.getId(), newTaskKey);
        } else {
            newTaskKey = task.getRemoteId();
            updateTask(newTaskKey, nativeTask);
            syncResult.addUpdatedTask(task.getId(), newTaskKey);
        }
        return newTaskKey;
    }

    protected synchronized boolean shouldStop() {
        return this.shouldStop;
    }

    public synchronized void stopSave() {
        this.shouldStop = true;
    }

    public T getConfig() {
        return config;
    }

    /**
     * The default implementation returns TRUE.
     */
    @Override
    public boolean isSaveStoppable() {
        return true;
    }
}
