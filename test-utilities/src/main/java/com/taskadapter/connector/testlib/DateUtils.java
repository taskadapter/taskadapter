package com.taskadapter.connector.testlib;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date getDateRoundedToMinutes() {
        var cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getDateRoundedToDay() {
        return getCalendarRoundedToDay().getTime();
    }

    public static Calendar getCalendarRoundedToDay() {
        var cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    public static Calendar setTaskStartYearAgo(GTask task) {
        var yearAgo = DateUtils.getCalendarRoundedToDay();
        yearAgo.add(Calendar.YEAR, -1);
        task.setValue(AllFields.startDate, yearAgo.getTime());
        return yearAgo;
    }

    public static Date yearAgo() {
        var yearAgo = DateUtils.getCalendarRoundedToDay();
        yearAgo.add(Calendar.YEAR, -1);
        return yearAgo.getTime();
    }

    public static Date nextYear() {
        var cal = DateUtils.getCalendarRoundedToDay();
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }
}
