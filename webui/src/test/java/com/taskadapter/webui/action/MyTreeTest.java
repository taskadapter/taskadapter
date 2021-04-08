package com.taskadapter.webui.action;

import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MyTreeTest {

    private final GTask task0 = createTestTask(0L,
            createTestTask(1L), createTestTask(2L),
            createTestTask(3L,
                    createTestTask(4L),
                    createTestTask(5L,
                            createTestTask(6L))
            ),
            createTestTask(7L),
            createTestTask(8L,
                    createTestTask(9L)
            )
    );
    private final List<GTask> rootLevelTasks = List.of(task0);
    private final PreviouslyCreatedTasksResolver resolver = PreviouslyCreatedTasksResolver.empty;
    private final static List<GTask> expectedRootLevelTasks2 = List.of(
            createTestTask(0L, createTestTask(3L, createTestTask(5L, createTestTask(6L))))
    );

    private static GTask createTestTask(long id, GTask... children) {
        var task = new GTask();
        task.setId(id);
        task.setKey(id + "");
        task.setChildren(Arrays.asList(children));
        return task;
    }

    @Ignore
    public void allTasksAreSelectedByDefault() {
        var myTree = new MyTree(resolver, rootLevelTasks, "some location");
        var selectedGTaskList = myTree.getSelectedRootLevelTasks();
        assertThat(selectedGTaskList).isNotNull();
        assertThat(selectedGTaskList).containsAll(rootLevelTasks);
    }

    private final List<GTask> expectedRootLevelTasks1 = List.of(createTestTask(0L, createTestTask(1L), createTestTask(2L),
            createTestTask(7L), createTestTask(8L, createTestTask(9L)))
    );

    @Ignore
    public void allChildrenAreUnselectedWhenParentIsUnselected() {
        var myTree = new MyTree(resolver, rootLevelTasks, "some location");
        // deselect parent #3
//    myTree.tree.getContainerProperty(new TaskId(3L, "3"), MyTree.ACTION_PROPERTY).getValue.asInstanceOf[Checkbox].setValue(false)
        var selectedGTaskList = myTree.getSelectedRootLevelTasks();
        assertThat(selectedGTaskList).isNotNull();
        assertThat(selectedGTaskList).containsAll(expectedRootLevelTasks1);
    }

    @Ignore
    public void allParentsUpTheHierarchyMustBeSelectedIfAtLeastOneChildIsSelected() {
        var myTree = new MyTree(resolver, rootLevelTasks, "some location");
        // deselect parent #0
//    myTree.tree.getContainerProperty(new TaskId(0L, "0"), MyTree.ACTION_PROPERTY).getValue.asInstanceOf[Checkbox].setValue(false)
        // select child #6
//    myTree.tree.getContainerProperty(new TaskId(6L, "6"), MyTree.ACTION_PROPERTY).getValue.asInstanceOf[Checkbox].setValue(true)
        var selectedGTaskList = myTree.getSelectedRootLevelTasks();
        assertThat(selectedGTaskList).isNotNull();
        assertThat(selectedGTaskList).containsAll(expectedRootLevelTasks2);
    }
}
