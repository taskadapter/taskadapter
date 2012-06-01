package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.*;
import com.google.common.base.Strings;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;

import java.util.*;

public class JiraTaskConverter {

    // XXX this is hardcoded!! https://www.hostedredmine.com/issues/18074
    private static final String ISSUE_TYPE_ID = "1";

    private final JiraConfig config;

    private final Map<String, String> priorities = new HashMap<String, String>();
    private final Map<String, String> prioritiesOtherWay = new HashMap<String, String>();
    private RemoteIssueType[] issueTypeList;

    public JiraTaskConverter(JiraConfig config) {
        this.config = config;
    }

    public RemoteIssue convertToJiraIssue(RemoteVersion[] versions, RemoteComponent[] components, GTask task) {
    	final Mappings mappings = config.getFieldMappings();
        RemoteIssue issue = new RemoteIssue();
        if (mappings.isFieldSelected(FIELD.SUMMARY)) {
            issue.setSummary(task.getSummary());
        }
        // issue.setParentId(parentIssueId);
        issue.setProject(config.getProjectKey());
        issue.setType(ISSUE_TYPE_ID);

        if (mappings.isFieldSelected(FIELD.DESCRIPTION)) {
            issue.setDescription(task.getDescription());
        }

        RemoteVersion affectedVersion = getVersion(versions, config.getAffectedVersion());
        RemoteVersion fixForVersion = getVersion(versions, config.getFixForVersion());
        RemoteCustomFieldValue[] customValues = getCustomFieldsForIssue(config.getCustomFields());
        RemoteComponent component = getComponent(components, config.getComponent());

        if (affectedVersion != null) {
            issue.setAffectsVersions(new RemoteVersion[]{affectedVersion});
        }

        if (fixForVersion != null) {
            issue.setFixVersions(new RemoteVersion[]{fixForVersion});
        }

        if (customValues.length != 0) {
            issue.setCustomFieldValues(customValues);
        }

        if (mappings.isFieldSelected(FIELD.TASK_TYPE)) {
            String issueType = getIssueTypeIdByName(task.getType());

            if (issueType == null) {
                issueType = getIssueTypeIdByName(config.getDefaultTaskType());
            }

            issue.setType(issueType);
        }

        if (mappings.isFieldSelected(FIELD.DUE_DATE) && task.getDueDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(task.getDueDate());
            issue.setDuedate(cal);
        }

        if (mappings.isFieldSelected(FIELD.ASSIGNEE)) {
            setAssignee(task, issue);
        }

        if (mappings.isFieldSelected(FIELD.PRIORITY)) {
            String jiraPriorityName = config.getPriorityByMSP(task.getPriority());

            if (!jiraPriorityName.isEmpty()) {
                issue.setPriority(priorities.get(jiraPriorityName));
            }
        }

        if (component != null) {
            issue.setComponents(new RemoteComponent[]{component});
        }

        return issue;
    }

    private void setAssignee(GTask task, RemoteIssue issue) {
        GUser ass = task.getAssignee();

        if ((ass != null) && (ass.getLoginName() != null)) {
            issue.setAssignee(ass.getLoginName());
        }
    }

    private static RemoteVersion getVersion(RemoteVersion[] versions, String versionName) {
        for (RemoteVersion v : versions) {
            if (v.getName().equals(versionName)) {
                return v;
            }
        }
        return null;
    }

    private static RemoteComponent getComponent(RemoteComponent[] objects, String name) {
        for (RemoteComponent o : objects) {
            if (o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }

    private RemoteCustomFieldValue[] getCustomFieldsForIssue(Map<String, String> configCustomFields) {

        RemoteCustomFieldValue[] values = new RemoteCustomFieldValue[configCustomFields.size()];

        int i = 0;
        for (Map.Entry<String, String> entry : configCustomFields.entrySet()) {
            // RemoteField field = getField(possibleCustomFields, key);
            // parentKey : Used for multidimensional custom fields such as
            // Cascading select lists. Null in other cases
            String parentKey = null;
            values[i++] = new RemoteCustomFieldValue(entry.getKey(), parentKey, new String[]{entry.getValue()});
        }
        return values;
    }

    public List<GTask> convertToGenericTaskList(List<RemoteIssue> tasks) {

        // TODO see http://jira.atlassian.com/browse/JRA-6896
        System.err.println("Jira: no tasks hierarchy is supported");

        List<GTask> rootLevelTasks = new ArrayList<GTask>();

        for (RemoteIssue issue : tasks) {
            GTask genericTask = convertToGenericTask(issue);
            rootLevelTasks.add(genericTask);
        }
        return rootLevelTasks;
    }

    public GTask convertToGenericTask(RemoteIssue issue) {
        GTask task = new GTask();
        Integer intId = Integer.parseInt(issue.getId());
        task.setId(intId);
        task.setKey(issue.getKey());

        String jiraUserLogin = issue.getAssignee();

        if (jiraUserLogin != null) {
            GUser genericUser = new GUser();

            // TODO note: user ID is not set here. should we use a newer Jira API library?
            genericUser.setLoginName(jiraUserLogin);

            task.setAssignee(genericUser);
        }

        task.setType(getIssueTypeNameById(issue.getType()));
        task.setSummary(issue.getSummary());
        task.setDescription(issue.getDescription());

        Calendar dueDate = issue.getDuedate();
        if (dueDate != null) {
            task.setDueDate(dueDate.getTime());
        }

        // TODO set these fields as well
        // task.setEstimatedHours(issue.getEstimatedHours());
        // task.setDoneRatio(issue.getDoneRatio());

        String jiraPriorityName = prioritiesOtherWay.get(issue.getPriority());

        if (!Strings.isNullOrEmpty(jiraPriorityName)) {
            Integer priorityValue = config.getPriorityByText(jiraPriorityName);
            task.setPriority(priorityValue);
        }

        return task;
    }

    public void setPriorities(RemotePriority[] jiraPriorities) {
        for (RemotePriority jiraPriority : jiraPriorities) {
            priorities.put(jiraPriority.getName(), jiraPriority.getId());
            prioritiesOtherWay.put(jiraPriority.getId(), jiraPriority.getName());
        }
    }

    public void setIssueTypeList(RemoteIssueType[] issueTypeList) {
        this.issueTypeList = issueTypeList;
    }

    public RemoteIssueType[] getIssueTypeList() {
        return this.issueTypeList;
    }

    public String getIssueTypeIdByName(String issueTypeName) {
        String resTypeId = null;

        if (issueTypeName != null && issueTypeList != null) {
            for (RemoteIssueType anIssueTypeList : issueTypeList) {
                if (anIssueTypeList.getName().equals(issueTypeName)) {
                    resTypeId = anIssueTypeList.getId();
                    break;
                }
            }
        }

        return resTypeId;
    }

    public String getIssueTypeNameById(String issueTypeId) {
        String resTypeName = null;

        if (issueTypeList != null) {
            for (RemoteIssueType anIssueTypeList : issueTypeList) {
                if (anIssueTypeList.getId().equals(issueTypeId)) {
                    resTypeName = anIssueTypeList.getName();
                    break;
                }
            }
        }

        return resTypeName;
    }
}
