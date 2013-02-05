package com.taskadapter.webui.license;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.license.LicenseManager;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class LicensePanel extends Panel implements LicenseChangeListener {

    private final LicenseManager licenseManager;
    private EnterLicensePanel enterLicensePanel;
    private LicenseInfoPanel licenseInfoPanel;

    public LicensePanel(LicenseManager licenseManager) {
        super("License Information");
        this.licenseManager = licenseManager;
        buildUI();
        licenseManager.addLicenseChangeListener(this);
        updateFormBasingOnLicense();
    }

    private void buildUI() {
        enterLicensePanel = new EnterLicensePanel(licenseManager);
        enterLicensePanel.setVisible(false);

        licenseInfoPanel = new LicenseInfoPanel(licenseManager);
        licenseInfoPanel.setVisible(false);

        VerticalLayout view = new VerticalLayout();
        view.addComponent(enterLicensePanel);
        view.addComponent(licenseInfoPanel);
        view.setMargin(true);
        setContent(view);
    }

    @Override
    public void licenseInfoUpdated() {
        updateFormBasingOnLicense();
    }

    private void updateFormBasingOnLicense() {
        if (licenseManager.isSomeLicenseInstalled()) {
            showRegisteredMode(licenseManager.getLicense());
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
