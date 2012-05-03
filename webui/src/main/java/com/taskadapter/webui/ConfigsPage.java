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
public class ConfigsPage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    private Table table;

    public ConfigsPage() {
        buildUI();
    }

    private void buildUI() {
        createAddButton();

        table = new Table();
        table.addStyleName("configsTable");
        table.addContainerProperty("Name", Button.class, null);
        layout.addComponent(table);
    }

    private void createAddButton() {
        Button addButton = new Button("New config");
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.show(Navigator.NEW_CONFIG_PAGE);
            }
        });
        layout.addComponent(addButton);
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
        return "Configs";
    }

    @Override
    public Component getUI() {
        reloadConfigs();
        return layout;
    }
}
