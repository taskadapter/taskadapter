package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Issue;
import org.redmine.ta.beans.IssueStatus;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.User;

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
    public void beforeSave() {
        this.mgr = RedmineManagerFactory.createRedmineManager(config
                .getServerInfo());
        try {
            rmProject = mgr.getProjectByKey(config.getProjectKey());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        converter.setUsers(loadUsers());
        converter.setStatusList(loadStatusList());
    }

    private List<User> loadUsers() {
        List<User> users;
        if (config.isFindUserByName()) {
            try {
                users = mgr.getUsers();
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        } else {
            users = new ArrayList<User>();
        }
        return users;
    }

    private List<IssueStatus> loadStatusList() {
        List<IssueStatus> statusList;

        try {
            statusList = mgr.getStatuses();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }

        return statusList;
    }

    @Override
    protected Issue convertToNativeTask(GTask task) {
        return converter.convertToRedmineIssue(rmProject, task);
    }

    @Override
    protected GTask createTask(Object nativeTask) {
        try {
            Issue newIssue = mgr.createIssue(rmProject.getIdentifier(),
                    (Issue) nativeTask);
            return converter.convertToGenericTask(newIssue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateTask(String taskId, Object nativeTask) {
        Issue rmIssue = (Issue) nativeTask;
        rmIssue.setId(Integer.parseInt(taskId));
        try {
            mgr.update(rmIssue);

            if (config.getSaveIssueRelations()) {
                mgr.deleteIssueRelationsByIssueId(rmIssue.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        } catch (Exception e) {
            syncResult
                    .addGeneralError("Can't create Tasks Relations. Note: this feature requires Redmine 1.3.0 or newer."
                    + "\nSee http://www.redmine.org/issues/7366 ."
                    +"\nThe error reported by server is: " + e.toString());
        }
    }

}
