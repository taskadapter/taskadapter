package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Properties;

public class RedmineEditorFactoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void before() {
        VaadinSession.setCurrent(new VaadinSession(new VaadinServletService(
                new VaadinServlet(), new DefaultDeploymentConfiguration(
                getClass(), new Properties()))));
    }

    @Test
    public void miniPanelIsCreated() {
        RedmineEditorFactory factory = new RedmineEditorFactory();
        factory.getMiniPanelContents(new Sandbox(true, tempFolder.getRoot()),
                new RedmineConfig());
    }
}
