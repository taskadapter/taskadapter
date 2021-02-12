package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import scala.collection.JavaConverters;

import java.util.List;

public class JiraTaskLoader {

    private final JiraRestClient client;
    private final JiraToGTask jiraToGTask;

    public JiraTaskLoader(JiraRestClient client, Priorities priorities) {
        this.client = client;
        jiraToGTask = new JiraToGTask(priorities);
    }

    List<GTask> loadTasks(JiraConfig config) throws ConnectorException {
        try {
            var resolver = JiraClientHelper.loadCustomFields(client);
            var jql = config.getQueryId() == null ?
                    JqlBuilder.findIssuesByProject(config.getProjectKey())
                    : JqlBuilder.findIssuesByProjectAndFilterId(config.getProjectKey(), config.getQueryId());

            var issues = JiraClientHelper.findIssues(client, jql);
            return jiraToGTask.convertToGenericTaskList(resolver, JavaConverters.iterableAsScalaIterable(issues));
        } catch (Exception e) {
            throw JiraUtils.convertException(e);
        }
    }

    GTask loadTask(String taskKey) {
        var resolver = JiraClientHelper.loadCustomFields(client);
        var promise = client.getIssueClient().getIssue(taskKey);
        var issue = promise.claim();
        return jiraToGTask.convertToGenericTask(resolver, issue);
    }
}
