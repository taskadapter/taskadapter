package com.taskadapter.webui.license;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.Panel;

public class LicensePanel extends Panel implements LicenseChangeListener {

    private EnterLicensePanel enterLicensePanel;
    private LicenseInfoPanel licenseInfoPanel;
    private Services services;

    public LicensePanel(Services services) {
        super("License Information");
        this.services = services;
        buildUI();
        services.getLicenseManager().addLicenseChangeListener(this);
        updateFormBasingOnLicense();
    }

    private void buildUI() {
        addStyleName("panelexample");
        enterLicensePanel = new EnterLicensePanel(services);
        enterLicensePanel.setVisible(false);
        addComponent(enterLicensePanel);

        licenseInfoPanel = new LicenseInfoPanel(services);
        licenseInfoPanel.setVisible(false);
        addComponent(licenseInfoPanel);
    }

    @Override
    public void licenseInfoUpdated() {
        updateFormBasingOnLicense();
    }

    private void updateFormBasingOnLicense() {
        if (services.getLicenseManager().isSomeLicenseInstalled()) {
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
        enterLicensePanel.clearLicenseTextArea();
        licenseInfoPanel.setVisible(false);
    }

}
