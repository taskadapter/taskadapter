package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.util.concurrent.Promise;
import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.Precedes$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final class JiraTaskSaver implements RelationSaver, BasicIssueSaveAPI<IssueWrapper> {
    private static final Logger LOG = LoggerFactory.getLogger(JiraTaskSaver.class);

    private final JiraRestClient client;

    public JiraTaskSaver(JiraRestClient client) {
        this.client = client;
    }

    @Override
    public TaskId createTask(IssueWrapper wrapper) throws ConnectorException {
        return JiraClientHelper.createTask(client, wrapper.getIssueInput());
    }

    @Override
    public void updateTask(IssueWrapper wrapper) throws ConnectorException {
        final Promise<Void> promise = client.getIssueClient().updateIssue(wrapper.getKey(), wrapper.getIssueInput());
        promise.claim();
    }

    @Override
    public void saveRelations(List<GRelation> relations) throws ConnectorException {
        for (GRelation gRelation : relations) {
            String taskKey = gRelation.taskId().key();
            String relatedTaskKey = gRelation.relatedTaskId().key();
            String linkTypeName;
            if (gRelation.type().equals(Precedes$.MODULE$)) {
                linkTypeName = JiraConstants.getJiraLinkNameForPrecedes();
                LinkIssuesInput input = new LinkIssuesInput(taskKey, relatedTaskKey, linkTypeName);
                final Promise<Void> promise = client.getIssueClient().linkIssue(input);
                promise.claim();
            } else {
                LOG.info("Ignoring not supported issue link type: " + gRelation.type().toString()
                        + ". JIRA connector only supports " + Precedes$.MODULE$);
            }
        }
    }
}
