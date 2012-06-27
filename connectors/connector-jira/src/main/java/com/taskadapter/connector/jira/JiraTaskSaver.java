package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.*;
import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;

import java.net.MalformedURLException;
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
    public void beforeSave() throws ConnectorException {
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
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        }
    }

    private RemoteIssueType[] checkDefaultIssueTypeExistsOnServer() throws RemoteException, BadConfigException {
        //check if default issue type exists in Jira
        boolean found = false;
        RemoteIssueType[] issueTypeList = connection.getIssueTypeList(config.getProjectKey());
        for (RemoteIssueType anIssueTypeList : issueTypeList) {
            if (anIssueTypeList.getName().equals(config.getDefaultTaskType())) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new BadConfigException("Default issue type " + config.getDefaultTaskType() + " does not exist in Jira");
        }
        return issueTypeList;
    }

    // TODO move this method to JiraTaskConverter class
    @Override
    protected RemoteIssue convertToNativeTask(GTask task) {
        return converter.convertToJiraIssue(versions, components, task);
    }

    @Override
    protected GTask createTask(Object nativeTask) throws ConnectorException {
        try {
            RemoteIssue createdIssue = connection.createIssue((RemoteIssue) nativeTask);
            return converter.convertToGenericTask(createdIssue);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        }
    }

    @Override
    protected void updateTask(String taskId, Object nativeTask) throws ConnectorException {
        try {
            connection.updateIssue(taskId, (RemoteIssue) nativeTask);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        }
    }

    @Override
    protected void saveRelations(List<GRelation> relations) throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("saveRelations");
        /* SEE this text in AbstractTaskSaver:

          // XXX get rid of the conversion, it won't work with Jira,
          // which has String Keys like "TEST-12"
          Integer relatedTaskId = Integer.parseInt(oldRelation.getRelatedTaskKey());
          */
    }

}
