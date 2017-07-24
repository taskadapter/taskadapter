package com.taskadapter.webui;

import com.taskadapter.web.SettingsManager;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;

import java.util.Arrays;
import java.util.List;

class LocalRemoteOptionsPanel extends Panel {
    private static final String LOCAL = Page.message("configurePage.local");
    private static final String REMOTE = Page.message("configurePage.remote");

    private static final List<String> options = Arrays.asList(LOCAL, REMOTE);
    
    /**
     * Creates a new local/remote settings panel.
     * 
     * @param settingsManager
     *            used settings manager.
     * @param canModify
     *            if <code>true</code>, user can change the settings. Otherwise
     *            user can see setting but cannot change it.
     * @return local/remote options panel.
     */
    public static Component createLocalRemoteOptions(
            final SettingsManager settingsManager, boolean canModify) {
        final Panel ui = new Panel(Page.message("configurePage.caption"));

        final HorizontalLayout configGroupLayout = new HorizontalLayout();

        final OptionGroup group = new OptionGroup("", options);
        group.select(settingsManager.isTAWorkingOnLocalMachine() ? LOCAL
                : REMOTE);
        group.setNullSelectionAllowed(false); // user can not deselect
        group.setImmediate(true); // send the change to the server at once
        group.setEnabled(canModify);

        if (canModify) {
            group.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    String selectedStringValue = (String) event.getProperty().getValue();
                    boolean localModeRequested = selectedStringValue.equals(LOCAL);
                    settingsManager.setLocal(localModeRequested);
                    Notification.show(Page.message("configurePage.saved"));
                }
            });
        }

        configGroupLayout.addComponent(group);
        ui.setContent(configGroupLayout);

        return ui;
    }
}
