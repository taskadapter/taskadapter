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
     * saves the file into <home>/<ta_login_name>/files/<filename>
     */
    public void saveFileOnServer(String userLoginName, String fileName, byte[] bytes) throws IOException {
        File file = new File(getUserFilesFolder(userLoginName), fileName);
        Files.createParentDirs(file);
        Files.write(bytes, file);
    }

    public List<File> getUserFiles(String userLoginName) {
        File userFilesFolder = getUserFilesFolder(userLoginName);
        return userFilesFolder.exists() ? Arrays.asList(userFilesFolder.listFiles()) : new ArrayList<File>();
    }

    public File getUserFilesFolder(String userLoginName) {
        return new File(getUserFolderName(userLoginName) + "/files");
    }

    public File getFileForUser(String userLoginName, String fileName) {
        File userFilesFolder = getUserFilesFolder(userLoginName);
        File file = new File(userFilesFolder, fileName);
        if (!file.exists()) {
            throw new RuntimeException("File does not exist: " + fileName + " in " + userFilesFolder + "folder");
        }
        return file;
    }
}
