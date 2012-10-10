package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.model.GTask;
import org.junit.Assert;
import org.junit.Test;

public class MSTaskLoaderIntegrationTest {
	// TODO !!! fix tests
/*
    @Test
    public void testLoadFileCreatedByMSPWith1Task() throws Exception {
        List<GTask> tasks = load("created_by_msp_1task.xml");
        Assert.assertEquals(1, tasks.size());
    }

    @Test
    public void testFind1Task() throws Exception {
        List<GTask> tasks = load("created_by_msp_1task.xml");
        GTask myTaskAddedFromMSP = TestUtils.findTaskBySummary(tasks, "task1");
        if (myTaskAddedFromMSP == null) {
            Assert.fail("required task not found in the tasks list");
        }
    }

    @Test
    public void testLoadFileCreatedByMSPWithManyTasks() throws Exception {
        List<GTask> tasks = load("created_by_msp_tasks.xml");
        Assert.assertEquals(4, tasks.size());

        GTask t1 = TestUtils.findTaskBySummary(tasks, "task1");
        Assert.assertNotNull("required task not found in the tasks list", t1);

        GTask t1Sub1 = TestUtils.findTaskBySummary(tasks, "task1-sub1");
        Assert.assertNotNull("required task not found in the tasks list", t1Sub1);

        GTask t2 = TestUtils.findTaskBySummary(tasks, "task2");
        Assert.assertNotNull("required task not found in the tasks list", t2);
    }

    @Test
    public void testLoadFileCreatedByTA() throws Exception {
        List<GTask> tasks = load("created_by_ta_27.xml");
        Assert.assertEquals(27, tasks.size());

        GTask t1 = TestUtils.findTaskBySummary(tasks, "improve components");
        Assert.assertNotNull("required task not found in the tasks list", t1);

        GTask sub1 = TestUtils.findTaskBySummary(tasks, "sub1");
        Assert.assertNotNull("required task not found in the tasks list", sub1);
    }


    @Test
    public void testLoadFileCreatedByTA1Task() throws Exception {
        List<GTask> tasks = load("created_by_ta_1.xml");
        Assert.assertEquals(1, tasks.size());

        GTask t1 = TestUtils.findTaskBySummary(tasks, "support me!");
        Assert.assertNotNull("required task not found in the tasks list", t1);
    }

    @Test
    public void testEmptyLinesAreSkipped() throws Exception {
        // total number of lines is 170 with 163 non-empty ones
        List<GTask> tasks = load("IT_Department_Project_Master.xml");
        Assert.assertEquals(163, tasks.size());
    }
*/
}
