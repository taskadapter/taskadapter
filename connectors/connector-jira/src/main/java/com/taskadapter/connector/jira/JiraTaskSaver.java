package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;

import java.util.List;

final class JiraTaskSaver implements RelationSaver, BasicIssueSaveAPI<IssueInput> {

    private final JiraConnection connection;

    public JiraTaskSaver(JiraConnection connection) {
        this.connection = connection;
    }

    @Override
    public String createTask(IssueInput nativeTask) throws ConnectorException {
        BasicIssue createdIssue = connection.createIssue(nativeTask);
        return createdIssue.getKey();
    }

    @Override
    public void updateTask(String taskId, IssueInput nativeTask) throws ConnectorException {
        connection.updateIssue(taskId, nativeTask);
    }

    @Override
    public void saveRelations(List<GRelation> relations) throws ConnectorException {
        for (GRelation gRelation : relations) {
            String taskKey = gRelation.getTaskKey();
            String relatedTaskKey = gRelation.getRelatedTaskKey();
            connection.linkIssue(taskKey, relatedTaskKey, gRelation.getType());
        }
    }
}
