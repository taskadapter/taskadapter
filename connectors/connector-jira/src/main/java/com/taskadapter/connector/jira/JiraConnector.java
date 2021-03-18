package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JiraConnector implements NewConnector {
    private static final Logger logger = LoggerFactory.getLogger(JiraConnector.class);

    /**
     * Keep it the same to enable backward compatibility with the existing
     * config files.
     */
    public static final String ID = "Atlassian JIRA";

    private final JiraConfig config;
    private final WebConnectorSetup setup;

    public JiraConnector(JiraConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
    }

    // XXX refactor this. we don't even need the IDs!

    public List<NamedKeyedObject> getFilters() throws ConnectorException {
        return withJiraRestClient(client -> {
            // TODO need all filters, not just favorites - but JIRA REST API does not support. (Dec 6 2015)
            var filtersPromise = client.getSearchClient().getFavouriteFilters();
            var filters = filtersPromise.claim();
            var list = new ArrayList<NamedKeyedObject>();
            for (Filter filter : filters) {
                list.add(new NamedKeyedObjectImpl(filter.getId() + "", filter.getName()));
            }
            return list;
        });
    }

    List<NamedKeyedObject> getComponents() throws ConnectorException {
        return withJiraRestClient(client -> {
            var projectKey = config.getProjectKey();
            if (projectKey == null) {
                throw new ProjectNotSetException();
            }
            var projectPromise = client.getProjectClient().getProject(projectKey);
            var project = projectPromise.claim();
            var components = project.getComponents();
            var list = new ArrayList<NamedKeyedObject>();
            for (BasicComponent c : components) {
                list.add(new NamedKeyedObjectImpl(String.valueOf(c.getId()), c.getName()));
            }
            return list;
        });
    }

    List<NamedKeyedObject> getVersions() throws ConnectorException {
        return withJiraRestClient(client -> {
            var projectKey = config.getProjectKey();
            if (projectKey == null) {
                throw new ProjectNotSetException();
            }
            var projectPromise = client.getProjectClient().getProject(projectKey);
            var project = projectPromise.claim();
            var versions = project.getVersions();
            var list = new ArrayList<NamedKeyedObject>();
            for (Version version : versions) {
                list.add(new NamedKeyedObjectImpl(String.valueOf(version.getId()), version.getName()));
            }
            return list;
        });
    }

    List<NamedKeyedObject> getAllIssueTypes() throws ConnectorException {
        return IssueTypesLoader.getIssueTypes(setup, new AllIssueTypesFilter());
    }

    List<? extends NamedKeyedObject> getIssueTypesForSubtasks() throws ConnectorException {
        return IssueTypesLoader.getIssueTypes(setup, new SubtaskTypesFilter());
    }

    public GTask loadTaskByKey(TaskId key, Iterable<FieldRow<?>> rows) throws ConnectorException {
        return withJiraRestClient(client -> {
            var loader = new JiraTaskLoader(client, config.getPriorities());
            return loader.loadTask(key.getKey());
        });
    }

    @Override
    public List<GTask> loadData() throws ConnectorException {
        return withJiraRestClient(client -> {
                    var loader = new JiraTaskLoader(client, config.getPriorities());
                    return loader.loadTasks(config);
                }
        );
    }

    @Override
    public SaveResult saveData(PreviouslyCreatedTasksResolver previouslyCreatedTasks, List<GTask> tasks,
                               ProgressMonitor monitor,
                               Iterable<FieldRow<?>> rows) {
        try {
            return withJiraRestClient(client -> {
                        var issueTypeList = loadIssueTypes(client);
                        var projectPromise = client.getProjectClient().getProject(config.getProjectKey());
                        var project = projectPromise.claim();
                        var versions = project.getVersions();
                        var components = project.getComponents();
                        // Need to load Jira server priorities because what we store in the config files is a
                        // priority name (string), while Jira returns the number value of the issue priority
                        var prioritiesPromise = client.getMetadataClient().getPriorities();
                        var priorities = prioritiesPromise.claim();

                        var resolver = JiraClientHelper.loadCustomFields(client);
                        var converter = new GTaskToJira(config, resolver, versions, components, priorities);
                        var saver = new JiraTaskSaver(client, issueTypeList, config.getDefaultTaskType(),
                                config.getDefaultIssueTypeForSubtasks());
                        var rb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, rows,
                                setup.getHost());
                        TaskSavingUtils.saveRemappedRelations(config, tasks, saver, rb);
                        return rb.getResult();
                    }
            );
        } catch (ConnectorException e) {
            return new SaveResult("", 0, 0, List.of(), List.of(e), List.of());
        }
    }

    private Iterable<IssueType> loadIssueTypes(JiraRestClient jiraRestClient) throws BadConfigException {
        var issueTypeListPromise = jiraRestClient.getMetadataClient().getIssueTypes();
        var issueTypeList = issueTypeListPromise.claim();
        //check if default issue type exists in Jira
        for (IssueType anIssueTypeList : issueTypeList) {
            if (anIssueTypeList.getName().equals(config.getDefaultTaskType())) {
                return issueTypeList;
            }
        }
        throw new BadConfigException("Default issue type " + config.getDefaultTaskType() + " does not exist in JIRA");
    }

    private <T> T withJiraRestClient(JiraRestClientAction<T> f) throws ConnectorException {
        try (JiraRestClient client = JiraConnectionFactory.createClient(setup)) {
            return f.apply(client);
        } catch (Exception e) {
            throw JiraUtils.convertException(e);
        }
    }

    @FunctionalInterface
    interface JiraRestClientAction<T> {
        T apply(JiraRestClient client) throws IOException, ConnectorException;
    }
}
