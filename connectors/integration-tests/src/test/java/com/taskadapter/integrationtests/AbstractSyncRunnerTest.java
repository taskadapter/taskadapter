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

    // TODO !!! delete
  /*  protected MSPConfig createTempMSPConfig() {

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
    }*/

}
