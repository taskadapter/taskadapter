package com.taskadapter.connector.github;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.common.TaskConverter;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.model.GTask;

public class GithubConnector extends AbstractConnector<GithubConfig> {

    public Descriptor getDescriptor() {
        return GithubDescriptor.instance;
    }

    public GithubConnector(GithubConfig config) {
        super(config);
    }

    public void updateRemoteIDs(ConnectorConfig sourceConfig, SyncResult actualSaveResult, ProgressMonitor monitor) {
        throw new RuntimeException("not implemented for this connector");
    }
    
    @Override
    public GTask loadTaskByKey(String key) {
        try {
            IssueService issueService = new ConnectionFactory(config)
                    .getIssueService();

            Integer id = Integer.valueOf(key);
            Issue issue = issueService.getIssue(config.getServerInfo()
                    .getUserName(), config.getProjectKey(), id);
            return getTaskConverter().convertToGenericTask(issue);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }
    
    private TaskConverter<Issue> getTaskConverter() {
        ConnectionFactory cf = new ConnectionFactory(config);
        return new GithubTaskConverter(cf.getUserService());
    }
    
    private IssueService getIssueService() {
        ConnectionFactory cf = new ConnectionFactory(config);
        return cf.getIssueService();
    }

    @Override
    public List<GTask> loadData(ProgressMonitor monitorIGNORED) {
        try {
            Map<String, String> issuesFilter = new HashMap<String, String>();
            issuesFilter.put(IssueService.FILTER_STATE,
                    config.getIssueState() == null ? IssueService.STATE_OPEN
                            : config.getIssueState());

            IssueService issueService = getIssueService();
            List<Issue> issues = issueService.getIssues(config.getServerInfo()
                    .getUserName(), config.getProjectKey(), issuesFilter);

            return getTaskConverter().convertToGenericTaskList(issues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    

    @Override
    public SyncResult saveData(List<GTask> tasks, ProgressMonitor monitor) {
    	return new GithubTaskSaver(config).saveData(tasks, monitor);
    }
}
