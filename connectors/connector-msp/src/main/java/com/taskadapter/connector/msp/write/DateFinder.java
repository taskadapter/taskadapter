package com.taskadapter.connector.msp.write;

import net.sf.mpxj.Task;

import java.util.Date;
import java.util.List;

final class DateFinder {
    /**
     * @return NULL if no tasks have start dates set
     */
    static Date findEarliestStartDate(List<Task> tasks) {
        Date earliestDate = null;
        for (Task t : tasks) {
            Date taskStartDate = t.getStart();
            if (taskStartDate == null)
                continue;
            if (earliestDate == null || earliestDate.after(taskStartDate)) {
                earliestDate = taskStartDate;
            }
        }
        return earliestDate;
    }
}
