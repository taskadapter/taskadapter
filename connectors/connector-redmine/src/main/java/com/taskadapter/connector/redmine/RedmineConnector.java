package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManager.INCLUDE;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedmineConnector implements Connector<RedmineConfig> {
    /**
     * Keep it the same to enable backward compatibility for previously created config files.
     */
    public static final String ID = "Redmine REST";

    private RedmineConfig config;

    public RedmineConnector(RedmineConfig config) {
        this.config = config;
    }

    @Override
    public void updateRemoteIDs(ConnectorConfig configuration,
                                Map<Integer, String> res, ProgressMonitor monitor, Mappings mappings) throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("updateRemoteIDs");
    }

    @Override
    public RedmineConfig getConfig() {
        return config;
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
	public SyncResult<TaskSaveResult, TaskErrors<Throwable>> saveData(List<GTask> tasks, ProgressMonitor monitor, Mappings mappings) throws ConnectorException {
		return new RedmineTaskSaver(config, mappings).saveData(tasks, monitor);
	}
}
