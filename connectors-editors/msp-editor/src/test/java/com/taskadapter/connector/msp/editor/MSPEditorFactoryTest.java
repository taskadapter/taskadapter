package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.editor.error.InputFileNameNotSetException;
import com.taskadapter.web.service.Sandbox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import scala.collection.Seq;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class MSPEditorFactoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void noInputFileNameFailsValidation() {
        MSPConfig config = new MSPConfig();
        Seq<BadConfigException> errors = new MSPEditorFactory()
                .validateForLoad(config, FileSetup.apply(MSPConnector.ID, "label1", "", ""));
        assertTrue(errors.head() instanceof InputFileNameNotSetException);
    }

    @Test
    public void miniPanelInstanceIsCreated() {
        final Sandbox sandbox = new Sandbox(true, new File(tempFolder.getRoot(), "admin/trash"));
        MSPEditorFactory factory = new MSPEditorFactory();
        factory.getMiniPanelContents(sandbox, new MSPConfig(),
                FileSetup.apply(MSPConnector.ID, "label1", "infile", "outfile"));
    }

}
