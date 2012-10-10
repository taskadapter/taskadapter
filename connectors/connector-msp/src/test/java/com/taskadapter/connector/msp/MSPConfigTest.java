package com.taskadapter.connector.msp;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ValidationException;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MSPConfigTest {
    @Test(expected = ValidationException.class)
    public void noInputFileNameFailsValidation() throws ValidationException {
        MSPConfig config = new MSPConfig();
        config.validateForLoad();
    }

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
        Priorities priorities = getSamplePriorities();
        config.setPriorities(priorities);
        MSPConfig cloned = new MSPConfig(config);
        Priorities clonedPriorities = cloned.getPriorities();
        assertEquals(priorities.getAllNames().size(), clonedPriorities.getAllNames().size());
        assertTrue(clonedPriorities.getAllNames().contains("Normal"));
        assertEquals(Integer.valueOf(700), clonedPriorities.getPriorityByText("High"));
    }

    private Priorities getSamplePriorities() {
        return new Priorities(new HashMap<String, Integer>() {
            {
                put("Low", 100);
                put("Normal", 500);
                put("High", 700);
                put("Urgent", 800);
            }
        });
    }
}
