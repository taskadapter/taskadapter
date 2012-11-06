package com.taskadapter.webui;

import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
    private final TextField filterField = new TextField();
    private String lastFilter;

    public ConfigsPage() {
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        createAddButton();
        
        final HorizontalLayout filterPanel = new HorizontalLayout();
        filterField.addListener(new FieldEvents.TextChangeListener() {            
            @Override
            public void textChange(TextChangeEvent event) {
                filterFields(event.getText());
            }
        });
        filterPanel.addComponent(new Label("Filter"));
        filterPanel.addComponent(filterField);
        filterPanel.setSpacing(true);
        layout.addComponent(filterPanel);

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
        filterFields(lastFilter);
    }

    private void filterFields(String filterStr) {
        lastFilter = filterStr;
        final String[] words = filterStr == null ? new String[0] : filterStr
                .split(" +");
        final Iterator<Component> compiter = configsLayout.getComponentIterator();
        while (compiter.hasNext()) {
            final ConfigActionsPanel cap = (ConfigActionsPanel) compiter.next();
            cap.setVisible(matches(cap.getConfig(), words));
        }
    }

    private boolean matches(UISyncConfig config, String[] filters) {
        if (filters.length == 0) {
            return true;
        }
        for (String name : filters) {
            if (!config.getLabel().contains(name)
                    && !config.getConnector1().getLabel().contains(name)
                    && !config.getConnector2().getLabel().contains(name)) {
                return false;
            }
        }
        return true;
    }

    private void addConfigToPage(final UISyncConfig config) {
        configsLayout.addComponent(new ConfigActionsPanel(MESSAGES, services, navigator, config));
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