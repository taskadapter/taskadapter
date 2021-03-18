package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.input.IssueInput;

import java.util.Optional;

public class IssueWrapper {
    private String key;
    private IssueInput issueInput;
    private String status;
    private Optional<String> taskType;

    public IssueWrapper(String key, IssueInput issueInput, String status, Optional<String> taskType) {
        this.key = key;
        this.issueInput = issueInput;
        this.status = status;
        this.taskType = taskType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public IssueInput getIssueInput() {
        return issueInput;
    }

    public void setIssueInput(IssueInput issueInput) {
        this.issueInput = issueInput;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Optional<String> getTaskType() {
        return taskType;
    }

    public void setTaskType(Optional<String> taskType) {
        this.taskType = taskType;
    }
}
