package com.taskadapter.core;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;
import static com.taskadapter.license.LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.model.GTask;

/**
 * Task loader. Implements strategies to load a list of tasks.
 */
public final class TaskLoader {
    
    private TaskLoader() {
    }
    
    private static final Logger logger = LoggerFactory.getLogger(TaskLoader.class);

    public static List<GTask> loadTasks(LicenseManager licenseManager,
            Connector<?> connectorFrom, Mappings sourceMappings,
            ProgressMonitor monitor) throws ConnectorException {
        if (monitor == null) {
            monitor = ProgressMonitorUtils.getDummyMonitor();
        }
        
        monitor.beginTask("Loading data from "
                + connectorFrom.getConfig().getLabel(), 100);
        List<GTask> flatTasksList = ConnectorUtils.loadDataOrderedById(connectorFrom, sourceMappings, monitor);
        flatTasksList = applyTrialIfNeeded(licenseManager, flatTasksList);

        final List<GTask> tasks = TreeUtils.buildTreeFromFlatList(flatTasksList);
        monitor.done();
        
        return tasks;

    }
    
    private static List<GTask> applyTrialIfNeeded(LicenseManager licenseManager, List<GTask> flatTasksList) {
        if (licenseManager.isSomeValidLicenseInstalled()) {
            return flatTasksList;

        } else {
            logger.info(TRIAL_MESSAGE);
            int tasksToLeave = Math.min(TRIAL_TASKS_NUMBER_LIMIT, flatTasksList.size());

            return flatTasksList.subList(0, tasksToLeave);
        }
    }

}
