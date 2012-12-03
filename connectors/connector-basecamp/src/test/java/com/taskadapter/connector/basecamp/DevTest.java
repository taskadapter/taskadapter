package com.taskadapter.connector.basecamp;

import java.util.List;

import org.junit.Test;

import junit.framework.Assert;

import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;

public class DevTest {
    private static final String USER_ID = "2081543";
    private static final String USER_LOGIN = "basecamp.tester@mailinator.com";// "Tester's Basecamp";

    private static final String USER_PASSWORD = "lkajsaMLNnqw37sdafa;kjlsdf";
    private static final String XXX_PROJECT_KEY = "1630040";

    private final ObjectAPIFactory factory = new ObjectAPIFactory(
            new BaseCommunicator());

    private static final BasecampConfig BASE_CONFIG;

    static {
        final BasecampConfig cfg = new BasecampConfig();
        final BasicBasecampAuth auth = new BasicBasecampAuth();
        auth.setLogin(USER_LOGIN);
        auth.setPassword(USER_PASSWORD);
        cfg.setAuth(auth);
        cfg.setAccountId(USER_ID);
        BASE_CONFIG = cfg;
    }

    @Test
    public void testSomethingWork() throws ConnectorException {
        final ObjectAPI api = factory.createObjectAPI(BASE_CONFIG);
        Assert.assertNotNull(api.getObject("people/me.json"));
    }

    @Test
    public void testListProjects() throws ConnectorException {
        final List<GProject> projects = BasecampUtils.loadProjects(factory,
                BASE_CONFIG);
        Assert.assertTrue(projects.size() > 0);
    }

    @Test
    public void testTodoLists() throws ConnectorException {
        final BasecampConfig conf = new BasecampConfig();
        conf.setAccountId(USER_ID);
        conf.setAuth(BASE_CONFIG.getAuth());
        conf.setProjectKey(XXX_PROJECT_KEY);
        final List<TodoList> lists = BasecampUtils.loadTodoLists(factory, conf);
        System.out.println(lists);
        Assert.assertTrue(lists.size() > 0);
    }
}
