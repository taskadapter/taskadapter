package com.taskadapter.core;

import com.taskadapter.connector.common.DataConnectorUtil;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.ArrayList;
import java.util.List;

public class SyncRunner {
    private Connector connectorFrom;
    private TaskSaver taskSaver;

    private List<GTask> tasks = new ArrayList<GTask>();

    /**
     * @param monitor can be NULL (ignored in this case)
     * @return
     */
    public List<GTask> load(ProgressMonitor monitor) {
        if (monitor != null) {
            monitor.beginTask(
                    "Loading data from " + connectorFrom.getDescriptor().getLabel(),
                    100);
        }
        try {
            List<GTask> flatTasksList = connectorFrom.loadData(monitor);
            flatTasksList = applyTrialIfNeeded(flatTasksList);

            // can be NULL if there was an exception
            if (flatTasksList != null) {
                this.tasks = TreeUtils.buildTreeFromFlatList(flatTasksList);
            }
        } catch (Exception e1) {
            throw new RuntimeException("Can't load data.\n" + e1.getMessage(), e1);
        }
        if (monitor != null) {
            monitor.done();
        }

        return this.tasks;
    }

    // TODO this is not used now because we ignore checkboxes shown for tasks in the confirmation dialog
    /**
     * Should be called after the confirmation dialog.
     *
     * @param tasks the confirmed tasks
     */
    public void setTasks(List<GTask> tasks) {
        this.tasks = tasks;
    }

    public SyncResult save(ProgressMonitor monitor) {
        int totalNumberOfTasks = DataConnectorUtil
                .calculateNumberOfTasks(tasks);
        if (monitor != null) {
            monitor.beginTask(
                    "Saving " + totalNumberOfTasks + " tasks to " + taskSaver.getConfig().getTargetLocation(),
                    totalNumberOfTasks);
        }
        List<GTask> treeToSave;
        if (taskSaver.getConfig().isFieldSelected(FIELD.REMOTE_ID)) {
            List<GTask> clonedTree = TreeUtils.cloneTree(tasks);
            TaskUtil.setRemoteIdField(clonedTree);
            treeToSave = clonedTree;
        } else {
            treeToSave = this.tasks;
        }
        SyncResult result = taskSaver.saveData(treeToSave, monitor);
        if (monitor != null) {
            monitor.done();
        }

        // tasks will have new Remote IDs after they were created in
        // the target systems.

        ConnectorConfig configFrom = connectorFrom.getConfig();
        if (configFrom.isFieldSelected(FIELD.REMOTE_ID)) {
            connectorFrom.updateRemoteIDs(configFrom,
                    result, null);
        }

        return result;

    }

    private List<GTask> applyTrialIfNeeded(List<GTask> flatTasksList) {
        if (!LicenseManager.isTaskAdapterLicenseOK()) {
            System.out.println(LicenseManager.TRIAL_MESSAGE);
            int tasksToLeave = Math.min(LicenseManager.TRIAL_TASKS_NUMBER_LIMIT, flatTasksList.size());
            return flatTasksList.subList(0, tasksToLeave);
        } else {
            return flatTasksList;
        }
    }

    public List<GTask> getTasks() {
        return tasks;
    }

    // TODO add a test to verify "load" can be done without setting taskSaver
    public void setTaskSaver(TaskSaver taskSaver) {
        this.taskSaver = taskSaver;
    }

    public void setConnectorFrom(Connector connectorFrom) {
        this.connectorFrom = connectorFrom;
    }
}
