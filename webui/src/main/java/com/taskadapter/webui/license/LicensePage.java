package com.taskadapter.webui.license;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.license.LicenseValidationException;
import com.taskadapter.webui.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class LicensePage extends Page implements LicenseChangeListener {
    private VerticalLayout layout = new VerticalLayout();

    private EnterLicensePanel enterLicensePanel;
    private LicenseInfoPanel licenseInfoPanel;

    public LicensePage() {
        buildUI();
        updateFormBasingOnLicense();
    }

    private void buildUI() {
        LicenseManager.addLicenseChangeListener(this);
        enterLicensePanel = new EnterLicensePanel();
        enterLicensePanel.setVisible(false);
        layout.addComponent(enterLicensePanel);

        licenseInfoPanel = new LicenseInfoPanel();
        licenseInfoPanel.setVisible(false);
        layout.addComponent(licenseInfoPanel);
    }

    private void updateFormBasingOnLicense() {
        try {
            License license = LicenseManager.getTaskAdapterLicense();
            showRegisteredMode(license);
        } catch (LicenseValidationException e) {
            showUnregisteredMode();
        }
    }

    private void showUnregisteredMode() {
        enterLicensePanel.setVisible(true);
        licenseInfoPanel.setVisible(false);
    }

    private void showRegisteredMode(License license) {
        licenseInfoPanel.setLicense(license);
        licenseInfoPanel.setVisible(true);
        enterLicensePanel.setVisible(false);
    }

    @Override
    public String getPageTitle() {
        return "Info";
    }

    @Override
    public Component getUI() {
        return layout;
    }

    @Override
    public void licenseInfoUpdated() {
        updateFormBasingOnLicense();
    }
}
