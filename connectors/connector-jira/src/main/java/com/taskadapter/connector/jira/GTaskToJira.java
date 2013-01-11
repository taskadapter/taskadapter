package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicPriority;
import com.atlassian.jira.rest.client.domain.IssueFieldId;
import com.atlassian.jira.rest.client.domain.IssueType;
import com.atlassian.jira.rest.client.domain.Priority;
import com.atlassian.jira.rest.client.domain.TimeTracking;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.google.common.collect.ImmutableList;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class GTaskToJira {

    // TODO this is hardcoded!! https://www.hostedredmine.com/issues/18074
    private static final Long ISSUE_TYPE_ID = 1l;

    private final JiraConfig config;
    private final Mappings mappings;

    private final Map<String, BasicPriority> priorities = new HashMap<String, BasicPriority>();
    private final Map<String, BasicPriority> prioritiesOtherWay = new HashMap<String, BasicPriority>();
    private final Iterable<IssueType> issueTypeList;
    private final Iterable<Version> versions;
    private final Iterable<BasicComponent> components;

    public GTaskToJira(JiraConfig config, Mappings mappings,
            Iterable<IssueType> issueTypeList, Iterable<Version> versions,
            Iterable<BasicComponent> components, Iterable<Priority> jiraPriorities) {
        this.config = config;
        this.mappings = mappings;
        this.issueTypeList = issueTypeList;
        this.versions = versions;
        this.components = components;
        for (Priority jiraPriority : jiraPriorities) {
            priorities.put(jiraPriority.getName(), jiraPriority);
            prioritiesOtherWay.put(String.valueOf(jiraPriority.getId()), jiraPriority);
        }
    }

    public IssueInput convertToJiraIssue(GTask task) {
        Long issueTypeId = ISSUE_TYPE_ID;
        if (mappings.isFieldSelected(FIELD.TASK_TYPE)) {
            issueTypeId = getIssueTypeIdByName(task.getType());

            if (issueTypeId == null) {
                issueTypeId = getIssueTypeIdByName(config.getDefaultTaskType());
            }
        }

        IssueInputBuilder issueInputBuilder = new IssueInputBuilder(config.getProjectKey(), issueTypeId);

        if (mappings.isFieldSelected(FIELD.SUMMARY)) {
            issueInputBuilder.setSummary(task.getSummary());
        }

        if (mappings.isFieldSelected(FIELD.DESCRIPTION)) {
            issueInputBuilder.setDescription(task.getDescription());
        }

        Version affectedVersion = getVersion(versions, config.getAffectedVersion());
        Version fixForVersion = getVersion(versions, config.getFixForVersion());
        //RemoteCustomFieldValue[] customValues = getCustomFieldsForIssue(config.getCustomFields());
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

/*        if (customValues.length != 0) {
            issue.setCustomFieldValues(customValues);
        }*/


        if (mappings.isFieldSelected(FIELD.DUE_DATE) && task.getDueDate() != null) {
            DateTime dueDateTime = new DateTime(task.getDueDate());
            issueInputBuilder.setDueDate(dueDateTime);
        }

        if (mappings.isFieldSelected(FIELD.ASSIGNEE)) {
            setAssignee(task, issueInputBuilder);
        }

        if (mappings.isFieldSelected(FIELD.PRIORITY)) {
            String jiraPriorityName = config.getPriorities().getPriorityByMSP(task.getPriority());

            if (!jiraPriorityName.isEmpty()) {
                issueInputBuilder.setPriority(priorities.get(jiraPriorityName));
            }
        }

        Float estimatedHours = task.getEstimatedHours();
        if (mappings.isFieldSelected(FIELD.ESTIMATED_TIME) && (estimatedHours != null)) {
            TimeTracking timeTracking = new TimeTracking(Math.round(estimatedHours * 60), null, null);
            issueInputBuilder.setFieldValue(IssueFieldId.TIMETRACKING_FIELD.id, timeTracking);
        }

        return issueInputBuilder.build();
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
}
