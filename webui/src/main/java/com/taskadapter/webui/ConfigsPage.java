package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigsPage extends Page {
    private static final int COLUMNS_NUMBER = 1;
    private VerticalLayout layout = new VerticalLayout();
    private GridLayout configsLayout = new GridLayout();
    public static final String SYSTEM_1_TITLE = "System 1";
    public static final String SYSTEM_2_TITLE = "System 2";

    public ConfigsPage() {
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        createAddButton();

        configsLayout.setColumns(COLUMNS_NUMBER);
        configsLayout.setSpacing(true);
        layout.addComponent(configsLayout);
        configsLayout.addStyleName("configsTable");
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
        configsLayout.removeAllComponents();
        List<TAFile> allConfigs = services.getConfigStorage().getAllConfigs();
        Collections.sort(allConfigs, new DescriptionComparator());
        for (TAFile file : allConfigs) {
            addTask(file);
        }
    }

    private void addTask(final TAFile file) {
        configsLayout.addComponent(new ConfigActionsPanel(navigator, file, services));
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

    class DescriptionComparator implements Comparator<TAFile> {

        @Override
        public int compare(TAFile o1, TAFile o2) {
            return o1.getConfigLabel().compareToIgnoreCase(o2.getConfigLabel());
        }
    }
}