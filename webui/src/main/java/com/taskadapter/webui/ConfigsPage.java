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
    public static final String SYSTEM_1_TITLE = "System 1";
    public static final String SYSTEM_2_TITLE = "System 2";

    private Table table;

    public ConfigsPage() {
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        createAddButton();

        table = new Table();
        table.addStyleName("configsTable");
        table.addContainerProperty("Name", Button.class, "");
        table.addContainerProperty(SYSTEM_1_TITLE, String.class, "");
        table.addContainerProperty(SYSTEM_2_TITLE, String.class, "");
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
        table.setPageLength(table.size());
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
        table.addItem(new Object[]{button, file.getConnectorDataHolder1().getData().getLabel(),
                file.getConnectorDataHolder2().getData().getLabel()}, i);
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
