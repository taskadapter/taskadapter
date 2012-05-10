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

    public File getServerDirectoryForTAFiles() {
        String rootFolderName = ConfigStorage.getRootFolderName();
        return new File(rootFolderName + "/files");
    }

    /**
     * saves the file into a special folder like home/user1/file
     */
    public void saveFileOnServer(String fileName, byte[] bytes) throws IOException {
        File file = new File(getServerDirectoryForTAFiles(), fileName);
        Files.createParentDirs(file);
        Files.write(bytes, file);
    }


    public String getFullFileNameOnServer(String fileName) {
        File file = new File(getServerDirectoryForTAFiles(), fileName);
        return file.getAbsolutePath();
    }

    public List<File> getUserFiles(String userLoginName) {
        File serverDirectoryForTAFiles = getServerDirectoryForTAFiles();
        File userDirectory = new File(serverDirectoryForTAFiles, userLoginName);
        return userDirectory.exists() ? Arrays.asList(userDirectory.listFiles()) : new ArrayList<File>();
    }
}
