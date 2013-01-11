package com.taskadapter.connector.github;

import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.IOException;

class GithubTaskSaver implements BasicIssueSaveAPI<Issue> {

    private IssueService issueService;

    private final String userName;
    private final String projectKey;

    public GithubTaskSaver(IssueService issueService, String userName,
            String projectKey) {
        this.issueService = issueService;
        this.userName = userName;
        this.projectKey = projectKey;
    }

    @Override
    public String createTask(Issue issue) throws ConnectorException {
        String repositoryName = projectKey;
        try {
            final Issue createdIssue = issueService.createIssue(userName,
                    repositoryName, issue);
            return String.valueOf(createdIssue.getNumber());
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
    }

    @Override
    public void updateTask(String taskId, Issue issue)
            throws ConnectorException {
        try {
            issueService.editIssue(userName, projectKey, issue);
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
    }
}
