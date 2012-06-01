package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;

import java.util.Arrays;
import java.util.List;

public class TestSaver {
    private Connector<?> connector;

    public TestSaver(Connector<?> connector) {
        this.connector = connector;
    }

    public TestSaver selectField(GTaskDescriptor.FIELD field) {
        connector.getConfig().getFieldMappings().selectField(field);
        return this;
    }

    public TestSaver unselectField(GTaskDescriptor.FIELD field) {
        connector.getConfig().getFieldMappings().deselectField(field);
        return this;
    }

    public GTask saveAndLoad(GTask task) {
        connector.saveData(Arrays.asList(task), null);
		List<GTask> loadedTasks = ConnectorUtils.loadDataOrderedById(connector);
        return TestUtils.findTaskBySummary(loadedTasks, task.getSummary());
    }
}
