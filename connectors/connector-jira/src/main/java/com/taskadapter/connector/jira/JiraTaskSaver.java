package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rpc.soap.client.*;
import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;

public class JiraTaskSaver extends AbstractTaskSaver<JiraConfig> {

    private JiraConnection connection;
    private Iterable<Version> versions;
    private Iterable<BasicComponent> components;
    private final JiraTaskConverter converter;

    public JiraTaskSaver(JiraConfig config) {
        super(config);
        converter = new JiraTaskConverter(config);
    }

    @Override
    public void beforeSave() throws ConnectorException {
        try {
            connection = JiraConnectionFactory.createConnection(config.getServerInfo());

            Iterable<IssueType> issueTypeList = checkDefaultIssueTypeExistsOnServer();

            converter.setIssueTypeList(issueTypeList);

            versions = connection.getVersions(config.getProjectKey());
            components = connection.getComponents(config.getProjectKey());

            /* Need to load Jira server priorities because what we store in the config files is a
                * priority name (string), while Jira returns the number value of the issue priority */
            Iterable<Priority> jiraPriorities = connection.getPriorities();
            converter.setPriorities(jiraPriorities);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    private Iterable<IssueType> checkDefaultIssueTypeExistsOnServer() throws RemoteException, BadConfigException {
        //check if default issue type exists in Jira
        boolean found = false;
        Iterable<IssueType> issueTypeList = connection.getIssueTypeList();
        for (IssueType anIssueTypeList : issueTypeList) {
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
    protected IssueInput convertToNativeTask(GTask task) {
        return converter.convertToJiraIssue(versions, components, task);
    }

    @Override
    protected GTask createTask(Object nativeTask) throws ConnectorException {
        BasicIssue createdIssue = connection.createIssue((IssueInput) nativeTask);
        return converter.convertToGenericTask(createdIssue);
    }

    @Override
    protected void updateTask(String taskId, Object nativeTask) throws ConnectorException {
        connection.updateIssue(taskId, (IssueInput) nativeTask);
    }

    @Override
    protected void saveRelations(List<GRelation> relations) throws UnsupportedConnectorOperation {
        for (GRelation gRelation : relations) {
            String taskKey = gRelation.getTaskKey();
            String relatedTaskKey = gRelation.getRelatedTaskKey();
            connection.linkIssue(taskKey, relatedTaskKey, gRelation.getType().toString());
        }
//        throw new UnsupportedConnectorOperation("saveRelations");
    }

}
