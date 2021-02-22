package com.taskadapter.connector.common;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResultBuilder;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;

import java.util.List;
import java.util.Optional;

public class TaskSavingUtils {

    /**
     * Saves relations between tasks. Result builder must have correct
     * mappings between old and new task identifiers. Exceptions reported as a
     * general exceptions in a result builder.
     *
     * @param config        to use. This config may prevent update of relations.
     * @param tasks         tasks to save.
     * @param saver         relation saver.
     * @param resultBuilder result builder.
     */
    public static void saveRemappedRelations(ConnectorConfig config,
                                             List<GTask> tasks,
                                             RelationSaver saver,
                                             SaveResultBuilder resultBuilder) {
        if (!config.getSaveIssueRelations()) {
            return;
        }

        var result = RelationUtils.convertRelationIds(tasks, resultBuilder);
        try {
            saver.saveRelations(result);
        } catch (ConnectorException e) {
            resultBuilder.addGeneralError(e);
        }
    }

    public static <N> SaveResultBuilder saveTasks(PreviouslyCreatedTasksResolver previouslyCreatedTasks,
                                                  List<GTask> tasks,
                                                  ConnectorConverter<GTask, N> converter,
                                                  BasicIssueSaveAPI<N> saveAPI,
                                                  ProgressMonitor progressMonitor,
                                                  Iterable<FieldRow<?>> fieldRows,
                                                  String targetLocation) {
        var result = new SaveResultBuilder();
        var saver = new SimpleTaskSaver<>(previouslyCreatedTasks, converter, saveAPI, result, progressMonitor, targetLocation);
        Optional<TaskId> parentIssueId = Optional.empty();
        saver.saveTasks(parentIssueId, tasks, fieldRows);
        return result;
    }
}
