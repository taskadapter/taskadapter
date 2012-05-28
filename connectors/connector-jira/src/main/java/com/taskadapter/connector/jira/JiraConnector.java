package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.*;
import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.common.TaskLoader;
import com.taskadapter.connector.definition.*;
import com.taskadapter.model.GTask;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;

import java.util.ArrayList;
import java.util.List;

public class JiraConnector extends AbstractConnector<JiraConfig> {

    public JiraConnector(ConnectorConfig config) {
        super((JiraConfig) config);
    }

    @Override
    public void updateRemoteIDs(ConnectorConfig configuration,
                                SyncResult res, ProgressMonitor monitor) {
        throw new RuntimeException("not implemented for this connector");
    }

    public GTask loadTaskByKey(WebServerInfo info, String key) {

        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(info);
            RemoteIssue issue = connection.getIssueByKey(key);
            JiraTaskConverter converter = new JiraTaskConverter(config);
            JiraUserConverter userConverter = new JiraUserConverter(connection);
            return userConverter.setAssigneeDisplayName(converter.convertToGenericTask(issue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // XXX refactor this. we don't even need the IDs!
    public List<NamedKeyedObject> getFilters() throws Exception {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            RemoteFilter[] objects = connection.getSavedFilters();
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();
            for (RemoteFilter o : objects) {
                list.add(new NamedKeyedObjectImpl(o.getId(), o.getName()));
            }
            return list;
        } catch (RemoteAuthenticationException e) {
            throw new JiraException(e);
        }
    }

    public List<NamedKeyedObject> getComponents() throws Exception {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            RemoteComponent[] components = connection.getComponents(config.getProjectKey());
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();
            for (RemoteComponent c : components) {
                list.add(new NamedKeyedObjectImpl(c.getId(), c.getName()));
            }
            return list;
        } catch (RemoteAuthenticationException e) {
            throw new JiraException(e);
        }
    }

    // XXX refactor this. we don't even need the IDs!
    public List<NamedKeyedObject> getVersions() throws Exception {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            RemoteVersion[] objects = connection.getVersions(config.getProjectKey());
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();
            for (RemoteVersion o : objects) {
                list.add(new NamedKeyedObjectImpl(o.getId(), o.getName()));
            }
            return list;
        } catch (RemoteAuthenticationException e) {
            throw new JiraException(e);
        }
    }

    public List<NamedKeyedObject> getIssueTypes() throws Exception {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            RemoteIssueType[] issueTypeList = connection.getIssueTypeList(config.getProjectKey());
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>(issueTypeList.length);

            for (RemoteIssueType issueType : issueTypeList) {
                list.add(new NamedKeyedObjectImpl(issueType.getId(), issueType.getName()));
            }

            return list;
        } catch (RemoteAuthenticationException e) {
            throw new JiraException(e);
        }
    }

    @Override
    public Descriptor getDescriptor() {
        return JiraDescriptor.instance;
    }

    @Override
    protected TaskLoader<JiraConfig> getTaskLoader() {
        return new JiraTaskLoader();
    }


}
