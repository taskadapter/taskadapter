package com.taskadapter.integrationtests;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import net.sf.mpxj.TaskField;

import java.io.File;
import java.io.IOException;

public class AbstractSyncRunnerTest {

    /**
     * Generate a temporary MSP file using the contents of the provided file.
     * This is useful when a test needs to overwrite a file. We don't want to destroy the
     * original test data.
     */
    protected MSPConfig getConfig(String fileNameInClasspath) throws IOException {

        File temp = File.createTempFile("pattern", ".suffix");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        String fileContents = Resources.toString(Resources.getResource(fileNameInClasspath), Charsets.UTF_8);
        Files.write(fileContents, temp, Charsets.UTF_8);

        MSPConfig mspConfig = new MSPConfig(temp.getAbsolutePath());
        mspConfig.getFieldMappings().selectField(FIELD.REMOTE_ID);
        mspConfig.getFieldMappings().setMapping(FIELD.REMOTE_ID, TaskField.TEXT22.toString());
        return mspConfig;
    }

    protected MSPConfig createTempMSPConfig() {

        File temp;
        try {
            temp = File.createTempFile("pattern", ".suffix");
        } catch (IOException e) {
            throw new RuntimeException(e.toString(), e);
        }
        temp.deleteOnExit();

        MSPConfig mspConfig = new MSPConfig();
        mspConfig.setInputAbsoluteFilePath(temp.getAbsolutePath());
        mspConfig.setOutputAbsoluteFilePath(temp.getAbsolutePath());
        mspConfig.getFieldMappings().selectField(FIELD.REMOTE_ID);
        mspConfig.getFieldMappings().setMapping(FIELD.REMOTE_ID, TaskField.TEXT22.toString());
        return mspConfig;
    }

}
