package com.taskadapter.webui.action;

import com.taskadapter.model.GTask;
import com.vaadin.ui.CheckBox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Igor Laishen
 */
public class MyTreeTest {
    private MyTree myTree;
    private GTask[] rootLevelTasks;

    @Before
    public void init() {
        GTask gTask0 = new GTask();
        gTask0.setId(0);
        gTask0.setChildren(Arrays.asList(
                new GTask(1),
                new GTask(2),
                new GTask(3, Arrays.asList(
                        new GTask(4),
                        new GTask(5, Arrays.asList(
                                new GTask(6)
                        ))
                )),
                new GTask(7),
                new GTask(8, Arrays.asList(
                        new GTask(9)
                ))
        ));

        rootLevelTasks = new GTask[]{gTask0};

        myTree = new MyTree();
        myTree.setTasks(Arrays.asList(rootLevelTasks));
    }

    @Test
    public void allTasksShouldBeSelected() {
        List<GTask> selectedGTaskList = myTree.getSelectedRootLevelTasks();

        Assert.assertNotNull(selectedGTaskList);
        Assert.assertArrayEquals(rootLevelTasks, selectedGTaskList.toArray(new GTask[selectedGTaskList.size()]));
    }

    @Test
    public void allChildrenShouldBeDeselectedIfParentIsUnchecked() {
        // deselect parent #3
        ((CheckBox) myTree.tree.getContainerProperty(3, MyTree.ACTION_PROPERTY).getValue()).setValue(false);

        List<GTask> selectedGTaskList = myTree.getSelectedRootLevelTasks();

        Assert.assertNotNull(selectedGTaskList);
        Assert.assertArrayEquals(expectedRootLevelTasks1(), selectedGTaskList.toArray(new GTask[selectedGTaskList.size()]));
    }

    private GTask[] expectedRootLevelTasks1() {
        GTask gTask0 = new GTask();
        gTask0.setId(0);
        gTask0.setChildren(Arrays.asList(
                new GTask(1),
                new GTask(2),
                new GTask(7),
                new GTask(8, Arrays.asList(
                        new GTask(9)
                ))
        ));

        return new GTask[]{gTask0};
    }

    @Test
    public void allParentsShouldBeSelectedIfChildrenIsSelected() {
        // deselect parent #0
        ((CheckBox) myTree.tree.getContainerProperty(0, MyTree.ACTION_PROPERTY).getValue()).setValue(false);

        // select child #6
        ((CheckBox) myTree.tree.getContainerProperty(6, MyTree.ACTION_PROPERTY).getValue()).setValue(true);


        List<GTask> selectedGTaskList = myTree.getSelectedRootLevelTasks();

        Assert.assertNotNull(selectedGTaskList);
        Assert.assertArrayEquals(expectedRootLevelTasks2(), selectedGTaskList.toArray(new GTask[selectedGTaskList.size()]));
    }

    private GTask[] expectedRootLevelTasks2() {
        GTask gTask0 = new GTask();
        gTask0.setId(0);
        gTask0.setChildren(Arrays.asList(
                new GTask(3, Arrays.asList(
                        new GTask(5, Arrays.asList(
                                new GTask(6)
                        ))
                ))
        ));

        return new GTask[]{gTask0};
    }
}
