package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;

import java.util.Collections;
import java.util.List;

class TodoUtil {
    static GTask create(BasecampConfig config, ObjectAPIFactory factory, GTask task) throws ConnectorException {
        final BasecampConnector connector = new BasecampConnector(config, factory);
        final TaskSaveResult res = connector.saveData(
                Collections.singletonList(task),
                ProgressMonitorUtils.getDummyMonitor(), getAllMappings());
        String remoteKey = res.getRemoteKey(task.getId());
        return connector.loadTaskByKey(remoteKey, getAllMappings());
    }

    static List<GTask> load(BasecampConfig config, ObjectAPIFactory factory) throws ConnectorException {
        BasecampConnector connector = new BasecampConnector(config, factory);
        return connector.loadData(getAllMappings(), ProgressMonitorUtils.getDummyMonitor());
    }

    static Mappings getAllMappings() {
        final Mappings allMappings = new Mappings();
        allMappings.setMapping(GTaskDescriptor.FIELD.SUMMARY, true, "content", "default summary");
        allMappings.setMapping(GTaskDescriptor.FIELD.DONE_RATIO, true, "done_ratio", "default done ratio");
        allMappings.setMapping(GTaskDescriptor.FIELD.DUE_DATE, true, "due_date", "default due date");
        allMappings.setMapping(GTaskDescriptor.FIELD.ASSIGNEE, true, "assignee", "default assignee");
        return allMappings;
    }

    static GTask buildTask(String summary) {
        GTask task = new GTask();
        task.setSummary(summary);
        return task;
    }
}
