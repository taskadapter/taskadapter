package com.taskadapter.connector.common;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeUtilsTest {
    @Test
    public void testCloneTree() {
        var tree = new ArrayList<GTask>();
        var genericTask = new GTask();
        genericTask.setValue(AllFields.summary, "genericTask");
        tree.add(genericTask);
        var sub1 = new GTask()
                .setValue(AllFields.summary, "sub1");
        var sub2 = new GTask();
        sub2.setValue(AllFields.summary, "sub2");
        genericTask.getChildren().add(sub1);
        genericTask.getChildren().add(sub2);
        var cloned = TreeUtils.cloneTree(tree);
        var NEW_TEXT = "newtext";
        sub1.setValue(AllFields.summary, NEW_TEXT);
        var clonedGenericTask = cloned.get(0);
        var clonedSub1 = clonedGenericTask.getChildren().get(0);
        assertThat(sub1.getValue(AllFields.summary))
                .isEqualTo(NEW_TEXT);
        assertThat(clonedSub1.getValue(AllFields.summary))
                .isEqualTo("sub1");
    }

    @Test
    public void shallowCloneSkipsChildren() {
        var task = new GTask();
        var id = 101L;
        var summary = "some summary here";
        task.setId(id);
        task.setValue(AllFields.summary, summary);
        var child1 = new GTask()
                .setId(1011L);
        child1.setValue(AllFields.summary, "child summary");
        task.getChildren().add(child1);
        var clonedTask = TreeUtils.createShallowCopyWithoutChildren(task);
        // TODO add more fields to check here;
        assertThat(clonedTask.getId()).isEqualTo(id);
        assertThat(clonedTask.getValue(AllFields.summary)).isEqualTo(summary);
        // the copy constructor does NOT copy children -;
        // this fact is used in some places in the code, so need to check in the test;
        assertThat(clonedTask.getChildren()).isEmpty();
    }

    @Test
    public void convertsFlatListToParentWithChildren() {
        var task = new GTask()
                .setValue(AllFields.summary, "genericTask");
        var sub1 = new GTask()
                .setValue(AllFields.summary, "sub1");
        var sub2 = new GTask()
                .setValue(AllFields.summary, "sub2");
        task.addChildTask(sub1);
        task.addChildTask(sub2);
        var tree = TreeUtils.buildTreeFromFlatList(List.of(task, sub1, sub2));

        assertThat(tree.get(0).getChildren()).hasSize(2);
    }
}
