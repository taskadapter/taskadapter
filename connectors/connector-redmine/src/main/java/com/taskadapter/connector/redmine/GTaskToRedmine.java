package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.CustomFieldDefinition;
import com.taskadapter.redmineapi.bean.CustomFieldFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.UserFactory;
import com.taskadapter.redmineapi.bean.Version;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GTaskToRedmine implements ConnectorConverter<GTask, Issue> {

    private final RedmineConfig config;
    private final Collection<FieldRow> fieldRows;
    private final List<User> users;
    private final List<CustomFieldDefinition> customFieldDefinitions;
    private final List<IssueStatus> statusList;
    private final List<Version> versions;
    private final Map<String, Integer> priorities;
    private final Project project;

    public GTaskToRedmine(RedmineConfig config, List<FieldRow> fieldRows,
                          Map<String, Integer> priorities, Project project, List<User> users,
                          List<CustomFieldDefinition> customFieldDefinitions,
                          List<IssueStatus> statusList,
                          List<Version> versions) {
        this.config = config;
        this.fieldRows = fieldRows;
        this.priorities = priorities;
        this.project = project;
        this.users = users;
        this.customFieldDefinitions = customFieldDefinitions;
        this.statusList = statusList;
        this.versions = versions;
    }

    private static Integer parseIntOrNull(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    // TODO refactor this into multiple tiny testable methods
    Issue convertToRedmineIssue(GTask task) {
        final String key = task.getKey();
        Integer numericKey = parseIntOrNull(key);
        Issue issue = IssueFactory.create(numericKey);
        issue.setParentId(parseIntOrNull(task.getParentKey()));
        issue.setProject(project);

        for (FieldRow row : fieldRows) {
            processField(row, task, issue);
        }
        processAssignee(task, issue);
        processTaskStatus(task, issue);

        return issue;
    }

    private void processField(FieldRow row, GTask task, Issue issue) {
        if (row.genericFieldName().equalsIgnoreCase(FIELD.SUMMARY.name())) {
            issue.setSubject(task.getSummary());
        }
        if (row.genericFieldName().equalsIgnoreCase(FIELD.START_DATE.name())) {
            issue.setStartDate(task.getStartDate());
        }
        if (row.genericFieldName().equalsIgnoreCase(FIELD.DUE_DATE.name())) {
            issue.setDueDate(task.getDueDate());
        }

        if (row.genericFieldName().equalsIgnoreCase(FIELD.ESTIMATED_TIME.name())) {
            issue.setEstimatedHours(task.getEstimatedHours());
        }

        if (row.genericFieldName().equalsIgnoreCase(FIELD.DONE_RATIO.name())) {
            issue.setDoneRatio(task.getDoneRatio());
        }

        if (row.genericFieldName().equalsIgnoreCase(FIELD.TASK_TYPE.name())) {
            String trackerName = task.getType();
            if (trackerName == null) {
                trackerName = config.getDefaultTaskType();
            }
            issue.setTracker(project.getTrackerByName(trackerName));
        }

        if (row.genericFieldName().equalsIgnoreCase(FIELD.TASK_STATUS.name())) {
            String statusName = task.getStatus();
            if (statusName == null) {
                statusName = config.getDefaultTaskStatus();
            }

            IssueStatus status = getStatusByName(statusName);
            if (status != null) {
                issue.setStatusId(status.getId());
                issue.setStatusName(status.getName());
            }
        }

        if (row.genericFieldName().equalsIgnoreCase(FIELD.DESCRIPTION.name())) {
            issue.setDescription(task.getDescription());
        }

        if (row.genericFieldName().equalsIgnoreCase(FIELD.PRIORITY.name())) {
            Integer priority = task.getPriority();
            if (priority != null) {
                final String priorityName = config.getPriorities()
                        .getPriorityByMSP(priority);
                final Integer val = priorities.get(priorityName);
                if (val != null) {
                    issue.setPriorityId(val);
                    issue.setPriorityText(priorityName);
                }
            }
        }

        if (row.genericFieldName().equalsIgnoreCase(FIELD.TARGET_VERSION.name())) {
            Version version = getVersionByName(task.getTargetVersionName());
            issue.setTargetVersion(version);
        }
        issue.setCreatedOn(task.getCreatedOn());
        issue.setUpdatedOn(task.getUpdatedOn());

        if (row.genericFieldName().isEmpty()) {
            // considering this a custom field
            Integer customFieldId = CustomFieldDefinitionFinder.findCustomFieldId(customFieldDefinitions, row.nameInTarget());
            CustomField customField = CustomFieldFactory.create(customFieldId, row.nameInTarget(),
                    task.getValue(row.nameInSource()).toString());
            issue.addCustomField(customField);
        }
    }

    private Version getVersionByName(String versionName) {
        if (versions == null || versionName == null) {
            return null;
        }
        for (Version version : versions) {
            if (version.getName().equals(versionName)) {
                return version;
            }
        }
        return null;
    }

    private void processAssignee(GTask genericTask, Issue redmineIssue) {
        if (FieldRowFinder.containsGenericField(fieldRows, FIELD.ASSIGNEE.name())) {
            GUser ass = genericTask.getAssignee();
            if ((ass != null) && (ass.getLoginName() != null || ass.getDisplayName() != null)) {
                User rmAss;
                if (config.isFindUserByName() || ass.getId() == null) {
                    rmAss = findRedmineUserInCache(ass);
                } else {
                    rmAss = UserFactory.create(ass.getId());
                    rmAss.setLogin(ass.getLoginName());
                }
                redmineIssue.setAssignee(rmAss);
            }
        }
    }

    private void processTaskStatus(GTask task, Issue issue) {
        if (FieldRowFinder.containsGenericField(fieldRows, FIELD.TASK_STATUS.name())) {
            String statusName = task.getStatus();
            if (statusName == null) {
                statusName = config.getDefaultTaskStatus();
            }

            IssueStatus status = getStatusByName(statusName);
            if (status != null) {
                issue.setStatusId(status.getId());
                issue.setStatusName(status.getName());
            }
        }
    }

    /**
     * @return NULL if the user is not found or if "users" weren't previously set via setUsers() method
     */
    User findRedmineUserInCache(GUser ass) {
        // getting best name to search
        String nameToSearch = ass.getLoginName();
        if (nameToSearch == null || "".equals(nameToSearch)) {
            nameToSearch = ass.getDisplayName();
        }
        if (users == null || nameToSearch == null || "".equals(nameToSearch)) {
            return null;
        }

        // Searching for the user
        User foundUser = null;
        for (User user : users) {
            if (nameToSearch.equalsIgnoreCase(user.getLogin())
                    || nameToSearch.equalsIgnoreCase(user.getFullName())) {
                foundUser = user;
                break;
            }
        }
        return foundUser;
    }

    /**
     * @return NULL if the status is not found or if "statusList" weren't previously set via setStatusList() method
     */
    private IssueStatus getStatusByName(String name) {
        if (statusList == null || name == null) {
            return null;
        }

        IssueStatus foundStatus = null;
        for (IssueStatus status : statusList) {
            if (status.getName().equalsIgnoreCase(name)) {
                foundStatus = status;
                break;
            }
        }

        return foundStatus;
    }

    @Override
    public Issue convert(GTask source) throws ConnectorException {
        return convertToRedmineIssue(source);
    }

}
