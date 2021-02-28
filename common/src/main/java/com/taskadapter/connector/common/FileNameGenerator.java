package com.taskadapter.connector.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileNameGenerator {

    private static Logger logger = LoggerFactory.getLogger(FileNameGenerator.class);

    /**
     * Search for an unused file name in the given folder starting with suffix 1.
     *
     * @param rootFolder folder to generate a file name for
     * @param format     sample: `MSP_export_%d.xml`
     * @return
     */
    public static File findSafeAvailableFileName(File rootFolder, String format) {
        return findSafeAvailableFileName(rootFolder, format, 10000);
    }

    public static File findSafeAvailableFileName(File rootFolder, String format, int numberOfTries) {

        var safeFormat = makeFileNameDiskSafe(format);
        var number = 1;
        rootFolder.mkdirs();
        while (number < numberOfTries) {
            var identifier = System.currentTimeMillis();
            var file = new File(rootFolder, String.format(safeFormat, identifier));
            logger.debug("Checking if file name " + file.getAbsolutePath() + " is available...");
            if (!file.exists()) return file;

            number += 1;
        }
        throw new RuntimeException("cannot generate available file name after " + numberOfTries + " attempts");
    }

    private static String makeFileNameDiskSafe(String potentialFileName) {
        return potentialFileName.replace(" ", "_");
    }
}
