package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineProcessingException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;

import java.util.List;

final class RedmineTaskSaver implements RelationSaver, BasicIssueSaveAPI<Issue> {

    private final RedmineManager mgr;
    private final Project rmProject;
    private final RedmineConfig config;

    public RedmineTaskSaver(RedmineManager mgr, Project rmProject,
            RedmineConfig config) {
        this.mgr = mgr;
        this.rmProject = rmProject;
        this.config = config;
    }

    @Override
    public String createTask(Issue nativeTask) throws ConnectorException {
        try {
            Issue newIssue = mgr.createIssue(rmProject.getIdentifier(),
                    nativeTask);
            return newIssue.getId().toString();
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    @Override
    public void updateTask(String taskId, Issue rmIssue) throws ConnectorException {
        rmIssue.setId(Integer.parseInt(taskId));
        try {
            mgr.update(rmIssue);

            if (config.getSaveIssueRelations()) {
                mgr.deleteIssueRelationsByIssueId(rmIssue.getId());
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
                mgr.createRelation(taskKey, relatedTaskKey, gRelation.getType().toString());
            }
        } catch (RedmineProcessingException e) {
            throw new RelationCreationException(e);
        } catch (RedmineException e) {
            throw new CommunicationException(e);
        }
    }

}
