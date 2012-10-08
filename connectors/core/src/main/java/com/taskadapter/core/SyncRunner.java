package com.taskadapter.core;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.common.DataConnectorUtil;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.*;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;
import static com.taskadapter.license.LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;

public class SyncRunner {
    private final Logger logger = LoggerFactory.getLogger(SyncRunner.class);

    // TODO: refactor!!!
    private Connector<?> connectorFrom;
    private String sourceConnectorId;

    /**
     * Target connector.
     */
    private Connector<?> connectorTo;
    private String destinationConnectorId;

    private LicenseManager licenseManager;

    private List<GTask> tasks = new ArrayList<GTask>();

    public SyncRunner(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    /**
     * @param monitor can be NULL (ignored in this case)
     * @throws ConnectorException
     */
    public List<GTask> load(ProgressMonitor monitor) throws ConnectorException {
        if (monitor != null) {
            monitor.beginTask(
                    "Loading data from " + connectorFrom.getConfig().getLabel(),
                    100);
        }
        List<GTask> flatTasksList = ConnectorUtils.loadDataOrderedById(
                connectorFrom, monitor);
        flatTasksList = applyTrialIfNeeded(flatTasksList);

        // can be NULL if there was an exception
        if (flatTasksList != null) {
            this.tasks = TreeUtils.buildTreeFromFlatList(flatTasksList);
        }

        if (monitor != null) {
            monitor.done();
        }

        return this.tasks;
    }

    /**
     * must be called after the confirmation dialog.
     *
     * @param tasks the confirmed tasks
     */
    public void setTasks(List<GTask> tasks) {
        this.tasks = tasks;
    }

    public SyncResult<TaskSaveResult, TaskErrors<ConnectorError<Throwable>>> save(ProgressMonitor monitor, Mappings mappings) throws ConnectorException {
        int totalNumberOfTasks = DataConnectorUtil
                .calculateNumberOfTasks(tasks);
        if (monitor != null) {
            monitor.beginTask("Saving " + totalNumberOfTasks + " tasks to "
                    + connectorTo.getConfig().getTargetLocation(),
                    totalNumberOfTasks);
        }
        List<GTask> treeToSave;
        if (connectorTo.getConfig().getFieldMappings().isFieldSelected(FIELD.REMOTE_ID)) {
            List<GTask> clonedTree = TreeUtils.cloneTree(tasks);
            TaskUtil.setRemoteIdField(clonedTree);
            treeToSave = clonedTree;
        } else {
            treeToSave = this.tasks;
        }

        TaskSaveResult saveResult;
        final TaskErrorsBuilder<ConnectorError<Throwable>> errors = new TaskErrorsBuilder<ConnectorError<Throwable>>();

        try {
            final SyncResult<TaskSaveResult, TaskErrors<Throwable>> saveTaskResult = connectorTo
                    .saveData(treeToSave, monitor, mappings);
            saveResult = saveTaskResult.getResult();
            errors.addErrors(connectorizeTasks(saveTaskResult.getErrors()
                    .getErrors(), destinationConnectorId));
            errors.addGeneralErrors(connectorize(saveTaskResult.getErrors()
                    .getGeneralErrors(), destinationConnectorId));
        } catch (ConnectorException e) {
            saveResult = null;
            errors.addGeneralError(new ConnectorError<Throwable>(e, sourceConnectorId));
        }

        if (monitor != null) {
            monitor.done();
        }

        // tasks will have new Remote IDs after they were created in
        // the target systems.

        ConnectorConfig configFrom = connectorFrom.getConfig();
        if (saveResult != null
                && configFrom.getFieldMappings().isFieldSelected(
                FIELD.REMOTE_ID) && (saveResult.getUpdatedTasksNumber() + saveResult.getCreatedTasksNumber()) > 0) {
            try {
                connectorFrom.updateRemoteIDs(configFrom,
                        saveResult.getIdToRemoteKeyMap(), null);
            } catch (ConnectorException e) {
                errors.addGeneralError(new ConnectorError<Throwable>(e,
                        destinationConnectorId));
            }
        }

        return new SyncResult<TaskSaveResult, TaskErrors<ConnectorError<Throwable>>>(
                saveResult, errors.getResult());

    }

    private static <T> List<TaskError<ConnectorError<T>>> connectorizeTasks(Collection<TaskError<T>> errors, String connectorId) {
        final List<TaskError<ConnectorError<T>>> result = new ArrayList<TaskError<ConnectorError<T>>>(errors.size());
        for (TaskError<T> error : errors)
            result.add(new TaskError<ConnectorError<T>>(error.getTask(), new ConnectorError<T>(error.getErrors(), connectorId)));
        return result;
    }

    private static <T> List<ConnectorError<T>> connectorize(Collection<T> errors, String connectorId) {
        final List<ConnectorError<T>> result = new ArrayList<ConnectorError<T>>(errors.size());
        for (T error : errors)
            result.add(new ConnectorError<T>(error, connectorId));
        return result;
    }

    private List<GTask> applyTrialIfNeeded(List<GTask> flatTasksList) {
        if (licenseManager.isSomeValidLicenseInstalled()) {
            return flatTasksList;

        } else {
            logger.info(TRIAL_MESSAGE);
            int tasksToLeave = Math.min(TRIAL_TASKS_NUMBER_LIMIT, flatTasksList.size());

            return flatTasksList.subList(0, tasksToLeave);
        }
    }

    public List<GTask> getTasks() {
        return tasks;
    }

    // TODO add a test to verify "load" can be done without setting taskSaver
    // TODO zzzzz delete the first parameter
    public void setDestination(Connector<?> destination, String destinationConnectorId) {
        this.connectorTo = destination;
        this.destinationConnectorId = destinationConnectorId;
    }

    // TODO zzzzz delete the first parameter
    public void setConnectorFrom(Connector<?> connectorFrom, String sourceConnectorId) {
        this.connectorFrom = connectorFrom;
        this.sourceConnectorId = sourceConnectorId;
    }
}
