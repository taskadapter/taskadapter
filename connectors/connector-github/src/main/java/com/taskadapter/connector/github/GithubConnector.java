package com.taskadapter.connector.github;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;

public class GithubConnector extends AbstractConnector<GithubConfig> {

    public Descriptor getDescriptor() {
        return GithubDescriptor.instance;
    }

    public GithubConnector(GithubConfig config) {
        super(config);
    }

    public void updateRemoteIDs(ConnectorConfig sourceConfig,
            SyncResult<Throwable> actualSaveResult, ProgressMonitor monitor)
            throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation(
                "update remote IDS not implemented for this connector");
    }
    
    @Override
    public GTask loadTaskByKey(String key) throws ConnectorException {
        IssueService issueService = new ConnectionFactory(config)
                .getIssueService();

        Integer id = Integer.valueOf(key);
        Issue issue;
        try {
            issue = issueService.getIssue(config.getServerInfo()
                    .getUserName(), config.getProjectKey(), id);
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
        return getTaskConverter().convertToGenericTask(issue);
    }
    
    private GithubTaskConverter getTaskConverter() {
        ConnectionFactory cf = new ConnectionFactory(config);
        return new GithubTaskConverter(cf.getUserService());
    }
    
    private IssueService getIssueService() {
        ConnectionFactory cf = new ConnectionFactory(config);
        return cf.getIssueService();
    }

    @Override
    public List<GTask> loadData(ProgressMonitor monitorIGNORED) throws ConnectorException {
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

        return getTaskConverter().convertToGenericTaskList(issues);
    }
    

    @Override
    public SyncResult<Throwable> saveData(List<GTask> tasks, ProgressMonitor monitor)
            throws ConnectorException {
        return new GithubTaskSaver(config).saveData(tasks, monitor);
    }
}
