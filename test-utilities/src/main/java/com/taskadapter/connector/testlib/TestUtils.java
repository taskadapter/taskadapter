package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.NewConnector;
import com.taskadapter.model.GTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TestUtils {
/*
    public static List<GTask> generateTasks(int quantity) {
        List<GTask> tasks = new ArrayList<>(quantity);
        for (int i = 0; i < quantity; i++) {
            tasks.add(generateTask());
        }
        return tasks;
    }
*/

    public static GTask findTaskInList(List<GTask> list, Integer createdTask1Id) {
        for (GTask task : list) {
            if (task.getId().equals(createdTask1Id)) {
                return task;
            }
        }
        return null;
    }

    public static GTask findTaskByKey(List<GTask> list, String key) {
        for (GTask t : list) {
            if (t.getKey().equals(key)) {
                return t;
            }
        }
        return null;
    }

/*    public static GTask findTaskBySummary(List<GTask> tasks, String summary) {
        for (GTask t : tasks) {
            if (t.getSummary().equals(summary)) {
                return t;
            }
        }
        return null;
    }

    public static GTask generateTask() {
        GTask t = new GTask();
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        t.setSummary("generic task " + timeInMillis);
        t.setDescription("some description " + timeInMillis);
        Random r = new Random();
        int hours = r.nextInt(50) + 1;
        t.setEstimatedHours((float) hours);
        return t;
    }*/

    public static Calendar getDateRoundedToDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    public static List<GTask> saveAndLoadAll(NewConnector connector, GTask task, List<FieldRow> rows) throws ConnectorException {
        connector.saveData(Arrays.asList(task), null, rows);
        return ConnectorUtils.loadDataOrderedById(connector, rows);
    }

    public static List<GTask> saveAndLoadList(NewConnector connector, List<GTask> tasks, List<FieldRow> rows) throws ConnectorException {
        connector.saveData(tasks, null, rows);
        return ConnectorUtils.loadDataOrderedById(connector, rows);
    }

    public static GTask saveAndLoad(NewConnector connector, GTask task, List<FieldRow> rows) throws ConnectorException {
        TaskSaveResult result = connector.saveData(Arrays.asList(task), null, rows);
        Collection<String> remoteKeys = result.getRemoteKeys();
        String remoteKey = remoteKeys.iterator().next();
        return connector.loadTaskByKey(remoteKey, rows);
    }

    /**
     * Load task that was previously created and its result is saved in {@link TaskSaveResult}
     */
    public static GTask loadCreatedTask(NewConnector connector, List<FieldRow> rows, TaskSaveResult result) throws ConnectorException {
        Collection<String> remoteKeys = result.getRemoteKeys();
        String remoteKey = remoteKeys.iterator().next();
        return connector.loadTaskByKey(remoteKey, rows);
    }

/*
    public static GTask saveAndLoadViaSummary(NewConnector connector, GTask task, List<FieldRow> rows) throws ConnectorException {
        List<GTask> loadedTasks = saveAndLoadAll(connector, task, rows);
        return findTaskBySummary(loadedTasks, task.getSummary());
    }
*/

    /**
     * @return the new task Key
     */
    public static String save(NewConnector connector, GTask task, List<FieldRow> rows) throws ConnectorException {
        TaskSaveResult result = connector.saveData(Arrays.asList(task), null, rows);
        Collection<String> remoteKeys = result.getRemoteKeys();
        return remoteKeys.iterator().next();
    }

    public static Calendar setTaskStartYearAgo(GTask task, String startDateFieldName) {
        Calendar yearAgo = getDateRoundedToDay();
        yearAgo.add(Calendar.YEAR, -1);
        task.setValue(startDateFieldName, yearAgo.getTime());
        return yearAgo;
    }

    public static Date getYearAgo() {
        Calendar yearAgo = getDateRoundedToDay();
        yearAgo.add(Calendar.YEAR, -1);
        return yearAgo.getTime();
    }

    public static Calendar setTaskDueDateNextYear(GTask task, String dueDateFieldName) {
        Calendar cal = getDateRoundedToDay();
        cal.add(Calendar.YEAR, 1);
        task.setValue(dueDateFieldName, cal.getTime());
        return cal;
    }

}
