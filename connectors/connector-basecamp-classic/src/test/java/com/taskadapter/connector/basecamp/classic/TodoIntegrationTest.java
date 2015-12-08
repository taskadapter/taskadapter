package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.connector.basecamp.classic.beans.TodoList;
import com.taskadapter.connector.basecamp.classic.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TodoIntegrationTest {

    /**
     * the Basecamp account used for testing must have this exact full name
     */
    private static final String BASECAMP_CLASSIC_USER_DISPLAY_NAME = "Al TAdev";

    private final ObjectAPIFactory factory = new ObjectAPIFactory(new BaseCommunicator());

    private BasecampConfig config;

    @Before
    public void beforeEachTest() throws ConnectorException {
        config = TestBasecampConfig.create();
        long someId = System.currentTimeMillis();
        TodoList todoList = BasecampUtils.createTodoList(factory, config, "list " + someId, "");
        config.setTodoKey(todoList.getKey());
    }

    @After
    public void afterEachTest() throws ConnectorException {
        BasecampUtils.deleteTodoList(factory, config);
    }

    @Test
    public void todosAreLoaded() throws ConnectorException {
        TodoUtil.create(config, factory, TodoUtil.buildTask("task1"));

        GTask task2 = TodoUtil.buildTask("task2");
        task2.setDoneRatio(100);
        TodoUtil.create(config, factory, task2);

        List<GTask> tasks = TodoUtil.load(config, factory);
        assertEquals(2, tasks.size());
    }

    @Test
    public void todoCreatedAndLoadedByKey() throws ConnectorException {
        String summary = "some summary here";
        final GTask task = TodoUtil.create(config, factory, TodoUtil.buildTask(summary));
        String key = task.getKey();

        BasecampConnector connector = new BasecampConnector(config, factory);
        connector.loadTaskByKey(key, new Mappings());
        assertEquals(summary, task.getSummary());
    }

    @Test
    public void todoCreatedAndUpdated() throws ConnectorException {
        final GTask task = new GTask();
        task.setDescription("Some description here - 1020");
        final Mappings mappings = new Mappings();
        mappings.setMapping(GTaskDescriptor.FIELD.DESCRIPTION, true, "content", "default description");

        final BasecampConnector conn = new BasecampConnector(config, factory);
        final TaskSaveResult res = conn.saveData(
                Collections.singletonList(task),
                ProgressMonitorUtils.DUMMY_MONITOR, mappings);
        Assert.assertEquals(1, res.getCreatedTasksNumber());
        Assert.assertEquals(0, res.getUpdatedTasksNumber());

        final String key = res.getRemoteKeys().iterator().next();
        final GTask update = new GTask();
        update.setKey(key);
        update.setDescription("Change country");
        final TaskSaveResult res1 = conn.saveData(
                Collections.singletonList(update),
                ProgressMonitorUtils.DUMMY_MONITOR, mappings);
        Assert.assertEquals(0, res1.getCreatedTasksNumber());
        Assert.assertEquals(1, res1.getUpdatedTasksNumber());

        factory.createObjectAPI(config).delete("todo_items/" + update.getKey() + ".xml");
    }

    @Test
    public void testMappingsSupported() throws ConnectorException {
        final Calendar cal = Calendar.getInstance();
        cal.set(2013, 2, 20, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Date date1 = cal.getTime();
        cal.set(2012, 2, 20, 0, 0, 0);
        final Date date2 = cal.getTime();
        final GTask taskToCreate = new GTask();
        taskToCreate.setSummary("This is a description");
        taskToCreate.setDoneRatio(100);
        taskToCreate.setDueDate(date1);
        final GUser me = new GUser();
        me.setId(321);
        me.setLoginName("support@taskadapter.com");
        me.setDisplayName(BASECAMP_CLASSIC_USER_DISPLAY_NAME);
        taskToCreate.setAssignee(me);

        final Mappings allMappings = TodoUtil.getAllMappings();

        final BasecampConnector connector = new BasecampConnector(config, factory);
        final TaskSaveResult result = connector.saveData(
                Collections.singletonList(taskToCreate),
                ProgressMonitorUtils.DUMMY_MONITOR, allMappings);
        Assert.assertEquals(1, result.getCreatedTasksNumber());

        final String remoteKey = result.getRemoteKeys().iterator().next();

        final GTask taskToUpdate = new GTask();
        taskToUpdate.setKey(remoteKey);
        taskToUpdate.setSummary("It should be updated");
        taskToUpdate.setDoneRatio(0);
        taskToUpdate.setDueDate(date2);
        taskToUpdate.setAssignee(null);
        taskToUpdate.setRemoteId(remoteKey);

        GTask updated = update(connector, taskToUpdate, GTaskDescriptor.FIELD.SUMMARY, "content");
        Assert.assertEquals(taskToUpdate.getSummary(), updated.getSummary());
        Assert.assertEquals(taskToCreate.getDoneRatio(), updated.getDoneRatio());
        Assert.assertEquals(taskToCreate.getDueDate(), updated.getDueDate());
        Assert.assertEquals(taskToCreate.getAssignee().getDisplayName(),
                updated.getAssignee().getDisplayName());

        connector.saveData(Collections.singletonList(taskToCreate),
                ProgressMonitorUtils.DUMMY_MONITOR, allMappings);

        updated = update(connector, taskToUpdate, GTaskDescriptor.FIELD.DONE_RATIO, "done_ratio");
        Assert.assertEquals(taskToUpdate.getSummary(), updated.getSummary());
        Assert.assertEquals(taskToUpdate.getDoneRatio(), updated.getDoneRatio());
        Assert.assertEquals(taskToCreate.getDueDate(), updated.getDueDate());
        Assert.assertEquals(taskToCreate.getAssignee().getDisplayName(),
                updated.getAssignee().getDisplayName());

        connector.saveData(Collections.singletonList(taskToCreate),
                ProgressMonitorUtils.DUMMY_MONITOR, allMappings);

        updated = update(connector, taskToUpdate, GTaskDescriptor.FIELD.DUE_DATE, "due_date");
        Assert.assertEquals(taskToUpdate.getSummary(), updated.getSummary());
        Assert.assertEquals(taskToUpdate.getDoneRatio(), updated.getDoneRatio());
        Assert.assertEquals(taskToUpdate.getDueDate(), updated.getDueDate());
        assertEquals(BASECAMP_CLASSIC_USER_DISPLAY_NAME, updated.getAssignee().getDisplayName());

        connector.saveData(Collections.singletonList(taskToCreate),
                ProgressMonitorUtils.DUMMY_MONITOR, allMappings);

        updated = update(connector, taskToUpdate, GTaskDescriptor.FIELD.ASSIGNEE, "assignee");
        Assert.assertNull(updated.getAssignee());
    }

    private GTask update(BasecampConnector conn, GTask newTask, GTaskDescriptor.FIELD field,
                         String target) throws ConnectorException {
        final Mappings newMappings = new Mappings();
        newMappings.setMapping(field, true, target, "some default value");
        final TaskSaveResult res = conn.saveData(
                Collections.singletonList(newTask),
                ProgressMonitorUtils.DUMMY_MONITOR, newMappings);
        Assert.assertEquals(1, res.getUpdatedTasksNumber());
        return conn.loadTaskByKey(newTask.getRemoteId(), newMappings);
    }

    @Test
    public void testResolveUser() throws ConnectorException {
        final GTask task = new GTask();
        task.setId(123);
        task.setDescription("Find anybody");
        final Mappings mappings = new Mappings();
        mappings.setMapping(GTaskDescriptor.FIELD.DESCRIPTION, true, "content", "default description");
        mappings.setMapping(GTaskDescriptor.FIELD.ASSIGNEE, true, "assignee", "default assignee");
        final GUser me = new GUser();
        me.setId(321);
        me.setLoginName("<noname>");
        me.setDisplayName("Tester Tester2");
        task.setAssignee(me);

        final BasecampConnector connector = new BasecampConnector(config, factory);
        final TaskSaveResult res = connector.saveData(
                Collections.singletonList(task),
                ProgressMonitorUtils.DUMMY_MONITOR, mappings);
        Assert.assertEquals(1, res.getCreatedTasksNumber());
        Assert.assertEquals(0, res.getUpdatedTasksNumber());
    }
}
