package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class JiraEditorFactoryTest extends FileBasedTest {
    @Before
    public void before() {
        VaadinSession.setCurrent(new VaadinSession(new VaadinServletService(
                new VaadinServlet(), new DefaultDeploymentConfiguration(
                getClass(), new Properties()))));
    }

    @Test
    public void miniPanelIsCreated() {
        JiraEditorFactory factory = new JiraEditorFactory();
        factory.getMiniPanelContents(new Sandbox(false, tempFolder), new JiraConfig());
    }

    @Test(expected = ServerURLNotSetException.class)
    public void serverURLIsRequiredForSave() throws BadConfigException {
        new JiraEditorFactory().validateForSave(new JiraConfig());
    }

    @Test(expected = ProjectNotSetException.class)
    public void projectKeyIsRequired() throws BadConfigException {
        JiraConfig config = new JiraConfig();
        config.setServerInfo(new WebServerInfo("http://somehost", "", ""));
        new JiraEditorFactory().validateForSave(config);
    }

}
