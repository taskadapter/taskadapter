package com.taskadapter.webui.license;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.web.LocalRemoteOptionsPanel;
import com.taskadapter.webui.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigureSystemPage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    private void createLocalRemoteSection() {
        layout.addComponent(new LocalRemoteOptionsPanel(services.getSettingsManager()));
    }

    private void createLicenseSection() {
        layout.addComponent(new LicensePanel(services));
    }

    @Override
    public String getPageTitle() {
        return "System configuration";
    }

    @Override
    public Component getUI() {
        layout.removeAllComponents();
        layout.setSpacing(true);

        createLocalRemoteSection();
        createLicenseSection();
        return layout;
    }
}
