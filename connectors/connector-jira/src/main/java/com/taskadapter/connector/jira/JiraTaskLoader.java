package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.Issue;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;

public class JiraTaskLoader {
    private JiraConnection connection;
    private JiraToGTask jiraToGTask;
    
    public JiraTaskLoader(JiraConfig config) throws ConnectorException {
    	try {
            jiraToGTask = new JiraToGTask(config);
		    connection = JiraConnectionFactory.createConnection(config.getServerInfo());
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    List<GTask> loadTasks(JiraConfig config) throws ConnectorException {
        List<GTask> rows;

        try {
            List<Issue> issues = connection.getIssuesFromFilter(config.getQueryString());

            rows = jiraToGTask.convertToGenericTaskList(issues);
            JiraUserConverter userConverter = new JiraUserConverter(connection);
            rows = userConverter.convertAssignees(rows);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        }
        return rows;
    }

    GTask loadTask(String taskKey) {
        Issue issue = connection.getIssueByKey(taskKey);
        return jiraToGTask.convertToGenericTask(issue);
    }
}
