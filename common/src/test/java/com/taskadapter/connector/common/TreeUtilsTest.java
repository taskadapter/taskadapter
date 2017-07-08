package com.taskadapter.connector.common;

import com.taskadapter.model.GTask;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TreeUtilsTest {

    @Test
    public void testCloneTree() {
        List<GTask> tree = new ArrayList<>();

        GTask genericTask = new GTask();
        genericTask.setValue("summary", "genericTask");

        tree.add(genericTask);

        GTask sub1 = new GTask();
        sub1.setValue("summary", "sub1");

        GTask sub2 = new GTask();
        sub2.setValue("summary", "sub2");

        genericTask.getChildren().add(sub1);
        genericTask.getChildren().add(sub2);

        List<GTask> cloned = TreeUtils.cloneTree(tree);

        final String NEW_TEXT = "newtext";
        sub1.setValue("summary", NEW_TEXT);

        GTask clonedGenericTask = cloned.get(0);
        GTask clonedSub1 = clonedGenericTask.getChildren().get(0);

        Assert.assertEquals(NEW_TEXT, sub1.getValue("summary"));
        Assert.assertEquals("sub1", clonedSub1.getValue("summary"));
    }

    @Test
    public void shallowCloneSkipsChildren() {
        GTask task = new GTask();
        Integer id = 101;
        String summary = "some summary here";
        task.setId(id);
        task.setValue("summary", summary);

        GTask child1 = new GTask();
        child1.setId(1011);
        child1.setValue("summary", "child summary");

        task.getChildren().add(child1);

        GTask clonedTask = TreeUtils.createShallowCopyWithoutChildren(task);
        // TODO add more fields to check here
        Assert.assertTrue(clonedTask.getId().equals(id));
        Assert.assertTrue(clonedTask.getValue("summary").equals(summary));

        // the copy constructor does NOT copy children -
        // this fact is used in some places in the code, so need to check in the test
        Assert.assertTrue(clonedTask.getChildren().isEmpty());
    }
}
