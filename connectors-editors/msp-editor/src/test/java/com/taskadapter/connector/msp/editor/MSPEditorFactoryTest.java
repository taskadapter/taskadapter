package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.editor.error.InputFileNameNotSetException;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.service.Sandbox;
import org.junit.Test;

import java.io.File;

public class MSPEditorFactoryTest extends FileBasedTest {
    @Test(expected = InputFileNameNotSetException.class)
    public void noInputFileNameFailsValidation() throws BadConfigException {
        MSPConfig config = new MSPConfig();
        new MSPEditorFactory().validateForLoad(config);
    }

    @Test
    public void miniPanelInstanceIsCreated() {
        final Sandbox sandbox = new Sandbox(true, new File(tempFolder, "admin/trash"));
        MSPEditorFactory factory = new MSPEditorFactory();
        factory.getMiniPanelContents(sandbox, new MSPConfig());
    }

}
