package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.beans.BasecampProject;
import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.exceptions.ObjectNotFoundException;
import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

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
        assertNotNull(api.getObject("people/me.json"));
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
