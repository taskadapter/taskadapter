package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubConnector implements Connector<GithubConfig> {

    /**
     * Keep it the same to enable backward compatibility with the existing
     * config files.
     */
    public static final String ID = "Github";

    private GithubConfig config;

    public GithubConnector(GithubConfig config) {
        this.config = config;
    }

    public void updateRemoteIDs(ConnectorConfig sourceConfig,
            Map<Integer, String> remoteIds, ProgressMonitor monitor, Mappings mappings)
            throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("updateRemoteIDs");
    }

    @Override
    public GTask loadTaskByKey(String key, Mappings mappings) throws ConnectorException {
        IssueService issueService = new ConnectionFactory(config.getServerInfo()).getIssueService();

        Integer id = Integer.valueOf(key);
        Issue issue;
        try {
            issue = issueService.getIssue(config.getServerInfo()
                    .getUserName(), config.getProjectKey(), id);
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
        return new GithubToGTask().toGtask(issue);
    }
    
    private IssueService getIssueService() {
        ConnectionFactory cf = new ConnectionFactory(config.getServerInfo());
        return cf.getIssueService();
    }

    @Override
    public List<GTask> loadData(Mappings mappings, ProgressMonitor monitorIGNORED) throws ConnectorException {
        Map<String, String> issuesFilter = new HashMap<String, String>();
        issuesFilter.put(IssueService.FILTER_STATE,
                config.getIssueState() == null ? IssueService.STATE_OPEN
                        : config.getIssueState());

        IssueService issueService = getIssueService();
        List<Issue> issues;
        try {
            issues = issueService.getIssues(config.getServerInfo()
                    .getUserName(), config.getProjectKey(), issuesFilter);
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }

        return new GithubToGTask().toGTaskList(issues);
    }
    

    @Override
    public SyncResult<TaskSaveResult, TaskErrors<Throwable>> saveData(List<GTask> tasks, ProgressMonitor monitor, Mappings mappings)
            throws ConnectorException {
        return new GithubTaskSaver(config, mappings).saveData(tasks, monitor);
    }
}
