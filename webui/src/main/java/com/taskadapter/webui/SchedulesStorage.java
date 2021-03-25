package com.taskadapter.webui;

import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.Schedule;
import scala.reflect.Manifest;
import scala.reflect.ManifestFactory$;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SchedulesStorage {
    private final File dataFolder;
    private final Storage storage;
    private static Manifest<Schedule> manifest = ManifestFactory$.MODULE$.classType(Schedule.class);

    public SchedulesStorage(File rootDir) {
        dataFolder = new File(rootDir, "schedules");
        storage = new Storage(dataFolder, "schedule", "json");
    }

    public void store(Schedule schedule) {
        storage.store(schedule, schedule.getId());
    }

    public List<Schedule> getSchedules(ConfigId configId) {
        return getSchedules().stream().filter(r -> r.getConfigId().equals(configId)).collect(Collectors.toList());
    }

    public List<Schedule> getSchedules() {
        return storage.getItemsJava(Schedule.class);
    }

    public Optional<Schedule> get(String id) {
        return storage.get(id, manifest);
    }

    public void delete(String id) {
        storage.delete(id);
    }
}
