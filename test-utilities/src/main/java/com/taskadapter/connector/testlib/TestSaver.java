package com.taskadapter.connector.testlib;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.NewConnector;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;

import java.util.Arrays;
import java.util.List;

public class TestSaver {
    private NewConnector connector;
    private Mappings mappings;

    public TestSaver(NewConnector connector, Mappings mappings) {
        this.connector = connector;
        this.mappings = mappings;
    }

    public TestSaver selectField(String field) {
        mappings.selectField(field);
        return this;
    }

    public TestSaver unselectField(GTaskDescriptor.FIELD field) {
        mappings.deselectField(field);
        return this;
    }

    public GTask saveAndLoad(GTask task) throws ConnectorException {
        connector.saveData(Arrays.asList(task), null, mappings);
		List<GTask> loadedTasks = ConnectorUtils.loadDataOrderedById(connector, mappings);
        return TestUtils.findTaskBySummary(loadedTasks, task.getSummary());
    }
}
