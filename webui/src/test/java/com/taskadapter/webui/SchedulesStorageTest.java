package com.taskadapter.webui;

import com.taskadapter.connector.testlib.RandomStringGenerator;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.Schedule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulesStorageTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void canSaveAndLoadSchedule() {
        var storage = new SchedulesStorage(folder.getRoot());
        var item = new Schedule("id", new ConfigId("admin", 1), 1, true, true);
        storage.store(item);

        assertThat(storage.getSchedules().get(0))
                .isEqualTo(item);
    }

    @Test
    public void findsScheduleByConfigId() {
        var storage = new SchedulesStorage(folder.getRoot());
        var id1 = new ConfigId("admin", 1);
        var id2 = new ConfigId("admin", 2);
        var item1 = withConfigId(id1);
        var item2 = withConfigId(id2);
        var item3 = withConfigId(id2);
        var item4 = withConfigId(id1);
        storage.store(item1);
        storage.store(item2);
        storage.store(item3);
        storage.store(item4);

        assertThat(storage.getSchedules(id2))
                .containsOnly(item2, item3);
    }

    private static Schedule withConfigId(ConfigId configId) {
        return new Schedule(RandomStringGenerator.randomAlphaNumeric(), configId, 1, true, true);
    }
}