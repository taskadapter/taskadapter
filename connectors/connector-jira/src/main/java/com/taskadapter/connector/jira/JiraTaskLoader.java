package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueType;
import com.atlassian.jira.rest.client.domain.Priority;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteIssueType;
import com.atlassian.jira.rpc.soap.client.RemotePriority;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class JiraTaskLoader {
    private JiraTaskConverter converter;
    private JiraConnection connection;
    
    public JiraTaskLoader(JiraConfig config) throws ConnectorException {
    	try {
		    connection = JiraConnectionFactory.createConnection(config.getServerInfo());
		
		    // Need to load Jira server priorities
		    // Because we store in config files string name of the priority
		    // and Jira returns the number value of the issue priority
		    Iterable<Priority> jiraPriorities = connection.getPriorities();
		    Iterable<IssueType> issueTypeList = connection.getIssueTypeList();

		    converter = new JiraTaskConverter(config);
		    converter.setPriorities(jiraPriorities);
		    converter.setIssueTypeList(issueTypeList);		
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
            List<Issue> issues = (List<Issue>) connection.getIssuesFromFilter(config.getQueryString());

            rows = converter.convertToGenericTaskList(issues);
            JiraUserConverter userConverter = new JiraUserConverter(connection);
            rows = userConverter.convertAssignees(rows);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        }
        return rows;
    }

    private String extractQueryId(JiraConfig connectorConfig) {
        return String.valueOf(connectorConfig.getQueryId());
    }

    GTask loadTask(JiraConfig config, String taskKey) {
        Issue issue = connection.getIssueByKey(taskKey);
        return converter.convertToGenericTask(issue);
    }
}
