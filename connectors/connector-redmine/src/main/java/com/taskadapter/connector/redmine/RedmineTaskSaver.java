package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineProcessingException;
import com.taskadapter.redmineapi.bean.Issue;

import java.util.List;

public final class RedmineTaskSaver implements RelationSaver, BasicIssueSaveAPI<Issue> {

    private final IssueManager issueManager;
    private final RedmineConfig config;

    public RedmineTaskSaver(IssueManager mgr, RedmineConfig config) {
        this.issueManager = mgr;
        this.config = config;
    }

    @Override
    public TaskId createTask(Issue nativeTask) throws ConnectorException {
        try {
            Issue newIssue = issueManager.createIssue(nativeTask);
            return new TaskId(newIssue.getId().longValue(), newIssue.getId().toString());
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    @Override
    public void updateTask(Issue rmIssue) throws ConnectorException {
        try {
            issueManager.update(rmIssue);

            // TODO why is it here and not in saveRelations() method?
            if (config.getSaveIssueRelations()) {
                issueManager.deleteIssueRelationsByIssueId(rmIssue.getId());
            }
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    @Override
    public void saveRelations(List<GRelation> relations) throws ConnectorException {
        try {
            for (GRelation gRelation : relations) {
                int taskKey = Integer.parseInt(gRelation.getTaskKey());
                int relatedTaskKey = Integer.parseInt(gRelation
                        .getRelatedTaskKey());
                issueManager.createRelation(taskKey, relatedTaskKey, gRelation.getType().toString());
            }
        } catch (RedmineProcessingException e) {
            throw new RelationCreationException(e);
        } catch (RedmineException e) {
            throw new CommunicationException(e);
        }
    }

}
