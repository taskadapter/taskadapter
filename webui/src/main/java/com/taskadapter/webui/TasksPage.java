package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author Alexey Skorokhodov
 */
public class TasksPage extends Page {
    public static final String ID = "tasks_list";

    private VerticalLayout layout = new VerticalLayout();

    private Table table;

    public TasksPage() {
        buildUI();
    }

    private void buildUI() {
        table = new Table();
        table.addStyleName("taskstable");
        table.addContainerProperty("Name", Button.class, null);
        layout.addComponent(table);
    }

    private void reloadConfigs() {
        table.removeAllItems();
        // items are not added to the table without specifying index unless we define an ID column.
        int i = 0;
        for (TAFile file : services.getConfigStorage().getAllConfigs()) {
            addTaskToTable(file, i++);
        }
        table.setPageLength(table.size() + 1);
    }

    private void addTaskToTable(final TAFile file, int i) {
        Button button = new Button(file.getConfigLabel());
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.showTaskDetailsPage(file);
            }
        });
        table.addItem(new Object[]{button}, i);
    }

    @Override
    public String getPageTitle() {
        return "Tasks";
    }

    @Override
    public Component getUI() {
        reloadConfigs();
        return layout;
    }
}
