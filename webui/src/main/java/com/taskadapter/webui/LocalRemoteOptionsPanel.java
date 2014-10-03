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

public class LocalRemoteOptionsPanel extends Panel {
    private static final String LOCAL = "TA is running on my LOCAL machine. The file paths are on MY computer.";
    private static final String REMOTE = "TA is running on some shared machine (SERVER).";

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
        final Panel ui = new Panel("Local / server mode");

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
                    Notification.show("Saved");
                }
            });
        }

        configGroupLayout.addComponent(group);
        ui.setContent(configGroupLayout);

        return ui;
    }
}
