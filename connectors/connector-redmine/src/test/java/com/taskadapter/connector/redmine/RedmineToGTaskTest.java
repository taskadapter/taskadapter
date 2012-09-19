package com.taskadapter.connector.redmine;

import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.bean.Issue;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RedmineToGTaskTest {

    private RedmineConfig config;
    private RedmineToGTask toGTask;

    @Before
    public void beforeEachTest() {
        this.config = RedmineTestConfig.getRedmineTestConfig();
        toGTask = new RedmineToGTask(config);
    }

    @Test
    public void summaryIsConvertedByDefault() {
        Issue redmineIssue = new Issue();
        redmineIssue.setSubject("text 1");
        GTask task = toGTask.convertToGenericTask(redmineIssue);
        assertEquals("text 1", task.getSummary());
    }
}
