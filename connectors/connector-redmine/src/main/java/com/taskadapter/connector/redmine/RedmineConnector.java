package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.common.TransportException;
import com.taskadapter.connector.definition.*;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManager.INCLUDE;
import com.taskadapter.redmineapi.RedmineTransportException;
import com.taskadapter.redmineapi.bean.Issue;

import java.util.ArrayList;
import java.util.List;

public class RedmineConnector extends AbstractConnector<RedmineConfig> {

    public RedmineConnector(RedmineConfig config) {
        super(config);
    }

    // TODO check if should change this to a flat list,
    // like it's already done for loadData() operation

    @Override
    public void updateRemoteIDs(ConnectorConfig configuration,
                                SyncResult res, ProgressMonitor monitor) {
        throw new RuntimeException("not implemented for this connector");
    }

    @Override
    public Descriptor getDescriptor() {
        return RedmineDescriptor.instance;
    }

    @Override
    public GTask loadTaskByKey(String key) {
        try {
            WebServerInfo serverInfo = config.getServerInfo();
            RedmineManager mgr = RedmineManagerFactory
                    .createRedmineManager(serverInfo);

            Integer intKey = Integer.parseInt(key);
            Issue issue = mgr.getIssueById(intKey, INCLUDE.relations);
            RedmineDataConverter converter = new RedmineDataConverter(config);
            return converter.convertToGenericTask(issue);
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<GTask> loadData(ProgressMonitor monitorIGNORED) {
        try {
            RedmineManager mgr = RedmineManagerFactory
                    .createRedmineManager(config.getServerInfo());

            List<Issue> issues = mgr.getIssues(config.getProjectKey(),
                    config.getQueryId(), INCLUDE.relations);
            return convertToGenericTasks(config, issues);
        } catch (RedmineTransportException e) {
            throw new TransportException("There was a problem communicating with Redmine server", e);
        } catch (RedmineException e) {
        	throw new RuntimeException(e);
		}
    }
    
	private List<GTask> convertToGenericTasks(RedmineConfig config,
			List<Issue> issues) {
		List<GTask> result = new ArrayList<GTask>(issues.size());
		RedmineDataConverter converter = new RedmineDataConverter(config);
		for (Issue issue : issues) {
			GTask task = converter.convertToGenericTask(issue);
			result.add(task);
		}
		return result;
	}
    
	@Override
	public SyncResult saveData(List<GTask> tasks, ProgressMonitor monitor) throws ConnectorException {
		return new RedmineTaskSaver(config).saveData(tasks, monitor);
	}
}
