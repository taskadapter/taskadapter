package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
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

    private final JiraConnection connection;
    private final GTaskToJira converter;
    private final JiraToGTask jiraToGTask;

    public JiraTaskSaver(JiraConfig config, Mappings mappings, ProgressMonitor monitor) throws ConnectorException {
        super(config, monitor);
        jiraToGTask = new JiraToGTask(config.getPriorities());
        
        try {
            connection = JiraConnectionFactory.createConnection(config.getServerInfo());

            final Iterable<IssueType> issueTypeList = checkDefaultIssueTypeExistsOnServer();
            final Iterable<Version> versions = connection.getVersions(config.getProjectKey());
            final Iterable<BasicComponent> components = connection.getComponents(config.getProjectKey());

            /* Need to load Jira server priorities because what we store in the config files is a
                * priority name (string), while Jira returns the number value of the issue priority */
            final Iterable<Priority> jiraPriorities = connection.getPriorities();
            
            converter = new GTaskToJira(config, mappings, issueTypeList,
                    versions, components, jiraPriorities);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    private Iterable<IssueType> checkDefaultIssueTypeExistsOnServer() throws BadConfigException {
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

    // TODO move this method to GTaskToJira class
    @Override
    protected IssueInput convertToNativeTask(GTask task) {
        return converter.convertToJiraIssue(task);
    }

    @Override
    protected GTask createTask(Object nativeTask) throws ConnectorException {
        BasicIssue createdIssue = connection.createIssue((IssueInput) nativeTask);
        return jiraToGTask.convertToGenericTask(createdIssue);
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
            connection.linkIssue(taskKey, relatedTaskKey, gRelation.getType());
        }
    }
}
