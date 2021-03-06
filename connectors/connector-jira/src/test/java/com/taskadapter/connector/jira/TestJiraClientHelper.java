package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.taskadapter.connector.definition.TaskId;
import io.atlassian.util.concurrent.Promise;

import java.util.Arrays;
import java.util.Optional;

public class TestJiraClientHelper {

    public static Iterable<Issue> findIssuesBySummary(JiraRestClient client, String summary) {
        String jql = "summary~\"" + summary + "\"";
        return JiraClientHelper.findIssues(client, jql);
    }

    public static void deleteTasks(JiraRestClient client, TaskId... ids) {
        Arrays.asList(ids).stream().forEach(taskId -> {
                    Promise<Void> promise = client.getIssueClient().deleteIssue(taskId.getKey(), true);
                    promise.claim();
                }
        );
    }

    public static void checkCustomFieldExists(JiraRestClient client, String customFieldName) {
        var resolver = JiraClientHelper.loadCustomFields(client);
        Optional<JiraFieldDefinition> id = resolver.getId(customFieldName);
        if (id.isEmpty()) {
            throw new RuntimeException("custom field with name " + customFieldName + "is not found on the Jira server");
        }
    }

}
