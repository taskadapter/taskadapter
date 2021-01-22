package com.taskadapter.config;

import java.io.File;

public class ConfigStorageJava {
    public static final String configFileExtension = ".conf";

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
}
