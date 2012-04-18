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

    private TreeTable tr;
    private List<GTask> rootLevelTasks;

    public MyTree() {
        buildUI();
    }

    private void buildUI() {
        tr = new TreeTable();
        tr.setSizeFull();
        tr.addContainerProperty("Action", CheckBox.class, null);
        tr.addContainerProperty("ID", String.class, null);
        tr.addContainerProperty("Summary", String.class, null);

//        Object basics = tr.addItem(new Object[]{"Basics", null}, "basics");
//        Object name = tr.addItem(new Object[]{"Name", new TextField()}, "name");
//        Object type = tr.addItem(new Object[]{"Type", new ComboBox()}, "type");
//        Object enabled = tr.addItem(new Object[]{"Enabled", new CheckBox()}, "enabled");

//        tr.setParent(name, basics);
//        tr.setParent(type, basics);
//        tr.setParent(enabled, basics);
        setCompositionRoot(tr);
    }

//    public int countAllTasks() {
//        // TODO
//        return -1;
//    }

    public List<GTask> getSelectedRootLevelTasks() {
        // TODO
        return rootLevelTasks;
    }

    public void setTasks(List<GTask> rootLevelTasks) {
        this.rootLevelTasks = rootLevelTasks;
        // TODO this will add the ROOT level only
        int i = 0;
        for (GTask t : rootLevelTasks) {
            String actionText = (t.getRemoteId() == null) ? "Create" : "Update";
            CheckBox checkBox = new CheckBox(actionText);
            checkBox.setValue(true);
            tr.addItem(new Object[]{checkBox, t.getId() + "", t.getSummary()}, i++);
        }

        int rowsNumber = Math.min(tr.size() + 1, MAX_ROWS_BEFORE_SCROLLBAR);
        tr.setPageLength(rowsNumber);
    }
}
