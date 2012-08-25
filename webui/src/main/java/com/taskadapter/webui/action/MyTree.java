package com.taskadapter.webui.action;

import com.taskadapter.model.GTask;
import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;

import java.util.*;

/**
 * @author Alexey Skorokhodov
 */
public class MyTree extends CustomComponent {
    private static final int MAX_ROWS_BEFORE_SCROLLBAR = 10;

    private static final String CREATE = "Create";
    private static final String UPDATE = "Update";

    public static final  String ACTION_PROPERTY  = "Action";
    private static final String ID_PROPERTY      = "ID";
    private static final String SUMMARY_PROPERTY = "Summary";

    TreeTable tree;
    private List<GTask> rootLevelTasks;

    public MyTree() {
        buildUI();
    }

    private void buildUI() {
        tree = new TreeTable();
        tree.setWidth("800px");
        tree.addContainerProperty(ACTION_PROPERTY, CheckBox.class, null);
        tree.addContainerProperty(ID_PROPERTY, String.class, null);
        tree.addContainerProperty(SUMMARY_PROPERTY, String.class, null);
        setCompositionRoot(tree);
    }

    public List<GTask> getSelectedRootLevelTasks() {
        Set<Integer> idSet = new HashSet<Integer>();

        Collection itemIds = tree.getItemIds();

        if (itemIds != null) {
            for (Object itemId : itemIds) {
                boolean checked = ((CheckBox) tree.getContainerProperty(itemId, ACTION_PROPERTY).getValue()).booleanValue();

                if (checked) {
                    idSet.add((Integer) itemId);
                }
            }
        }

        return getSelectedTasks(rootLevelTasks, idSet);
    }

    private List<GTask> getSelectedTasks(List<GTask> gTaskList, Set<Integer> idSet) {
        List<GTask> selectedTasks = new ArrayList<GTask>();

        for (GTask gTask : gTaskList) {
            if(idSet.contains(gTask.getId())) {
                if(gTask.hasChildren()) {
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
            final Integer taskId = task.getId();

            final CheckBox checkBox = new CheckBox((task.getRemoteId() == null) ? CREATE : UPDATE);
            checkBox.setValue(true);
            checkBox.setData(parentId);
            checkBox.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    if (checkBox.booleanValue()) {
                        Integer parentId = (Integer) checkBox.getData();

                        if (parentId != null) {
                            // if some child is selected then also select his parent
                            ((CheckBox) tree.getContainerProperty(parentId, ACTION_PROPERTY).getValue()).setValue(true);
                        }
                    } else {
                        // if parent is deselected then deselect all his children
                        if (tree.hasChildren(taskId)) {
                            for (Object itemId : tree.getChildren(taskId)) {
                                ((CheckBox) tree.getContainerProperty(itemId, ACTION_PROPERTY).getValue()).setValue(false);
                            }
                        }
                    }
                }
            });
            checkBox.setImmediate(true);

            tree.addItem(
                    new Object[]{
                            checkBox,                  // ACTION
                            String.valueOf(taskId),    // ID
                            task.getSummary()          // SUMMARY
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
}
