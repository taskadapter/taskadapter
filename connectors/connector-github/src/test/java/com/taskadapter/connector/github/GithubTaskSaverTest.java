package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.exceptions.NotAuthorizedException;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;
import org.junit.Test;

public class GithubTaskSaverTest {

    @Test(expected = NotAuthorizedException.class)
    public void testCreateTask() throws Exception {
        GithubConfig invalidCredentialsConfig = new GithubConfig();
        invalidCredentialsConfig.getServerInfo().setUserName("invalidUser");
        invalidCredentialsConfig.setProjectKey("invalidRepo");
        invalidCredentialsConfig.getServerInfo().setPassword("invalidpassword");
        GithubTaskSaver saver = new GithubTaskSaver(invalidCredentialsConfig, TestMappingUtils
        .fromFields(GithubSupportedFields.SUPPORTED_FIELDS));
        GTask task = new GTask();
        task.setSummary("api66");
        Issue issue = saver.convertToNativeTask(task);
        GTask savedTask = saver.createTask(issue);
        System.out.println(savedTask);
    }
}
