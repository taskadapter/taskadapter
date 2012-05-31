package com.taskadapter.connector.redmine;

import java.util.ArrayList;
import java.util.List;

import org.redmine.ta.RedmineException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.RedmineTransportException;
import org.redmine.ta.RedmineManager.INCLUDE;
import org.redmine.ta.beans.Issue;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.common.TransportException;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GTask;

public class RedmineConnector extends AbstractConnector<RedmineConfig> {

    public RedmineConnector(ConnectorConfig config) {
        super((RedmineConfig) config);
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
	public SyncResult saveData(List<GTask> tasks, ProgressMonitor monitor) {
		return new RedmineTaskSaver(config).saveData(tasks, monitor);
	}
}
