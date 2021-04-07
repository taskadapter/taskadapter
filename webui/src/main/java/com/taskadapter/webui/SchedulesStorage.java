package com.taskadapter.webui;

import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.Schedule;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SchedulesStorage {
    private final Storage storage;

    public SchedulesStorage(File rootDir) {
        File dataFolder = new File(rootDir, "schedules");
        storage = new Storage(dataFolder, "schedule", "json");
    }

    public void store(Schedule schedule) {
        storage.store(schedule, schedule.getId());
    }

    public List<Schedule> getSchedules(ConfigId configId) {
        return getSchedules().stream().filter(r -> r.getConfigId().equals(configId)).collect(Collectors.toList());
    }

    public List<Schedule> getSchedules() {
        return storage.getItems(Schedule.class);
    }

    public Optional<Schedule> get(String id) {
        return storage.get(id, Schedule.class);
    }

    public void delete(String id) {
        storage.delete(id);
    }
}
