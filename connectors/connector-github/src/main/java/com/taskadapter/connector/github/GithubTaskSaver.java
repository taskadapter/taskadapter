package com.taskadapter.connector.github;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;

public class GithubTaskSaver extends AbstractTaskSaver<GithubConfig, Issue> {

    private final Mappings mappings;
    private IssueService issueService;

    private GithubToGTask taskConverter;
    private final UserService userService;

    public GithubTaskSaver(GithubConfig config, Mappings mappings, ProgressMonitor monitor) {
        super(config, monitor);
        this.mappings = mappings;
        ConnectionFactory ghConnector = new ConnectionFactory(config.getServerInfo());
        issueService = ghConnector.getIssueService();
        userService = ghConnector.getUserService();
        taskConverter = new GithubToGTask();
    }

    @Override
    protected Issue convertToNativeTask(GTask task) throws ConnectorException {
        return new GTaskToGithub(userService, mappings).toIssue(task);
    }

    @Override
    protected GTask createTask(Issue issue) throws ConnectorException {
        String userName = config.getServerInfo().getUserName();
        String repositoryName = config.getProjectKey();
        try {
            Issue createdIssue = issueService.createIssue(userName, repositoryName, issue);
            return taskConverter.toGtask(createdIssue);
        } catch (IOException e) {
            throw GithubUtils.convertException(e); 
        }
    }

    @Override
    protected void updateTask(String taskId, Issue issue) throws ConnectorException {
        try {
            issueService.editIssue(config.getServerInfo().getUserName(), config.getProjectKey(), issue);
        } catch (IOException e) {
            throw GithubUtils.convertException(e); 
        }
    }
}
