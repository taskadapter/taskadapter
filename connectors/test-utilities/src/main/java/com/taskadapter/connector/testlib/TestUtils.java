package com.taskadapter.connector.testlib;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TestUtils {
    public static List<GTask> generateTasks(int quantity) {
        List<GTask> tasks = new ArrayList<GTask>(quantity);
        for (int i = 0; i < quantity; i++) {
            tasks.add(generateTask());
        }
        return tasks;
    }

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

    public static GTask findTaskBySummary(List<GTask> tasks, String summary) {
        for (GTask t : tasks) {
            if (t.getSummary().equals(summary)) {
                return t;
            }
        }
        return null;
    }

    public static GTask generateTask() {
        GTask t = new GTask();
        t.setSummary("generic task " + Calendar.getInstance().getTimeInMillis());
        t.setDescription("some descr" + Calendar.getInstance().getTimeInMillis() + "1");
        Random r = new Random();
        int hours = r.nextInt(50) + 1;
        t.setEstimatedHours((float) hours);
        return t;
    }

    public static Calendar getDateRoundedToDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    public static List<GTask> saveAndLoadAll(Connector<?> connector, GTask task, Mappings mappings) throws ConnectorException {
        connector.saveData(Arrays.asList(task), null, mappings);
        return ConnectorUtils.loadDataOrderedById(connector, mappings);
    }

    public static List<GTask> saveAndLoadList(Connector<?> connector, List<GTask> tasks, Mappings mappings) throws ConnectorException {
        connector.saveData(tasks, null, mappings);
        return ConnectorUtils.loadDataOrderedById(connector, mappings);
    }

    public static GTask saveAndLoad(Connector<?> connector, GTask task, Mappings mappings) throws ConnectorException {
        SyncResult<TaskSaveResult,TaskErrors<Throwable>> syncResult = connector.saveData(Arrays.asList(task), null, mappings);
        TaskSaveResult result = syncResult.getResult();
        Collection<String> remoteKeys = result.getRemoteKeys();
        String remoteKey = remoteKeys.iterator().next();
        return connector.loadTaskByKey(remoteKey, mappings);
    }

    public static GTask saveAndLoadViaSummary(Connector<?> connector, GTask task, Mappings mappings) throws ConnectorException {
        List<GTask> loadedTasks = saveAndLoadAll(connector, task, mappings);
        return findTaskBySummary(loadedTasks, task.getSummary());
    }

    /**
     * @return the new task Key
     */
    public static String save(Connector<?> connector, GTask task, Mappings mappings) throws ConnectorException {
        SyncResult<TaskSaveResult,TaskErrors<Throwable>> syncResult = connector.saveData(Arrays.asList(task), null, mappings);
        TaskSaveResult result = syncResult.getResult();
        Collection<String> remoteKeys = result.getRemoteKeys();
        return remoteKeys.iterator().next();
    }

    public static Calendar setTaskStartYearAgo(GTask task) {
        Calendar yearAgo = getDateRoundedToDay();
        yearAgo.add(Calendar.YEAR, -1);
        task.setStartDate(yearAgo.getTime());
        return yearAgo;
    }

    public static Date getYearAgo() {
        Calendar yearAgo = getDateRoundedToDay();
        yearAgo.add(Calendar.YEAR, -1);
        return yearAgo.getTime();
    }

    public static Calendar setTaskDueDateNextYear(GTask task) {
        Calendar cal = getDateRoundedToDay();
        cal.add(Calendar.YEAR, 1);
        task.setDueDate(cal.getTime());
        return cal;
    }

}
