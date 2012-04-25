package com.taskadapter.webui.license;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.license.LicenseValidationException;
import com.taskadapter.web.LocalRemoteOptionsPanel;
import com.taskadapter.webui.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigureSystemPage extends Page implements LicenseChangeListener {
    private VerticalLayout layout = new VerticalLayout();

    private EnterLicensePanel enterLicensePanel;
    private LicenseInfoPanel licenseInfoPanel;

    public ConfigureSystemPage() {
    }

    private void createLocalRemoteSection() {
        layout.addComponent(new LocalRemoteOptionsPanel(services.getSettingsManager()));
        Button saveButton = new Button("Save settings");
        saveButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveSettings();
            }
        });
        layout.addComponent(saveButton);
    }

    private void saveSettings() {
        navigator.showNotification("TODO", "TODO: Save settings to DISK!");
    }

    private void createLicenseSection() {
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
