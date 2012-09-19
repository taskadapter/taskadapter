package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineProcessingException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;

import java.util.ArrayList;
import java.util.List;

public class RedmineTaskSaver extends AbstractTaskSaver<RedmineConfig> {

    private RedmineManager mgr;
    private Project rmProject;
    private GTaskToRedmine converter;
    private RedmineToGTask toGTask;

    public RedmineTaskSaver(RedmineConfig config) {
        super(config);
        converter = new GTaskToRedmine(config);
        toGTask = new RedmineToGTask(config);
    }

    @Override
    public void beforeSave() throws ConnectorException {
        this.mgr = RedmineManagerFactory.createRedmineManager(config
                .getServerInfo());
        try {
            rmProject = mgr.getProjectByKey(config.getProjectKey());
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
        converter.setUsers(loadUsers());
        converter.setStatusList(loadStatusList());
    }

    private List<User> loadUsers() {
        List<User> users;
        if (config.isFindUserByName()) {
            try {
                users = mgr.getUsers();
            } catch (RedmineException e) {
                throw new RuntimeException(e);
            }
        } else {
            users = new ArrayList<User>();
        }
        return users;
    }

    private List<IssueStatus> loadStatusList() throws ConnectorException {
        List<IssueStatus> statusList;

        try {
            statusList = mgr.getStatuses();
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }

        return statusList;
    }

    @Override
    protected Issue convertToNativeTask(GTask task) {
        return converter.convertToRedmineIssue(rmProject, task);
    }

    @Override
    protected GTask createTask(Object nativeTask) throws ConnectorException {
        try {
            Issue newIssue = mgr.createIssue(rmProject.getIdentifier(),
                    (Issue) nativeTask);
            return toGTask.convertToGenericTask(newIssue);
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    @Override
    protected void updateTask(String taskId, Object nativeTask) throws ConnectorException {
        Issue rmIssue = (Issue) nativeTask;
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
    protected void saveRelations(List<GRelation> relations) {
        try {
            for (GRelation gRelation : relations) {
                int taskKey = Integer.parseInt(gRelation.getTaskKey());
                int relatedTaskKey = Integer.parseInt(gRelation
                        .getRelatedTaskKey());
                mgr.createRelation(taskKey, relatedTaskKey, gRelation.getType().toString());
            }
        } catch (RedmineProcessingException e) {
            errors.addGeneralError(new RelationCreationException(e));
        } catch (RedmineException e) {
            errors.addGeneralError(new CommunicationException(e));
        }
    }

}
