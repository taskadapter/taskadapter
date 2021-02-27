package com.taskadapter.model;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GTaskTest {

    @Test
    public void newTaskHasNoChildren() {
        GTask task = new GTask();
        assertTrue("a new task must have no children", task.getChildren().isEmpty());
        assertFalse("a new task must have no children", task.hasChildren());
    }

    @Test
    public void nullChildrenReturnsFalse() {
        GTask task = new GTask();
        task.setChildren(null);
        assertFalse(task.hasChildren());
    }

    @Test
    public void emptyChildrenListReturnsFalse() {
        GTask task = new GTask();
        task.setChildren(Collections.<GTask>emptyList());
        assertFalse(task.hasChildren());
    }

    @Test
    public void hasChildrenReturnsTrueWithChildren() {
        GTask task = new GTask();
        GTask child1 = new GTask();
        child1.setId(1011L);
        task.getChildren().add(child1);

        assertTrue(task.hasChildren());
    }

    @Test
    public void constructorFieldsAreDeepCloned() {
        var field = Field.apply("str");
        var task = new GTask();
        task.setValue(field, "123");
        var cloned = GTask.shallowClone(task);
        task.setValue(field, "updated");
        assertThat(cloned.getValue(field)).isEqualTo("123");
    }

    @Test
    public void constructorSetsEmptyCollections() {
        var task = new GTask();
        assertThat(task.getChildren()).isEmpty();
        assertThat(task.getRelations()).isEmpty();
    }

    @Test
    public void identityNullIdConvertedToZeroInIdentity() {
        assertThat(new GTask().getIdentity().getId()).isEqualTo(0);
    }

    @Test
    public void identityNullIdStaysNull() {
        var task = new GTask();
        task.setId(null);
        assertThat(task.getId()).isNull();
    }
}
