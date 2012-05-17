package com.taskadapter;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class FileManager {

    // TODO maybe return File?
    public static String getDataRootFolderName() {
        String userHome = System.getProperty("user.home");
        return userHome + "/taskadapter";
    }

    public static File getUserFolder(String userLoginName) {
        return new File(getDataRootFolderName() + "/" + userLoginName);
    }

    public static String getUserFolderName(String userLoginName) {
        return getDataRootFolderName() + "/" + userLoginName;
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

    public File getFileForUser(String userLoginName, String fileName) {
        File userDirectory = getUserFolder(userLoginName);
        File file = new File(userDirectory, fileName);
        if (!file.exists()) {
            throw new RuntimeException("File does not exist: " + fileName + " in " + userDirectory + "folder");
        }
        return file;
    }
}
