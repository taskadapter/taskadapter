package com.taskadapter.webui;

import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.config.ConfigAccessor;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class ConfigsPage extends Page {
    
    private final Comparator<UISyncConfig> CONFIG_COMPARATOR = new Comparator<UISyncConfig>() {
        @Override
        public int compare(UISyncConfig o1, UISyncConfig o2) {
            final String uname = services.getCurrentUserInfo().getUserName();
            final boolean isMyConfig1 = uname.equals(o1.getOwnerName());
            final boolean isMyConfig2 = uname.equals(o2.getOwnerName());

            if (isMyConfig1 != isMyConfig2)
                return isMyConfig1 ? -1 : 1;
            
            final String n1 = configAccessor.nameOf(o1);
            final String n2 = configAccessor.nameOf(o2);

            return n1.compareTo(n2);
        }
    };
    
    private VerticalLayout layout = new VerticalLayout();
    private VerticalLayout configsLayout = new VerticalLayout();
    private final TextField filterField = new TextField();
    private String lastFilter;
    private HorizontalLayout actionPanel;
    private List<UISyncConfig> cachedConfigs;
    private ConfigAccessor configAccessor;

    public ConfigsPage() {
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        layout.setWidth(560, PIXELS);

        configsLayout.setSpacing(true);
        configsLayout.setWidth(560, PIXELS);

        addActionBar();
        addCreateNewConfigButton();
        addFilter();

        layout.addComponent(configsLayout);
    }

    private void addActionBar() {
        actionPanel = new HorizontalLayout();
        actionPanel.setWidth(100, PERCENTAGE);
        layout.addComponent(actionPanel);
    }

    private void addFilter() {
        final HorizontalLayout filterPanel = new HorizontalLayout();
        filterField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                filterFields(event.getText());
            }
        });
        filterPanel.addComponent(new Label(MESSAGES.get("configsPage.filter")));
        filterPanel.addStyleName("filterPanel");
        filterPanel.addComponent(filterField);
        filterPanel.setSpacing(true);
        actionPanel.addComponent(filterPanel);
        actionPanel.setComponentAlignment(filterPanel, Alignment.MIDDLE_RIGHT);
    }

    private void addCreateNewConfigButton() {
        Button addButton = new Button(MESSAGES.get("configsPage.buttonNewConfig"));
        addButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.show(new NewConfigPage());
            }
        });
        actionPanel.addComponent(addButton);
        actionPanel.setComponentAlignment(addButton, Alignment.MIDDLE_LEFT);

    }

    private void reloadConfigs() {
        configsLayout.removeAllComponents();

        configAccessor = services.getConfigAccessor().createBestAccessor();
        final List<UISyncConfig> allConfigs = configAccessor.getConfigs();
        Collections.sort(allConfigs, CONFIG_COMPARATOR);
        for (UISyncConfig config : allConfigs) {
            addConfigToPage(config);
        }
        this.cachedConfigs = allConfigs;
        filterFields(lastFilter);
    }

    private void filterFields(String filterStr) {
        lastFilter = filterStr;
        final String[] words = filterStr == null ? new String[0] : filterStr
                .toLowerCase().split(" +");
        configsLayout.removeAllComponents();
        for (UISyncConfig config : cachedConfigs) {
            if (matches(config, words)) {
                addConfigToPage(config);
            }
        }
    }

    private boolean matches(UISyncConfig config, String[] filters) {
        if (filters.length == 0) {
            return true;
        }
        for (String name : filters) {
            if (!configAccessor.nameOf(config).toLowerCase().contains(name)
                    && !config.getConnector1().getLabel().toLowerCase()
                            .contains(name)
                    && !config.getConnector2().getLabel().toLowerCase()
                            .contains(name)) {
                return false;
            }
        }
        return true;
    }

    private void addConfigToPage(final UISyncConfig config) {
        configsLayout.addComponent(new ConfigActionsPanel(MESSAGES, services,
                navigator, config, configAccessor));
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