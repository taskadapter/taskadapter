package com.taskadapter.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.common.JsonUtil;
import com.taskadapter.connector.definition.TaskKeyMapping;
import com.taskadapter.core.PreviouslyCreatedTasksCache;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.google.common.io.Files;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.core.TaskKeeperLocation;

public class TaskKeeperLocationStorage {
    private static final String fileName = "cache_file_links.json";
    private final File cacheFolder;

    public TaskKeeperLocationStorage(File rootFolder) {
        cacheFolder = new File(rootFolder, "cache");
    }

    /**
     * Store elements in the cache on disk. New items are added to existing ones, skipping duplicates.
     * If file does not exist yet, it will be created.
     */
    public void store(String location1, String location2, List<TaskKeyMapping> items) throws IOException {
        var file = getOrCreateFileLocation(location1, location2);
        var existingCache = loadCache(location1, location2);
        var allItems = new ArrayList<>(existingCache.getItems());
        allItems.addAll(items);
        List<TaskKeyMapping> distinctItems = Lists.newArrayList(Sets.newHashSet(allItems));
        var newCache = new PreviouslyCreatedTasksCache(location1, location2, distinctItems);

        var jsonString = JsonUtil.toJsonString(newCache);
        Files.write(jsonString, file, StandardCharsets.UTF_8);
    }

    public PreviouslyCreatedTasksResolver loadTasks(String location1, String location2) throws IOException {
        return new PreviouslyCreatedTasksResolver(loadCache(location1, location2));
    }

    private PreviouslyCreatedTasksCache loadCache(String location1, String location2) throws IOException {
        var file = getOrCreateFileLocation(location1, location2);
        if (!file.exists() || file.length() == 0) {
            return new PreviouslyCreatedTasksCache("", "", java.util.List.of());
        }
        var fileBody = Files.toString(file, StandardCharsets.UTF_8);
        return JsonUtil.parseJsonString(fileBody, PreviouslyCreatedTasksCache.class);
    }

    private void saveCacheLocation(String location1, String location2, String newFileName) throws IOException {
        var previousEntries = loadCache();
        var newEntries = previousEntries;
        newEntries.add(new TaskKeeperLocation(location1, location2, newFileName));

        var jsonString = ConfigUtils.createDefaultGson().toJsonTree(newEntries).toString();
        Files.write(jsonString, new File(cacheFolder, fileName), StandardCharsets.UTF_8);
    }

    private List<TaskKeeperLocation> loadCache() throws IOException {
        var file = new File(cacheFolder, fileName);
        if (file.exists()) {
            var jsonString = Files.toString(file, StandardCharsets.UTF_8);
            var jsonValue = new JsonParser().parse(jsonString);
            List<TaskKeeperLocation> list = ConfigUtils.createDefaultGson().fromJson(jsonValue,
                    new TypeToken<List<TaskKeeperLocation>>() {
                    }.getType());
            return list;
        }
        return new ArrayList<>();
    }

    private File getOrCreateFileLocation(String location1, String location2) throws IOException {
        var linksToCaches = loadCache();
        var filePath = findCacheLocation(linksToCaches, location1, location2);
        if (filePath.isPresent()) {
            return new File(cacheFolder, filePath.get()).getAbsoluteFile();
        }

        var newFileName = Math.abs(new Random().nextInt()) + ".json";
        var newFile = new File(cacheFolder, newFileName);
        cacheFolder.mkdirs();
        newFile.createNewFile();

        saveCacheLocation(location1, location2, newFileName);
        return newFile.getAbsoluteFile();
    }

    private Optional<String> findCacheLocation(List<TaskKeeperLocation> caches, String location1, String location2) {
        return caches.stream().filter(e ->
                // either direction is fine
                (e.getLocation1().equals(location1) && e.getLocation2().equals(location2))
                        ||
                        (e.getLocation2().equals(location1) && e.getLocation1().equals(location2))
        ).findFirst().map(TaskKeeperLocation::getCacheFileLocation);
    }

    public String getCacheFolderAbsoluteName() {
        return cacheFolder.getAbsolutePath();
    }
}
