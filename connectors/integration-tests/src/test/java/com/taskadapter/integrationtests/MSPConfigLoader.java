package com.taskadapter.integrationtests;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.msp.MSPConnector;

import java.io.File;
import java.io.IOException;

public class MSPConfigLoader {
    /**
     * Generate a temporary MSP file using the contents of the provided file.
     * This is useful when a test needs to overwrite a file. We don't want to destroy the
     * original test data.
     */
    public static FileSetup generateTemporaryConfig(String fileNameInClasspath) throws IOException {

        File temp = File.createTempFile("pattern", ".xml");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        String fileContents = Resources.toString(Resources.getResource(fileNameInClasspath), Charsets.UTF_8);
        Files.write(fileContents, temp, Charsets.UTF_8);

        return FileSetup.apply(MSPConnector.ID, "label", temp.getAbsolutePath(), temp.getAbsolutePath());
    }

}
