package com.taskadapter.connector.jira;

import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.model.GTask;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JiraConnectorTest {
    @Test
    public void testLoadTaskByKey() throws Exception {
        JiraConnector connector = new JiraConnector(new JiraTestData().createTestConfig());
        GTask task = new GTask();
        String summary = "load by key";
        task.setSummary(summary);
        task.setType("Bug");
        String key = TestUtils.save(connector, task);
        GTask loadedTask = connector.loadTaskByKey(key);
        assertEquals(summary, loadedTask.getSummary());
    }
}
