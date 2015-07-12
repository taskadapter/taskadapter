package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;

import java.util.List;

final class JiraTaskSaver implements RelationSaver, BasicIssueSaveAPI<IssueWrapper> {

    private final JiraConnection connection;

    public JiraTaskSaver(JiraConnection connection) {
        this.connection = connection;
    }

    @Override
    public String createTask(IssueWrapper wrapper) throws ConnectorException {
        BasicIssue createdIssue = connection.createIssue(wrapper.getIssueInput());
        return createdIssue.getKey();
    }

    @Override
    public void updateTask(IssueWrapper wrapper) throws ConnectorException {
        connection.updateIssue(wrapper.getKey(), wrapper.getIssueInput());
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
