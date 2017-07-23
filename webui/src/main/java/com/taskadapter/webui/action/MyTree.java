package com.taskadapter.webui.action;

import com.google.common.base.Strings;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;
import com.taskadapter.webui.Page;
import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;

import java.util.*;

/**
 * @author Alexey Skorokhodov
 */
public class MyTree extends CustomComponent {
    private static final long serialVersionUID = -4455731550636762518L;
    private PreviouslyCreatedTasksResolver resolver;

    private final class TreeItemSelectionHandler implements
            Property.ValueChangeListener {
        private static final long serialVersionUID = -9011190599386011166L;
        private final CheckBox checkBox;
        private final Long taskId;

        TreeItemSelectionHandler(CheckBox checkBox, Long taskId) {
            this.checkBox = checkBox;
            this.taskId = taskId;
        }

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            if (checkBox.getValue()) {
                // if some child is selected then also select his parent
                selectParent();
            } else {
                // if parent is deselected then deselect all his children
                deselectChildren();
            }
        }

        private void deselectChildren() {
            if (tree.hasChildren(taskId)) {
                for (Object itemId : tree.getChildren(taskId)) {
                    getCheckBox(itemId).setValue(false);
                }
            }
        }

        private void selectParent() {
            Long parentId = (Long) checkBox.getData();

            if (parentId != null) {
                getCheckBox(parentId).setValue(true);
            }
        }
    }

    private static final int MAX_ROWS_BEFORE_SCROLLBAR = 10;

    static final String ACTION_PROPERTY = Page.message("exportConfirmation.column.action");

    TreeTable tree;
    private List<GTask> rootLevelTasks;

    public MyTree(PreviouslyCreatedTasksResolver resolver) {
        this.resolver = resolver;
        buildUI();
    }

    private void buildUI() {
        tree = new TreeTable();
        tree.setWidth("800px");
        tree.addContainerProperty(ACTION_PROPERTY, CheckBox.class, null);
        tree.addContainerProperty(Page.message("exportConfirmation.column.sourceId"), String.class, null);
        tree.addContainerProperty(Page.message("exportConfirmation.column.summary"), String.class, null);
        setCompositionRoot(tree);
    }

    public List<GTask> getSelectedRootLevelTasks() {
        Set<Long> idSet = new HashSet<>();

        Collection<?> itemIds = tree.getItemIds();

        if (itemIds != null) {
            for (Object itemId : itemIds) {
                boolean checked = getCheckBox(itemId).getValue();

                if (checked) {
                    idSet.add((Long) itemId);
                }
            }
        }

        return getSelectedTasks(rootLevelTasks, idSet);
    }

    CheckBox getCheckBox(Object itemId) {
        return (CheckBox) tree.getContainerProperty(itemId, ACTION_PROPERTY).getValue();
    }

    private List<GTask> getSelectedTasks(List<GTask> gTaskList, Set<Long> idSet) {
        List<GTask> selectedTasks = new ArrayList<>();

        for (GTask gTask : gTaskList) {
            if (idSet.contains(gTask.getId())) {
                if (gTask.hasChildren()) {
                    gTask.setChildren(getSelectedTasks(gTask.getChildren(), idSet));
                }
                selectedTasks.add(gTask);
            }
        }

        return selectedTasks;
    }

    public void setTasks(List<GTask> rootLevelTasks) {
        this.rootLevelTasks = rootLevelTasks;
        addTasksToTree(null, rootLevelTasks);

        int rowsNumber = Math.min(tree.size() + 1, MAX_ROWS_BEFORE_SCROLLBAR);
        tree.setPageLength(rowsNumber);
    }

    private void addTasksToTree(Object parentId, List<GTask> tasks) {
        for (GTask task : tasks) {
            addTaskToTree(parentId, task);
        }
    }

    private void addTaskToTree(Object parentId, GTask task) {
        final Long taskId = task.getId();

        final CheckBox checkBox = new CheckBox(
                resolver.findSourceSystemIdentity(task).isDefined() ?
                        Page.message("exportConfirmation.action.update")
                        : Page.message("exportConfirmation.action.create"));

        checkBox.setValue(true);
        checkBox.setData(parentId);
        checkBox.addValueChangeListener(new TreeItemSelectionHandler(checkBox, taskId));
        checkBox.setImmediate(true);

        tree.addItem(
                new Object[]{
                        checkBox,                  // ACTION
                        Strings.nullToEmpty(task.getSourceSystemId()), // ID FROM SOURCE SYSTEM
                        task.getValue("Summary") // TODO TA3 use a proper connector-specific field name here
                },
                taskId
        );

        if (parentId != null) {
            tree.setParent(taskId, parentId);
        }

        if (task.hasChildren()) {
            addTasksToTree(taskId, task.getChildren());
            tree.setCollapsed(taskId, false);
        }
    }
}
