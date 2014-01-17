package com.taskadapter.connector.msp;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Task;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MSPFileReaderTest {
    /**
     * MAke sure a real MPP file can be loaded.
     * This is more of a test for MPXJ library itself.
     * It may be useful to check if newer MPXJ versions can still load what we need.
     */
    @Test
    public void smokeTestForMPFile() throws FileNotFoundException, MPXJException {
        List<Task> list = MSPTestUtils.loadToMSPTaskList("b4ubuild_sample_07.mpp");
        assertEquals(63, list.size());
        Task rootTask = list.get(0);
        assertEquals(0, rootTask.getUniqueID().intValue());

        Task task1 = list.get(1);
        assertEquals("Contracts", task1.getName());
        assertTrue(task1.getSummary());
        List<Task> task1ChildTasks = task1.getChildTasks();
        assertEquals(7, task1ChildTasks.size());


    }
}
