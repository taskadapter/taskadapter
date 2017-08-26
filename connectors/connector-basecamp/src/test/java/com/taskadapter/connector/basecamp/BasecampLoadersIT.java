package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.beans.BasecampProject;
import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.exceptions.ObjectNotFoundException;
import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// Tired of re-creating Basecamp demo accounts. we don't have any Basecamp users, so
// let's just ignore these tests.
@Ignore
public class BasecampLoadersIT {

    private final ObjectAPIFactory factory = new ObjectAPIFactory(
            new BaseCommunicator());

    private BasecampConfig config;
    private WebConnectorSetup setup = TestBasecampConfig.setup();

    @Before
    public void beforeEachTest() {
        config = TestBasecampConfig.config();
    }

    @Test
    public void smokeTest() throws ConnectorException {
        final ObjectAPI api = factory.createObjectAPI(config, setup);
        assertNotNull(api.getObject("people/me.json"));
    }

    @Test
    public void someProjectsAreLoaded() throws ConnectorException {
        final List<BasecampProject> projects = BasecampUtils.loadProjects(factory, config, setup);
        assertTrue(projects.size() > 0);
    }

    @Test
    public void someTodoListsAreLoaded() throws ConnectorException {
        final List<TodoList> lists = BasecampUtils.loadTodoLists(factory,
                config, setup);
        assertTrue(lists.size() > 0);
    }

    @Test
    public void projectIsLoaded() throws ConnectorException {
        assertNotNull(BasecampUtils.loadProject(factory, config, setup));
    }

    @Test
    public void todoListIsCreatedAndDeleted() throws ConnectorException {
        long time = System.currentTimeMillis();

        String todoListName = "list" + time;
        String todoListDescription = "some description here" + time;
        TodoList todoList = BasecampUtils.createTodoList(factory, config, setup, todoListName, todoListDescription);
        assertEquals(todoListName, todoList.getName());
        assertEquals(todoListDescription, todoList.getDescription());

        String key = todoList.getKey();
        config.setTodoKey(key);
        BasecampUtils.deleteTodoList(factory, config, setup);

        try {
            BasecampUtils.loadTodoList(factory, config, setup);
            fail("Must have failed with ObjectNotFoundException.");
        } catch (ObjectNotFoundException e) {
            System.out.println("Got expected ObjectNotFoundException");
        }

    }
}
