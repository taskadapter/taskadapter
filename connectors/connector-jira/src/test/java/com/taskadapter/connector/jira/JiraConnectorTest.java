package com.taskadapter.connector.jira;

import com.taskadapter.connector.common.CommonTests;
import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.model.GTask;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JiraConnectorTest {
    @Test
    public void testLoadTaskByKey() throws Exception {
        // TODO !!! fix test

/*
        JiraConnector connector = getConnector();
        GTask task = new GTask();
        String summary = "load by key";
        task.setSummary(summary);
        task.setType("Bug");
        String key = TestUtils.save(connector, task);
        GTask loadedTask = connector.loadTaskByKey(key);
        assertEquals(summary, loadedTask.getSummary());
*/
    }

    @Test
    public void testDefaultDescriptionMapping() throws Exception {
        new CommonTests().descriptionSavedByDefault(getConnector());
    }

    private JiraConnector getConnector() {
        return new JiraConnector(new JiraTestData().createTestConfig());
    }

}
