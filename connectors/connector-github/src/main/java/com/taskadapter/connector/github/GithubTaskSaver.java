package com.taskadapter.connector.github;

import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.IOException;

public class GithubTaskSaver implements BasicIssueSaveAPI<Issue> {
    private final IssueService issueService;
    private final String userName;
    private final String projectKey;

    public GithubTaskSaver(IssueService issueService, String userName, String projectKey) {
        this.issueService = issueService;
        this.userName = userName;
        this.projectKey = projectKey;
    }

    public TaskId createTask(Issue issue) throws ConnectorException {
        var repositoryName = projectKey;
        try {
            var createdIssue = issueService.createIssue(userName, repositoryName, issue);
            return new TaskId((long) createdIssue.getNumber(), createdIssue.getNumber() + "");
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
    }

    public void updateTask(Issue issue) throws ConnectorException {
        try {
            issueService.editIssue(userName, projectKey, issue);
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
    }
}
