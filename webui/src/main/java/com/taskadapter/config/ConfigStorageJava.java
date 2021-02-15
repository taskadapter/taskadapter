package com.taskadapter.config;

import java.io.File;
import java.io.FilenameFilter;

public class ConfigStorageJava {

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
