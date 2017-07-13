package com.taskadapter.connector.msp.write;

import com.taskadapter.model.GTask;

import java.util.Date;
import java.util.List;

final class DateFinder {
    /**
     * @return NULL if no tasks have start dates set
     */
    static Date findEarliestStartDate(List<GTask> tasks) {
        Date earliestDate = null;
        for (GTask gTask : tasks) {
            // TODO TA3 MSP restore project start date
            final Date taskStartDate = null; // gTask.getStartDate();
            if (taskStartDate == null)
                continue;
            if (earliestDate == null || earliestDate.after(taskStartDate)) {
                earliestDate = taskStartDate;
            }
        }
        return earliestDate;
    }
}
