package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.util.concurrent.Promise;
import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
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
    public String createTask(IssueWrapper wrapper) throws ConnectorException {
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
            String taskKey = gRelation.getTaskKey();
            String relatedTaskKey = gRelation.getRelatedTaskKey();
            String linkTypeName;
            if (gRelation.getType().equals(GRelation.TYPE.precedes)) {
                linkTypeName = JiraConstants.getJiraLinkNameForPrecedes();
                LinkIssuesInput input = new LinkIssuesInput(taskKey, relatedTaskKey, linkTypeName);
                final Promise<Void> promise = client.getIssueClient().linkIssue(input);
                promise.claim();
            } else {
                LOG.info("Ignoring not supported issue link type: " + gRelation.getType().toString() + ". JIRA connector only supports " + GRelation.TYPE.precedes.toString());
            }
        }
    }
}
