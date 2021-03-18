package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.AddressableNamedEntity;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.google.common.collect.ImmutableList;
import com.taskadapter.connector.common.ValueTypeResolver;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.AssigneeLoginName;
import com.taskadapter.model.Children;
import com.taskadapter.model.Components;
import com.taskadapter.model.Description;
import com.taskadapter.model.DueDate;
import com.taskadapter.model.EstimatedTime;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Id;
import com.taskadapter.model.Key;
import com.taskadapter.model.ParentKey;
import com.taskadapter.model.Relations;
import com.taskadapter.model.ReporterLoginName;
import com.taskadapter.model.SourceSystemId;
import com.taskadapter.model.Summary;
import com.taskadapter.model.TaskStatus;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GTaskToJira implements ConnectorConverter<GTask, IssueWrapper> {

    private JiraConfig config;
    private CustomFieldResolver customFieldResolver;
    private Iterable<Version> versions;
    private Iterable<BasicComponent> components;
    private final Map<String, Priority> priorities;

    public GTaskToJira(JiraConfig config,
                       CustomFieldResolver customFieldResolver,
                       Iterable<Version> versions,
                       Iterable<BasicComponent> components,
                       Iterable<Priority> jiraPriorities) {
        this.config = config;
        this.customFieldResolver = customFieldResolver;
        this.versions = versions;
        this.components = components;
        priorities = StreamSupport.stream(jiraPriorities.spliterator(), false)
                .collect(Collectors.toMap(AddressableNamedEntity::getName, p -> p));
    }

    IssueWrapper convertToJiraIssue(GTask task) throws FieldConversionException {
        var issueInputBuilder = new IssueInputBuilder();
        issueInputBuilder.setProjectKey(config.getProjectKey());

        // issue type has to be set later in JiraTaskSaver because it depends on create/update

        if (task.getParentIdentity() != null) {
            // See http://stackoverflow.com/questions/14699893/how-to-create-subtasks-using-jira-rest-java-client
            var parent = new HashMap<String, Object>();
            parent.put("key", task.getParentIdentity().getKey());
            var parentField = new FieldInput("parent", new ComplexIssueInputFieldValue(parent));
            issueInputBuilder.setFieldInput(parentField);
        }
        for (Map.Entry<Field<?>, Object> row : task.getFields().entrySet()) {
            try {
                processField(issueInputBuilder, row.getKey(), row.getValue());
            } catch (Exception x) {
                throw new FieldConversionException(JiraConnector.ID, row.getKey(), row.getValue(), x.getMessage());
            }
        }
        var affectedVersion = GTaskToJira.getVersion(versions, config.getAffectedVersion());
        var fixForVersion = GTaskToJira.getVersion(versions, config.getFixForVersion());
        if (affectedVersion != null) issueInputBuilder.setAffectedVersions(ImmutableList.of(affectedVersion));
        if (fixForVersion != null) issueInputBuilder.setFixVersions(ImmutableList.of(fixForVersion));
        var issueInput = issueInputBuilder.build();

        var status = task.getValue(AllFields.taskStatus);
        return new IssueWrapper(task.getKey(), issueInput, status, Optional.ofNullable(task.getValue(AllFields.taskType)));
    }

    private void processField(IssueInputBuilder issueInputBuilder, Field<?> field, Object value) throws ConnectorException {
        if (field instanceof Children) {
            // processed in another place
            return;
        }
        if (field instanceof Id) {
            // ignore ID field because it does not need to be provided when saving
            return;
        }
        if (field instanceof Key) {
            // processed in <<DefaultValueSetter>>
            return;
        }
        if (field instanceof SourceSystemId) {
            // processed in <<DefaultValueSetter>>
            return;
        }
        if (field instanceof ParentKey) {
            // processed above
            return;
        }
        if (field instanceof Relations) {
            // processed in another place
            return;
        }
        if (field instanceof Summary) {
            issueInputBuilder.setSummary((String) value);
            return;
        }
        if (field instanceof Components) {
            // only first value from the list is used
            if (value != null) {
                var strings = (List<String>) value;
                if (!strings.isEmpty()) {
                    var firstComponentName = strings.get(0);
                    var component = GTaskToJira.getComponent(components, firstComponentName);
                    component.ifPresent(issueInputBuilder::setComponents);
                }
            } else {
                // this will erase any existing components in this task
                issueInputBuilder.setComponents();
            }
            return;
        }

        if (field instanceof TaskStatus) {
            // Task Status is processed separately, cannot be set to Issue due to JIRA API design.
            return;
        }
        if (field instanceof Description) {
            issueInputBuilder.setDescription((String) value);
            return;
        }

        if (field instanceof DueDate) {
            if (value != null) {
                var dueDateTime = new DateTime(value);
                issueInputBuilder.setDueDate(dueDateTime);
            }
            return;
        }
        if ((field instanceof AssigneeLoginName) && (value != null)) {
            issueInputBuilder.setAssigneeName((String) value);
            return;
        }

        if (field instanceof ReporterLoginName && value != null) {
            issueInputBuilder.setReporterName((String) value);
            return;
        }

        if (field instanceof com.taskadapter.model.Priority) {
            var priorityNumber = (Integer) value;
            var jiraPriorityName = config.getPriorities().getPriorityByMSP(priorityNumber);
            if (!jiraPriorityName.isEmpty()) {
                var priority = priorities.get(jiraPriorityName);
                if (priority != null) {
                    issueInputBuilder.setPriority(priority);
                } else {
                    throw new ConnectorException("Priority with name " + jiraPriorityName
                            + " is not found on the server. Please check your JIRA priorities settings");
                }
            }
            return;
        }
        if (field instanceof EstimatedTime && value != null) {
            var estimatedHours = (float) value;
            var currentTimeTracking = getTimeTrackingElement(issueInputBuilder);
            var timeTracking = currentTimeTracking.isPresent() ?
                    // maybe reset the current value. leave other values intact
                    new TimeTracking(roundedMinutes(estimatedHours),
                            currentTimeTracking.get().getRemainingEstimateMinutes(),
                            currentTimeTracking.get().getTimeSpentMinutes())
                    :
                    new TimeTracking(roundedMinutes(estimatedHours), null, null);

            issueInputBuilder.setFieldValue(IssueFieldId.TIMETRACKING_FIELD.id, timeTracking);
            return;
        }

        // supposedly only custom fields left by now

        var fieldSchema = customFieldResolver.getId(field.getFieldName());
        if (fieldSchema.isPresent()) {
            var fullIdForSave = fieldSchema.get().getFullIdForSave();
            var valueWithProperJiraType = getConvertedValue(fieldSchema.get(), value);
            issueInputBuilder.setFieldValue(fullIdForSave, valueWithProperJiraType);
        }

    }

    private static Integer roundedMinutes(Float hours) {
        return Math.round(hours * 60);
    }

    private static Optional<TimeTracking> getTimeTrackingElement(IssueInputBuilder issueInputBuilder) {
        var maybeElement = issueInputBuilder.build().getField(IssueFieldId.TIMETRACKING_FIELD.id);
        return Optional.ofNullable(maybeElement).map(f -> (TimeTracking) f.getValue());
    }

    /**
     * If the value is not of type String, this will throw exception. This is to fail fast rather than to attempt
     * to recover from incorrect types in the passed data.
     */
    private static boolean isNonEmptyString(Object value) {
        return value != null && !((String) value).isEmpty();
    }

    private static Object getConvertedValue(JiraFieldDefinition fieldSchema, Object value) {
        if (fieldSchema.getTypeName().equals("array")
                && fieldSchema.getItemsTypeIfArray().get().equals("string")) {
            return List.of(value);
        }
        if (fieldSchema.getTypeName().equals("array")
                && fieldSchema.getItemsTypeIfArray().get().equals("option")) {
            return getComplexValueList(value);
        }
        if (fieldSchema.getTypeName().equals("number")) {
            return ValueTypeResolver.getValueAsFloat(value);
        }
        return value;
    }

    private static List<ComplexIssueInputFieldValue> getComplexValueList(Object value) {
        // TODO 14 this check does not quite work due to type erasure, even though the code still works because this
        // method is only called in the correct context (for the value of list<string> type).
        if (value instanceof List) {
            var list = (List<String>) value;
            return list.stream()
                    .map(str -> ComplexIssueInputFieldValue.with("value", str))
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("unknown value type: " + value);
    }

    @Override
    public IssueWrapper convert(GTask source) throws ConnectorException {
        return convertToJiraIssue(source);
    }

    private static Version getVersion(Iterable<Version> versions, String versionName) {
        return StreamSupport.stream(versions.spliterator(), false)
                .filter(v -> v.getName().equals(versionName))
                .findFirst()
                .orElse(null);
    }

    private static Optional<BasicComponent> getComponent(Iterable<BasicComponent> objects, String name) {
        return StreamSupport.stream(objects.spliterator(), false)
                .filter(basicComponent -> basicComponent.getName().equals(name))
                .findFirst();
    }
}
