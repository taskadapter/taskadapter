package com.taskadapter.connector.testlib;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;

import java.util.Arrays;
import java.util.List;

public class TestSaver {
    private Connector<?> connector;
    private Mappings mappings;

    public TestSaver(Connector<?> connector, Mappings mappings) {
        this.connector = connector;
        this.mappings = mappings;
    }

    public TestSaver selectField(GTaskDescriptor.FIELD field) {
        mappings.selectField(field);
        return this;
    }

    public TestSaver unselectField(GTaskDescriptor.FIELD field) {
        mappings.getSelectedFields().remove(field);
        return this;
    }

    public GTask saveAndLoad(GTask task) throws ConnectorException {
        connector.saveData(Arrays.asList(task), null, mappings);
		List<GTask> loadedTasks = ConnectorUtils.loadDataOrderedById(connector, mappings);
        return TestUtils.findTaskBySummary(loadedTasks, task.getSummary());
    }
}
