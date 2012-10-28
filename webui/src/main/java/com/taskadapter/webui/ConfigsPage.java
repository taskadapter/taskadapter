package com.taskadapter.webui;

import com.taskadapter.web.uiapi.UISyncConfig;
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
    
    private static final Comparator<UISyncConfig> CONFIG_COMPARATOR = 
            new Comparator<UISyncConfig>() {
                @Override
                public int compare(UISyncConfig o1, UISyncConfig o2) {
                    return o1.getLabel().compareTo(o2.getLabel());
                }
            };
    
    private static final int COLUMNS_NUMBER = 1;
    private VerticalLayout layout = new VerticalLayout();
    private GridLayout configsLayout = new GridLayout();

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
                navigator.show(new NewConfigPage());
            }
        });
        layout.addComponent(addButton);
    }

    private void reloadConfigs() {
        configsLayout.removeAllComponents();

        final String userLoginName = services.getAuthenticator().getUserName();
        final List<UISyncConfig> allConfigs = services.getUIConfigStore().getUserConfigs(userLoginName);
        Collections.sort(allConfigs, CONFIG_COMPARATOR);
        for (UISyncConfig config : allConfigs) {
            addConfigToPage(config);
        }
    }

    private void addConfigToPage(final UISyncConfig config) {
        configsLayout.addComponent(new ConfigActionsPanel(services, navigator, config));
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "home";
    }

    @Override
    public Component getUI() {
        reloadConfigs();
        return layout;
    }
}