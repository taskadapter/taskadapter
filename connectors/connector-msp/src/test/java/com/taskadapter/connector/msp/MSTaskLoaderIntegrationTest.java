package com.taskadapter.connector.msp;

import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomString;
import net.sf.mpxj.TaskField;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MSTaskLoaderIntegrationTest {
    @Test
    public void testLoadFileCreatedByMSPWith1Task() {
        var tasks = MSPTestUtils.load("created_by_msp_1task.xml");
        assertThat(tasks).hasSize(1);
    }

    @Test
    public void testFind1Task() {
        var tasks = MSPTestUtils.load("created_by_msp_1task.xml");
        var myTaskAddedFromMSP = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "task1");
        assertThat(myTaskAddedFromMSP)
                .withFailMessage("required task not found in the tasks list")
                .isNotNull();
    }

    @Test
    public void testLoadFileCreatedByMSPWithManyTasks() {
        var tasks = MSPTestUtils.load("created_by_msp_tasks.xml");
        assertThat(tasks).hasSize(4);

        TestUtils.findTaskByFieldNameOrFail(tasks, AllFields.summary, "task1");
        TestUtils.findTaskByFieldNameOrFail(tasks, AllFields.summary, "task1-sub1");
        TestUtils.findTaskByFieldNameOrFail(tasks, AllFields.summary, "task2");
    }

    @Test
    public void loadFileCreatedByTaskAdapter() {
        var tasks = MSPTestUtils.load("created_by_ta_27.xml");
        assertThat(tasks).hasSize(27);
        var t1 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "improve components");
        assertThat(t1).withFailMessage("required task not found in the tasks list")
                .isNotNull();

        // verify TextXX fields are loaded. this is a regression test for
        // https://bitbucket.org/taskadapter/taskadapter/issues/74/issue-type-is-reset-to-default-setting
        assertThat(t1.getValue(new CustomString(TaskField.TEXT1.getName())))
                .isEqualTo("49");
        assertThat(t1.getValue(new CustomString(TaskField.TEXT2.getName())))
                .isEqualTo("Feature");

        var sub1 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "sub1");
        assertThat(sub1)
                .withFailMessage("required task not found in the tasks list")
                .isNotNull();
    }

    @Test
    public void loadFileCreatedByTA1Task() {
        var tasks = MSPTestUtils.load("created_by_ta_1.xml");
        assertThat(tasks).hasSize(1);
        var t1 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "support me!");
        assertThat(t1)
                .withFailMessage("required task not found in the tasks list")
                .isNotNull();
    }

    @Test
    public void emptyLinesInMspXmlFileAreSkipped() {
        // total number of lines is 170 with 163 non-empty ones
        var tasks = MSPTestUtils.load("IT_Department_Project_Master.xml");
        assertThat(tasks).hasSize(163);
    }
}
