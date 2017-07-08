package com.taskadapter.connector.redmine;

import com.google.common.base.Strings;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.redmineapi.bean.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class GTaskToRedmine implements ConnectorConverter<GTask, Issue> {

    private final RedmineConfig config;
    private final List<User> users;
    private final List<CustomFieldDefinition> customFieldDefinitions;
    private final List<IssueStatus> statusList;
    private final List<Version> versions;
    private final Map<String, Integer> priorities;
    private final Project project;

    public GTaskToRedmine(RedmineConfig config,
                          Map<String, Integer> priorities, Project project, List<User> users,
                          List<CustomFieldDefinition> customFieldDefinitions,
                          List<IssueStatus> statusList,
                          List<Version> versions) {
        this.config = config;
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

    Issue convertToRedmineIssue(GTask task) {
        final String key = task.getKey();
        Integer numericKey = parseIntOrNull(key);
        Issue issue = IssueFactory.create(numericKey);
        issue.setParentId(parseIntOrNull(task.getParentKey()));
        issue.setProject(project);

        for (Map.Entry<String, Object> row : task.getFields().entrySet()) {
            processField(issue, row.getKey(), row.getValue());
        }
        return issue;
    }

    private void processField(Issue issue, String fieldName, Object value) {
        if (fieldName.equalsIgnoreCase(FIELD.SUMMARY.name())) {
            issue.setSubject((String) value);
            return;
        }
        if (fieldName.equalsIgnoreCase(FIELD.START_DATE.name())) {
            issue.setStartDate((Date) value);
            return;
        }
        if (fieldName.equalsIgnoreCase(FIELD.DUE_DATE.name())) {
            issue.setDueDate((Date) value);
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.ESTIMATED_TIME.name())) {
            issue.setEstimatedHours((Float) value);
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.DONE_RATIO.name())) {
            issue.setDoneRatio((Integer) value);
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.TASK_TYPE.name())) {
            String trackerName = (String) value;
            if (Strings.isNullOrEmpty(trackerName)) {
                trackerName = config.getDefaultTaskType();
            }
            issue.setTracker(project.getTrackerByName(trackerName));
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.TASK_STATUS.name())) {
            String statusString = (String) value;
            if (Strings.isNullOrEmpty(statusString)) {
                statusString = config.getDefaultTaskStatus();
            }
            IssueStatus status = getStatusByName(statusString);
            if (status != null) {
                issue.setStatusId(status.getId());
                issue.setStatusName(status.getName());
            }
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.DESCRIPTION.name())) {
            issue.setDescription((String) value);
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.PRIORITY.name())) {
            Integer priority = (Integer) value;
            if (priority != null) {
                final String priorityName = config.getPriorities()
                        .getPriorityByMSP(priority);
                final Integer val = priorities.get(priorityName);
                if (val != null) {
                    issue.setPriorityId(val);
                    issue.setPriorityText(priorityName);
                }
            }
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.TARGET_VERSION.name())) {
            Version version = getVersionByName((String) value);
            issue.setTargetVersion(version);
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.CREATED_ON.name())) {
            issue.setCreatedOn((Date) value);
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.UPDATED_ON.name())) {
            issue.setUpdatedOn((Date) value);
            return;
        }

        if (fieldName.equalsIgnoreCase(FIELD.RELATIONS.name())) {
            return; // processed in another place (for now?)
        }
        if (fieldName.equalsIgnoreCase(FIELD.CHILDREN.name())) {
            return; // processed in another place (for now?)
        }

        if (fieldName.equalsIgnoreCase("assignee")) {
            processAssignee(issue, value);
        }
        if (fieldName.equalsIgnoreCase("status")) {
            processTaskStatus(issue, (String) value);
        }


        // all known fields are processed. considering this a custom field

        Integer customFieldId = CustomFieldDefinitionFinder.findCustomFieldId(customFieldDefinitions, fieldName);
        if (customFieldId == null) {
            throw new RuntimeException("Cannot find Id for custom field " + fieldName + ". Known fields are:" + customFieldDefinitions);
        }
        CustomField customField = CustomFieldFactory.create(customFieldId, fieldName, (String) value);
        issue.addCustomField(customField);
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

    private void processAssignee(Issue redmineIssue, Object value) {
        String userLoginName = (String) value;
        if (!Strings.isNullOrEmpty(userLoginName)) {
            User rmAss;
//            if (config.isFindUserByName()) {
                rmAss = findRedmineUserInCache(userLoginName);
//            } else {
//                rmAss = UserFactory.create(ass.getId());
//                rmAss.setLogin(ass.getLoginName());
//            }
            redmineIssue.setAssignee(rmAss);
        }
    }

    private void processTaskStatus(Issue issue, String value) {
        String statusName = value;
        if (statusName == null) {
            statusName = config.getDefaultTaskStatus();
        }

        IssueStatus status = getStatusByName(statusName);
        if (status != null) {
            issue.setStatusId(status.getId());
            issue.setStatusName(status.getName());
        }
    }

    /**
     * @return NULL if the user is not found or if "users" weren't previously set via setUsers() method
     */
    User findRedmineUserInCache(String userLoginName) {
        if (users == null || Strings.isNullOrEmpty(userLoginName)) {
            return null;
        }

        // Searching for the user
        User foundUser = null;
        for (User user : users) {
            if (userLoginName.equalsIgnoreCase(user.getLogin())
                    || userLoginName.equalsIgnoreCase(user.getFullName())) {
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
