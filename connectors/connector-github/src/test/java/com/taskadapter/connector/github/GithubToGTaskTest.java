package com.taskadapter.connector.github;

import com.taskadapter.connector.testlib.TestDataLoader;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GithubToGTaskTest {
    @Test
    public void testToGtask() throws Exception {
        GithubToGTask toGTask = new GithubToGTask();
        Issue issue = (Issue) TestDataLoader.load("issue.json", Issue.class);
        GTask task = toGTask.toGtask(issue);
        assertEquals("task 1", task.getSummary());
    }
}
