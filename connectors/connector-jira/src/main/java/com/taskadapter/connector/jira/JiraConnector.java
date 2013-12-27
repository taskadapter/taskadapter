package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueType;
import com.atlassian.jira.rest.client.domain.Priority;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rpc.soap.client.RemoteFilter;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JiraConnector implements Connector<JiraConfig> {

    /**
     * Keep it the same to enable backward compatibility with the existing
     * config files.
     */
    public static final String ID = "Atlassian Jira";
    private JiraConfig config;

    public JiraConnector(JiraConfig config) {
        this.config = config;
    }

    @Override
    public void updateRemoteIDs(
            Map<Integer, String> res, ProgressMonitor monitor, Mappings mappings)
            throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("updateRemoteIDs");
    }

    public GTask loadTaskByKey(WebServerInfo info, String key) throws ConnectorException {
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(info);
            Issue issue = connection.getIssueByKey(key);
            JiraUserConverter userConverter = new JiraUserConverter(connection);
            JiraToGTask jiraToGTask = new JiraToGTask(config.getPriorities());
            return userConverter.setAssigneeDisplayName(jiraToGTask.convertToGenericTask(issue));
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
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
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    public List<NamedKeyedObject> getComponents() throws ConnectorException {
        JiraConfigValidator.validateServerURLSet(config);
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            String projectKey = config.getProjectKey();
            if (projectKey == null) {
                throw new ProjectNotSetException();
            }
            Iterable<BasicComponent> components = connection.getComponents(projectKey);
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();
            for (BasicComponent c : components) {
                list.add(new NamedKeyedObjectImpl(String.valueOf(c.getId()), c.getName()));
            }
            return list;
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    // XXX refactor this. we don't even need the IDs!
    public List<NamedKeyedObject> getVersions() throws ConnectorException {
        JiraConfigValidator.validateServerURLSet(config);
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            String projectKey = config.getProjectKey();
            if (projectKey == null) {
                throw new ProjectNotSetException();
            }
            Iterable<Version> objects = connection.getVersions(projectKey);
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();
            for (Version o : objects) {
                list.add(new NamedKeyedObjectImpl(String.valueOf(o.getId()), o.getName()));
            }
            return list;
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    public List<NamedKeyedObject> getAllIssueTypes() throws ConnectorException {
        return IssueTypesLoader.getIssueTypes(config, new AllIssueTypesFilter());
    }

    public List<? extends NamedKeyedObject> getIssueTypesForSubtasks() throws ConnectorException {
        return IssueTypesLoader.getIssueTypes(config, new SubtaskTypesFilter());
    }

    @Override
    public GTask loadTaskByKey(String key, Mappings mappings) throws ConnectorException {
        final JiraTaskLoader loader = new JiraTaskLoader(config);
        return loader.loadTask(key);
    }

    @Override
    public List<GTask> loadData(Mappings mappings, ProgressMonitor monitorIGNORED) throws ConnectorException {
        final JiraTaskLoader loader = new JiraTaskLoader(config);
        return loader.loadTasks(config);
    }

    @Override
    public TaskSaveResult saveData(List<GTask> tasks, ProgressMonitor monitor, Mappings mappings) throws ConnectorException {
        try {
            final JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());

            final Iterable<IssueType> issueTypeList = loadIssueTypes(connection);
            final Iterable<Version> versions = connection.getVersions(config.getProjectKey());
            final Iterable<BasicComponent> components = connection.getComponents(config.getProjectKey());
            /* Need to load Jira server priorities because what we store in the config files is a
             * priority name (string), while Jira returns the number value of the issue priority */
            final Iterable<Priority> jiraPriorities = connection.getPriorities();
            final GTaskToJira converter = new GTaskToJira(config, mappings,
                    issueTypeList, versions, components, jiraPriorities);

            final JiraTaskSaver saver = new JiraTaskSaver(connection);
            final TaskSaveResultBuilder rb = TaskSavingUtils.saveTasks(tasks,
                    converter, saver, monitor);
            TaskSavingUtils.saveRemappedRelations(config, tasks, saver, rb);
            return rb.getResult();
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    private Iterable<IssueType> loadIssueTypes(JiraConnection connection) throws BadConfigException {
        Iterable<IssueType> issueTypeList = connection.getIssueTypeList();

        //check if default issue type exists in Jira
        for (IssueType anIssueTypeList : issueTypeList) {
            if (anIssueTypeList.getName().equals(config.getDefaultTaskType())) {
                return issueTypeList;
            }
        }

        throw new BadConfigException("Default issue type "
                + config.getDefaultTaskType() + " does not exist in Jira");
    }
}