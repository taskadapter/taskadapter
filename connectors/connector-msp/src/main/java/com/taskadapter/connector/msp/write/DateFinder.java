package com.taskadapter.connector.msp.write;

import com.taskadapter.model.GTask;

import java.util.Date;
import java.util.List;

public class DateFinder {
    /**
     * @param tasks
     * @return NULL if no tasks have start dates set
     */
    static Date findEarliestStartDate(List<GTask> tasks) {
        Date earliestDate = null;
        for (GTask gTask : tasks) {
            final Date taskStartDate = gTask.getStartDate();
            if (taskStartDate == null)
                continue;
            if (earliestDate == null || earliestDate.after(taskStartDate)) {
                earliestDate = taskStartDate;
            }
        }
        return earliestDate;
    }
}
