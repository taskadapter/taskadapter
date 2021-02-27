package com.taskadapter.core;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.TaskKeyMapping;
import com.taskadapter.model.GTask;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PreviouslyCreatedTasksResolver {
    private final PreviouslyCreatedTasksCache cache;
    private final Map<TaskId, TaskId> mapLeftToRight;
    private final Map<TaskId, TaskId> mapRightToLeft;

    public PreviouslyCreatedTasksResolver(PreviouslyCreatedTasksCache cache) {
        this.cache = cache;
        mapLeftToRight = cache.getItems()
                .stream()
                .collect(Collectors.toMap(TaskKeyMapping::getOriginalId, TaskKeyMapping::getNewId));
        mapRightToLeft = cache.getItems()
                .stream()
                .collect(Collectors.toMap(TaskKeyMapping::getNewId, TaskKeyMapping::getOriginalId));
    }

    public static final PreviouslyCreatedTasksResolver empty =
            new PreviouslyCreatedTasksResolver(new PreviouslyCreatedTasksCache("", "", Collections.emptyList()));

    /**
     * @param task           must contain sourceSystemId value
     * @param targetLocation
     */
    public Optional<TaskId> findSourceSystemIdentity(GTask task, String targetLocation) {
        return findSourceSystemIdentity(task.getSourceSystemId(), targetLocation);
    }

    public Optional<TaskId> findSourceSystemIdentity(TaskId sourceSystemId, String targetLocation) {
        var mapToSearchIn = targetLocation.equals(cache.getLocation2()) ? mapLeftToRight : mapRightToLeft;

        if (sourceSystemId != null && mapToSearchIn.containsKey(sourceSystemId)) {
            return Optional.of(mapToSearchIn.get(sourceSystemId));
        }

        return Optional.empty();
    }

    public Map<TaskId, TaskId> getMapLeftToRight() {
        return mapLeftToRight;
    }
}
