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
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GTaskToJira implements ConnectorConverter<GTask, IssueWrapper> {

    private final JiraConfig config;
    private final Collection<FIELD> fieldsToExport;

    private final Map<String, BasicPriority> priorities = new HashMap<>();
    private final Iterable<IssueType> issueTypeList;
    private final Iterable<Version> versions;
    private final Iterable<BasicComponent> components;

    public GTaskToJira(JiraConfig config, Collection<FIELD> fieldsToExport,
            Iterable<IssueType> issueTypeList, Iterable<Version> versions,
            Iterable<BasicComponent> components, Iterable<Priority> jiraPriorities) {
        this.config = config;
        this.fieldsToExport = fieldsToExport;
        this.issueTypeList = issueTypeList;
        this.versions = versions;
        this.components = components;
        for (Priority jiraPriority : jiraPriorities) {
            priorities.put(jiraPriority.getName(), jiraPriority);
        }
    }

    public IssueWrapper convertToJiraIssue(GTask task) {
        
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder(
                config.getProjectKey(), findIssueTypeId(task));
        
        if (task.getParentKey() != null && fieldsToExport.contains(FIELD.TASK_TYPE)) {
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
        
        if (fieldsToExport.contains(FIELD.SUMMARY)) {
            issueInputBuilder.setSummary(task.getSummary());
        }

        if (fieldsToExport.contains(FIELD.DESCRIPTION)) {
            issueInputBuilder.setDescription(task.getDescription());
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

        if (fieldsToExport.contains(FIELD.DUE_DATE) && task.getDueDate() != null) {
            DateTime dueDateTime = new DateTime(task.getDueDate());
            issueInputBuilder.setDueDate(dueDateTime);
        }

        if (fieldsToExport.contains(FIELD.ASSIGNEE)) {
            setAssignee(task, issueInputBuilder);
        }

        if (fieldsToExport.contains(FIELD.PRIORITY)) {
            final Integer priorityNumber = task.getPriority();
            final String jiraPriorityName = config.getPriorities().getPriorityByMSP(priorityNumber);
            if (!jiraPriorityName.isEmpty()) {
                final BasicPriority priority = priorities.get(jiraPriorityName);
                if (priority != null) {
                    issueInputBuilder.setPriority(priority);
                }
            }
        }

        Float estimatedHours = task.getEstimatedHours();
        if (fieldsToExport.contains(FIELD.ESTIMATED_TIME) && (estimatedHours != null)) {
            TimeTracking timeTracking = new TimeTracking(Math.round(estimatedHours * 60), null, null);
            issueInputBuilder.setFieldValue(IssueFieldId.TIMETRACKING_FIELD.id, timeTracking);
        }

        if (fieldsToExport.contains(FIELD.ENVIRONMENT)) {
            issueInputBuilder.setFieldValue("environment", task.getEnvironment());
        }

        final IssueInput issueInput = issueInputBuilder.build();
        return new IssueWrapper(task.getKey(), issueInput);
    }
    
    /**
     * Finds an issue type id to use.
     * @param task task to get an issue id.
     * @return issue type id.
     */
    private Long findIssueTypeId(GTask task) {
        /* Use explicit task type when possible. */
        if (fieldsToExport.contains(FIELD.TASK_TYPE)) {
            final Long explicitTypeId = getIssueTypeIdByName(task.getType());
            if (explicitTypeId != null)
                return explicitTypeId;
        }
        
        /* Use default type for the task when  */
        return getIssueTypeIdByName(task.getParentKey() == null ? config
                .getDefaultTaskType() : config.getDefaultIssueTypeForSubtasks());
    }

    private void setAssignee(GTask task, IssueInputBuilder issue) {
        GUser ass = task.getAssignee();

        if ((ass != null) && (ass.getLoginName() != null)) {
            issue.setAssigneeName(ass.getLoginName());
        }
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
