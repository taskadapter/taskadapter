package com.taskadapter.connector.basecamp;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import junit.framework.Assert;

import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class BasecampIntegrationTest {
    private static final String USER_ID = "2081543";
    private static final String USER_LOGIN = "basecamp.tester@mailinator.com";// "Tester's Basecamp";
    private static final String USER_PASSWORD = "lkajsaMLNnqw37sdafa;kjlsdf";
    private static final String PROJECT_KEY = "1630040";
    private static final String TODO_KEY = "3991077";

    private final ObjectAPIFactory factory = new ObjectAPIFactory(
            new BaseCommunicator());

    private static final BasecampConfig BASE_CONFIG;

    static {
        final BasecampConfig cfg = new BasecampConfig();
        final BasecampAuth auth = new BasecampAuth();
        auth.setLogin(USER_LOGIN);
        auth.setPassword(USER_PASSWORD);
        auth.setUseAPIKeyInsteadOfLoginPassword(false);
        cfg.setAuth(auth);
        cfg.setAccountId(USER_ID);
        cfg.setProjectKey(PROJECT_KEY);
        cfg.setTodoKey(TODO_KEY);
        cfg.setLookupUsersByName(true);
        BASE_CONFIG = cfg;
    }

    @Test
    public void testSomethingWork() throws ConnectorException {
        final ObjectAPI api = factory.createObjectAPI(BASE_CONFIG);
        assertNotNull(api.getObject("people/me.json"));
    }

    @Test
    public void someProjectsAreLoaded() throws ConnectorException {
        final List<GProject> projects = BasecampUtils.loadProjects(factory,
                BASE_CONFIG);
        assertTrue(projects.size() > 0);
    }

    @Test
    public void someTodoListsAreLoaded() throws ConnectorException {
        final List<TodoList> lists = BasecampUtils.loadTodoLists(factory,
                BASE_CONFIG);
        assertTrue(lists.size() > 0);
    }

    @Test
    public void predefinedTaskLoadedByKey() throws ConnectorException {
        final GTask task = new BasecampConnector(BASE_CONFIG, factory)
                .loadTaskByKey("23172907", new Mappings());
        assertNotNull(task);
        Assert.assertEquals("Create pron", task.getSummary());
        Assert.assertEquals("Create pron", task.getDescription());
    }

    @Test
    public void someTodosAreLoaded() throws ConnectorException {
        final List<GTask> tasks = new BasecampConnector(BASE_CONFIG, factory)
                .loadData(new Mappings(),
                        ProgressMonitorUtils.getDummyMonitor());
        assertNotNull(tasks);
        assertTrue(tasks.size() >= 0);
    }

    @Test
    public void projectIsLoaded() throws ConnectorException {
        assertNotNull(BasecampUtils.loadProject(factory, BASE_CONFIG));
    }

    @Test
    public void testGetTodoList() throws ConnectorException {
        assertNotNull(BasecampUtils.loadTodoList(factory, BASE_CONFIG));
    }

    @Test
    public void testManipulateCreateTodos() throws ConnectorException {
        final GTask task = new GTask();
        task.setId(123);
        task.setDescription("Hide from police");
        final Mappings mappings = new Mappings();
        mappings.setMapping(FIELD.DESCRIPTION, true, "content");

        final BasecampConnector conn = new BasecampConnector(BASE_CONFIG,
                factory);
        final TaskSaveResult res = conn.saveData(
                Collections.singletonList(task),
                ProgressMonitorUtils.getDummyMonitor(), mappings);
        Assert.assertEquals(1, res.getCreatedTasksNumber());
        Assert.assertEquals(0, res.getUpdatedTasksNumber());

        final GTask update = new GTask();
        update.setId(321);
        update.setRemoteId(res.getRemoteKey(123));
        update.setDescription("Change country");
        final TaskSaveResult res1 = conn.saveData(
                Collections.singletonList(update),
                ProgressMonitorUtils.getDummyMonitor(), mappings);
        Assert.assertEquals(0, res1.getCreatedTasksNumber());
        Assert.assertEquals(1, res1.getUpdatedTasksNumber());

        factory.createObjectAPI(BASE_CONFIG).delete(
                "/projects/" + PROJECT_KEY + "/todos/" + update.getRemoteId()
                        + ".json");
    }

    @Test
    public void testResolveUser() throws ConnectorException {
        final GTask task = new GTask();
        task.setId(123);
        task.setDescription("Find anybody");
        final Mappings mappings = new Mappings();
        mappings.setMapping(FIELD.DESCRIPTION, true, "content");
        mappings.setMapping(FIELD.ASSIGNEE, true, "assignee");
        final GUser me = new GUser();
        me.setId(321);
        me.setLoginName("<noname>");
        me.setDisplayName("Tester");
        task.setAssignee(me);

        final BasecampConnector conn = new BasecampConnector(BASE_CONFIG,
                factory);
        final TaskSaveResult res = conn.saveData(
                Collections.singletonList(task),
                ProgressMonitorUtils.getDummyMonitor(), mappings);
        Assert.assertEquals(1, res.getCreatedTasksNumber());
        Assert.assertEquals(0, res.getUpdatedTasksNumber());

        factory.createObjectAPI(BASE_CONFIG).delete(
                "/projects/" + PROJECT_KEY + "/todos/" + res.getRemoteKey(123)
                        + ".json");
    }

    @Test
    public void testMappingsSupported() throws ConnectorException {
        final Calendar cal = Calendar.getInstance();
        cal.set(2013, 2, 20, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Date date1 = cal.getTime();
        cal.set(2012, 2, 20, 0, 0, 0);
        final Date date2 = cal.getTime();
        final GTask template = new GTask();
        template.setId(123);
        template.setSummary("This is a description");
        template.setDoneRatio(100);
        template.setDueDate(date1);
        final GUser me = new GUser();
        me.setId(321);
        me.setLoginName("<noname>");
        me.setDisplayName("Tester");
        template.setAssignee(me);

        final Mappings allMappings = new Mappings();
        allMappings.setMapping(FIELD.SUMMARY, true, "content");
        allMappings.setMapping(FIELD.DONE_RATIO, true, "done_ratio");
        allMappings.setMapping(FIELD.DUE_DATE, true, "due_date");
        allMappings.setMapping(FIELD.ASSIGNEE, true, "assignee");

        final BasecampConnector conn = new BasecampConnector(BASE_CONFIG,
                factory);
        final TaskSaveResult res = conn.saveData(
                Collections.singletonList(template),
                ProgressMonitorUtils.getDummyMonitor(), allMappings);
        Assert.assertEquals(1, res.getCreatedTasksNumber());

        final String remoteId = res.getRemoteKey(123);
        template.setRemoteId(remoteId);

        final GTask update = new GTask();
        update.setId(123);
        update.setSummary("It should be updated");
        update.setDoneRatio(0);
        update.setDueDate(date2);
        update.setAssignee(null);
        update.setRemoteId(remoteId);

        try {
            GTask updated = update(conn, update, FIELD.SUMMARY, "content");
            Assert.assertEquals(update.getSummary(), updated.getSummary());
            Assert.assertEquals(template.getDoneRatio(), updated.getDoneRatio());
            Assert.assertEquals(template.getDueDate(), updated.getDueDate());
            Assert.assertEquals(template.getAssignee().getDisplayName(),
                    updated.getAssignee().getDisplayName());

            conn.saveData(Collections.singletonList(template),
                    ProgressMonitorUtils.getDummyMonitor(), allMappings);

            updated = update(conn, update, FIELD.DONE_RATIO, "done_ratio");
            Assert.assertEquals(template.getSummary(), updated.getSummary());
            Assert.assertEquals(update.getDoneRatio(), updated.getDoneRatio());
            Assert.assertEquals(template.getDueDate(), updated.getDueDate());
            Assert.assertEquals(template.getAssignee().getDisplayName(),
                    updated.getAssignee().getDisplayName());

            conn.saveData(Collections.singletonList(template),
                    ProgressMonitorUtils.getDummyMonitor(), allMappings);

            updated = update(conn, update, FIELD.DUE_DATE, "due_date");
            Assert.assertEquals(template.getSummary(), updated.getSummary());
            Assert.assertEquals(template.getDoneRatio(), updated.getDoneRatio());
            Assert.assertEquals(update.getDueDate(), updated.getDueDate());
            Assert.assertEquals(template.getAssignee().getDisplayName(),
                    updated.getAssignee().getDisplayName());

            conn.saveData(Collections.singletonList(template),
                    ProgressMonitorUtils.getDummyMonitor(), allMappings);

            updated = update(conn, update, FIELD.ASSIGNEE, "assignee");
            Assert.assertEquals(template.getSummary(), updated.getSummary());
            Assert.assertEquals(template.getDoneRatio(), updated.getDoneRatio());
            Assert.assertEquals(template.getDueDate(), updated.getDueDate());
            Assert.assertNull(updated.getAssignee());
        } finally {
            factory.createObjectAPI(BASE_CONFIG)
                    .delete("/projects/" + PROJECT_KEY + "/todos/" + remoteId
                            + ".json");
        }
    }

    private GTask update(BasecampConnector conn, GTask newTask, FIELD field,
            String target) throws ConnectorException {
        final Mappings newMappings = new Mappings();
        newMappings.setMapping(field, true, target);
        final TaskSaveResult res = conn.saveData(
                Collections.singletonList(newTask),
                ProgressMonitorUtils.getDummyMonitor(), newMappings);
        Assert.assertEquals(1, res.getUpdatedTasksNumber());
        return conn.loadTaskByKey(newTask.getRemoteId(), newMappings);
    }
}
