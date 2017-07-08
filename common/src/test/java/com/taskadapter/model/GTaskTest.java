package com.taskadapter.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

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
        child1.setId(1011);
        task.getChildren().add(child1);

        assertTrue(task.hasChildren());
    }
}
