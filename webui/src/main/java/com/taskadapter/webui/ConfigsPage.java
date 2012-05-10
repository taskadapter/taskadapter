package com.taskadapter.webui;

import com.taskadapter.config.ConnectorDataHolder;
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
        table.addContainerProperty(SYSTEM_1_TITLE, Button.class, "");
        table.addContainerProperty(SYSTEM_2_TITLE, Button.class, "");
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
        // config title link-button
        Button button = new Button(file.getConfigLabel());
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addStyleName("configsTableButton");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.showTaskDetailsPage(file);
            }
        });

        // buttons of concrete system config page
        ConnectorDataHolder dh1 = file.getConnectorDataHolder1();
        ConnectorDataHolder dh2 = file.getConnectorDataHolder2();
        Button b1 = addDataHolderButton(dh1.getData().getLabel(), new Exporter(navigator, services.getPluginManager(), dh1, dh2, file));
        Button b2 = addDataHolderButton(dh2.getData().getLabel(), new Exporter(navigator, services.getPluginManager(), dh2, dh1, file));
        
        table.addItem(new Object[]{button, b1, b2}, i);
    }

    /**
     * this button opens config page for given System
     * @param label button text
     * @param exporter Exporter instance
     * @return button instance
     */
    private Button addDataHolderButton(String label, final Exporter exporter) {
        Button button = new Button(label);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addStyleName("configsTableButton");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                exporter.export();
            }
        });
        return button;
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
