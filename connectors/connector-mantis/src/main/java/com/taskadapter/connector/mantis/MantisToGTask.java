package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.RelationshipData;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GRelationType;
import com.taskadapter.model.GTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MantisToGTask {
    private static final Logger logger = LoggerFactory.getLogger(MantisToGTask.class);
    // TODO this can be moved to properties section to be defined by user.

    private static final int defaultPriority = 500;

    private static final Map<String, Integer> priorityNumbers = Map.of(
            "none", 100,
            "low", 100,
            "normal", defaultPriority,
            "high", 700,
            "urgent", 800,
            "immediate", 800
    );

    static GTask convertToGenericTask(IssueData issue) {
        var task = new GTask();
        var longId = issue.getId().longValue();
        task.setId(longId);
        var stringId = String.valueOf(issue.getId());
        task.setKey(stringId);
        // must set source system id, otherwise "update task" is impossible later
        task.setSourceSystemId(new TaskId(longId, stringId));

        var mantisUser = issue.getHandler();
        if (mantisUser != null) {
            task.setValue(AllFields.assigneeLoginName, mantisUser.getName());
            task.setValue(AllFields.assigneeFullName, mantisUser.getReal_name());
        }
        task.setValue(AllFields.summary, issue.getSummary());
        task.setValue(AllFields.description, issue.getDescription());
        task.setValue(AllFields.createdOn, issue.getDate_submitted().getTime());
        task.setValue(AllFields.updatedOn, issue.getLast_updated().getTime());
        Integer priorityValue = priorityNumbers.getOrDefault(issue.getPriority().getName(), defaultPriority);
        task.setValue(AllFields.priority, priorityValue);
        if (issue.getDue_date() != null) {
            task.setValue(AllFields.dueDate, issue.getDue_date().getTime());
        }
        processRelations(issue, task);
        return task;
    }

    private static void processRelations(IssueData mntIssue, GTask genericTask) {
        var relations = mntIssue.getRelationships();
        if (relations != null) {
            for (RelationshipData relation : relations) {
                if (relation.getType().getName().equals("child of")) {
                    var r = new GRelation(
                            new TaskId(relation.getId().longValue(), String.valueOf(relation.getId())),
                            new TaskId(relation.getTarget_id().longValue(), String.valueOf(relation.getTarget_id())),
                            GRelationType.precedes);
                    genericTask.getRelations().add(r);
                } else {
                    logger.info("Relation type is not supported: " + relation.getType()
                            + " - skipping it for issue " + mntIssue.getId());
                }
            }
        }
    }
}