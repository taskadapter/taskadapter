package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rpc.soap.client.*;
import com.google.common.base.Strings;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JiraTaskConverter {

    private final Logger logger = LoggerFactory.getLogger(JiraTaskConverter.class);

    // TODO this is hardcoded!! https://www.hostedredmine.com/issues/18074
    private static final String ISSUE_TYPE_ID = "1";

    private final JiraConfig config;

    private final Map<String, BasicPriority> priorities = new HashMap<String, BasicPriority>();
    private final Map<String, BasicPriority> prioritiesOtherWay = new HashMap<String, BasicPriority>();
    private Iterable<IssueType> issueTypeList;

    public JiraTaskConverter(JiraConfig config) {
        this.config = config;
    }

    public IssueInput convertToJiraIssue(RemoteVersion[] versions, RemoteComponent[] components, GTask task) {
    	final Mappings mappings = config.getFieldMappings();

        String issueType = ISSUE_TYPE_ID;
        if (mappings.isFieldSelected(FIELD.TASK_TYPE)) {
            issueType = getIssueTypeIdByName(task.getType());

            if (issueType == null) {
                issueType = getIssueTypeIdByName(config.getDefaultTaskType());
            }
        }

        IssueInputBuilder issueInputBuilder = new IssueInputBuilder(config.getProjectKey(), Long.valueOf(issueType));

        if (mappings.isFieldSelected(FIELD.SUMMARY)) {
            issueInputBuilder.setSummary(task.getSummary());
        }

        if (mappings.isFieldSelected(FIELD.DESCRIPTION)) {
            issueInputBuilder.setDescription(task.getDescription());
        }

/*        RemoteVersion affectedVersion = getVersion(versions, config.getAffectedVersion());
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
        }*/


        if (mappings.isFieldSelected(FIELD.DUE_DATE) && task.getDueDate() != null) {
            DateTime dueDateTime = new DateTime(task.getDueDate());
            issueInputBuilder.setDueDate(dueDateTime);
        }

        if (mappings.isFieldSelected(FIELD.ASSIGNEE)) {
            setAssignee(task, issueInputBuilder);
        }

        if (mappings.isFieldSelected(FIELD.PRIORITY)) {
            String jiraPriorityName = config.getPriorityByMSP(task.getPriority());

            if (!jiraPriorityName.isEmpty()) {
                issueInputBuilder.setPriority(priorities.get(jiraPriorityName));
            }
        }

/*        if (component != null) {
            issue.setComponents(new RemoteComponent[]{component});
        }*/

        return issueInputBuilder.build();
    }

    private void setAssignee(GTask task, IssueInputBuilder issue) {
        GUser ass = task.getAssignee();

        if ((ass != null) && (ass.getLoginName() != null)) {
            issue.setAssigneeName(ass.getLoginName());
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

    public List<GTask> convertToGenericTaskList(List<Issue> tasks) {

        // TODO see http://jira.atlassian.com/browse/JRA-6896
        logger.info("Jira: no tasks hierarchy is supported");

        List<GTask> rootLevelTasks = new ArrayList<GTask>();

        for (Issue issue : tasks) {
            GTask genericTask = convertToGenericTask(issue);
            rootLevelTasks.add(genericTask);
        }
        return rootLevelTasks;
    }

    public GTask convertToGenericTask(Issue issue) {
        GTask task = new GTask();
        Integer intId = Integer.parseInt(issue.getId());
        task.setId(intId);
        task.setKey(issue.getKey());

        String jiraUserLogin = issue.getAssignee().getName();

        if (jiraUserLogin != null) {
            GUser genericUser = new GUser();

            // TODO note: user ID is not set here. should we use a newer Jira API library?
            genericUser.setLoginName(jiraUserLogin);

            task.setAssignee(genericUser);
        }

        task.setType(issue.getIssueType().getName());
        task.setSummary(issue.getSummary());
        task.setDescription(issue.getDescription());

        DateTime dueDate = issue.getDueDate();
        if (dueDate != null) {
            task.setDueDate(dueDate.toDate());
        }

        // TODO set these fields as well
        // task.setEstimatedHours(issue.getEstimatedHours());
        // task.setDoneRatio(issue.getDoneRatio());

        String jiraPriorityName = issue.getPriority().getName();

        if (!Strings.isNullOrEmpty(jiraPriorityName)) {
            Integer priorityValue = config.getPriorityByText(jiraPriorityName);
            task.setPriority(priorityValue);
        }

        return task;
    }

    public GTask convertToGenericTask(BasicIssue issue) {
        GTask task = new GTask();
        Integer intId = Integer.parseInt(issue.getId());
        task.setId(intId);
        task.setKey(issue.getKey());

        // TODO set these fields as well
        // task.setEstimatedHours(issue.getEstimatedHours());
        // task.setDoneRatio(issue.getDoneRatio());

        return task;
    }

    public void setPriorities(Iterable<Priority> jiraPriorities) {
        for (Priority jiraPriority : jiraPriorities) {
            priorities.put(jiraPriority.getName(), jiraPriority);
            prioritiesOtherWay.put(String.valueOf(jiraPriority.getId()), jiraPriority);
        }
    }

    public void setIssueTypeList(Iterable<IssueType> issueTypeList) {
        this.issueTypeList = issueTypeList;
    }

    public Iterable<IssueType> getIssueTypeList() {
        return this.issueTypeList;
    }

    public String getIssueTypeIdByName(String issueTypeName) {
        String resTypeId = null;

        if (issueTypeName != null && issueTypeList != null) {
            for (IssueType anIssueTypeList : issueTypeList) {
                if (anIssueTypeList.getName().equals(issueTypeName)) {
                    resTypeId = String.valueOf(anIssueTypeList.getId());
                    break;
                }
            }
        }

        return resTypeId;
    }

    public String getIssueTypeNameById(String issueTypeId) {
        String resTypeName = null;

        if (issueTypeList != null) {
            for (IssueType anIssueTypeList : issueTypeList) {
                if (anIssueTypeList.getId().equals(issueTypeId)) {
                    resTypeName = anIssueTypeList.getName();
                    break;
                }
            }
        }

        return resTypeName;
    }
}
