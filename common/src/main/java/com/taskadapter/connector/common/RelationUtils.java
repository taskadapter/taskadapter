package com.taskadapter.connector.common;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;

public final class RelationUtils {
    private RelationUtils() {
        throw new UnsupportedOperationException();
    }

    public static List<GRelation> convertRelationIds(List<GTask> tasks,
            TaskSaveResultBuilder localToRemote) {
        final List<GRelation> result = new ArrayList<GRelation>();
        aggregateRelations(result, tasks, localToRemote);
        return result;

    }

    private static void aggregateRelations(List<GRelation> res,
            List<GTask> tasks, TaskSaveResultBuilder localToRemote) {
        for (GTask task : tasks) {
            String newSourceTaskKey = localToRemote.getRemoteKey(task.getId());
            for (GRelation oldRelation : task.getRelations()) {
                // TODO get rid of the conversion, it won't work with Jira,
                // which has String Keys like "TEST-12"
                Integer relatedTaskId = Integer.parseInt(oldRelation
                        .getRelatedTaskKey());
                String newRelatedKey = localToRemote.getRemoteKey(relatedTaskId);
                // #25443 Export from MSP fails when newRelatedKey is null
                // (which is a valid case in MSP)
                if (newSourceTaskKey != null && newRelatedKey != null) {
                    res.add(new GRelation(newSourceTaskKey, newRelatedKey,
                            oldRelation.getType()));
                }
            }
        }

    }
}
