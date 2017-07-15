package com.taskadapter.webui.action;

import com.taskadapter.model.GTask;
import com.vaadin.ui.CheckBox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MyTreeTest {
    private MyTree myTree;
    private GTask[] rootLevelTasks;

    @Before
    public void init() {
        GTask gTask0 = createTestTask(0l,
                createTestTask(1l),
                createTestTask(2l),
                createTestTask(3l,
                        createTestTask(4l),
                        createTestTask(5l,
                                createTestTask(6l)
                        )
                ),
                createTestTask(7l),
                createTestTask(8l,
                        createTestTask(9l)
                )
        );

        rootLevelTasks = new GTask[]{gTask0};

        myTree = new MyTree();
        myTree.setTasks(Arrays.asList(rootLevelTasks));
    }

    private GTask createTestTask(Long id, GTask... children) {
        GTask task = new GTask();
        task.setId(id);
        task.setChildren(Arrays.asList(children));
        return task;
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
        ((CheckBox) myTree.tree.getContainerProperty(3l, MyTree.ACTION_PROPERTY).getValue()).setValue(false);

        List<GTask> selectedGTaskList = myTree.getSelectedRootLevelTasks();

        Assert.assertNotNull(selectedGTaskList);
        Assert.assertArrayEquals(expectedRootLevelTasks1(), selectedGTaskList.toArray(new GTask[selectedGTaskList.size()]));
    }

    private GTask[] expectedRootLevelTasks1() {
        GTask gTask0 = createTestTask(0l,
                createTestTask(1l),
                createTestTask(2l),
                createTestTask(7l),
                createTestTask(8l,
                        createTestTask(9l)
                )
        );

        return new GTask[]{gTask0};
    }

    @Test
    public void allParentsShouldBeSelectedIfChildrenIsSelected() {
        // deselect parent #0
        ((CheckBox) myTree.tree.getContainerProperty(0l, MyTree.ACTION_PROPERTY).getValue()).setValue(false);

        // select child #6
        ((CheckBox) myTree.tree.getContainerProperty(6l, MyTree.ACTION_PROPERTY).getValue()).setValue(true);


        List<GTask> selectedGTaskList = myTree.getSelectedRootLevelTasks();

        Assert.assertNotNull(selectedGTaskList);
        Assert.assertArrayEquals(expectedRootLevelTasks2(), selectedGTaskList.toArray(new GTask[selectedGTaskList.size()]));
    }

    private GTask[] expectedRootLevelTasks2() {
        GTask gTask0 = createTestTask(0l,
                createTestTask(3l,
                        createTestTask(5l,
                                createTestTask(6l)
                        )
                )
        );

        return new GTask[]{gTask0};
    }
}
