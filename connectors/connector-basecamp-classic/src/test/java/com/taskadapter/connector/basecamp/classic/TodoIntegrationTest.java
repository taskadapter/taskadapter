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
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class TodoIntegrationTest {

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
        task.setId(123);
        task.setDescription("Some description here - 1020");
        final Mappings mappings = new Mappings();
        mappings.setMapping(GTaskDescriptor.FIELD.DESCRIPTION, true, "content");

        final BasecampConnector conn = new BasecampConnector(config, factory);
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

        factory.createObjectAPI(config).delete("todo_items/" + update.getRemoteId() + ".xml");
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
        me.setLoginName("support@taskadapter.com");
        me.setDisplayName("Alexey Skorokhodov");
        template.setAssignee(me);

        final Mappings allMappings = TodoUtil.getAllMappings();

        final BasecampConnector connector = new BasecampConnector(config, factory);
        final TaskSaveResult result = connector.saveData(
                Collections.singletonList(template),
                ProgressMonitorUtils.getDummyMonitor(), allMappings);
        Assert.assertEquals(1, result.getCreatedTasksNumber());

        final String remoteId = result.getRemoteKey(123);
        template.setRemoteId(remoteId);

        final GTask update = new GTask();
        update.setId(123);
        update.setSummary("It should be updated");
        update.setDoneRatio(0);
        update.setDueDate(date2);
        update.setAssignee(null);
        update.setRemoteId(remoteId);

        GTask updated = update(connector, update, GTaskDescriptor.FIELD.SUMMARY, "content");
        Assert.assertEquals(update.getSummary(), updated.getSummary());
        Assert.assertEquals(template.getDoneRatio(), updated.getDoneRatio());
        Assert.assertEquals(template.getDueDate(), updated.getDueDate());
        Assert.assertEquals(template.getAssignee().getDisplayName(),
                updated.getAssignee().getDisplayName());

        connector.saveData(Collections.singletonList(template),
                ProgressMonitorUtils.getDummyMonitor(), allMappings);

        updated = update(connector, update, GTaskDescriptor.FIELD.DONE_RATIO, "done_ratio");
        Assert.assertEquals(template.getSummary(), updated.getSummary());
        Assert.assertEquals(update.getDoneRatio(), updated.getDoneRatio());
        Assert.assertEquals(template.getDueDate(), updated.getDueDate());
        Assert.assertEquals(template.getAssignee().getDisplayName(),
                updated.getAssignee().getDisplayName());

        connector.saveData(Collections.singletonList(template),
                ProgressMonitorUtils.getDummyMonitor(), allMappings);

        updated = update(connector, update, GTaskDescriptor.FIELD.DUE_DATE, "due_date");
        Assert.assertEquals(template.getSummary(), updated.getSummary());
        Assert.assertEquals(template.getDoneRatio(), updated.getDoneRatio());
        Assert.assertEquals(update.getDueDate(), updated.getDueDate());
        Assert.assertEquals(template.getAssignee().getDisplayName(),
                updated.getAssignee().getDisplayName());

        connector.saveData(Collections.singletonList(template),
                ProgressMonitorUtils.getDummyMonitor(), allMappings);

        updated = update(connector, update, GTaskDescriptor.FIELD.ASSIGNEE, "assignee");
        Assert.assertEquals(template.getSummary(), updated.getSummary());
        Assert.assertEquals(template.getDoneRatio(), updated.getDoneRatio());
        Assert.assertEquals(template.getDueDate(), updated.getDueDate());
        Assert.assertNull(updated.getAssignee());
    }

    private GTask update(BasecampConnector conn, GTask newTask, GTaskDescriptor.FIELD field,
                         String target) throws ConnectorException {
        final Mappings newMappings = new Mappings();
        newMappings.setMapping(field, true, target);
        final TaskSaveResult res = conn.saveData(
                Collections.singletonList(newTask),
                ProgressMonitorUtils.getDummyMonitor(), newMappings);
        Assert.assertEquals(1, res.getUpdatedTasksNumber());
        return conn.loadTaskByKey(newTask.getRemoteId(), newMappings);
    }

    @Test
    public void testResolveUser() throws ConnectorException {
        final GTask task = new GTask();
        task.setId(123);
        task.setDescription("Find anybody");
        final Mappings mappings = new Mappings();
        mappings.setMapping(GTaskDescriptor.FIELD.DESCRIPTION, true, "content");
        mappings.setMapping(GTaskDescriptor.FIELD.ASSIGNEE, true, "assignee");
        final GUser me = new GUser();
        me.setId(321);
        me.setLoginName("<noname>");
        me.setDisplayName("Tester Tester2");
        task.setAssignee(me);

        final BasecampConnector connector = new BasecampConnector(config, factory);
        final TaskSaveResult res = connector.saveData(
                Collections.singletonList(task),
                ProgressMonitorUtils.getDummyMonitor(), mappings);
        Assert.assertEquals(1, res.getCreatedTasksNumber());
        Assert.assertEquals(0, res.getUpdatedTasksNumber());
    }
}
