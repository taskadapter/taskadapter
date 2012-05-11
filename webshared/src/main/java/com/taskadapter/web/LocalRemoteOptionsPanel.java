package com.taskadapter.web;

import com.vaadin.data.Property;
import com.vaadin.ui.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class LocalRemoteOptionsPanel extends Panel {
    private static final String LOCAL = "TA is running on my LOCAL machine. The file paths are on MY computer.";
    private static final String REMOTE = "TA is running on some shared machine (SERVER). The files are stored on the remote machine.";

    private static final List<String> options = Arrays.asList(LOCAL, REMOTE);
    private OptionGroup group;
    private SettingsManager settingsManager;
    private ProgressElement progressElement;

    public LocalRemoteOptionsPanel(SettingsManager settingsManager) {
        super("Local / server mode");
        this.settingsManager = settingsManager;
        buildUI();
        selectCurrentSetting();
        setupListener();
    }

    private void buildUI() {
        addStyleName("panelexample");
        group = new OptionGroup("Local/remote mode", options);
        HorizontalLayout configGroupLayout = new HorizontalLayout();
        progressElement = new ProgressElement();

        group.setNullSelectionAllowed(false); // user can not 'unselect'
        group.setImmediate(true); // send the change to the server at once
        configGroupLayout.addComponent(group);
        configGroupLayout.addComponent(progressElement);
        configGroupLayout.setComponentAlignment(progressElement, Alignment.MIDDLE_CENTER);

        addComponent(configGroupLayout);
    }

    private void selectCurrentSetting() {
        if (settingsManager.isLocal()) {
            group.select(LOCAL);
        } else {
            group.select(REMOTE);
        }
    }

    private void setupListener() {
        group.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean isLocal = event.getProperty().toString().equals(LOCAL);
                settingsManager.setLocal(isLocal);
                progressElement.start();
            }
        });
    }

}
