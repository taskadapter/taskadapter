package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.editor.error.InputFileNameNotSetException;
import com.taskadapter.web.service.Sandbox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class MSPEditorFactoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected = InputFileNameNotSetException.class)
    public void noInputFileNameFailsValidation() throws BadConfigException {
        MSPConfig config = new MSPConfig();
        new MSPEditorFactory().validateForLoad(config, new WebServerInfo());
    }

    @Test
    public void miniPanelInstanceIsCreated() {
        final Sandbox sandbox = new Sandbox(true, new File(tempFolder.getRoot(), "admin/trash"));
        MSPEditorFactory factory = new MSPEditorFactory();
        factory.getMiniPanelContents(sandbox, new MSPConfig(), new WebServerInfo());
    }

}
