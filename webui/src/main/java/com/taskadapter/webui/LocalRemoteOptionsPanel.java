package com.taskadapter.webui;

import com.taskadapter.webui.service.Services;
import com.vaadin.data.Property;
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
    private OptionGroup group;
    private Services services;

    public LocalRemoteOptionsPanel(Services services) {
        super("Local / server mode");
        this.services = services;
        buildUI();
        selectLocalOrRemoteMode();
        setupLocalRemoteModeListener();
    }

    private void buildUI() {
        group = new OptionGroup("", options);
        HorizontalLayout configGroupLayout = new HorizontalLayout();
        group.setNullSelectionAllowed(false);   // user can not deselect
        group.setImmediate(true);               // send the change to the server at once
        group.setEnabled(services.getAuthorizedOperations().canChangeServerSettings());
        configGroupLayout.addComponent(group);
        setContent(configGroupLayout);
    }

    private void selectLocalOrRemoteMode() {
        if (services.getSettingsManager().isTAWorkingOnLocalMachine()) {
            group.select(LOCAL);
        } else {
            group.select(REMOTE);
        }
    }

    private void setupLocalRemoteModeListener() {
        group.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean localModeRequested = event.getProperty().toString().equals(LOCAL);
                services.getSettingsManager().setLocal(localModeRequested);
                Notification.show("Saved");
            }
        });
    }

    OptionGroup getGroup() {
        return group;
    }
}
