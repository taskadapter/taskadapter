package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.RemoteAuthenticationException;
import com.atlassian.jira.rpc.soap.client.RemotePriority;
import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.NamedKeyedObjectImpl;

import java.util.ArrayList;
import java.util.List;

public class JiraPriorityLoader implements PriorityLoader {

    @Override
    public List<NamedKeyedObjectImpl> getPriorities(WebServerInfo serverInfo) throws ValidationException {
        if (!serverInfo.isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }

        List<NamedKeyedObjectImpl> priorityList = new ArrayList<NamedKeyedObjectImpl>();

        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(serverInfo);
            RemotePriority[] priorities = connection.getPriorities();

            for (int i = 0; i < priorities.length; i++) {
                priorityList.add(new NamedKeyedObjectImpl(priorities[i].getName(), "0"));
            }
        } catch (RemoteAuthenticationException e) {
            throw new RuntimeException(e.getFaultString());
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }

        return priorityList;
    }

}
