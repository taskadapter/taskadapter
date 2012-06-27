package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.*;
import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.definition.*;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JiraConnector extends AbstractConnector<JiraConfig> {

    public JiraConnector(JiraConfig config) {
        super(config);
    }

    @Override
    public void updateRemoteIDs(ConnectorConfig configuration,
            Map<Integer, String> res, ProgressMonitor monitor)
            throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("updateRemoteIDs");
    }

    public GTask loadTaskByKey(WebServerInfo info, String key) throws ConnectorException {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(info);
            RemoteIssue issue;
                issue = connection.getIssueByKey(key);
            JiraTaskConverter converter = new JiraTaskConverter(config);
            JiraUserConverter userConverter = new JiraUserConverter(connection);
            return userConverter.setAssigneeDisplayName(converter.convertToGenericTask(issue));
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        }
    }

    // XXX refactor this. we don't even need the IDs!
    public List<NamedKeyedObject> getFilters() throws ConnectorException {
            try {
                JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
                RemoteFilter[] objects = connection.getSavedFilters();
                List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();
                for (RemoteFilter o : objects) {
                    list.add(new NamedKeyedObjectImpl(o.getId(), o.getName()));
                }
                return list;
            } catch (RemoteException e) {
                throw JiraUtils.convertException(e);
            } catch (MalformedURLException e) {
                throw JiraUtils.convertException(e);
            }
    }

    public List<NamedKeyedObject> getComponents() throws ConnectorException {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            RemoteComponent[] components = connection.getComponents(config.getProjectKey());
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();
            for (RemoteComponent c : components) {
                list.add(new NamedKeyedObjectImpl(c.getId(), c.getName()));
            }
            return list;
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        }
    }

    // XXX refactor this. we don't even need the IDs!
    public List<NamedKeyedObject> getVersions() throws ConnectorException {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            RemoteVersion[] objects = connection.getVersions(config.getProjectKey());
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();
            for (RemoteVersion o : objects) {
                list.add(new NamedKeyedObjectImpl(o.getId(), o.getName()));
            }
            return list;
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        }
    }

    public List<NamedKeyedObject> getIssueTypes() throws ConnectorException {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            RemoteIssueType[] issueTypeList = connection.getIssueTypeList(config.getProjectKey());
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>(issueTypeList.length);

            for (RemoteIssueType issueType : issueTypeList) {
                list.add(new NamedKeyedObjectImpl(issueType.getId(), issueType.getName()));
            }

            return list;
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        }
    }

    @Override
    public Descriptor getDescriptor() {
        return JiraDescriptor.instance;
    }

    @Override
    public GTask loadTaskByKey(String key) throws ConnectorException {
    	final JiraTaskLoader loader = new JiraTaskLoader(config);
    	return loader.loadTask(config, key);
    }
    
    @Override
    public List<GTask> loadData(ProgressMonitor monitorIGNORED) throws ConnectorException {
    	final JiraTaskLoader loader = new JiraTaskLoader(config);
    	return loader.loadTasks(config);
    }
    
    @Override
    public SyncResult<TaskSaveResult, TaskErrors<Throwable>> saveData(List<GTask> tasks, ProgressMonitor monitor) throws ConnectorException {
    	return new JiraTaskSaver(config).saveData(tasks, monitor);
    }
}
