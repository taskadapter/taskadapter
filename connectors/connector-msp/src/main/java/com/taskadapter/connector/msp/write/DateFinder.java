package com.taskadapter.connector.msp.write;

import com.taskadapter.model.GTask;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateFinder {
    static Date findEarliestStartDate(List<GTask> tasks) {
        Calendar maxCal = Calendar.getInstance();
        maxCal.add(Calendar.YEAR, 9999);
        Date earliestDate = maxCal.getTime();
        boolean atLeast1StartDateSet = false;
        for (GTask gTask : tasks) {
            if (gTask.getStartDate() != null && earliestDate.after(gTask.getStartDate())) {
                earliestDate = gTask.getStartDate();
                atLeast1StartDateSet = true;
            }
        }
        if (atLeast1StartDateSet) {
            return earliestDate;
        }
        return null;
    }
}
