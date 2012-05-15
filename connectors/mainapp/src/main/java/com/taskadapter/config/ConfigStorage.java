package com.taskadapter.config;

import com.taskadapter.PluginManager;
import com.taskadapter.util.MyIOUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigStorage {
    private static final String FILE_EXTENSION = "ta_conf";
    private static final String NUMBER_SEPARATOR = "_";

    private PluginManager pluginManager;

    public ConfigStorage(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public List<TAFile> getAllConfigs() {
        File root = new File(getRootFolderName());
        String[] fileNames = root.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION);
            }
        });
        List<TAFile> files = new ArrayList<TAFile>();
        if (fileNames != null) {
            for (String name : fileNames) {
                File file = new File(root, name);
                try {
                    String fileBody = MyIOUtils.loadFile(file.getAbsolutePath());
                    ConfigFileParser parser = new ConfigFileParser(pluginManager);
                    TAFile taFile = parser.parse(fileBody);
                    taFile.setAbsoluteFilePath(file.getAbsolutePath());
                    files.add(taFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return files;
    }

    public void saveConfig(TAFile taFile) {
        String fileContents = new ConfigFileParser(pluginManager).convertToJSonString(taFile);
        try {
            File rootDir = new File(getRootFolderName());
            rootDir.mkdirs();
            MyIOUtils.writeToFile(taFile.getAbsoluteFilePath(), fileContents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNewConfig(TAFile taFile) {
        String fileContents = new ConfigFileParser(pluginManager).convertToJSonString(taFile);
        try {
            File rootDir = new File(getRootFolderName());
            rootDir.mkdirs();
            String absoluteFilePathForNewConfig = findUnusedAbsoluteFilePath(taFile);
            // TODO do not set it, delete
            taFile.setAbsoluteFilePath(absoluteFilePathForNewConfig);
            MyIOUtils.writeToFile(absoluteFilePathForNewConfig, fileContents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO add unit tests
    private String findUnusedAbsoluteFilePath(TAFile taFile) {
        String relativeFileNameForNewConfig = createFileNameForNewConfig(taFile);
        File file = new File(getRootFolderName(), relativeFileNameForNewConfig + "." + FILE_EXTENSION);
        while (file.exists()) {
            int i = relativeFileNameForNewConfig.lastIndexOf(NUMBER_SEPARATOR);
            String numberStringWithExtension = relativeFileNameForNewConfig.substring(i + 1);
            int configFileNumber = Integer.parseInt(numberStringWithExtension);
            configFileNumber++;
            relativeFileNameForNewConfig = relativeFileNameForNewConfig.substring(0, i + 1) + configFileNumber;
            file = new File(getRootFolderName(), relativeFileNameForNewConfig + "." + FILE_EXTENSION);
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

    public static String getRootFolderName() {
        String userHome = System.getProperty("user.home");
        return userHome + "/taskadapter";
    }

    public void cloneConfig(TAFile file, String clonedConfigLabel) {
        TAFile cfg = new TAFile(clonedConfigLabel, file.getConnectorDataHolder1(), file.getConnectorDataHolder2());
        this.saveConfig(cfg);
    }
}