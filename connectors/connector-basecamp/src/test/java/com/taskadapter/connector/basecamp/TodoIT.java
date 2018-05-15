package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

// Tired of re-creating Basecamp demo accounts. we don't have any Basecamp users, so
// let's just ignore these tests.
public class TodoIT {

/*

    @Test
    public void completedTodosAreLoadedOnlyWhenAsked() throws ConnectorException {
        TodoUtil.create(config, factory, TodoUtil.buildTask("task1"));

        GTask task2 = TodoUtil.buildTask("task2");
        task2.setValue(BasecampField.doneRatio(), 100);
        TodoUtil.create(config, factory, task2);

        config.setLoadCompletedTodos(true);
        List<GTask> tasks = TodoUtil.load(config, factory);
        assertEquals(2, tasks.size());

        config.setLoadCompletedTodos(false);
        List<GTask> onlyActiveTasks = TodoUtil.load(config, factory);
        assertEquals(1, onlyActiveTasks.size());
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
        me.setDisplayName("Tester");
        task.setAssignee(me);

        final BasecampConnector connector = new BasecampConnector(config, factory);
        final TaskSaveResult res = connector.saveData(
                Collections.singletonList(task),
                ProgressMonitorUtils.DUMMY_MONITOR, mappings);
        Assert.assertEquals(1, res.createdTasksNumber());
        Assert.assertEquals(0, res.updatedTasksNumber());
    }*/
}
