package com.taskadapter.webui.action;

import com.taskadapter.model.GTask;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class MyTree extends CustomComponent {
    private static final int MAX_ROWS_BEFORE_SCROLLBAR = 15;

    private TreeTable tree;
    private List<GTask> rootLevelTasks;

    public MyTree() {
        buildUI();
    }

    private void buildUI() {
        tree = new TreeTable();
        tree.setSizeFull();
        tree.addContainerProperty("Action", CheckBox.class, null);
        tree.addContainerProperty("ID", String.class, null);
        tree.addContainerProperty("Summary", String.class, null);
        setCompositionRoot(tree);
    }

    public List<GTask> getSelectedRootLevelTasks() {
        return rootLevelTasks;
    }

    public void setTasks(List<GTask> rootLevelTasks) {
        this.rootLevelTasks = rootLevelTasks;
        addTasksToTree(null, rootLevelTasks);

        int rowsNumber = Math.min(tree.size() + 1, MAX_ROWS_BEFORE_SCROLLBAR);
        tree.setPageLength(rowsNumber);
    }

    private void addTasksToTree(Object parentId, List<GTask> tasks) {
        for (GTask task : tasks) {
            String actionText = (task.getRemoteId() == null) ? "Create" : "Update";
            CheckBox checkBox = new CheckBox(actionText);
            // TODO disabled because of http://www.hostedredmine.com/issues/64169
            // ("checkboxes in the tree on "export these tasks" page are ignored")
            checkBox.setEnabled(false);
            checkBox.setValue(true);
            Object newItemId = tree.addItem(new Object[]{checkBox, task.getId() + "", task.getSummary()}, task.getId());
            if (parentId != null) {
                tree.setParent(newItemId, parentId);
            }
            if (task.hasChildren()) {
                addTasksToTree(newItemId, task.getChildren());
                tree.setCollapsed(newItemId, false);
            }
        }
    }
}
