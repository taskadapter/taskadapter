package com.taskadapter.connector.msp;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.definition.ValidationException;
import net.sf.mpxj.TaskField;
import org.junit.Test;

import java.util.HashMap;

import static com.taskadapter.model.GTaskDescriptor.FIELD;
import static org.junit.Assert.*;

public class MSPConfigTest {

    @Test(expected = ValidationException.class)
    public void noOutputFileNameFailsSaveValidation() throws ValidationException {
        MSPConfig config = new MSPConfig();
        config.validateForSave();
    }

    @Test(expected = ValidationException.class)
    public void noInputFileNameFailsValidation() throws ValidationException {
        MSPConfig config = new MSPConfig();
        config.validateForLoad();
    }

    @Test
    public void unselectedMappingCopiedPropertyByCopyConstructor() {
        MSPConfig config = new MSPConfig();
        config.setFieldMapping(FIELD.ESTIMATED_TIME, new Mapping(false, TaskField.DURATION.toString()));
        MSPConfig cloned = new MSPConfig(config);
        assertFalse(cloned.isFieldSelected(FIELD.ESTIMATED_TIME));
    }

    @Test
    public void mappingCopiedProperlyByCopyConstructor() {
        MSPConfig cloned = createAndCloneConfig(FIELD.ESTIMATED_TIME, true, TaskField.DURATION.toString());
        assertTrue(cloned.isFieldSelected(FIELD.ESTIMATED_TIME));
        assertEquals(TaskField.DURATION.toString(), cloned.getFieldMappedValue(FIELD.ESTIMATED_TIME));
    }

    @Test
    public void summaryFieldSelectedByDefault() {
        assertTrue(new MSPConfig().isFieldSelected(FIELD.SUMMARY));
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

    private static MSPConfig createAndCloneConfig(FIELD field, boolean selected, String value) {
        MSPConfig config = new MSPConfig();
        config.setFieldMapping(field, new Mapping(selected, value));
        return new MSPConfig(config);
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
