package com.taskadapter.config;

import com.google.common.io.Files;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.SetupId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ConfigStorage {

    /**
     * file name extension for legacy configs
     * <p>
     * legacy configs do not have "ta.id" field and thus have no numeric id in them. they used full file name
     * as "id" until November 2020. Yay, 2020 is almost over now! I hope you all survived it.
     */
    private static final String legacyConfigFileExtension = ".ta_conf";
    private static final String setupFileExtension = "json";
    public static final String configFileExtension = ".conf";

    public static final FilenameFilter CONFIG_FILE_FILTER = (dir, name) -> name.endsWith(configFileExtension);

    public static final FilenameFilter LEGACY_CONFIG_FILE_FILTER = (dir, name) -> name.endsWith(legacyConfigFileExtension);

    public static final FilenameFilter setupFileFilter = (dir, name) -> name.endsWith(setupFileExtension);

    private static final Logger logger = LoggerFactory.getLogger(ConfigStorage.class);

    private final File rootDir;

    public ConfigStorage(File rootDir) {
        this.rootDir = rootDir;
    }

    public File getRootDir() {
        return rootDir;
    }

    public static int findUnusedConfigId(File userFolder) {
        int numberOfFiles = countNumberOfFilesInDirectory(userFolder);

        // try the next number (+1)
        int nextCandidateId = numberOfFiles;
        File file;
        do {
            nextCandidateId += 1;
            file = new File(userFolder, createFileName(nextCandidateId));
        } while (
                file.exists()
        );

        return nextCandidateId;
    }

    private static int countNumberOfFilesInDirectory(File folder) {
        return folder.list().length;
    }

    public static String createFileName(int id) {
        return id + configFileExtension;
    }

    public static File getUserConfigsFolder(File rootDir, String userLoginName) {
        return new File(getUserFolder(rootDir, userLoginName), "configs");
    }

    public static File getUserFolder(File rootDir, String userLoginName) {
        return new File(rootDir, userLoginName);
    }

    public Optional<StoredExportConfig> getConfig(ConfigId configId) {
        var file = getConfigFile(configId);
        if (file.exists()) {
            return parseFile(file);
        }
        return Optional.empty();
    }

    private static Optional<StoredExportConfig> parseFile(File file) {
        try {
            String fileBody = Files.toString(file, StandardCharsets.UTF_8);
            return Optional.of(NewConfigParser.parse(fileBody));
        } catch (IOException e) {
            logger.error("cannot parse config file " + file.getAbsolutePath() + ". the error is: " + e, e);
            return Optional.empty();
        }
    }

    public File getUserFolder(String userLoginName) {
        return ConfigStorage.getUserFolder(rootDir, userLoginName);
    }

    public List<StoredExportConfig> getUserConfigs(String userLoginName) {
        var folder = ConfigStorage.getUserConfigsFolder(rootDir, userLoginName);

        var configFiles = folder.listFiles(ConfigStorage.CONFIG_FILE_FILTER);
        var configs = getConfigsInFolder(configFiles);

        var configIds = configs.stream()
                .map(StoredExportConfig::getId)
                .collect(Collectors.toList());
        var largestIdInNonLegacyConfigs = configIds.stream().mapToInt(v -> v).max().orElse(0);

        var legacyConfigFiles = folder.listFiles(ConfigStorage.LEGACY_CONFIG_FILE_FILTER);
        var legacyConfigs = getLegacyConfigsInFolder(userLoginName, legacyConfigFiles, largestIdInNonLegacyConfigs);

        configs.addAll(legacyConfigs);
        return configs;
    }

    private List<StoredExportConfig> getConfigsInFolder(File[] configFiles) {
        if (configFiles == null) {
            return List.of();
        }
        return Arrays.stream(configFiles)
                .map(ConfigStorage::parseFile)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param largestIdInNonLegacyConfigs is used to name newly converted configs, to ensure their names do not conflict
     */
    private List<StoredExportConfig> getLegacyConfigsInFolder(String userLoginName, File[] configFiles,
                                                              int largestIdInNonLegacyConfigs) {
        final AtomicInteger lastUsedId = new AtomicInteger(largestIdInNonLegacyConfigs);
        if (configFiles == null) {
            return List.of();
        }
        return Arrays.stream(configFiles).map(file -> parseLegacyConfig(userLoginName, lastUsedId, file))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private Optional<StoredExportConfig> parseLegacyConfig(String userLoginName, AtomicInteger lastUsedId, File file) {
        try {
            var fileBody = Files.toString(file, StandardCharsets.UTF_8);
            var newId = lastUsedId.incrementAndGet();
            var newConfig = NewConfigParser.parseLegacyConfig(newId, fileBody);
            saveConfig(userLoginName, newId, newConfig.getName(),
                    newConfig.getConnector1().getConnectorTypeId(), newConfig.getConnector1().getConnectorSavedSetupId(), newConfig.getConnector1().getSerializedConfig(),
                    newConfig.getConnector2().getConnectorTypeId(), newConfig.getConnector2().getConnectorSavedSetupId(), newConfig.getConnector2().getSerializedConfig(),
                    newConfig.getMappingsString());
            file.renameTo(new File(file.getAbsoluteFile() + ".bak"));
            return Optional.of(newConfig);
        } catch (Exception e) {
            logger.error("Error loading legacy config file " + file.getAbsolutePath() + ": " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    // TODO TA3 unify saveConfig() and createNewConfig()
    public void saveConfig(String userLoginName, int configId, String configName,
                           String connector1Id,
                           SetupId connector1SavedSetupId,
                           String connector1Data,
                           String connector2Id,
                           SetupId connector2SavedSetupId,
                           String connector2Data,
                           String mappings) throws StorageException {
        logger.info("Saving config " + configId + " for user " + userLoginName);
        var fileContents = NewConfigParser.toFileContent(configId, configName, connector1Id, connector1SavedSetupId, connector1Data,
                connector2Id, connector2SavedSetupId, connector2Data, mappings);
        try {
            var folder = ConfigStorage.getUserConfigsFolder(rootDir, userLoginName);
            folder.mkdirs();
            var newConfigFile = getConfigFile(new ConfigId(userLoginName, configId));
            Files.write(fileContents, newConfigFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    /**
     * @return unique id for the new config to find it in the storage
     */

    public ConfigId createNewConfig(String userLoginName, String configName,
                                    String connector1Id, SetupId connector1SavedSetupId, String connector1Data,
                                    String connector2Id, SetupId connector2SavedSetupId, String connector2Data,
                                    String mappings) throws StorageException {
        try {
            var folder = ConfigStorage.getUserConfigsFolder(rootDir, userLoginName);
            folder.mkdirs();
            var newId = ConfigStorage.findUnusedConfigId(folder);
            var newConfigFile = getConfigFile(new ConfigId(userLoginName, newId));

            var fileContents = NewConfigParser.toFileContent(newId, configName,
                    connector1Id, connector1SavedSetupId, connector1Data,
                    connector2Id, connector2SavedSetupId, connector2Data,
                    mappings);

            Files.write(fileContents, newConfigFile, StandardCharsets.UTF_8);
            return new ConfigId(userLoginName, newId);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public void saveConnectorSetup(String userLoginName, SetupId setupId, String connectorSetup) throws StorageException {
        logger.info("Saving connector setup for user " + userLoginName + ". id " + setupId);
        try {
            var folder = ConfigStorage.getUserFolder(rootDir, userLoginName);
            folder.mkdirs();
            var newConfigFile = new File(folder, setupId.getId());
            Files.write(connectorSetup, newConfigFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public String loadConnectorSetupAsString(String userName, SetupId setupId) throws IOException {
        var folder = ConfigStorage.getUserFolder(rootDir, userName);
        var file = new File(folder, setupId.getId());
        return Files.toString(file, StandardCharsets.UTF_8);
    }

    public List<String> getAllConnectorSetupsAsStrings(String userLoginName) {
        var folder = ConfigStorage.getUserFolder(rootDir, userLoginName);
        var setupFiles = folder.listFiles(ConfigStorage.setupFileFilter);
        if (setupFiles == null) {
            return List.of();
        }

        return Arrays.stream(setupFiles)
                .map(file -> {
                    try {
                        return Files.toString(file, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    public void deleteConfig(ConfigId configId) {
        getConfigFile(configId)
                .delete();
    }

    public void deleteSetup(String userName, SetupId id) {
        new File(ConfigStorage.getUserFolder(rootDir, userName), id.getId())
                .delete();
    }

    private File getConfigFile(ConfigId configId) {
        return new File(ConfigStorage.getUserConfigsFolder(rootDir, configId.getOwnerName()),
                ConfigStorage.createFileName(configId.getId()));
    }
}
