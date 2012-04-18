package com.taskadapter.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

public class GTaskTest {

    @Test
    public void testCopyConstructor() {
        GTask task = new GTask();
        Integer id = 101;
        String summary = "some summary here";
        task.setId(id);
        task.setSummary(summary);

        GTask child1 = new GTask();
        child1.setId(1011);
        child1.setSummary("child summary");

        task.getChildren().add(child1);

        GTask clonedTask = new GTask(task);
        // TODO add more fields to check here
        Assert.assertTrue(clonedTask.getId().equals(id));
        Assert.assertTrue(clonedTask.getSummary().equals(summary));

        // the copy constructor does NOT copy children -
        // this fact is used in some places in the code, so need to check in the test
        Assert.assertTrue(clonedTask.getChildren().isEmpty());
    }

    @Test
    public void emptyChildrenListReturnedWhenNoChildren() {
        GTask task = new GTask();
        assertTrue("an empty collection must be returned when the task has no children", task.getChildren().isEmpty());
    }

    @Test
    public void hasChildrenReturnsFalseWhenNoChildren() {
        GTask task = new GTask();
        assertFalse(task.hasChildren());
    }

    @Test
    public void hasChildrenReturnsTrueWithChildren() {
        GTask task = new GTask();
        GTask child1 = new GTask();
        child1.setId(1011);
        child1.setSummary("child summary");
        task.getChildren().add(child1);

        assertTrue(task.hasChildren());
    }
}
