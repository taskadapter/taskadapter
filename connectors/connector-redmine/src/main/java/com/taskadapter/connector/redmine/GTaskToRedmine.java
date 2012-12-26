package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class GTaskToRedmine {

    private final RedmineConfig config;
    private Mappings mapping;
    private List<User> users;
    private List<IssueStatus> statusList;
    private final Map<String, Integer> priorities;

    GTaskToRedmine(RedmineConfig config, Mappings mapping, Map<String, Integer> priorities) {
        this.config = config;
        this.mapping = mapping;
        this.priorities = priorities;
        this.users = new ArrayList<User>();
    }

    // TODO refactor this into multiple tiny testable methods
    public Issue convertToRedmineIssue(Project rmProject, GTask task) {
        Issue issue = new Issue();
        if (task.getParentKey() != null) {
            issue.setParentId(Integer.parseInt(task.getParentKey()));
        }
        issue.setProject(rmProject);

        if (mapping.isFieldSelected(FIELD.SUMMARY)) {
            issue.setSubject(task.getSummary());
        }
        if (mapping.isFieldSelected(FIELD.START_DATE)) {
            issue.setStartDate(task.getStartDate());
        }
        if (mapping.isFieldSelected(FIELD.DUE_DATE)) {
            issue.setDueDate(task.getDueDate());
        }

        if (mapping.isFieldSelected(FIELD.ESTIMATED_TIME)) {
            issue.setEstimatedHours(task.getEstimatedHours());
        }

        if (mapping.isFieldSelected(FIELD.DONE_RATIO)) {
            issue.setDoneRatio(task.getDoneRatio());
        }

        if (mapping.isFieldSelected(FIELD.TASK_TYPE)) {
            String trackerName = task.getType();
            if (trackerName == null) {
                trackerName = config.getDefaultTaskType();
            }
            issue.setTracker(rmProject.getTrackerByName(trackerName));
        }

        if (mapping.isFieldSelected(FIELD.TASK_STATUS)) {
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
        
        if (mapping.isFieldSelected(FIELD.DESCRIPTION)) {
            issue.setDescription(task.getDescription());
        }
        
        if (mapping.isFieldSelected(FIELD.PRIORITY)) {
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
        
        issue.setCreatedOn(task.getCreatedOn());
        issue.setUpdatedOn(task.getUpdatedOn());

        processAssignee(task, issue);
        processTaskStatus(task, issue);

        return issue;
    }

    private void processAssignee(GTask genericTask, Issue redmineIssue) {
        if (mapping.isFieldSelected(FIELD.ASSIGNEE)) {
            GUser ass = genericTask.getAssignee();
            if ((ass != null) && (ass.getLoginName() != null || ass.getDisplayName() != null)) {
                User rmAss;
                if (config.isFindUserByName() || ass.getId() == null) {
                    rmAss = findRedmineUserInCache(ass);
                } else {
                    rmAss = new User();
                    rmAss.setId(ass.getId());
                    rmAss.setLogin(ass.getLoginName());
                }
                redmineIssue.setAssignee(rmAss);
            }
        }
    }

    private void processTaskStatus(GTask task, Issue issue) {
        if (mapping.isFieldSelected(FIELD.TASK_STATUS)) {
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

    // TODO add test for users
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setStatusList(List<IssueStatus> statusList) {
        this.statusList = statusList;
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
        if (nameToSearch == null || "".equals(nameToSearch)) {
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

}
