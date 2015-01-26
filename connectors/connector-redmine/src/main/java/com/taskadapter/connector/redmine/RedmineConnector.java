package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.DefaultValueSetter;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManager.INCLUDE;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.Version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedmineConnector implements Connector<RedmineConfig> {
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
            Issue issue = mgr.getIssueById(intKey, INCLUDE.relations);
            RedmineToGTask converter = new RedmineToGTask(config);
            return converter.convertToGenericTask(issue);
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }
    
    @Override
    public List<GTask> loadData(Mappings mappings, ProgressMonitor monitorIGNORED) throws ConnectorException {
        try {
            RedmineManager mgr = RedmineManagerFactory.createRedmineManager(config.getServerInfo());

            List<Issue> issues = mgr.getIssues(config.getProjectKey(), config.getQueryId(), INCLUDE.relations);
            addFullUsers(issues, mgr);
            return convertToGenericTasks(config, issues);
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }
    
	private void addFullUsers(List<Issue> issues, RedmineManager mgr) throws RedmineException {
	    final Map<Integer, User> users = new HashMap<Integer, User>(); 
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
        
        final User loaded = mgr.getUserById(user.getId());
        users.put(user.getId(), loaded);
        
        return loaded;
    }

    private List<GTask> convertToGenericTasks(RedmineConfig config,
			List<Issue> issues) {
		List<GTask> result = new ArrayList<GTask>(issues.size());
        RedmineToGTask converter = new RedmineToGTask(config);
		for (Issue issue : issues) {
			GTask task = converter.convertToGenericTask(issue);
			result.add(task);
		}
		return result;
	}
    
	@Override
	public TaskSaveResult saveData(List<GTask> tasks, ProgressMonitor monitor, Mappings mappings) throws ConnectorException {
	    try {
            final RedmineManager mgr = RedmineManagerFactory
                    .createRedmineManager(config.getServerInfo());
            try {
                final Project rmProject = mgr.getProjectByKey(config
                        .getProjectKey());
                final Map<String, Integer> priorities = loadPriorities(
                        mappings, mgr);
                final List<User> users = !config.isFindUserByName() ? new ArrayList<User>()
                        : mgr.getUsers();
                final List<IssueStatus> statusList = mgr.getStatuses();
                final List<Version> versions = mgr.getVersions(rmProject.getId());
                final GTaskToRedmine converter = new GTaskToRedmine(config,
                        mappings, priorities, rmProject, users, statusList, versions);
                
                final RedmineTaskSaver rts = new RedmineTaskSaver(mgr,
                        rmProject, config);
                final TaskSaveResultBuilder tsrb = TaskSavingUtils.saveTasks(
                        tasks, converter, rts, monitor, new DefaultValueSetter(mappings));
                TaskSavingUtils.saveRemappedRelations(config, tasks, rts, tsrb);
                return tsrb.getResult();
            } finally {
                mgr.shutdown();
            }
	    } catch (RedmineException e) {
	        throw RedmineExceptions.convertException(e);
	    }
	}
	
    private static Map<String, Integer> loadPriorities(final Mappings mappings,
            final RedmineManager mgr) throws RedmineException {
        if (!mappings.isFieldSelected(FIELD.PRIORITY)) {
            return new HashMap<String, Integer>();
        }
        final Map<String, Integer> res = new HashMap<String, Integer>();
        for (IssuePriority prio : mgr.getIssuePriorities()) {
            res.put(prio.getName(), prio.getId());
        }
        return res;
    }

}
