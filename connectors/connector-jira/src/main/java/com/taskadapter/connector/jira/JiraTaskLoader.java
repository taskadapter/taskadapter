package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;

public class JiraTaskLoader {
    private final JiraRestClient client;
    private final JiraToGTask jiraToGTask;
    
    public JiraTaskLoader(JiraRestClient client, Priorities priorities) {
        this.client = client;
        jiraToGTask = new JiraToGTask(priorities);
    }

    List<GTask> loadTasks(JiraConfig config) throws ConnectorException {
        List<GTask> rows;

        try {
            String jql;
            if (config.getQueryId() != null) {
                jql = JqlBuilder.findIssuesByProjectAndFilterId(config.getProjectKey(), config.getQueryId());
            } else {
                jql = JqlBuilder.findIssuesByProject(config.getProjectKey());
            }
            final Iterable<Issue> issues = JiraClientHelper.findIssues(client, jql);
            rows = jiraToGTask.convertToGenericTaskList(issues);
            JiraUserConverter userConverter = new JiraUserConverter(client);
//            rows = userConverter.convertAssignees(rows);
        } catch (Exception e) {
            throw JiraUtils.convertException(e);
        }
        return rows;
    }

    GTask loadTask(String taskKey) {
        final Promise<Issue> promise = client.getIssueClient().getIssue(taskKey);
        final Issue issue = promise.claim();
        return jiraToGTask.convertToGenericTask(issue);
    }
}
