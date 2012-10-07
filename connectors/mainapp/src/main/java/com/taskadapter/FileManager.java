package com.taskadapter;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    private final File dataRootFolder;

    public FileManager(File dataRootFolder) {
        this.dataRootFolder = dataRootFolder;
    }

    public File getUserFolder(String userLoginName) {
        if (userLoginName == null) {
            throw new IllegalArgumentException("User name is null");
        }
        return new File(dataRootFolder, userLoginName);
    }
    
    public String[] listUsers() {
        return dataRootFolder.list();
    }

    /**
     * saves the file into &lt;home>/&lt;ta_login_name>/files/&lt;filename>
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
        return new File(getUserFolder(userLoginName), "files");
    }

    public File getFileForUser(String userLoginName, String fileName) {
        File userFilesFolder = getUserFilesFolder(userLoginName);
        File file = new File(userFilesFolder, fileName);
        if (!file.exists()) {
            throw new RuntimeException("File does not exist: " + fileName + " in " + userFilesFolder + "folder");
        }
        return file;
    }

    public static void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                deleteRecursively(c);
            }
        }
        if (!file.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + file);
        }
    }

    // TODO move to MSP Connector
    /**
     * Search for unused file name in user folder starting from postfix 1
     * TODO think about performance and optimization
     *
     * @return the new file name
     */
    public String createDefaultMSPFileName(String userLoginName) {
        String baseNameFormat = "MSP_export_%d.xml";
        int number = 1;
        while (number < 10000) {// give a chance to exit
            File userFilesFolder = getUserFilesFolder(userLoginName);
            userFilesFolder.mkdirs();
            File file = new File(userFilesFolder, String.format(baseNameFormat, number++));
            if (!file.exists()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
}
