package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.mantis.MantisConnector;
import com.taskadapter.web.service.Sandbox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class MantisEditorFactoryTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private final MantisEditorFactory factory = new MantisEditorFactory();
    private final MantisConfig config = new MantisConfig();
    private final WebConnectorSetup setup = WebConnectorSetup.apply(MantisConnector.ID,
            "label1", "host", "user", "password", false, "");

    @Test
    public void miniPanelInstanceIsCreated() {
        factory.getMiniPanelContents(new Sandbox(false, tempFolder.getRoot()), new MantisConfig(), setup);
    }

    @Test
    public void saveGivesErrorForEmptyProjectKey() {
        var errors = factory.validateForSave(new MantisConfig(), setup, Collections.emptyList());
        assertThat(errors.get(0)).isInstanceOf(ProjectNotSetException.class);
    }

    @Test
    public void saveGivesErrorForEmptyHostName() {
        setup.setHost("");
        var errors = factory.validateForSave(new MantisConfig(), setup, Collections.emptyList());
        assertThat(errors.get(0)).isInstanceOf(ServerURLNotSetException.class);
    }

    @Test
    public void loadGivesErrorWhenBothProjectKeyAndQueryIdAreEmpty() {
        var errors = factory.validateForLoad(config, setup);
        assertThat(errors.get(0)).isInstanceOf(BothProjectKeyAndQueryIsAreMissingException.class);
    }

    @Test
    public void loadPassesWhenProjectKeyIsEmptyButQueryIdIsDdefined() {
        config.setQueryId(123L);
        var errors = factory.validateForLoad(config, setup);
        assertThat(errors).isEmpty();
    }

    @Test
    public void loadPassesWhenProjectKeyIsDefinedButQueryIdIsEmpty() {
        config.setProjectKey("project1");
        var errors = factory.validateForLoad(config, setup);
        assertThat(errors).isEmpty();
    }
}
