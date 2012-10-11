package com.taskadapter.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.taskadapter.FileManager;
import com.taskadapter.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigStorage {
    private final Logger logger = LoggerFactory.getLogger(ConfigStorage.class);

    private static final String FILE_EXTENSION = "ta_conf";
    private static final String NUMBER_SEPARATOR = "_";
    
    private static final FilenameFilter CONFIG_FILE_FILTER = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(FILE_EXTENSION);
        }
    }; 

    /**
     * @deprecated plugin manager should be used "one layer up". It is a simple
     * data store.
     */
    @Deprecated
    private final PluginManager pluginManager;
    private final FileManager fileManager;

    public ConfigStorage(PluginManager pluginManager, FileManager fileManager) {
        this.pluginManager = pluginManager;
        this.fileManager = fileManager;
    }
    
    public List<StoredExportConfig> getUserConfigs(String userLoginName) {
        return getNewConfigsInFolder(getUserConfigsFolder(userLoginName));
    }

    private File getUserConfigsFolder(String userLoginName) {
        File userFolder = fileManager.getUserFolder(userLoginName);
        return new File(userFolder, "configs");
    }

    private List<StoredExportConfig> getNewConfigsInFolder(File folder) {
        File[] configs = folder.listFiles(CONFIG_FILE_FILTER);
        final List<StoredExportConfig> files = new ArrayList<StoredExportConfig>();
        if (configs == null) {
            return files;
        }
        
        for (File file : configs) {
            try {
                final String fileBody = Files.toString(file, Charsets.UTF_8);
                files.add(NewConfigParser.parse(file.getAbsolutePath(), fileBody));
            } catch (Exception e) {
                logger.error("Error loading file " + file.getAbsolutePath() + ": " + e.getMessage(), e);
            }
        }
        return files;
    }
    
    public void saveConfig(String userLoginName, String configId,
            String configName, String connector1Id, String connector1Data,
            String connector2Id, String connector2Data, String mappings) throws StorageException {
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

    @Deprecated
    public void saveConfig(String userLoginName, TAFile taFile) {
        String fileContents = new ConfigFileParser(pluginManager).convertToJSonString(taFile);
        try {
            File folder = getUserConfigsFolder(userLoginName);
            folder.mkdirs();
            Files.write(fileContents, new File(taFile.getAbsoluteFilePath()), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String createNewConfig(String userLoginName, 
            String configName, String connector1Id, String connector1Data,
            String connector2Id, String connector2Data, String mappings) throws StorageException {
        final String fileContents = NewConfigParser.toFileContent(configName,
                connector1Id, connector1Data, connector2Id, connector2Data,
                mappings);
        try {
            final File folder = getUserConfigsFolder(userLoginName);
            folder.mkdirs();
            final File newConfigFile = findUnusedConfigFile(folder, connector1Id, connector2Id);
            Files.write(fileContents, newConfigFile, Charsets.UTF_8);
            return newConfigFile.getAbsolutePath();
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Deprecated
    public void createNewConfig(String userLoginName, TAFile taFile) {
        String fileContents = new ConfigFileParser(pluginManager).convertToJSonString(taFile);
        try {
            File userFolder = getUserConfigsFolder(userLoginName);
            userFolder.mkdirs();
            String absoluteFilePathForNewConfig = findUnusedAbsoluteFilePath(userFolder, taFile);
            taFile.setAbsoluteFilePath(absoluteFilePathForNewConfig);
            Files.write(fileContents, new File(absoluteFilePathForNewConfig), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO add unit tests
    private String findUnusedAbsoluteFilePath(File userFolder, TAFile taFile) {
        String relativeFileNameForNewConfig = createFileNameForNewConfig(taFile);
        File file = new File(userFolder, relativeFileNameForNewConfig + "." + FILE_EXTENSION);
        while (file.exists()) {
            int i = relativeFileNameForNewConfig.lastIndexOf(NUMBER_SEPARATOR);
            String numberStringWithExtension = relativeFileNameForNewConfig.substring(i + 1);
            int configFileNumber = Integer.parseInt(numberStringWithExtension);
            configFileNumber++;
            relativeFileNameForNewConfig = relativeFileNameForNewConfig.substring(0, i + 1) + configFileNumber;
            file = new File(userFolder, relativeFileNameForNewConfig + "." + FILE_EXTENSION);
        }
        return file.getAbsolutePath();
    }

    private File findUnusedConfigFile(File userFolder, String type1, String type2) {
        final String namePrefix = createFileNamePrefix(type1, type2);
        int fileOrdinal = 1;
        
        File file;
        do {
            file = new File(userFolder, namePrefix + fileOrdinal + "." + FILE_EXTENSION);
            fileOrdinal++;
        } while (file.exists());
        return file;
    }
    
    // TODO add unit tests
    private String createFileNameForNewConfig(TAFile file) {
        String fileName = file.getConnectorDataHolder1().getType() + "_" + file.getConnectorDataHolder2().getType() + NUMBER_SEPARATOR + "1";
        fileName = fileName.replaceAll(" ", "-");
        return fileName;
    }

    private String createFileNamePrefix(String type1, String type2) {
        String fileName = type1 + "_" + type2 + NUMBER_SEPARATOR;
        fileName = fileName.replaceAll(" ", "-");
        return fileName;
    }
    
    public void delete(String configId) {
        new File(configId).delete();
    }
    
    @Deprecated
    public void delete(TAFile config) {
        File file = new File(config.getAbsoluteFilePath());
        file.delete();
    }

    public void cloneConfig(String userLoginName, TAFile file) {
        TAFile cfg = new TAFile(file.getConfigLabel(), file.getConnectorDataHolder1(), file.getConnectorDataHolder2());
        this.createNewConfig(userLoginName, cfg);
    }
    
    public StoredExportConfig getConfig(String userLoginName, String configId)
            throws StorageException {
        final File file = new File(configId);
        try {
            final String fileBody = Files.toString(file, Charsets.UTF_8);
            return NewConfigParser.parse(file.getAbsolutePath(), fileBody);
        } catch (Exception e) {
            logger.error("Error loading file " + file.getAbsolutePath() + ": " + e.getMessage(), e);
            throw new StorageException("Failed to load file " + configId, e);
        }
    }
    
    @Deprecated
    public TAFile getLegacyConfig(String userLoginName, String configId) {
        final File file = new File(configId);
        String fileBody;
        try {
            fileBody = Files.toString(new File(file.getAbsolutePath()), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file", e);
        }
        ConfigFileParser parser = new ConfigFileParser(pluginManager);
        TAFile taFile = parser.parse(fileBody);
        taFile.setAbsoluteFilePath(file.getAbsolutePath());
        return taFile;
   }
}