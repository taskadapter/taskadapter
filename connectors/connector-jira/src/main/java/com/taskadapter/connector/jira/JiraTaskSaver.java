package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GRelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JiraTaskSaver implements RelationSaver, BasicIssueSaveAPI<IssueWrapper> {
    private static final Logger logger = LoggerFactory.getLogger(JiraTaskSaver.class);

    private final JiraRestClient client;
    private final Iterable<IssueType> issueTypeList;
    private final String defaultTaskTypeName;
    private final String defaultIssueTypeForSubtasks;

    public JiraTaskSaver(JiraRestClient client, Iterable<IssueType> issueTypeList,
                         String defaultTaskTypeName, String defaultIssueTypeForSubtasks) {
        this.client = client;
        this.issueTypeList = issueTypeList;
        this.defaultTaskTypeName = defaultTaskTypeName;
        this.defaultIssueTypeForSubtasks = defaultIssueTypeForSubtasks;
    }

    @Override
    public TaskId createTask(IssueWrapper wrapper) throws ConnectorException {
        var issueTypeName = JiraTaskTypeResolver.resolveIssueTypeNameForCreate(wrapper, defaultTaskTypeName, defaultIssueTypeForSubtasks);
        var issueTypeId = findIssueTypeId(issueTypeName);
        var issueWithTypeIdSet = getWithTaskTypeSet(wrapper, issueTypeId);

        var taskId = JiraClientHelper.createTask(client, issueWithTypeIdSet);

        // yes, reload the issue we just created. JIRA API is horrific
        var existingIssue = client.getIssueClient().getIssue(taskId.getKey()).claim();
        updateStatusIfNeeded(existingIssue, wrapper.getStatus());
        return taskId;
    }

    @Override
    public void updateTask(IssueWrapper wrapper) throws ConnectorException {
        var existingIssue = client.getIssueClient().getIssue(wrapper.getKey()).claim();

        var issueTypeId = (wrapper.getTaskType().isPresent() && !wrapper.getTaskType().get().isEmpty()) ?
                findIssueTypeId(wrapper.getTaskType().get())
                :
                existingIssue.getIssueType().getId();


        var issueWithTypeIdSet = getWithTaskTypeSet(wrapper, issueTypeId);
        client.getIssueClient().updateIssue(wrapper.getKey(), issueWithTypeIdSet).claim();

        updateStatusIfNeeded(existingIssue, wrapper.getStatus());
    }

    private static IssueInput getWithTaskTypeSet(IssueWrapper wrapper, Long issueTypeId) {
        var newMap = wrapper.getIssueInput().getFields().entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals("issuetype"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        newMap.put("issuetype", createType(issueTypeId));
        return new IssueInput(newMap, List.of());
    }

    private static FieldInput createType(Long issueTypeId) {
        return new FieldInput(IssueFieldId.ISSUE_TYPE_FIELD, ComplexIssueInputFieldValue.with("id", issueTypeId.toString()));
    }

    private void updateStatusIfNeeded(Issue issue, String requiredStatus) {
        var oldStatus = issue.getStatus().getName();
        if (requiredStatus != null && !oldStatus.equals(requiredStatus)) {
            updateStatus(issue, requiredStatus);
        }
    }

    private void updateStatus(Issue existingIssue, String newStatus) {
        var transitions = client.getIssueClient().getTransitions(existingIssue).claim();

        var transitionInputMaybe = StreamSupport.stream(transitions.spliterator(), false)
                .filter(t -> t.getName().equals(newStatus))
                .findFirst()
                .map(t -> new TransitionInput(t.getId()));

        transitionInputMaybe.ifPresent(
                transitionInput -> client.getIssueClient().transition(existingIssue, transitionInput).claim());
    }

    @Override
    public void saveRelations(List<GRelation> relations) throws ConnectorException {
        relations.stream().forEach(
                relation -> {
                    var taskKey = relation.getTaskId().getKey();
                    var relatedTaskKey = relation.getRelatedTaskId().getKey();

                    if (relation.getType() == GRelationType.precedes) {
                        var linkTypeName = JiraConstants.getJiraLinkNameForPrecedes();
                        var input = new LinkIssuesInput(taskKey, relatedTaskKey, linkTypeName);
                        var promise = client.getIssueClient().linkIssue(input);
                        promise.claim();
                    } else {
                        logger.info("Ignoring not supported issue link type: " + relation.getType()
                                + ". JIRA connector only supports " + GRelationType.precedes);
                    }
                }
        );
    }

    /**
     * Finds an issue type id to use.
     *
     * @return issue type id.
     */
    private Long findIssueTypeId(String taskType) {
        var explicitTypeId = getIssueTypeIdByName(taskType);
        return explicitTypeId;
    }

    private Long getIssueTypeIdByName(String issueTypeName) {
        return StreamSupport.stream(issueTypeList.spliterator(), false)
                .filter(i -> i.getName().equals(issueTypeName))
                .findFirst()
                .map(i -> i.getId())
                .orElse(null);
    }
}
