package com.taskadapter.integrationtests;

import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.util.MyIOUtils;
import net.sf.mpxj.TaskField;

import java.io.*;
import java.net.URL;
import java.util.Map;

// XXX Alexey: I don't see a need in "inheritance" here , this should be a utility method, not abstract class 
public class AbstractSyncRunnerTest {

    /**
     * Generate a temporary MSP file using the contents of the provided file.
     * This is useful when a test needs to overwrite the file. We don't want to destroy the
     * original test data.
     */
    protected MSPConfig getConfig(String fileName) throws IOException {

        File temp = File.createTempFile("pattern", ".suffix");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        // Write to temp file
        copyFile(fileName, temp);

        Map<FIELD, Mapping> fieldMapped = TestUtils.getFieldMapped(FIELD.REMOTE_ID, true, TaskField.TEXT22.toString());
        MSPConfig mspConfig = new MSPConfig(temp.getAbsolutePath());
        mspConfig.getFieldsMapping().putAll(fieldMapped);
        return mspConfig;
    }

    private void copyFile(String fileName, File temp)
            throws UnsupportedEncodingException, FileNotFoundException,
            IOException {
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

        Map<FIELD, Mapping> fieldMapped = TestUtils.getFieldMapped(FIELD.REMOTE_ID, true, TaskField.TEXT22.toString());
        MSPConfig mspConfig = new MSPConfig();
        mspConfig.setInputFileName(temp.getAbsolutePath());
        mspConfig.setOutputFileName(temp.getAbsolutePath());
        mspConfig.getFieldsMapping().putAll(fieldMapped);
        return mspConfig;
    }

}
