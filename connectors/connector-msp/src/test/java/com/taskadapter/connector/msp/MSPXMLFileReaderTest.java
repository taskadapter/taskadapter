package com.taskadapter.connector.msp;

import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import com.taskadapter.connector.msp.MSPTestUtils;

public class MSPXMLFileReaderTest {

    @Test
    public void readFile() throws Exception {
        Assert.assertNotNull(MSPTestUtils.readTestProjectFile());
    }

    @Test
    public void actualDurationValidation() throws Exception {
        List<Task> list = MSPTestUtils.loadToMSPTaskList("actual_duration.mpp");
        Assert.assertEquals(0, list.get(0).getActualDuration().compareTo(Duration.getInstance(0, TimeUnit.HOURS)));
    }
}
