package com.taskadapter.connector.common;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResultBuilder;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;

import java.util.List;
import java.util.Optional;

public class SimpleTaskSaver<N> {

    private PreviouslyCreatedTasksResolver previouslyCreatedTasksResolver;
    private ConnectorConverter<GTask, N> converter;
    private BasicIssueSaveAPI<N> saveAPI;
    private SaveResultBuilder result;
    private ProgressMonitor progressMonitor;
    private String targetLocation;

    public SimpleTaskSaver(PreviouslyCreatedTasksResolver previouslyCreatedTasksResolver,
                           ConnectorConverter<GTask, N> converter,
                           BasicIssueSaveAPI<N> saveAPI,
                           SaveResultBuilder result,
                           ProgressMonitor progressMonitor,
                           String targetLocation) {
        this.previouslyCreatedTasksResolver = previouslyCreatedTasksResolver;
        this.converter = converter;
        this.saveAPI = saveAPI;
        this.result = result;
        this.progressMonitor = progressMonitor;
        this.targetLocation = targetLocation;
    }

    public void saveTasks(Optional<TaskId> parentIssueKey,
                          List<GTask> tasks,
                          Iterable<FieldRow<?>> fieldRows) {
        tasks.forEach(task -> {
            try {
                if (parentIssueKey.isPresent()) {
                    task.setParentIdentity(parentIssueKey.get());
                }
                var transformedTask = DefaultValueSetter.adapt(fieldRows, task);
                var withPossiblyNewId = replaceIdentityIfPreviouslyCreatedByUs(previouslyCreatedTasksResolver, transformedTask,
                        targetLocation);

                var readyForNative = withPossiblyNewId;
                var nativeIssueToCreateOrUpdate = converter.convert(readyForNative);
                var identity = new TaskId(readyForNative.getId(), readyForNative.getKey());

                TaskId newTaskIdentity;
                if (identity.key() == null || identity.key().equals("")) {
                    var newTaskKey = saveAPI.createTask(nativeIssueToCreateOrUpdate);
                    result.addCreatedTask(new TaskId(task.getId(), task.getKey()), newTaskKey); // save originally requested task Id to enable tests and ...?
                    newTaskIdentity = newTaskKey;
                } else {
                    saveAPI.updateTask(nativeIssueToCreateOrUpdate);
                    result.addUpdatedTask(new TaskId(task.getId(), task.getKey()), identity);
                    newTaskIdentity = identity;
                }
                progressMonitor.worked(1);
                if (task.hasChildren()) {
                    saveTasks(Optional.of(newTaskIdentity), task.getChildren(), fieldRows);
                }
            } catch (ConnectorException e) {
                result.addTaskError(task, e);
            } catch (Exception t) {
                result.addTaskError(task, t);
                t.printStackTrace();
            }
        });
    }

    private GTask replaceIdentityIfPreviouslyCreatedByUs(PreviouslyCreatedTasksResolver resolver, GTask gTask,
                                                         String targetLocation) {
        var result = new GTask(gTask);
        var maybeId = previouslyCreatedTasksResolver.findSourceSystemIdentity(gTask, targetLocation);
        if (maybeId.isDefined()) {
            result.setId(maybeId.get().id());
            result.setKey(maybeId.get().key());
        }
        return result;
    }
}
