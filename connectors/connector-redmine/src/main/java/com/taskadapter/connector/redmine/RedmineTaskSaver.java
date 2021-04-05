package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineProcessingException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.internal.Transport;

import java.util.List;

public final class RedmineTaskSaver implements RelationSaver, BasicIssueSaveAPI<Issue> {

    private final RedmineConfig config;
    private final Transport transport;

    public RedmineTaskSaver(Transport transport, RedmineConfig config) {
        this.transport = transport;
        this.config = config;
    }

    @Override
    public TaskId createTask(Issue nativeTask) throws ConnectorException {
        try {
            nativeTask.setTransport(transport);
            Issue newIssue = nativeTask.create();
            return new TaskId(newIssue.getId().longValue(), newIssue.getId().toString());
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    @Override
    public void updateTask(Issue rmIssue) throws ConnectorException {
        try {
            rmIssue.setTransport(transport);
            rmIssue.update();

            // TODO this is here and not in saveRelations() because it needs issue ID to delete the old relations for
            if (config.getSaveIssueRelations()) {
                for (IssueRelation relation : rmIssue.getRelations()) {
                    relation.delete();
                }
            }
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    @Override
    public void saveRelations(List<GRelation> relations) throws ConnectorException {
        try {
            for (GRelation gRelation : relations) {
                TaskId taskId = gRelation.getTaskId();
                Integer intTaskId = taskId.getId().intValue();
                TaskId relatedTaskKey = gRelation.getRelatedTaskId();
                Integer intRelatedId = relatedTaskKey.getId().intValue();
                new IssueRelation(transport, intTaskId, intRelatedId, gRelation.getType().toString())
                        .create();
            }
        } catch (RedmineProcessingException e) {
            throw new RelationCreationException(e);
        } catch (RedmineException e) {
            throw new CommunicationException(e);
        }
    }

}
