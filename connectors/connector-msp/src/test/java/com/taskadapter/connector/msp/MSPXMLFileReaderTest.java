package com.taskadapter.connector.msp;

import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class MSPXMLFileReaderTest {

    @Test
    public void readFile() {
        Assert.assertNotNull(MSPTestUtils.readTestProjectFile());
    }

    @Test
    public void actualDurationValidation() {
        List<Task> list = MSPTestUtils.loadToMSPTaskList("actual_duration.mpp");
        Task task = list.get(0);
        Assert.assertEquals(0, task.getActualDuration().compareTo(Duration.getInstance(0, TimeUnit.HOURS)));
    }
}
