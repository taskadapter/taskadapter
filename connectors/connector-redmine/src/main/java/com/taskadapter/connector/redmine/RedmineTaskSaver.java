package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;

import java.util.ArrayList;
import java.util.List;

public class RedmineTaskSaver extends AbstractTaskSaver<RedmineConfig> {

    private RedmineManager mgr;
    private Project rmProject;
    private RedmineDataConverter converter;

    public RedmineTaskSaver(RedmineConfig config) {
        super(config);
        converter = new RedmineDataConverter(config);
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
            return converter.convertToGenericTask(newIssue);
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
        } catch (RedmineException e) {
            syncResult
                    .addGeneralError("Can't create Tasks Relations. Note: this feature requires Redmine 1.3.0 or newer."
                    + "\nSee http://www.redmine.org/issues/7366 ."
                    +"\nThe error reported by server is: " + e.toString());
        }
    }

}
