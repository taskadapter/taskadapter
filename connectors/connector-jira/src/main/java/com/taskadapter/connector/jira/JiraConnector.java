package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.util.concurrent.Promise;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.core.TaskKeeper;
import com.taskadapter.model.GTask;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JiraConnector implements NewConnector {
    private static final Logger logger = LoggerFactory.getLogger(JiraConnector.class);

    /**
     * Keep it the same to enable backward compatibility with the existing
     * config files.
     */
    public static final String ID = "Atlassian Jira";

    private final JiraConfig config;

    public JiraConnector(JiraConfig config) {
        this.config = config;
    }

    @Override
    public void updateRemoteIDs(
            Map<Integer, String> res, ProgressMonitor monitor, List<FieldRow> rows)
             {
        throw new RuntimeException("not supported: updateRemoteIDs");
    }

    // XXX refactor this. we don't even need the IDs!
    public List<NamedKeyedObject> getFilters() throws ConnectorException {
        return withJiraRestClient(client -> {
            // TODO need all filters, not just favorites - but JIRA REST API does not support. (Dec 6 2015)
            final Promise<Iterable<Filter>> filtersPromise = client.getSearchClient().getFavouriteFilters();
            final Iterable<Filter> filters = filtersPromise.claim();
            List<NamedKeyedObject> list = new ArrayList<>();
            for (Filter filter : filters) {
                list.add(new NamedKeyedObjectImpl(filter.getId() + "", filter.getName()));
            }
            return list;
        });
    }

    public List<NamedKeyedObject> getComponents() throws ConnectorException {
        JiraConfigValidator.validateServerURLSet(config);
        return withJiraRestClient(client -> {
            String projectKey = config.getProjectKey();
            if (projectKey == null) {
                throw new ProjectNotSetException();
            }
            Promise<Project> projectPromise = client.getProjectClient().getProject(projectKey);
            final Project project = projectPromise.claim();
            final Iterable<BasicComponent> components = project.getComponents();
            List<NamedKeyedObject> list = new ArrayList<>();
            for (BasicComponent c : components) {
                list.add(new NamedKeyedObjectImpl(String.valueOf(c.getId()), c.getName()));
            }
            return list;
        });
    }

    // XXX refactor this. we don't even need the IDs!
    public List<NamedKeyedObject> getVersions() throws ConnectorException {
        JiraConfigValidator.validateServerURLSet(config);
        return withJiraRestClient(client -> {
            String projectKey = config.getProjectKey();
            if (projectKey == null) {
                throw new ProjectNotSetException();
            }
            final Promise<Project> projectPromise = client.getProjectClient().getProject(projectKey);
            final Project project = projectPromise.claim();
            final Iterable<Version> versions = project.getVersions();
            List<NamedKeyedObject> list = new ArrayList<>();
            for (Version version : versions) {
                list.add(new NamedKeyedObjectImpl(String.valueOf(version.getId()), version.getName()));
            }
            return list;
        });
    }

    public List<NamedKeyedObject> getAllIssueTypes() throws ConnectorException {
        return IssueTypesLoader.getIssueTypes(config, new AllIssueTypesFilter());
    }

    public List<? extends NamedKeyedObject> getIssueTypesForSubtasks() throws ConnectorException {
        return IssueTypesLoader.getIssueTypes(config, new SubtaskTypesFilter());
    }

    @Override
    public GTask loadTaskByKey(String key, Iterable<FieldRow> rows) {
        return withJiraRestClient(client -> {
            final JiraTaskLoader loader = new JiraTaskLoader(client, config.getPriorities());
            return loader.loadTask(key);
        });
    }

    @Override
    public List<GTask> loadData() {
        return withJiraRestClient(client -> {
            final JiraTaskLoader loader = new JiraTaskLoader(client, config.getPriorities());
            return loader.loadTasks(config);
        });
    }

    @Override
    public TaskSaveResult saveData(TaskKeeper taskKeeper, List<GTask> tasks, ProgressMonitor monitor, Iterable<FieldRow> rows) {
        return withJiraRestClient(client -> {
            final Iterable<IssueType> issueTypeList = loadIssueTypes(client);
            final Promise<Project> projectPromise = client.getProjectClient().getProject(config.getProjectKey());
            final Project project = projectPromise.claim();
            final Iterable<Version> versions = project.getVersions();
            final Iterable<BasicComponent> components = project.getComponents();
            /* Need to load Jira server priorities because what we store in the config files is a
             * priority name (string), while Jira returns the number value of the issue priority */
            final Promise<Iterable<Priority>> prioritiesPromise = client.getMetadataClient().getPriorities();
            final Iterable<Priority> priorities = prioritiesPromise.claim();
            final GTaskToJira converter = new GTaskToJira(config,
                    issueTypeList, versions, components, priorities);

            final JiraTaskSaver saver = new JiraTaskSaver(client);
            final TaskSaveResultBuilder rb = TaskSavingUtils.saveTasks(taskKeeper, tasks, converter, saver, monitor, rows);
            TaskSavingUtils.saveRemappedRelations(config, tasks, saver, rb);
            return rb.getResult();
        });
    }

    private Iterable<IssueType> loadIssueTypes(JiraRestClient jiraRestClient) throws BadConfigException {
        Promise<Iterable<IssueType>> issueTypeListPromise = jiraRestClient.getMetadataClient().getIssueTypes();
        final Iterable<IssueType> issueTypeList = issueTypeListPromise.claim();

        //check if default issue type exists in Jira
        for (IssueType anIssueTypeList : issueTypeList) {
            if (anIssueTypeList.getName().equals(config.getDefaultTaskType())) {
                return issueTypeList;
            }
        }

        throw new BadConfigException("Default issue type "
                + config.getDefaultTaskType() + " does not exist in JIRA");
    }

    private <T> T withJiraRestClient(JiraRestClientAction<T> f) {
        try (JiraRestClient client = JiraConnectionFactory.createClient(config.getServerInfo())) {
            return f.apply(client);
        } catch (Exception e) {
//            throw JiraUtils.convertException(e);
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    interface JiraRestClientAction<T> {
        T apply(JiraRestClient client) throws IOException, ConnectorException;
    }
}