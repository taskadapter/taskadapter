package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.util.concurrent.Promise;

public final class TestJiraClientHelper {

    static Iterable<Issue> findIssuesBySummary(JiraRestClient client, String summary) {
        final  String jql = "summary~\"" + summary + "\"";
        return JiraClientHelper.findIssues(client, jql);
    }

    public static void deleteTasks(JiraRestClient client, String... keys) {
        for (String key : keys) {
            final Promise<Void> promise = client.getIssueClient().deleteIssue(key, true);
            promise.claim();
        }
    }
}
