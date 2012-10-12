package com.taskadapter.connector.msp;

import com.taskadapter.connector.Priorities;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MSPConfigTest {
    @Test
    public void unselectedSaveIssueRelationsIsCopiedByCopyConstructor() {
        MSPConfig config = new MSPConfig();
        config.setSaveIssueRelations(false);
        MSPConfig cloned = new MSPConfig(config);
        assertFalse(cloned.getSaveIssueRelations());
    }

    @Test
    public void selectedSaveIssueRelationsIsCopiedByCopyConstructor() {
        MSPConfig config = new MSPConfig();
        config.setSaveIssueRelations(true);
        MSPConfig cloned = new MSPConfig(config);
        assertTrue(cloned.getSaveIssueRelations());
    }

    @Test
    public void defaultTaskTypeCopiedByCopyConstructor() {
        MSPConfig config = new MSPConfig();
        String taskType = "mytype";
        config.setDefaultTaskType(taskType);
        MSPConfig cloned = new MSPConfig(config);
        assertEquals(taskType, cloned.getDefaultTaskType());
    }

    @Test
    public void prioritiesCopiedByCopyConstructor() {
        MSPConfig config = new MSPConfig();
        final Priorities sourcePriorities = config.getPriorities();
        sourcePriorities.clear();
        sourcePriorities.setPriority("Low", 100);
        sourcePriorities.setPriority("Normal", 500);
        sourcePriorities.setPriority("High", 700);
        sourcePriorities.setPriority("Urgent", 800);
        MSPConfig cloned = new MSPConfig(config);
        Priorities clonedPriorities = cloned.getPriorities();
        assertEquals(sourcePriorities.getAllNames().size(), clonedPriorities.getAllNames().size());
        assertTrue(clonedPriorities.getAllNames().contains("Normal"));
        assertEquals(Integer.valueOf(700), clonedPriorities.getPriorityByText("High"));
        
        sourcePriorities.setPriority("Urgent", 123);
        assertEquals(Integer.valueOf(800), clonedPriorities.getPriorityByText("Urgent"));
        assertEquals(Integer.valueOf(123), sourcePriorities.getPriorityByText("Urgent"));
    }
}
