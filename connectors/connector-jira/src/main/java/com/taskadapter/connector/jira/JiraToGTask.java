package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Precedes$;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConverters;
import scala.collection.immutable.Seq;

import java.util.ArrayList;
import java.util.List;

public class JiraToGTask {
    private static final Logger logger = LoggerFactory.getLogger(JiraToGTask.class);

    private final Priorities priorities;

    public JiraToGTask(Priorities priorities) {
        this.priorities = priorities;
    }

    public List<GTask> convertToGenericTaskList(Iterable<Issue> issues) {
        // TODO see http://jira.atlassian.com/browse/JRA-6896
//        logger.info("Jira: no tasks hierarchy is supported");

        List<GTask> rootLevelTasks = new ArrayList<>();

        for (Issue issue : issues) {
            GTask genericTask = convertToGenericTask(issue);
            rootLevelTasks.add(genericTask);
        }
        return rootLevelTasks;
    }

    public GTask convertToGenericTask(Issue issue) {
        GTask task = new GTask();
        final Long longId = issue.getId();
        task.setId(longId);
        task.setKey(issue.getKey());
        task.setSourceSystemId(new TaskId(longId, issue.getKey()));

        if (issue.getAssignee() != null) {
            String jiraUserLogin = issue.getAssignee().getName();
            task.setValue(JiraField.assignee().name(), jiraUserLogin);
        }

        task.setValue(JiraField.taskType(), issue.getIssueType().getName());
        task.setValue(JiraField.summary(), issue.getSummary());
        task.setValue(JiraField.description(), issue.getDescription());

        DateTime dueDate = issue.getDueDate();
        if (dueDate != null) {
            task.setValue(JiraField.dueDate(), dueDate.toDate());
        }

        // TODO set Done Ratio
        // task.setDoneRatio(issue.getDoneRatio());

        String jiraPriorityName = null;
        if (issue.getPriority() != null) {
            jiraPriorityName = issue.getPriority().getName();
        }
        Integer priorityValue = priorities.getPriorityByText(jiraPriorityName);
        task.setValue(JiraField.priority(), priorityValue);

        TimeTracking timeTracking = issue.getTimeTracking();
        if (timeTracking != null) {
            Integer originalEstimateMinutes = timeTracking.getOriginalEstimateMinutes();
            if (originalEstimateMinutes != null
                    && !originalEstimateMinutes.equals(0)) {
                task.setValue(JiraField.estimatedTime(), (float) (originalEstimateMinutes / 60.0));
            }
        }

        processCustomFields(issue, task);
        processRelations(issue, task);
        processParentTask(issue, task);
        return task;
    }

    private void processCustomFields(Issue issue, GTask task) {
        issue.getFields().forEach(f -> {
            if (f.getId().startsWith("customfield")) {
                // custom field
                Object nativeValue = f.getValue();
                Object gValue = null;
                try {
                    gValue = convertToGenericValue(nativeValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                task.setValue(f.getName(), gValue);
            }
        });
    }

    private Object convertToGenericValue(Object nativeValue) throws JSONException {
        if (nativeValue == null) {
            return null;
        }
        if (nativeValue instanceof JSONArray) {
            Seq<Object> strings = parseJsonArray((JSONArray) nativeValue);
            return strings;
        }
        if (nativeValue instanceof JSONObject) {
            return parseJsonObject((JSONObject) nativeValue);
        }
        return nativeValue.toString();
    }

    private Seq<Object> parseJsonArray(JSONArray array) throws JSONException {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object o = array.get(i);
            result.add(convertToGenericValue(o));
        }
        return JavaConverters.asScalaBuffer(result).toList().toSeq();
    }

    private String parseJsonObject(JSONObject jsonObject) {
        String value;
        try {
            value = jsonObject.getString("value");
        } catch (JSONException e) {
            value = "";
        }
        return value;
    }

    static class StringFieldValueParser implements JsonObjectParser<String> {
        private static final String VALUE_ATTRIBUTE = "value";

        @Override
        public String parse(JSONObject jsonObject) throws JSONException {
            return JsonParseUtil.getNullableString(jsonObject, VALUE_ATTRIBUTE);
        }
    }


    private static void processParentTask(Issue issue, GTask task) {
        if (issue.getIssueType().isSubtask()) {
            Object parent = issue.getField("parent").getValue();
            JSONObject json = (JSONObject) parent;
            try {
                String parentKey = (String) json.get("key");
                Long id = json.getLong("id");
                task.setParentIdentity(new TaskId(id, parentKey));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void processRelations(Issue issue, GTask genericTask) {
        Iterable<IssueLink> links = issue.getIssueLinks();
        if (links != null) {
            for (IssueLink link : links) {
                if (link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.OUTBOUND)) {
                    String name = link.getIssueLinkType().getName();
                    if (name.equals(JiraConstants.getJiraLinkNameForPrecedes())) {
//                        String targetIssueIdFromURI = JiraUtils.getIdFromURI(link.getTargetIssueUri());
                        GRelation r = new GRelation(
                                new TaskId(issue.getId(), issue.getKey()),
                                new TaskId(-1, link.getTargetIssueKey()),
                                Precedes$.MODULE$);
                        genericTask.getRelations().add(r);
                    } else {
                        logger.info("Relation type is not supported: " + link.getIssueLinkType() +
                                " - this link will be skipped for issue " + issue.getKey());
                    }
                }
            }
        }
    }

}
