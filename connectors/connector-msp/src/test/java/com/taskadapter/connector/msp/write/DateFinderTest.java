package com.taskadapter.connector.msp.write;

import com.taskadapter.connector.msp.MSPTestUtils;
import net.sf.mpxj.Task;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DateFinderTest {
    private List<Task> list = MSPTestUtils.loadToMSPTaskList("b4ubuild_sample_07.mpp");

    @Test
    public void earliestDateFound() throws Exception {
        Date date = DateFinder.findEarliestStartDate(list);
        LocalDateTime expectedDate = LocalDateTime.of(2008, Month.JUNE, 1, 8,0);
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        assertEquals(expectedDate, ldt);
    }
}
