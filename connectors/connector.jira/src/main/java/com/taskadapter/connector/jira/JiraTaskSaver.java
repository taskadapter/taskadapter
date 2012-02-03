package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.*;
import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;

import java.rmi.RemoteException;
import java.util.List;

public class JiraTaskSaver extends AbstractTaskSaver<JiraConfig> {

    private JiraConnection connection;
    private RemoteVersion[] versions;
    private RemoteComponent[] components;
    private final JiraTaskConverter converter;

    public JiraTaskSaver(JiraConfig config) {
        super(config);
        converter = new JiraTaskConverter(config);
    }

    @Override
    public void beforeSave() {
        try {
            connection = JiraConnectionFactory.createConnection(config.getServerInfo());

            RemoteIssueType[] issueTypeList = checkDefaultIssueTypeExistsOnServer();

            converter.setIssueTypeList(issueTypeList);

            versions = connection.getVersions(config.getProjectKey());
            components = connection.getComponents(config.getProjectKey());

            /* Need to load Jira server priorities because what we store in the config files is a
                * priority name (string), while Jira returns the number value of the issue priority */
            RemotePriority[] jiraPriorities = connection.getPriorities();
            converter.setPriorities(jiraPriorities);
        } catch (RemoteException e) {
            throw new JiraException(e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private RemoteIssueType[] checkDefaultIssueTypeExistsOnServer()
            throws RemotePermissionException, RemoteAuthenticationException,
            RemoteException, ValidationException {
        //check if default issue type exists in Jira
        boolean found = false;
        RemoteIssueType[] issueTypeList = connection.getIssueTypeList(config.getProjectKey());
        for (int i = 0; i < issueTypeList.length; i++) {
            if (issueTypeList[i].getName().equals(config.getDefaultTaskType())) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ValidationException("Default issue type " + config.getDefaultTaskType() + " does not exist in Jira");
        }
        return issueTypeList;
    }

    // TODO move this method to JiraTaskConverter class
    @Override
    protected RemoteIssue convertToNativeTask(GTask task) {
        return converter.convertToJiraIssue(versions, components, task);
    }

    @Override
    protected GTask createTask(Object nativeTask) {
        try {
            RemoteIssue createdIssue = connection.createIssue((RemoteIssue) nativeTask);
            return converter.convertToGenericTask(createdIssue);
        } catch (RemoteException e) {
            throw new JiraException(e);
        }
    }

    @Override
    protected void updateTask(String taskId, Object nativeTask) {
        try {
            connection.updateIssue(taskId, (RemoteIssue) nativeTask);
        } catch (RemoteException e) {
            throw new JiraException(e);
        }
    }

    @Override
    protected void saveRelations(List<GRelation> relations) {
        System.out.println("not implemented");
        /* SEE this text in AbstractTaskSaver:

          // XXX get rid of the conversion, it won't work with Jira,
          // which has String Keys like "TEST-12"
          Integer relatedTaskId = Integer.parseInt(oldRelation.getRelatedTaskKey());
          */
    }

}
