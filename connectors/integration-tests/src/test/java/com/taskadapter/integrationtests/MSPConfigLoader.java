package com.taskadapter.integrationtests;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.taskadapter.connector.msp.MSPConfig;

import java.io.File;
import java.io.IOException;

public class MSPConfigLoader {
    /**
     * Generate a temporary MSP file using the contents of the provided file.
     * This is useful when a test needs to overwrite a file. We don't want to destroy the
     * original test data.
     */
    public static MSPConfig generateTemporaryConfig(String fileNameInClasspath) throws IOException {

        File temp = File.createTempFile("pattern", ".suffix");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        String fileContents = Resources.toString(Resources.getResource(fileNameInClasspath), Charsets.UTF_8);
        Files.write(fileContents, temp, Charsets.UTF_8);

        MSPConfig mspConfig = new MSPConfig(temp.getAbsolutePath());
        return mspConfig;
    }

}
