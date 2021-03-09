package com.taskadapter.web;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.TaskKeyMapping;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskKeeperLocationStorageTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void keepsExistingDataWhenCalledWithEmptyResults() throws IOException {
        var sourceTarget = createIds(1, 100);
        var storage = new TaskKeeperLocationStorage(tempFolder.getRoot());
        storage.store("location1", "location2", Arrays.asList(sourceTarget));
        storage.store("location1", "location2", java.util.List.of());
        var loaded = storage.loadTasks("location1", "location2");
        assertThat(loaded.findSourceSystemIdentity(sourceTarget.originalId, "location2"))
                .contains(sourceTarget.newId);
    }

    @Test
    public void canSaveAndLoadData() throws IOException {
        var sourceTarget = createIds(1, 100);
        var storage = new TaskKeeperLocationStorage(tempFolder.getRoot());
        storage.store("location1", "location2", Arrays.asList(sourceTarget));

        var loaded = storage.loadTasks("location1", "location2");
        assertThat(loaded.findSourceSystemIdentity(sourceTarget.originalId, "location2"))
                .contains(sourceTarget.newId);
    }

    @Test
    public void addsNewResultsToExistingData() throws IOException {
        var sourceTarget = createIds(1, 100);
        var storage = new TaskKeeperLocationStorage(tempFolder.getRoot());
        storage.store("location1", "location2", Arrays.asList(sourceTarget));

        var anotherSourceTarget = createIds(2, 200);
        storage.store("location1", "location2", Arrays.asList(anotherSourceTarget));

        var loaded = storage.loadTasks("location1", "location2");
        assertThat(loaded.findSourceSystemIdentity(sourceTarget.originalId, "location2"))
                .contains(sourceTarget.newId);

        assertThat(loaded.findSourceSystemIdentity(anotherSourceTarget.originalId, "location2"))
                .contains(anotherSourceTarget.newId);
    }

    @Test
    public void skipsDuplicateElementsOnAppend() throws IOException {
        var sourceTarget = createIds(1, 100);
        var storage = new TaskKeeperLocationStorage(tempFolder.getRoot());
        storage.store("location1", "location2", Arrays.asList(sourceTarget));
        // add the same element again
        storage.store("location1", "location2", Arrays.asList(sourceTarget));

        var loaded = storage.loadTasks("location1", "location2");
        assertThat(loaded.getMapLeftToRight().keySet()).hasSize(1);
    }

    @Test
    public void findsSourceIdForReverseOperation() throws IOException {
        var sourceTarget = createIds(1, 100);
        var storage = new TaskKeeperLocationStorage(tempFolder.getRoot());
        storage.store("location1", "location2", Arrays.asList(sourceTarget));

        var loaded = storage.loadTasks("location1", "location2");
        assertThat(loaded.findSourceSystemIdentity(sourceTarget.newId, "location1"))
                .contains(sourceTarget.originalId);
    }

    private static TaskKeyMapping createIds(Integer sourceId, Integer targetId) {
        return new TaskKeyMapping(new TaskId(sourceId.longValue(), "task" + sourceId),
                new TaskId(targetId.longValue(), "task" + targetId));
    }
}
