package com.taskadapter.webui.pages;

import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.Page;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

public final class ConfigsPage {

    /**
     * Callback for config list page.
     */
    public interface Callback {
        /**
         * User requested to edit config.
         * 
         * @param config
         *            selected config.
         */
        void edit(UISyncConfig config);

        /**
         * User requested synchronization in "forward" directions (from left to
         * right).
         * 
         * @param config
         *            config for the operation.
         */
        void forwardSync(UISyncConfig config);

        /**
         * User requested synchronization in "reverse" direction (from right to
         * left).
         * 
         * @param config
         *            config for the operation.
         */
        void backwardSync(UISyncConfig config);

        /**
         * Performs a forward drop-in.
         * 
         * @param config
         *            config to use.
         * @param file
         *            file to receive.
         */
        void forwardDropIn(UISyncConfig config, Html5File file);

        /**
         * Performs a backward drop-in.
         * 
         * @param config
         *            config to use.
         * @param file
         *            file to receive.
         */
        void backwardDropIn(UISyncConfig config, Html5File file);

        /**
         * User requested creation of a new config.
         */
        void newConfig();
    }

    /**
     * Config display mode.
     */
    public enum DisplayMode {
        /**
         * This page is displaying only owned configs.
         */
        OWNED_CONFIGS {
            String nameOf(UISyncConfig config) {
                return config.getLabel();
            }
        },

        /**
         * This page is displaying all configs.
         */
        ALL_CONFIGS {
            String nameOf(UISyncConfig config) {
                return config.getOwnerName() + " : " + config.getLabel();
            }
        };

        /**
         * Provides a name of the config.
         */
        abstract String nameOf(UISyncConfig config);
    }

    /**
     * Comparator for configuration files.
     */
    private static final class ConfigComparator implements
            Comparator<UISyncConfig> {
        /**
         * Name of the current user.
         */
        private final String userName;

        ConfigComparator(String userName) {
            this.userName = userName;
        }

        @Override
        public int compare(UISyncConfig o1, UISyncConfig o2) {
            final boolean isMyConfig1 = userName.equals(o1.getOwnerName());
            final boolean isMyConfig2 = userName.equals(o2.getOwnerName());

            if (isMyConfig1 != isMyConfig2)
                return isMyConfig1 ? -1 : 1;

            final int ucomp = o1.getOwnerName().compareTo(o2.getOwnerName());
            if (ucomp != 0)
                return ucomp;

            return o1.getLabel().compareTo(o2.getLabel());
        }

    }

    private final VerticalLayout layout;
    private final Collection<UISyncConfig> configs;
    private final DisplayMode displayMode;
    private final Callback callback;
    private final VerticalLayout configsLayout;

    private ConfigsPage(Collection<UISyncConfig> configs, DisplayMode mode,
            final Callback callback) {

        this.configs = configs;
        this.displayMode = mode;
        this.callback = callback;

        layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setWidth(560, PIXELS);

        final HorizontalLayout actionPanel = new HorizontalLayout();
        actionPanel.setWidth(100, PERCENTAGE);
        layout.addComponent(actionPanel);

        final Button addButton = new Button(
                Page.MESSAGES.get("configsPage.buttonNewConfig"));
        addButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                callback.newConfig();
            }
        });
        actionPanel.addComponent(addButton);
        actionPanel.setComponentAlignment(addButton, Alignment.MIDDLE_LEFT);

        final HorizontalLayout filterPanel = new HorizontalLayout();
        TextField filterField = new TextField();
        filterField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                filterFields(event.getText());
            }
        });
        filterPanel.addComponent(new Label(Page.MESSAGES
                .get("configsPage.filter")));
        filterPanel.addStyleName("filterPanel");
        filterPanel.addComponent(filterField);
        filterPanel.setSpacing(true);
        actionPanel.addComponent(filterPanel);
        actionPanel.setComponentAlignment(filterPanel, Alignment.MIDDLE_RIGHT);

        configsLayout = new VerticalLayout();
        configsLayout.setSpacing(true);
        configsLayout.setWidth(560, PIXELS);

        setDisplayedConfigs(configs);

        layout.addComponent(configsLayout);
    }

    /**
     * Sets a new displayed configs.
     * 
     * @param dispConfigs
     *            new displayed configs.
     */
    private void setDisplayedConfigs(Collection<UISyncConfig> dispConfigs) {
        configsLayout.removeAllComponents();

        for (UISyncConfig config : dispConfigs) {
            configsLayout.addComponent(ConfigActionsPanel.render(config,
                    displayMode, callback));
        }
    }

    private void filterFields(String filterStr) {
        final String[] words = filterStr == null ? new String[0] : filterStr
                .toLowerCase().split(" +");
        final List<UISyncConfig> res = new ArrayList<>(
                configs.size());
        for (UISyncConfig config : configs) {
            if (matches(config, words)) {
                res.add(config);
            }
        }

        setDisplayedConfigs(res);
    }

    private boolean matches(UISyncConfig config, String[] filters) {
        if (filters.length == 0) {
            return true;
        }
        for (String name : filters) {
            final String confName = displayMode.nameOf(config);
            if (!confName.toLowerCase().contains(name)
                    && !config.getConnector1().getLabel().toLowerCase()
                            .contains(name)
                    && !config.getConnector2().getLabel().toLowerCase()
                            .contains(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Renders a config page.
     * 
     * @param userName
     *            name of current user.
     * @param configs
     *            configs to show.
     * @param mode
     *            active display mode.
     * @param callback
     *            user action callback.
     * @return config page UI.
     */
    public static Component render(String userName,
            Collection<UISyncConfig> configs, DisplayMode mode,
            final Callback callback) {
        final List<UISyncConfig> configCopy = new ArrayList<>(
                configs);
        Collections.sort(configCopy, new ConfigComparator(userName));
        return new ConfigsPage(configCopy, mode, callback).layout;
    }
}