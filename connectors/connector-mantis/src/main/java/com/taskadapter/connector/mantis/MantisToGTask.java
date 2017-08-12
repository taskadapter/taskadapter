package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.RelationshipData;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import com.taskadapter.model.Precedes$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public final class MantisToGTask {
    private static final Logger logger = LoggerFactory.getLogger(MantisToGTask.class);

    private static HashMap<String, Integer> priorityNumbers = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 516389048716909610L;
        {
            // TODO this can be moved to properties section to be defined by user.
            put("none", 100);
            put("low", 100);
            put("normal", 500);
            put("high", 700);
            put("urgent", 800);
            put("immediate", 800);
        }
    };

    public static GUser convertToGUser(AccountData mantisUser) {
        GUser user = new GUser();
        user.setId(mantisUser.getId().intValue());
        user.setLoginName(mantisUser.getName());
        user.setDisplayName(mantisUser.getReal_name());
        return user;
    }

    public static GTask convertToGenericTask(IssueData issue) {
        GTask task = new GTask();

        long longId = issue.getId().longValue();
        task.setId(longId);
        String stringId = String.valueOf(issue.getId());
        task.setKey(stringId);
        // must set source system id, otherwise "update task" is impossible later
        task.setSourceSystemId(new TaskId(longId, stringId));

        AccountData mantisUser = issue.getHandler();
        if (mantisUser != null) {
            GUser ass = convertToGUser(mantisUser);
            task.setValue(MantisField.assignee(), ass);
        }

        task.setValue(MantisField.summary(), issue.getSummary());
        task.setValue(MantisField.description(), issue.getDescription());
        task.setValue(MantisField.createdOn(), issue.getDate_submitted().getTime());
        task.setValue(MantisField.updatedOn(), issue.getLast_updated().getTime());

        Integer priorityValue = priorityNumbers.get(issue.getPriority().getName());
        task.setValue(MantisField.priority(), priorityValue);

        if (issue.getDue_date() != null) {
            task.setValue(MantisField.dueDate(), issue.getDue_date().getTime());
        }

        processRelations(issue, task);

        return task;
    }

    private static void processRelations(IssueData mntIssue, GTask genericTask) {
        RelationshipData[] relations = mntIssue.getRelationships();
        if (relations != null) {
            for (RelationshipData relation : relations) {
                if (relation.getType().getName().equals("child of")) {
                    GRelation r = new GRelation(
                            new TaskId(relation.getId().longValue(), String.valueOf(relation.getId())),
                            new TaskId(relation.getTarget_id().longValue(), String.valueOf(relation.getTarget_id())),
                            Precedes$.MODULE$);
                    genericTask.getRelations().add(r);
                } else {
                    logger.info("Relation type is not supported: " + relation.getType()
                            + " - skipping it for issue " + mntIssue.getId());
                }
            }
        }
    }

}
