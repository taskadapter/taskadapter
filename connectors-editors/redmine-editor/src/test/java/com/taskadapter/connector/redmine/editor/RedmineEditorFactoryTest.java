package com.taskadapter.connector.redmine.editor;

import java.util.Properties;

import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

import org.junit.Before;
import org.junit.Test;

public class RedmineEditorFactoryTest extends FileBasedTest {
    @Before
    public void before() {
        VaadinSession.setCurrent(new VaadinSession(new VaadinServletService(
                new VaadinServlet(), new DefaultDeploymentConfiguration(
                getClass(), new Properties()))));
    }

    @Test
    public void miniPanelIsCreated() {
        RedmineEditorFactory factory = new RedmineEditorFactory();
        factory.getMiniPanelContents(new Sandbox(true, tempFolder),
                new RedmineConfig());
    }
}
