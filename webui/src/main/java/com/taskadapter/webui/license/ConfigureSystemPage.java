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
public class ConfigureSystemPage extends Page implements LicenseChangeListener {
    private VerticalLayout layout = new VerticalLayout();

    private EnterLicensePanel enterLicensePanel;
    private LicenseInfoPanel licenseInfoPanel;

    private void createLocalRemoteSection() {
        layout.addComponent(new LocalRemoteOptionsPanel(services.getSettingsManager()));
        layout.addComponent(new Label("(Changes are saved automatically)"));
    }

    private void createLicenseSection() {
        services.getLicenseManager().addLicenseChangeListener(this);

        enterLicensePanel = new EnterLicensePanel(services);
        enterLicensePanel.setVisible(false);
        layout.addComponent(enterLicensePanel);

        licenseInfoPanel = new LicenseInfoPanel(services);
        licenseInfoPanel.setVisible(false);
        layout.addComponent(licenseInfoPanel);
    }

    private void updateFormBasingOnLicense() {
        if(services.getLicenseManager().isTaskAdapterLicenseOk()) {
            showRegisteredMode(services.getLicenseManager().getLicense());
        } else {
            showUnregisteredMode();
        }
    }

    private void showRegisteredMode(License license) {
        licenseInfoPanel.setLicense(license);
        licenseInfoPanel.setVisible(true);
        enterLicensePanel.setVisible(false);
    }

    private void showUnregisteredMode() {
        enterLicensePanel.setVisible(true);
        licenseInfoPanel.setVisible(false);
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
        updateFormBasingOnLicense();
        return layout;
    }

    @Override
    public void licenseInfoUpdated() {
        updateFormBasingOnLicense();
    }
}
