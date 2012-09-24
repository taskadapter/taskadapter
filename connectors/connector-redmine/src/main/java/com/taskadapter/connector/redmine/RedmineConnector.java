package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.definition.*;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManager.INCLUDE;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedmineConnector extends AbstractConnector<RedmineConfig> {

    public RedmineConnector(RedmineConfig config) {
        super(config);
    }

    @Override
    public void updateRemoteIDs(ConnectorConfig configuration,
                                Map<Integer, String> res, ProgressMonitor monitor) throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("updateRemoteIDs");
    }

    @Override
    public Descriptor getDescriptor() {
        return RedmineDescriptor.instance;
    }

    @Override
    public GTask loadTaskByKey(String key) throws ConnectorException {
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
    public List<GTask> loadData(ProgressMonitor monitorIGNORED) throws ConnectorException {
        try {
            RedmineManager mgr = RedmineManagerFactory
                    .createRedmineManager(config.getServerInfo());
            final List<User> users = mgr.getUsers();

            List<Issue> issues = mgr.getIssues(config.getProjectKey(),
                    config.getQueryId(), INCLUDE.relations);
            addFullUsers(issues, users);
            return convertToGenericTasks(config, issues);
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }
    
	private void addFullUsers(List<Issue> issues, List<User> users) {
	    for (Issue issue : issues) {
	        issue.setAssignee(patchAssignee(issue.getAssignee(), users));
            issue.setAuthor(patchAssignee(issue.getAuthor(), users));
	    }
    }

    private User patchAssignee(User user, List<User> users) {
        if (user == null) {
            return null;
        }
        for (User newUser : users) {
            if (newUser.getId() == user.getId()) {
                return newUser;
            }
        }
        return user;
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
	public SyncResult<TaskSaveResult, TaskErrors<Throwable>> saveData(List<GTask> tasks, ProgressMonitor monitor) throws ConnectorException {
		return new RedmineTaskSaver(config).saveData(tasks, monitor);
	}
}
