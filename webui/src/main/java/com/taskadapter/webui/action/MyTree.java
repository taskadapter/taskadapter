package com.taskadapter.webui.action;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Summary$;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;


import java.util.*;

public class MyTree {
    private PreviouslyCreatedTasksResolver resolver;
    private String targetLocation;

/*    private final class TreeItemSelectionHandler implements Property.ValueChangeListener {
        private final Checkbox checkBox;
        private final TaskId taskId;

        TreeItemSelectionHandler(Checkbox checkBox, TaskId taskId) {
            this.checkBox = checkBox;
            this.taskId = taskId;
        }


        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            if (checkBox.getValue()) {
                // if some child is selected then also select its parent
                selectParent();
            } else {
                // if parent is deselected then deselect all its children
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
            TaskId parentId = (TaskId) checkBox.getData();

            if (parentId != null) {
                getCheckBox(parentId).setValue(true);
            }
        }
    }

   */
    private static final int MAX_ROWS_BEFORE_SCROLLBAR = 10;

    static final String ACTION_PROPERTY = Page.message("exportConfirmation.column.action");

    private TreeData<TreeTaskItem> treeData = new TreeData<>();
    private TreeGrid<TreeTaskItem> tree = new TreeGrid<>();
    private List<GTask> rootLevelTasks;

    public MyTree(PreviouslyCreatedTasksResolver resolver, List<GTask> rootLevelTasks, String targetLocation) {
        this.resolver = resolver;
        this.targetLocation = targetLocation;
        buildUI();
        List<GTask> clonedToAvoidDamagingTasks = new ArrayList<>();
        for (GTask task : rootLevelTasks) {
            clonedToAvoidDamagingTasks.add(GTask.shallowClone(task));
        }
        tree.setTreeData(treeData);
        setTasks(clonedToAvoidDamagingTasks);
    }

    private void buildUI() {
        tree.setWidth("800px");
        tree.addComponentHierarchyColumn(TreeTaskItem::getCheckBox)
                .setHeader(ACTION_PROPERTY);
        tree.addColumn(TreeTaskItem::getSourceSystemId)
                .setHeader(Page.message("exportConfirmation.column.sourceId"));
        tree.addColumn(TreeTaskItem::getTaskSummary)
                .setHeader(Page.message("exportConfirmation.column.summary"));
    }

    public List<GTask> getSelectedRootLevelTasks() {
        Set<TaskId> idSet = new HashSet<>();
        Collection<TreeTaskItem> items = tree.getTreeData().getRootItems();
        if (items != null) {
            for (TreeTaskItem item : items) {
                boolean checked = item.checkBox.getValue();
                if (checked) {
                    idSet.add(item.taskId);
                }
            }
        }
        return getSelectedTasks(rootLevelTasks, idSet);
    }

    private List<GTask> getSelectedTasks(List<GTask> gTaskList, Set<TaskId> idSet) {
        List<GTask> selectedTasks = new ArrayList<>();

        for (GTask gTask : gTaskList) {
            if (idSet.contains(gTask.getIdentity())) {
                if (gTask.hasChildren()) {
                    gTask.setChildren(getSelectedTasks(gTask.getChildren(), idSet));
                }
                selectedTasks.add(gTask);
            }
        }

        return selectedTasks;
    }

    private void setTasks(List<GTask> rootLevelTasks) {
        this.rootLevelTasks = rootLevelTasks;
        addTasksToTree(null, rootLevelTasks);

        int rowsNumber = Math.min(rootLevelTasks.size(), MAX_ROWS_BEFORE_SCROLLBAR);
        tree.setPageSize(rowsNumber);
    }

    private void addTasksToTree(TreeTaskItem parentId, List<GTask> tasks) {
        for (GTask task : tasks) {
            addTaskToTree(parentId, task);
        }
    }

    private void addTaskToTree(TreeTaskItem parentId, GTask task) {
        TaskId taskId = task.getIdentity();

        String actionText = resolver.findSourceSystemIdentity(task, targetLocation).isPresent() ?
                Page.message("exportConfirmation.action.update")
                : Page.message("exportConfirmation.action.create");
        Checkbox checkBox = new Checkbox(
                actionText);

        checkBox.setValue(true);
//        checkBox.setValue(parentId);
//        checkBox.addValueChangeListener(new TreeItemSelectionHandler(checkBox, taskId));
//        checkBox.setImmediate(true);

        String sourceSystemId = task.getSourceSystemId() == null ? "" :
                Strings.nullToEmpty(task.getSourceSystemId().getKey());
        TreeTaskItem taskItem = new TreeTaskItem(
                taskId,
                actionText,
                checkBox,
                sourceSystemId, // ID FROM SOURCE SYSTEM
                task.getValue(Summary$.MODULE$) // TODO TA3 use a proper connector-specific field name here
        );
        treeData.addItem(
                parentId,
                taskItem
        );

//        if (parentId != null) {
//            tree.setParent(taskId, parentId);
//        }

        if (task.hasChildren()) {
            addTasksToTree(taskItem, task.getChildren());
//            tree.setCollapsed(taskId, false);
        }
    }

    static class TreeTaskItem {

        TaskId taskId;
        String actionText;
        Checkbox checkBox;
        String sourceSystemId;
        String taskSummary;

        TreeTaskItem(TaskId taskId, String actionText, Checkbox checkBox, String sourceSystemId, String taskSummary) {
            this.taskId = taskId;
            this.actionText = actionText;
            this.checkBox = checkBox;
            this.sourceSystemId = sourceSystemId;
            this.taskSummary = taskSummary;
        }

        public TaskId getTaskId() {
            return taskId;
        }

        public String getActionText() {
            return actionText;
        }

        public Checkbox getCheckBox() {
            return checkBox;
        }

        public String getSourceSystemId() {
            return sourceSystemId;
        }

        public String getTaskSummary() {
            return taskSummary;
        }
    }

    public TreeGrid<TreeTaskItem> getTree() {
        return tree;
    }
}
