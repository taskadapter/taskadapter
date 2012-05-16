package com.taskadapter.web;

import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.web.service.Services;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class LocalRemoteOptionsPanel extends Panel {
    private static final String LOCAL = "TA is running on my LOCAL machine. The file paths are on MY computer.";
    private static final String REMOTE = "TA is running on some shared machine (SERVER).";

    private static final List<String> options = Arrays.asList(LOCAL, REMOTE);
    private OptionGroup group;
    private ProgressElement progressElement;
    private Services services;

    public LocalRemoteOptionsPanel(Services services) {
        super("Local / server mode");
        this.services = services;
        buildUI();
        selectLocalOrRemoteMode();
        setupLocalRemoteModeListener();
        setupLicenseListener();
    }

    private void buildUI() {
        addStyleName("panelexample");
        group = new OptionGroup("", options);
        HorizontalLayout configGroupLayout = new HorizontalLayout();
        progressElement = new ProgressElement();

        group.setNullSelectionAllowed(false); // user can not 'unselect'
        group.setImmediate(true); // send the change to the server at once
        configGroupLayout.addComponent(group);
        configGroupLayout.addComponent(progressElement);
        configGroupLayout.setComponentAlignment(progressElement, Alignment.MIDDLE_CENTER);

        addComponent(configGroupLayout);
    }

    private void selectLocalOrRemoteMode() {
        if (services.getSettingsManager().isLocalSingleUserMode()) {
            group.select(LOCAL);
        } else {
            group.select(REMOTE);
        }
        forceLocalModeIfSingleUserLicenseInstalled();
    }

    private void forceLocalModeIfSingleUserLicenseInstalled() {
        if (services.getLicenseManager().isSingleUserLicenseInstalled()) {
            group.select(LOCAL);
            group.setEnabled(false);
        } else {
            group.setEnabled(true);
        }
    }

    private void setupLocalRemoteModeListener() {
        group.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean localModeRequested = event.getProperty().toString().equals(LOCAL);
                if (services.getLicenseManager().isSingleUserLicenseInstalled()) {
                    throw new RuntimeException("Illegal state: can't change local/server mode when a single-user license is installed");
                }
                services.getSettingsManager().setLocal(localModeRequested);
                progressElement.start();
            }
        });
    }

    private void setupLicenseListener() {
        services.getLicenseManager().addLicenseChangeListener(new LicenseChangeListener() {
            @Override
            public void licenseInfoUpdated() {
                forceLocalModeIfSingleUserLicenseInstalled();
            }
        });
    }

}
