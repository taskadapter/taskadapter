package com.taskadapter.connector.msp.write;

import com.taskadapter.model.GTask;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DateFinderTest {
    private DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    // TODO TA3 Msp date tests

    /*
    @Test
    public void earliestDateFoundWhenAllStartDatesSet() throws Exception {
        Date date1 = formatter.parse("01/29/2010");
        Date date2 = formatter.parse("01/29/2011");
        Date date3 = formatter.parse("04/14/2012");
        assertEquals(date1, DateFinder.findEarliestStartDate(Arrays.asList(getTask(date1), getTask(date2), getTask(date3))));
    }

    @Test
    public void earliestDateFoundWhenSomeStartDatesAreNotSet() throws Exception {
        Date date1 = formatter.parse("01/29/2010");
        Date date3 = formatter.parse("04/14/2012");
        assertEquals(date1, DateFinder.findEarliestStartDate(Arrays.asList(getTask(date1), new GTask(), getTask(date3))));
    }

    @Test
    public void nullReturnedWhenNoStartDatesAreSet() throws Exception {
        assertNull(DateFinder.findEarliestStartDate(Arrays.asList(new GTask(), new GTask(), new GTask())));
    }

    private GTask getTask(Date time) {
        GTask task = new GTask();
        task.setStartDate(time);
        return task;
    }*/
}
