package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.RemoteAuthenticationException;
import com.atlassian.jira.rpc.soap.client.RemotePriority;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.WebServerInfo;

public class JiraLoaders {

	public static Priorities loadPriorities(WebServerInfo serverInfo) {
	    final Priorities defaultPriorities = JiraConfig.createDefaultPriorities();
	    final Priorities result = new Priorities();
	
	    try {
	        JiraConnection connection = JiraConnectionFactory.createConnection(serverInfo);
	        RemotePriority[] priorities = connection.getPriorities();
	
	        for (RemotePriority priority : priorities) {
				result.setPriority(priority.getName(),
						defaultPriorities.getPriorityByText(priority.getName()));
	        }
	    } catch (RemoteAuthenticationException e) {
	        throw new RuntimeException(e.getFaultString());
	    } catch (Exception e) {
	        throw new RuntimeException(e.toString());
	    }
	
	    return result;
	}

}
