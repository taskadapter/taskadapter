package com.taskadapter.connector.redmine;

import com.google.common.base.Strings;
import com.taskadapter.connector.common.ValueTypeResolver;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.AssigneeFullName;
import com.taskadapter.model.AssigneeLoginName;
import com.taskadapter.model.Children;
import com.taskadapter.model.Components;
import com.taskadapter.model.CreatedOn;
import com.taskadapter.model.Description;
import com.taskadapter.model.DoneRatio;
import com.taskadapter.model.DueDate;
import com.taskadapter.model.EstimatedTime;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Id;
import com.taskadapter.model.Key;
import com.taskadapter.model.ParentKey;
import com.taskadapter.model.Priority;
import com.taskadapter.model.Relations;
import com.taskadapter.model.ReporterFullName;
import com.taskadapter.model.ReporterLoginName;
import com.taskadapter.model.SourceSystemId;
import com.taskadapter.model.StartDate;
import com.taskadapter.model.Summary;
import com.taskadapter.model.TargetVersion;
import com.taskadapter.model.TaskStatus;
import com.taskadapter.model.TaskType;
import com.taskadapter.model.UpdatedOn;
import com.taskadapter.redmineapi.bean.CustomFieldDefinition;
import com.taskadapter.redmineapi.bean.CustomFieldFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueCategory;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GTaskToRedmine implements ConnectorConverter<GTask, Issue> {
    private static final Logger logger = LoggerFactory.getLogger(GTaskToRedmine.class);

    private final RedmineConfig config;
    private final Map<String, Integer> priorities;
    private final Project project;
    private final RedmineUserCache usersCache;
    private final List<CustomFieldDefinition> customFieldDefinitions;
    private final List<IssueStatus> statusList;
    private final List<Version> versions;
    private final List<IssueCategory> categories;

    public GTaskToRedmine(RedmineConfig config, Map<String, Integer> priorities,
                          Project project, RedmineUserCache usersCache,
                          List<CustomFieldDefinition> customFieldDefinitions,
                          List<IssueStatus> statusList, List<Version> versions, List<IssueCategory> categories) {
        this.config = config;
        this.priorities = priorities;
        this.project = project;
        this.usersCache = usersCache;
        this.customFieldDefinitions = customFieldDefinitions;
        this.statusList = statusList;
        this.versions = versions;
        this.categories = categories;
    }

    private static Integer parseIntOrNull(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return null;

        }
    }

    private Issue convertToRedmineIssue(GTask task) throws FieldConversionException {
        var longId = task.getId();
        var issue = longId == null ? IssueFactory.create(null) : IssueFactory.create(longId.intValue());

        issue.setProjectId(project.getId());
        issue.setProjectName(project.getName());
        if (task.getParentIdentity() != null) {
            issue.setParentId(task.getParentIdentity().getId().intValue());
        }
        for (Map.Entry<Field<?>, Object> entry : task.getFields().entrySet()) {
            try {
                processField(issue, entry.getKey(), entry.getValue());
            } catch (Exception e) {
                throw new FieldConversionException(RedmineConnector.ID, entry.getKey(), entry.getValue(), e.getMessage());
            }
        }
        return issue;
    }

    private void processField(Issue issue, Field<?> field, Object value) throws ConnectorException {
        if (field instanceof Id) {
            // ignore ID field because it does not need to be provided when saving
            return;
        }
        if (field instanceof ParentKey) {
            // processed above
            return;
        }
        if (field instanceof Relations) {
            // processed in another place (for now?);
            return;
        }

        if (field instanceof Children) {// processed in another place (for now?)
            return;
        }

        if (field instanceof Key) {
            // processed in <<DefaultValueSetter>> for now
            return;
        }

        if (field instanceof SourceSystemId) {
            // processed in <<DefaultValueSetter>> for now
            return;
        }

        if (field instanceof Components) {
            var categoryName = ValueTypeResolver.getValueAsString(value);
            var maybeCategory = getCategoryByName(categoryName);
            issue.setCategory(maybeCategory.orElse(null));
            return;
        }

        if (field instanceof Summary) {
            issue.setSubject((String) value);
            return;
        }

        if (field instanceof StartDate) {
            issue.setStartDate((Date) value);
            return;
        }

        if (field instanceof DueDate) {
            issue.setDueDate((Date) value);
            return;
        }

        if (field instanceof EstimatedTime) {
            issue.setEstimatedHours(ValueTypeResolver.getValueAsFloat(value));
            return;
        }

//      SpentTime
        // does not work - ignored by Redmine server. need to add a Time Entry via a separate REST call
//        issue.setSpentHours(ValueTypeResolver.getValueAsFloat(value));

        if (field instanceof DoneRatio) {
            issue.setDoneRatio(ValueTypeResolver.getValueAsInt(value));
            return;
        }

        if (field instanceof TaskType) {
            var trackerName = (String) value;
            if (Strings.isNullOrEmpty(trackerName)) {
                trackerName = config.getDefaultTaskType();
            }

            issue.setTracker(project.getTrackerByName(trackerName));
            return;
        }

        if (field instanceof TaskStatus) {
            processTaskStatus(issue, (String) value);
            return;
        }

        if (field instanceof Description) {
            issue.setDescription((String) value);
            return;
        }

        if (field instanceof Priority) {
            var priority = (Integer) value;
            if (priority != null) {
                var priorityName = config.getPriorities().getPriorityByMSP(priority);
                var val = priorities.get(priorityName);
                if (val != null) {
                    issue.setPriorityId(val);
                    issue.setPriorityText(priorityName);
                } else {
                    throw new ConnectorException("Priority with name " + priorityName +
                            " is not found on the server. Please check your Redmine priorities settings");
                }
            }
            return;
        }

        if (field instanceof TargetVersion) {
            var version = getVersionByName((String) value);
            issue.setTargetVersion(version);
            return;
        }

        if (field instanceof CreatedOn) {
            issue.setCreatedOn((Date) value);
            return;
        }

        if (field instanceof UpdatedOn) {
            issue.setUpdatedOn((Date) value);
            return;
        }

        if (field instanceof AssigneeLoginName) {
            var maybeId = getUserIdByLogin((String) value);
            maybeId.ifPresent(issue::setAssigneeId);
            return;
        }
        if (field instanceof AssigneeFullName) {
            var maybeId = getUserIdByFullName((String) value);
            maybeId.ifPresent(issue::setAssigneeId);
            return;
        }
        if (field instanceof ReporterLoginName) {
            var maybeId = getUserIdByLogin((String) value);
            maybeId.ifPresent(issue::setAuthorId);
            return;
        }
        if (field instanceof ReporterFullName) {
            var maybeId = getUserIdByFullName((String) value);
            maybeId.ifPresent(issue::setAuthorId);
            return;
        }

        // all known fields are processed. considering this a custom field
        var customFieldId = CustomFieldDefinitionFinder.findCustomFieldId(customFieldDefinitions, field);
        if (customFieldId == null)
            throw new RuntimeException("Cannot find Id for custom field " + field + ". Known fields are:" + customFieldDefinitions);
        var customField = CustomFieldFactory.create(customFieldId, field.getFieldName(), (String) value);
        issue.addCustomField(customField);
    }


    private Version getVersionByName(String versionName) {
        if (versions == null || versionName == null) {
            return null;
        }
        return versions.stream()
                .filter(c -> c.getName().equals(versionName))
                .findFirst()
                .orElse(null);
    }

    private Optional<IssueCategory> getCategoryByName(String name) {
        if (categories == null || name == null) {
            return Optional.empty();
        }
        return categories.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    private Optional<Integer> getUserIdByLogin(String login) {
        return usersCache.findRedmineUserByLogin(login).map(User::getId);
    }

    private Optional<Integer> getUserIdByFullName(String fullName) {
        return usersCache.findRedmineUserByFullName(fullName).map(User::getId);
    }

    private void processTaskStatus(Issue issue, String value) {
        var statusName = value;
        if (statusName == null) statusName = config.getDefaultTaskStatus();
        var status = getStatusByName(statusName);
        if (status != null) {
            issue.setStatusId(status.getId());
            issue.setStatusName(status.getName());
        }
    }

    /**
     * @return NULL if the status is not found or if "statusList" weren't previously set via setStatusList() method
     */
    private IssueStatus getStatusByName(String name) {
        if (statusList == null || name == null) {
            return null;
        }
        return statusList.stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Issue convert(GTask source) throws FieldConversionException {
        return convertToRedmineIssue(source);
    }
}
