package com.taskadapter.connector.msp;

import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTest {
    private static final List<GTask> gtasks = MSPTestUtils.load("start_date_by_constraint.xml");

    @Test
    public void startDateMustStartOn() {
        var gtask = TestUtils.findTaskByFieldName(gtasks, AllFields.summary, "must start on");
        assertThat(gtask.getValue(MspField.mustStartOn))
                .isEqualTo(createMSPDate(15, 9, 2011, 8));
    }

    @Test
    public void startDateNoLaterThan() {
        var gtask = TestUtils.findTaskByFieldName(gtasks, AllFields.summary, "start no later than");
        assertThat(gtask.getValue(MspField.startNoLaterThan))
                .isEqualTo(createMSPDate(10, 9, 2011, 8));
    }

    @Test
    public void mustFinishOn() {
        var gtask = TestUtils.findTaskByFieldName(gtasks, AllFields.summary, "must finish on");
        assertThat(gtask.getValue(MspField.mustFinishOn)).isEqualTo(createMSPDate(3, 12, 2011, 17));
    }

    private static Date createMSPDate(int day, int month, int year, int hour) {
        var calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, day, hour, 0);
        return calendar.getTime();
    }
}
