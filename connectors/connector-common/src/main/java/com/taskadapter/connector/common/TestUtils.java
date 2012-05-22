package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.*;

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

    public static GTask saveAndLoad(Connector connector, FIELD field, Mapping mapping, GTask task) {
        ConnectorConfig config = connector.getConfig();
        Mapping savedMapping = config.getFieldMapping(field);
        config.setFieldMapping(field, mapping); // ugly, but ...
        GTask loadedTask = saveAndLoad(connector, task);
        config.setFieldMapping(field, savedMapping);

        return loadedTask;
    }

    public static GTask saveAndLoad(Connector connector, GTask task) {
        List<GTask> loadedTasks = saveAndLoadAll(connector, task);
        return findTaskBySummary(loadedTasks, task.getSummary());
    }

    public static List<GTask> saveAndLoadAll(Connector connector, GTask task) {
        connector.saveData(Arrays.asList(task), null);
        return connector.loadData(null);
    }

    public static List<GTask> saveAndLoadList(Connector connector, List<GTask> tasks) {
        connector.saveData(tasks, null);
        return connector.loadData(null);
    }

    public static Calendar setTaskStartYearAgo(GTask task) {
        Calendar yearAgo = getDateRoundedToDay();
        yearAgo.add(Calendar.YEAR, -1);
        task.setStartDate(yearAgo.getTime());
        return yearAgo;
    }

    public static Calendar setTaskDueDateNextYear(GTask task) {
        Calendar cal = getDateRoundedToDay();
        cal.add(Calendar.YEAR, 1);
        task.setDueDate(cal.getTime());
        return cal;
    }

}
