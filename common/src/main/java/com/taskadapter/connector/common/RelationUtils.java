package com.taskadapter.connector.common;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.SaveResultBuilder;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;

public final class RelationUtils {
    public static List<GRelation> convertRelationIds(List<GTask> tasks,
            SaveResultBuilder localToRemote) {
        final List<GRelation> result = new ArrayList<>();
        aggregateRelations(result, tasks, localToRemote);
        return result;
    }

    private static void aggregateRelations(List<GRelation> res,
            List<GTask> tasks, SaveResultBuilder localToRemote) {
        for (GTask task : tasks) {
            for (GRelation oldRelation : task.getRelations()) {
                // TODO get rid of the conversion, it won't work with Jira: String Keys like "TEST-12"
                TaskId relatedTaskId = oldRelation.getRelatedTaskId();
                TaskId newRelatedKey = localToRemote.getRemoteKey(relatedTaskId);
                // #25443 Export from MSP fails when newRelatedKey is null
                // (which is a valid case in MSP)
                TaskId originalId = task.getIdentity();
                TaskId newSourceTaskKey = localToRemote.getRemoteKey(originalId);
                if (newSourceTaskKey != null && newRelatedKey != null) {
                    res.add(new GRelation(newSourceTaskKey, newRelatedKey,
                            oldRelation.getType()));
                }
            }
            aggregateRelations(res, task.getChildren(), localToRemote);
        }
    }
}
