package com.taskadapter.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ConfigStorage {
    private final Logger logger = LoggerFactory.getLogger(ConfigStorage.class);

    private static final String FILE_EXTENSION = "ta_conf";
    private static final String NUMBER_SEPARATOR = "_";

    private static final FilenameFilter CONFIG_FILE_FILTER = (dir, name) -> name.endsWith(FILE_EXTENSION);

    public final File rootDir;

    public ConfigStorage(File rootDir) {
        this.rootDir = rootDir;
    }

    public List<StoredExportConfig> getUserConfigs(String userLoginName) {
        return getNewConfigsInFolder(getUserConfigsFolder(userLoginName));
    }

    private File getUserConfigsFolder(String userLoginName) {
        final File userFolder = new File(rootDir, userLoginName);
        return new File(userFolder, "configs");
    }

    private List<StoredExportConfig> getNewConfigsInFolder(File folder) {
        File[] configs = folder.listFiles(CONFIG_FILE_FILTER);
        final List<StoredExportConfig> files = new ArrayList<>();
        if (configs == null) {
            return files;
        }

        for (File file : configs) {
            try {
                final String fileBody = Files.toString(file, Charsets.UTF_8);
                files.add(NewConfigParser.parse(file.getAbsolutePath(),
                        fileBody));
            } catch (Exception e) {
                logger.error("Error loading file " + file.getAbsolutePath()
                        + ": " + e.getMessage(), e);
            }
        }
        return files;
    }

    public void saveConfig(String userLoginName, String configId,
            String configName, String connector1Id, String connector1Data,
            String connector2Id, String connector2Data, String mappings)
            throws StorageException {
        final String fileContents = NewConfigParser.toFileContent(configName,
                connector1Id, connector1Data, connector2Id, connector2Data,
                mappings);
        try {
            File folder = getUserConfigsFolder(userLoginName);
            folder.mkdirs();
            Files.write(fileContents, new File(configId), Charsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public String createNewConfig(String userLoginName, String configName,
            String connector1Id, String connector1Data, String connector2Id,
            String connector2Data, String mappings) throws StorageException {
        final String fileContents = NewConfigParser.toFileContent(configName,
                connector1Id, connector1Data, connector2Id, connector2Data,
                mappings);
        try {
            final File folder = getUserConfigsFolder(userLoginName);
            folder.mkdirs();
            final File newConfigFile = findUnusedConfigFile(folder,
                    connector1Id, connector2Id);
            Files.write(fileContents, newConfigFile, Charsets.UTF_8);
            return newConfigFile.getAbsolutePath();
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public void saveConnectorSetup(String userLoginName, String setupLabel,String connectorSetup) throws StorageException {
        try {
            final File folder = getUserConfigsFolder(userLoginName);
            folder.mkdirs();
            File newConfigFile = new File(folder, setupLabel + ".json");
            Files.write(connectorSetup, newConfigFile, Charsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public String loadConnectorSetupAsString(String userName, String setupLabel) throws StorageException {
        try {
            final File folder = getUserConfigsFolder(userName);
            File file = new File(folder, setupLabel + ".json");
            return Files.toString(file, Charsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    private File findUnusedConfigFile(File userFolder, String type1,
            String type2) {
        final String namePrefix = createFileNamePrefix(type1, type2);
        int fileOrdinal = 1;

        File file;
        do {
            file = new File(userFolder, namePrefix + fileOrdinal + "."
                    + FILE_EXTENSION);
            fileOrdinal++;
        } while (file.exists());
        return file;
    }

    private String createFileNamePrefix(String type1, String type2) {
        String fileName = type1 + "_" + type2 + NUMBER_SEPARATOR;
        fileName = fileName.replaceAll(" ", "-");
        return fileName;
    }

    public void delete(String configId) {
        new File(configId).delete();
    }
}