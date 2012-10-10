package com.taskadapter.connector.msp;

import com.taskadapter.model.GTask;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.taskadapter.connector.common.TestUtils.findTaskBySummary;
import static org.junit.Assert.assertEquals;

public class DateTest {
	// TODO !!! fix tests
/*    private List<GTask> gtasks;

    @Before
    public void init() throws Exception {
        gtasks = load("start_date_by_constraint.xml");
    }

    @Test
    public void startDateMustStartOn() throws Exception {
        GTask gtask = findTaskBySummary(gtasks, "must start on");
        assertEquals(createMSPDate(15, 9, 2011, 8), gtask.getStartDate());
    }

    @Test
    public void startDateNoLaterThan() throws Exception {
        GTask gtask = findTaskBySummary(gtasks, "start no later than");
        assertEquals(createMSPDate(10, 9, 2011, 8), gtask.getStartDate());
    }

    @Test
    public void startDateMustFinishOn() throws Exception {
        GTask gtask = findTaskBySummary(gtasks, "must finish on");
        assertEquals(createMSPDate(3, 12, 2011, 17), gtask.getStartDate());
    }

    private Date createMSPDate(int day, int month, int year, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, day, hour, 0);
        return calendar.getTime();
    }*/
}
