package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.connector.basecamp.classic.beans.BasecampProject;
import com.taskadapter.connector.basecamp.classic.beans.TodoList;
import com.taskadapter.connector.basecamp.classic.exceptions.ObjectNotFoundException;
import com.taskadapter.connector.basecamp.classic.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BasecampIntegrationTest {

    private final ObjectAPIFactory factory = new ObjectAPIFactory(
            new BaseCommunicator());

    private BasecampConfig config;

    @Before
    public void beforeEachTest() {
        config = TestBasecampConfig.create();
    }

    @Test
    public void smokeTest() throws ConnectorException {
        final ObjectAPI api = factory.createObjectAPI(config);
        assertNotNull(api.getObject("people/me.xml"));
    }

    @Test
    public void someProjectsAreLoaded() throws ConnectorException {
        final List<BasecampProject> projects = BasecampUtils.loadProjects(factory, config);
        assertTrue(projects.size() > 0);
    }

    @Test
    public void someTodoListsAreLoaded() throws ConnectorException {
        final List<TodoList> lists = BasecampUtils.loadTodoLists(factory,
                config);
        assertTrue(lists.size() > 0);
    }

    @Test
    public void projectIsLoaded() throws ConnectorException {
        assertNotNull(BasecampUtils.loadProject(factory, config));
    }

    @Test
    public void todoListIsCreatedAndDeleted() throws ConnectorException {
        long time = System.currentTimeMillis();

        String todoListName = "list" + time;
        String todoListDescription = "some description here" + time;
        TodoList todoList = BasecampUtils.createTodoList(factory, config, todoListName, todoListDescription);
        assertEquals(todoListName, todoList.getName());
        assertEquals(todoListDescription, todoList.getDescription());

        String key = todoList.getKey();
        config.setTodoKey(key);
        BasecampUtils.deleteTodoList(factory, config);

        try {
            BasecampUtils.loadTodoList(factory, config);
            fail("Must have failed with ObjectNotFoundException.");
        } catch (ObjectNotFoundException e) {
            System.out.println("Got expected ObjectNotFoundException");
        }

    }
}
