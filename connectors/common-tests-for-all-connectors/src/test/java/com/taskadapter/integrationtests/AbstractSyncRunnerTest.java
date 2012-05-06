package com.taskadapter.integrationtests;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.util.MyIOUtils;
import net.sf.mpxj.TaskField;

import java.io.*;
import java.net.URL;

public class AbstractSyncRunnerTest {

    /**
     * Generate a temporary MSP file using the contents of the provided file.
     * This is useful when a test needs to overwrite a file. We don't want to destroy the
     * original test data.
     */
    protected MSPConfig getConfig(String fileName) throws IOException {

        File temp = File.createTempFile("pattern", ".suffix");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        // Write to temp file
        copyFile(fileName, temp);

        MSPConfig mspConfig = new MSPConfig(temp.getAbsolutePath());
        mspConfig.selectField(FIELD.REMOTE_ID);
        mspConfig.setFieldMappedValue(FIELD.REMOTE_ID, TaskField.TEXT22.toString());
        return mspConfig;
    }

    private void copyFile(String fileName, File temp) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter
                (new FileOutputStream(temp), "UTF8"));

        URL resource = AbstractSyncRunnerTest.class.getResource(fileName);
        InputStream stream = resource.openStream();

        String oldFileStr = MyIOUtils.convertStreamToString(stream);
        out.write(oldFileStr);
        out.close();
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
        mspConfig.setInputFileName(temp.getAbsolutePath());
        mspConfig.setOutputFileName(temp.getAbsolutePath());
        mspConfig.selectField(FIELD.REMOTE_ID);
        mspConfig.setFieldMappedValue(FIELD.REMOTE_ID, TaskField.TEXT22.toString());
        return mspConfig;
    }

}
