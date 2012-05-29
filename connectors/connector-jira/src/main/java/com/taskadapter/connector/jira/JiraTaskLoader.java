package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteIssueType;
import com.atlassian.jira.rpc.soap.client.RemotePriority;
import com.taskadapter.model.GTask;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class JiraTaskLoader {
    private JiraTaskConverter converter;
    private JiraConnection connection;
    
    public JiraTaskLoader(JiraConfig config) {
    	try {
		    connection = JiraConnectionFactory.createConnection(config.getServerInfo());
		
		    // Need to load Jira server priorities
		    // Because we store in config files string name of the priority
		    // and Jira returns the number value of the issue priority
		    RemotePriority[] jiraPriorities = connection.getPriorities();
		    RemoteIssueType[] issueTypeList = connection.getIssueTypeList(config.getProjectKey());
		
		    converter = new JiraTaskConverter(config);
		    converter.setPriorities(jiraPriorities);
		    converter.setIssueTypeList(issueTypeList);
		
		} catch (RemoteException e) {
		    throw new JiraException(e);
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}
	}

    List<GTask> loadTasks(JiraConfig config) {
        List<GTask> rows;
        try {
            List<RemoteIssue> issues = Arrays.asList(connection.getIssuesFromFilter(extractQueryId(config)));

            rows = converter.convertToGenericTaskList(issues);
            JiraUserConverter userConverter = new JiraUserConverter(connection);
            rows = userConverter.convertAssignees(rows);
        } catch (RemoteException e) {
            throw new JiraException(e);
        }
        return rows;
    }

    private String extractQueryId(JiraConfig connectorConfig) {
        return String.valueOf(connectorConfig.getQueryId());
    }

    GTask loadTask(JiraConfig config, String taskKey) {
        try {
            RemoteIssue issue = connection.getIssueByKey(taskKey);
            return converter.convertToGenericTask(issue);
        } catch (RemoteException e) {
            throw new JiraException(e);
        }
    }
}
