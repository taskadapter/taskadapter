package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.mantis.MantisConnector;
import com.taskadapter.web.service.Sandbox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import scala.Option;

public class MantisEditorFactoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void miniPanelInstanceIsCreated() {
        MantisEditorFactory factory = new MantisEditorFactory();
        factory.getMiniPanelContents(new Sandbox(false, tempFolder.getRoot()), new MantisConfig(),
                new WebConnectorSetup(MantisConnector.ID(), Option.empty(), "label1", "host", "user",
                        "password", false, ""));
    }
}
