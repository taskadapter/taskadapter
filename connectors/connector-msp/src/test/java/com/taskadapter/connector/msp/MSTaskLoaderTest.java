package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.model.GTask;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class MSTaskLoaderTest {
    @Test
    public void testLoadTaskType() throws Exception {
        List<GTask> loadedTasks = MSPTestUtils.loadWithDefaultMappings("Projeto1.xml");
        assertEquals(3, loadedTasks.size());
        GTask firstGTask = loadedTasks.get(0);
        assertNotNull(firstGTask);
        assertEquals("MyTracker", firstGTask.getType());
    }

    @Test
    public void testLoadTree() throws Exception {
        List<GTask> loadedTasks = MSPTestUtils.loadWithDefaultMappings("Projeto1.xml");
        assertEquals(3, loadedTasks.size());
        List<GTask> tree = TreeUtils.buildTreeFromFlatList(loadedTasks);
        assertEquals(1, tree.size());
        assertEquals(2, tree.get(0).getChildren().size());
    }

    @Test
    public void fileCreatedByMSP2013IsLoaded() throws Exception {
        List<GTask> tasks = MSPTestUtils.loadWithDefaultMappings("msp_2013.xml");
        assertEquals(2, tasks.size());
        GTask task1 = tasks.get(0);
        assertEquals("task 1", task1.getSummary());
        assertEquals("alex", task1.getAssignee().getDisplayName());
        assertEquals(12f, task1.getEstimatedHours().floatValue(), 0);

        Date expectedStartDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse("12/11/2013 08:00");
        assertEquals(expectedStartDate, task1.getStartDate());

        Date expectedFinishDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse("12/12/2013 12:00");
        assertEquals(expectedFinishDate, task1.getDueDate());
    }

    @Test
    public void targetVersionFieldIsLoaded() throws Exception {
        List<GTask> tasks = MSPTestUtils.loadWithDefaultMappings("msp_with_target_version.xml");
        assertEquals(2, tasks.size());
        GTask task1 = tasks.get(0);
        assertEquals("for version 2", task1.getSummary());
        assertEquals("version 2.0", task1.getTargetVersionName());

        GTask task2 = tasks.get(1);
        assertEquals("without version", task2.getSummary());
        assertThat(task2.getTargetVersionName()).isNull();
    }

}
