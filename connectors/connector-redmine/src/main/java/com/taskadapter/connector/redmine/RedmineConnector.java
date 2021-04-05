package com.taskadapter.connector.redmine;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssuePriority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RedmineConnector implements NewConnector {

    /**
     * Keep it the same to enable backward compatibility for previously created config files.
     */
    public static final String ID = "Redmine";

    private final RedmineConfig config;
    private final WebConnectorSetup setup;

    public RedmineConnector(RedmineConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
    }

    @Override
    public GTask loadTaskByKey(TaskId id, Iterable<FieldRow<?>> rows) {
        var httpClient = RedmineManagerFactory.createRedmineHttpClient(setup.getHost());
        try {
            var mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient);
            var intKey = id.getId().intValue();
            var issue = mgr.getIssueManager().getIssueById(intKey, Include.relations);
            var userCache = loadUsersIfAllowed(mgr);
            var converter = new RedmineToGTask(config, userCache);
            return converter.convertToGenericTask(issue);
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    @Override
    public List<GTask> loadData() {
        var httpClient = RedmineManagerFactory.createRedmineHttpClient(setup.getHost());
        try {
            var mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient);
            var usersCache = loadUsersIfAllowed(mgr);
            Integer queryId = config.getQueryId() == null ? null : config.getQueryId().intValue();
            var issues = mgr.getIssueManager().getIssues(config.getProjectKey(), queryId, Include.relations);
            return convertToGenericTasks(config, issues, usersCache);
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private List<GTask> convertToGenericTasks(RedmineConfig config, List<Issue> issues, RedmineUserCache usersCache) {
        var converter = new RedmineToGTask(config, usersCache);
        return issues.stream()
                .map(converter::convertToGenericTask)
                .collect(Collectors.toList());
    }

    @Override
    public SaveResult saveData(PreviouslyCreatedTasksResolver previouslyCreatedTasks, List<GTask> tasks,
                               ProgressMonitor monitor,
                               Iterable<FieldRow<?>> fieldRows) {
        try {
            var httpClient = RedmineManagerFactory.createRedmineHttpClient(setup.getHost());
            var mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient);
            try {
                var rmProject = mgr.getProjectManager().getProjectByKey(config.getProjectKey());
                var priorities = RedmineConnector.loadPriorities(fieldRows, mgr);
                var statusList = mgr.getIssueManager().getStatuses();
                var versions = mgr.getProjectManager().getVersions(rmProject.getId());
                var categories = mgr.getIssueManager().getCategories(rmProject.getId());
                var customFieldDefinitions = mgr.getCustomFieldManager().getCustomFieldDefinitions();
                var userCache = loadUsersIfAllowed(mgr);
                var converter = new GTaskToRedmine(config, priorities, rmProject, userCache, customFieldDefinitions, statusList,
                        versions, categories);
                var saver = new RedmineTaskSaver(mgr.getTransport(), config);
                var tsrb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, fieldRows,
                        setup.getHost());
                TaskSavingUtils.saveRemappedRelations(config, tasks, saver, tsrb);
                return tsrb.getResult();
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
        } catch (RedmineException e) {
            return SaveResult.withError(e);
        }
    }

    private RedmineUserCache loadUsersIfAllowed(RedmineManager mgr) throws RedmineException {
        if (!config.isFindUserByName()) {
            return new RedmineUserCache(List.of());
        }
        return new RedmineUserCache(mgr.getUserManager().getUsers());

    }

    private static Map<String, Integer> loadPriorities(Iterable<FieldRow<?>> rows,
                                                       RedmineManager mgr) throws RedmineException {
        if (FieldRowFinder.containsTargetField(rows, AllFields.priority)) {
            return loadPriorities(mgr);
        }
        return new HashMap<>();
    }

    private static Map<String, Integer> loadPriorities(RedmineManager mgr) throws RedmineException {
        return mgr.getIssueManager().getIssuePriorities().stream()
                .collect(Collectors.toMap(IssuePriority::getName, IssuePriority::getId));
    }
}
