package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTask;
import org.junit.Test;

import java.text.ParseException;

import static org.fest.assertions.Assertions.assertThat;

public class DefaultValueSetterTest {

    @Test
    public void cloneAndReplaceEmptySelectedFieldsWithDefaultValuesActuallyClonesTheTask() throws ParseException {
        Mappings mappings = new Mappings();
        DefaultValueSetter setter = new DefaultValueSetter(mappings);
        GTask originalTask = new GTask();
        originalTask.setType("original type");
        originalTask.setDescription("original description");
        // TODO REVIEW Does this method perform a shallow copy or a deep copy?
        GTask newTask = setter.cloneAndReplaceEmptySelectedFieldsWithDefaultValues(originalTask);

        originalTask.setType("new type");
        originalTask.setDescription("new description");

        assertThat(newTask.getType()).isEqualTo("original type");
        assertThat(newTask.getDescription()).isEqualTo("original description");
    }
}