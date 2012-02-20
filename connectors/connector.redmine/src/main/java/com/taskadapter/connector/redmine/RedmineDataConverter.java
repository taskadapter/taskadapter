package com.taskadapter.connector.redmine;

import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import org.redmine.ta.beans.Issue;
import org.redmine.ta.beans.IssueRelation;
import org.redmine.ta.beans.IssueStatus;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.User;

import java.util.List;

public class RedmineDataConverter {

    private final RedmineConfig config;
    private List<User> users;
    private List<IssueStatus> statusList;

    public RedmineDataConverter(RedmineConfig config) {
        this.config = config;
    }

    public static GUser convertToGUser(User redmineUser) {
        GUser user = new GUser();
        user.setId(redmineUser.getId());
        user.setLoginName(redmineUser.getLogin());
        user.setDisplayName(redmineUser.getFullName());
        return user;
    }

    public Issue convertToRedmineIssue(Project rmProject, GTask task) {
        Issue issue = new Issue();
        if (task.getParentKey() != null) {
            issue.setParentId(Integer.parseInt(task.getParentKey()));
        }
        issue.setProject(rmProject);

        if (config.isFieldSelected(FIELD.SUMMARY)) {
            issue.setSubject(task.getSummary());
        }
        if (config.isFieldSelected(FIELD.START_DATE)) {
            issue.setStartDate(task.getStartDate());
        }
        if (config.isFieldSelected(FIELD.DUE_DATE)) {
            issue.setDueDate(task.getDueDate());
        }

        if (config.isFieldSelected(FIELD.ESTIMATED_TIME)) {
            issue.setEstimatedHours(task.getEstimatedHours());
        }

        if (config.isFieldSelected(FIELD.DONE_RATIO)) {
            issue.setDoneRatio(task.getDoneRatio());
        }

        if (config.isFieldSelected(FIELD.TASK_TYPE)) {
            String trackerName = task.getType();
            if (trackerName == null) {
                trackerName = config.getDefaultTaskType();
            }
            issue.setTracker(rmProject.getTrackerByName(trackerName));
        }

        if (config.isFieldSelected(FIELD.TASK_STATUS)) {
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

        if (config.isFieldSelected(FIELD.DESCRIPTION)) {
            issue.setDescription(task.getDescription());
        }
        issue.setCreatedOn(task.getCreatedOn());
        issue.setUpdatedOn(task.getUpdatedOn());

        if (config.isFieldSelected(FIELD.ASSIGNEE)) {
            GUser ass = task.getAssignee();
            if ((ass != null) && (ass.getLoginName() != null || ass.getDisplayName() != null)) {
                User rmAss;
                if (ass.getId() != null) {
                    rmAss = new User();
                    rmAss.setId(ass.getId());
                    rmAss.setLogin(ass.getLoginName());
                } else {
                    rmAss = findUser(ass);
                }
                issue.setAssignee(rmAss);
            }
        }

        return issue;
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
    private User findUser(GUser ass) {
        if (users == null) {
            return null;
        }

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
                System.out.println("relation type is not supported: "
                        + relation.getType());
            }
        }
    }

}
