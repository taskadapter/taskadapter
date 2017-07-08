package com.taskadapter.connector.redmine;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.*;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.core.NewConnector;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedmineConnector implements NewConnector {
    /**
     * Keep it the same to enable backward compatibility for previously created config files.
     */
    public static final String ID = "Redmine";

    private RedmineConfig config;

    public RedmineConnector(RedmineConfig config) {
        this.config = config;
    }
    
    @Override
    public void updateRemoteIDs(Map<Integer, String> res, ProgressMonitor monitor, Mappings mappings) throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("updateRemoteIDs");
    }

    @Override
    public GTask loadTaskByKey(String key, Mappings mappings) throws ConnectorException {
        try {
            WebServerInfo serverInfo = config.getServerInfo();
            RedmineManager mgr = RedmineManagerFactory
                    .createRedmineManager(serverInfo);

            Integer intKey = Integer.parseInt(key);
            Issue issue = mgr.getIssueManager().getIssueById(intKey, Include.relations);
            RedmineToGTask converter = new RedmineToGTask(config);
            return converter.convertToGenericTask(issue);
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }
    
    @Override
    public List<GTask> loadData(List<FieldRow> rows, ProgressMonitor monitorIGNORED) throws ConnectorException {
        try {
            RedmineManager mgr = RedmineManagerFactory.createRedmineManager(config.getServerInfo());

            List<Issue> issues = mgr.getIssueManager().getIssues(config.getProjectKey(), config.getQueryId(), Include.relations);
            addFullUsers(issues, mgr);
            return convertToGenericTasks(config, issues);
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }
    
	private void addFullUsers(List<Issue> issues, RedmineManager mgr) throws RedmineException {
	    final Map<Integer, User> users = new HashMap<>();
	    for (Issue issue : issues) {
	        issue.setAssignee(patchAssignee(issue.getAssignee(), users, mgr));
            issue.setAuthor(patchAssignee(issue.getAuthor(), users, mgr));
	    }
    }

    private User patchAssignee(User user, Map<Integer, User> users, RedmineManager mgr) throws RedmineException {
        if (user == null) {
            return null;
        }
        final User guess = users.get(user.getId());
        if (guess != null) {
            return guess;
        }
        
        final User loaded = mgr.getUserManager().getUserById(user.getId());
        users.put(user.getId(), loaded);
        
        return loaded;
    }

    private List<GTask> convertToGenericTasks(RedmineConfig config,
			List<Issue> issues) {
		List<GTask> result = new ArrayList<>(issues.size());
        RedmineToGTask converter = new RedmineToGTask(config);
		for (Issue issue : issues) {
			GTask task = converter.convertToGenericTask(issue);
			result.add(task);
		}
		return result;
	}
    
    @Override
    public List<GTask> loadData()  {
        try {
            RedmineManager mgr = RedmineManagerFactory.createRedmineManager(config.getServerInfo());

            List<Issue> issues = mgr.getIssueManager().getIssues(config.getProjectKey(), config.getQueryId(), Include.relations);
            addFullUsers(issues, mgr);
            return convertToGenericTasks(config, issues);
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TaskSaveResult saveData(List<GTask> tasks, ProgressMonitor monitor, List<FieldRow> fieldRows) {
        try {
            final RedmineManager mgr = RedmineManagerFactory.createRedmineManager(config.getServerInfo());
            try {
                final Project rmProject = mgr.getProjectManager().getProjectByKey(config.getProjectKey());
                final Map<String, Integer> priorities = loadPriorities(fieldRows, mgr);
                final List<User> users = !config.isFindUserByName() ? new ArrayList<>()
                        : mgr.getUserManager().getUsers();
                final List<IssueStatus> statusList = mgr.getIssueManager().getStatuses();
                final List<Version> versions = mgr.getProjectManager().getVersions(rmProject.getId());
                List<CustomFieldDefinition> customFieldDefinitions = mgr.getCustomFieldManager().getCustomFieldDefinitions();

                final GTaskToRedmine converter = new GTaskToRedmine(config, priorities, rmProject, users,
                        customFieldDefinitions, statusList, versions);

                final RedmineTaskSaver saver = new RedmineTaskSaver(mgr.getIssueManager(), config);
                final TaskSaveResultBuilder tsrb = TaskSavingUtils.saveTasks(
                        tasks, converter, saver, monitor, fieldRows);
                TaskSavingUtils.saveRemappedRelations(config, tasks, saver, tsrb);
                return tsrb.getResult();
            } finally {
                mgr.shutdown();
            }
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Integer> loadPriorities(List<FieldRow> rows, RedmineManager mgr) throws RedmineException {
        if (FieldRowFinder.containsTargetField(rows, RedmineField.priority())) {
            return loadPriorities(mgr);
        }
        return new HashMap<>();
    }

    private static Map<String, Integer> loadPriorities(final RedmineManager mgr) throws RedmineException {
        final Map<String, Integer> res = new HashMap<>();
        for (IssuePriority prio : mgr.getIssueManager().getIssuePriorities()) {
            res.put(prio.getName(), prio.getId());
        }
        return res;
    }

}
