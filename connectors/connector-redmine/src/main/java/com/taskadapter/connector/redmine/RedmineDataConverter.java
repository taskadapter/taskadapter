package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RedmineDataConverter {

    private static final Logger logger = LoggerFactory.getLogger(RedmineDataConverter.class);

    private final RedmineConfig config;
    private List<User> users;
    private List<IssueStatus> statusList;

    public RedmineDataConverter(RedmineConfig config) {
        this.config = config;
        this.users = new ArrayList<User>();
    }

    public static GUser convertToGUser(User redmineUser) {
        GUser user = new GUser();
        user.setId(redmineUser.getId());
        user.setLoginName(redmineUser.getLogin());
        user.setDisplayName(redmineUser.getFullName());
        return user;
    }

    // TODO refactor this into multiple tiny testable methods
    public Issue convertToRedmineIssue(Project rmProject, GTask task) {
    	final Mappings mapping = config.getFieldMappings();
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
        issue.setCreatedOn(task.getCreatedOn());
        issue.setUpdatedOn(task.getUpdatedOn());

        processAssignee(task, issue);
        processTaskStatus(task, issue);

        return issue;
    }

    private void processAssignee(GTask genericTask, Issue redmineIssue) {
        if (config.getFieldMappings().isFieldSelected(FIELD.ASSIGNEE)) {
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
        if (config.getFieldMappings().isFieldSelected(FIELD.TASK_STATUS)) {
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

    /**
     * convert Redmine issues to internal model representation required for
     * Task Adapter app.
     *
     * @param issue Redmine issue
     */
    public GTask convertToGenericTask(Issue issue) {
        GTask task = new GTask();

        task.setId(issue.getId());
        task.setKey(Integer.toString(issue.getId()));
        if (issue.getParentId() != null) {
            task.setParentKey(issue.getParentId() + "");
        }
        User rmAss = issue.getAssignee();
        if (rmAss != null) {
            task.setAssignee(convertToGUser(rmAss));
        }

        task.setType(issue.getTracker().getName());
        task.setStatus(issue.getStatusName());
        task.setSummary(issue.getSubject());
        task.setEstimatedHours(issue.getEstimatedHours());
        task.setDoneRatio(issue.getDoneRatio());
        task.setStartDate(issue.getStartDate());
        task.setDueDate(issue.getDueDate());
        task.setCreatedOn(issue.getCreatedOn());
        task.setUpdatedOn(issue.getUpdatedOn());
        Integer priorityValue = config.getPriorityByText(issue.getPriorityText());//priorityNumbers.get(issue.getPriorityText());
        task.setPriority(priorityValue);
        task.setDescription(issue.getDescription());

        processRelations(issue, task);
        return task;
    }

    private static void processRelations(Issue rmIssue, GTask genericTask) {
        List<IssueRelation> relations = rmIssue.getRelations();
        for (IssueRelation relation : relations) {
            if (relation.getType().equals("precedes")) {
                // if NOT equal to self!
                // See http://www.redmine.org/issues/7366#note-11
                if (!relation.getIssueToId().equals(rmIssue.getId())) {
                    GRelation r = new GRelation(Integer.toString(rmIssue.getId()), Integer.toString(relation
                            .getIssueToId()), GRelation.TYPE.precedes);
                    genericTask.getRelations().add(r);
                }
            } else {
                logger.error("relation type is not supported: " + relation.getType());
            }
        }
    }

}
