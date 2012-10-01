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

    private PluginManager pluginManager;
    private File dataRootFolder;

    public ConfigStorage(PluginManager pluginManager, File dataRootFolder) {
        this.pluginManager = pluginManager;
        this.dataRootFolder = dataRootFolder;
    }

    public List<TAFile> getConfigs(String userLoginName) {
        File userFolder = getUserConfigsFolder(userLoginName);
        return getConfigsInFolder(userFolder);
    }

    private File getUserConfigsFolder(String userLoginName) {
        File userFolder = new FileManager(dataRootFolder).getUserFolder(userLoginName);
        return new File(userFolder, "configs");
    }

    private List<TAFile> getConfigsInFolder(File folder) {
        String[] fileNames = folder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION);
            }
        });
        List<TAFile> files = new ArrayList<TAFile>();
        if (fileNames != null) {
            for (String name : fileNames) {
                File file = new File(folder, name);
                try {
                    String fileBody = Files.toString(new File(file.getAbsolutePath()), Charsets.UTF_8);
                    ConfigFileParser parser = new ConfigFileParser(pluginManager);
                    TAFile taFile = parser.parse(fileBody);
                    taFile.setAbsoluteFilePath(file.getAbsolutePath());
                    files.add(taFile);
                } catch (Exception e) {
                    logger.error("Error loading file " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                }

            }
        }
        return files;
    }

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

    // TODO add unit tests
    private String createFileNameForNewConfig(TAFile file) {
        String fileName = file.getConnectorDataHolder1().getType() + "_" + file.getConnectorDataHolder2().getType() + NUMBER_SEPARATOR + "1";
        fileName = fileName.replaceAll(" ", "-");
        return fileName;
    }

    public void delete(TAFile config) {
        File file = new File(config.getAbsoluteFilePath());
        file.delete();
    }

    public void cloneConfig(String userLoginName, TAFile file) {
        TAFile cfg = new TAFile(file.getConfigLabel(), file.getConnectorDataHolder1(), file.getConnectorDataHolder2());
        this.createNewConfig(userLoginName, cfg);
    }
}