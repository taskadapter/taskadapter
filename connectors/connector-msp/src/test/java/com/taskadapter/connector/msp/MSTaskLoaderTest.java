package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.model.GTask;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

public class MSTaskLoaderTest {
	// TODO !!! fix tests
	/*
    @Test
    public void testLoadTaskType() throws Exception {
        List<GTask> loadedTasks = MSPTestUtils.load("Projeto1.xml");
        Assert.assertEquals(3, loadedTasks.size());
        GTask firstGTask = loadedTasks.get(0);
        Assert.assertNotNull(firstGTask);
        Assert.assertEquals("MyTracker", firstGTask.getType());
    }

    @Test
    public void testLoadTree() throws Exception {
        List<GTask> loadedTasks = MSPTestUtils.load("Projeto1.xml");
        Assert.assertEquals(3, loadedTasks.size());
        List<GTask> tree = TreeUtils.buildTreeFromFlatList(loadedTasks);
        Assert.assertEquals(1, tree.size());
        Assert.assertEquals(2, tree.get(0).getChildren().size());
    }*/
}
