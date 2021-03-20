package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.editor.testlib.VaadinTestHelper;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.flow.data.binder.Binder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class RedmineEditorFactoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void beforeEachTest() {
        VaadinTestHelper.initVaadinSession(RedmineEditorFactoryTest.class);
    }

    private RedmineEditorFactory factory = new RedmineEditorFactory();
    private RedmineConfig config = new RedmineConfig();
    private WebConnectorSetup setup = WebConnectorSetup.apply(RedmineConnector.ID, "label1", "http://somehost", "", "", false, "");
    private Binder<RedmineConfig> binder = new Binder<>();

    @Test
    public void miniPanelIsCreated() {
        factory.getMiniPanelContents(new Sandbox(true, tempFolder.getRoot()), new RedmineConfig(), setup);
    }

    @Test
    public void givesErrorForEmptyProjectKey() {
        var exceptions = factory.validateForSave(new RedmineConfig(), setup, Collections.emptyList());
        assertThat(exceptions.get(0)).isInstanceOf(ProjectNotSetException.class);
    }

    @Test
    public void passesWithSomeProjectKey() {
        config.setProjectKey("project123");
        var errors = factory.validateForSave(config, setup, Collections.emptyList());
        assertThat(errors).isEmpty();
    }
}
