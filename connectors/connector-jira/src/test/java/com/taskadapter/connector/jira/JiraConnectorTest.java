package com.taskadapter.connector.jira;

import com.taskadapter.connector.testlib.CommonTests;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.GTask;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JiraConnectorTest {
    @Test
    public void testLoadTaskByKey() throws Exception {
        JiraConnector connector = getConnector();
        GTask task = new GTask();
        String summary = "load by key";
        task.setSummary(summary);
        task.setType("Bug");
        String key = TestUtils.save(connector, task, DefaultJiraMappings.generate());
        GTask loadedTask = connector.loadTaskByKey(key, DefaultJiraMappings.generate());
        assertEquals(summary, loadedTask.getSummary());
    }

    @Test
    public void descriptionSavedByDefault() throws Exception {
        new CommonTests().descriptionSavedByDefault(getConnector(), DefaultJiraMappings.generate());
    }

    private JiraConnector getConnector() {
        return new JiraConnector(new JiraTestData().createTestConfig());
    }
}
