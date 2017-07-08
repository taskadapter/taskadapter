package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.google.common.collect.ImmutableList;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class GTaskToJira implements ConnectorConverter<GTask, IssueWrapper> {

    private final JiraConfig config;

    private final Map<String, BasicPriority> priorities = new HashMap<>();
    private final Iterable<IssueType> issueTypeList;
    private final Iterable<Version> versions;
    private final Iterable<BasicComponent> components;

    GTaskToJira(JiraConfig config,
                Iterable<IssueType> issueTypeList, Iterable<Version> versions,
                Iterable<BasicComponent> components, Iterable<Priority> jiraPriorities) {
        this.config = config;
        this.issueTypeList = issueTypeList;
        this.versions = versions;
        this.components = components;
        for (Priority jiraPriority : jiraPriorities) {
            priorities.put(jiraPriority.getName(), jiraPriority);
        }
    }

    IssueWrapper convertToJiraIssue(GTask task) {

        IssueInputBuilder issueInputBuilder = new IssueInputBuilder(
                config.getProjectKey(), findIssueTypeId(task));

        if (task.getParentKey() != null) {
            /* 
             * See:
             * http://stackoverflow.com/questions/14699893/how-to-create-subtasks-using-jira-rest-java-client
             */
            final Map<String, Object> parent = new HashMap<>();
            parent.put("key", task.getParentKey());
            final FieldInput parentField = new FieldInput("parent",
                    new ComplexIssueInputFieldValue(parent));
            issueInputBuilder.setFieldInput(parentField);
        }

        for (Map.Entry<String, Object> row : task.getFields().entrySet()) {
            processField(issueInputBuilder, row.getKey(), row.getValue());
        }

        Version affectedVersion = getVersion(versions, config.getAffectedVersion());
        Version fixForVersion = getVersion(versions, config.getFixForVersion());
        BasicComponent component = getComponent(components, config.getComponent());

        if (affectedVersion != null) {
            issueInputBuilder.setAffectedVersions(ImmutableList.of(affectedVersion));
        }

        if (fixForVersion != null) {
            issueInputBuilder.setFixVersions(ImmutableList.of(fixForVersion));
        }

        if (component != null) {
            issueInputBuilder.setComponents(ImmutableList.of(component));
        }

        final IssueInput issueInput = issueInputBuilder.build();
        return new IssueWrapper(task.getKey(), issueInput);
    }

    private void processField(IssueInputBuilder issueInputBuilder, String fieldName, Object value) {

        if (fieldName.equals(JiraField.summary())) {
            issueInputBuilder.setSummary((String) value);
        }

        if (fieldName.equals(JiraField.description())) {
            issueInputBuilder.setDescription((String) value);
        }

        if (fieldName.equals(JiraField.dueDate()) && value != null) {
            DateTime dueDateTime = new DateTime(value);
            issueInputBuilder.setDueDate(dueDateTime);
        }

        if (fieldName.equals(JiraField.assignee()) && value != null) {
            issueInputBuilder.setAssigneeName((String) value);
        }

        if (fieldName.equals(JiraField.priority())) {
            final Integer priorityNumber = (Integer) value;
            final String jiraPriorityName = config.getPriorities().getPriorityByMSP(priorityNumber);
            if (!jiraPriorityName.isEmpty()) {
                final BasicPriority priority = priorities.get(jiraPriorityName);
                if (priority != null) {
                    issueInputBuilder.setPriority(priority);
                }
            }
        }

        if (fieldName.equals(JiraField.estimatedTime()) && value != null) {
            Float estimatedHours = (Float) value;
            TimeTracking timeTracking = new TimeTracking(Math.round(estimatedHours * 60), null, null);
            issueInputBuilder.setFieldValue(IssueFieldId.TIMETRACKING_FIELD.id, timeTracking);
        }

        if (fieldName.equals(JiraField.environment())) {
            issueInputBuilder.setFieldValue(fieldName, value);
        }

    }

    /**
     * Finds an issue type id to use.
     *
     * @param task task to get an issue id.
     * @return issue type id.
     */
    private Long findIssueTypeId(GTask task) {
        /* Use explicit task type when possible. */
        Object value = task.getValue(JiraField.taskType());
        final Long explicitTypeId = getIssueTypeIdByName((String) value);
            if (explicitTypeId != null)
                return explicitTypeId;

        /* Use default type for the task when  */
        return getIssueTypeIdByName(task.getParentKey() == null ? config
                .getDefaultTaskType() : config.getDefaultIssueTypeForSubtasks());
    }

    private static Version getVersion(Iterable<Version> versions, String versionName) {
        if (versionName.isEmpty()) {
            return null;
        }
        for (Version v : versions) {
            if (v.getName().equals(versionName)) {
                return v;
            }
        }
        return null;
    }

    private static BasicComponent getComponent(Iterable<BasicComponent> objects, String name) {
        for (BasicComponent o : objects) {
            if (o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }

    private Long getIssueTypeIdByName(String issueTypeName) {
        for (IssueType anIssueTypeList : issueTypeList) {
            if (anIssueTypeList.getName().equals(issueTypeName)) {
                return anIssueTypeList.getId();
            }
        }
        return null;
    }

    @Override
    public IssueWrapper convert(GTask source) throws ConnectorException {
        return convertToJiraIssue(source);
    }
}
