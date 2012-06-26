package com.taskadapter.core;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.common.DataConnectorUtil;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;
import static com.taskadapter.license.LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;

public class SyncRunner {
    private final Logger logger = LoggerFactory.getLogger(SyncRunner.class);

    // TODO: refactor!!!
    private Connector<?> connectorFrom;
    
    /**
     * Target connector.
     */
    private Connector<?> connectorTo;
    
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

    // TODO this is not used now because we ignore checkboxes shown for tasks in the confirmation dialog

    /**
     * Should be called after the confirmation dialog.
     *
     * @param tasks the confirmed tasks
     */
    public void setTasks(List<GTask> tasks) {
        this.tasks = tasks;
    }

    public SyncResult save(ProgressMonitor monitor) throws RemoteIdUpdateFailedException {
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

        SyncResult result;
        try {
            result = connectorTo.saveData(treeToSave, monitor);
        } catch (ConnectorException e) {
            result = new SyncResult();
            result.addGeneralError(e);
        }

        if (monitor != null) {
            monitor.done();
        }

        // tasks will have new Remote IDs after they were created in
        // the target systems.

        ConnectorConfig configFrom = connectorFrom.getConfig();
        if (configFrom.getFieldMappings().isFieldSelected(FIELD.REMOTE_ID)) {
            try {
                connectorFrom.updateRemoteIDs(configFrom,
                        result, null);
            } catch (ConnectorException e) {
                throw new RemoteIdUpdateFailedException(e);
            }
        }

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
    public void setDestination(Connector<?> destination) {
        this.connectorTo = destination;
    }

    public void setConnectorFrom(Connector<?> connectorFrom) {
        this.connectorFrom = connectorFrom;
    }
}
