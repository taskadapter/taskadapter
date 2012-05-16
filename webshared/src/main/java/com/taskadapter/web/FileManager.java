package com.taskadapter.web;

import com.google.common.io.Files;
import com.taskadapter.config.ConfigStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class FileManager {

    private File getServerDirectoryForTAFiles() {
        String rootFolderName = ConfigStorage.getRootFolderName();
        return new File(rootFolderName + "/files");
    }

    /**
     * saves the file into a special folder like home/user1/file
     */
    public void saveFileOnServer(String userLoginName, String fileName, byte[] bytes) throws IOException {
        File file = new File(getUserFolder(userLoginName), fileName);
        Files.createParentDirs(file);
        Files.write(bytes, file);
    }

    public List<File> getUserFiles(String userLoginName) {
        File userDirectory = getUserFolder(userLoginName);
        return userDirectory.exists() ? Arrays.asList(userDirectory.listFiles()) : new ArrayList<File>();
    }

    public File getUserFolder(String userLoginName) {
        File serverDirectoryForTAFiles = getServerDirectoryForTAFiles();
        return new File(serverDirectoryForTAFiles, userLoginName);
    }

    public File getFileForUser(String userLoginName, String fileName) {
        File userDirectory = getUserFolder(userLoginName);
        File file = new File(userDirectory, fileName);
        if (!file.exists()) {
            throw new RuntimeException("File does not exist: " + fileName + " in " + userDirectory + "folder");
        }
        return file;
    }
}
