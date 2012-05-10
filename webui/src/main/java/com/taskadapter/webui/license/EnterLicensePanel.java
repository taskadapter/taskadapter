package com.taskadapter.webui.license;

import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class EnterLicensePanel extends VerticalLayout {
    private TextArea licenseArea;
    private Services services;

    public EnterLicensePanel(Services services) {
        this.services = services;
        buildUI();
    }

    private void buildUI() {
        addComponent(new Label("UNREGISTERED"));

        licenseArea = new TextArea("Paste the complete contents of the license file here");
        licenseArea.setStyleName("license-area");

        addComponent(licenseArea);

        Button saveButton = new Button("Save license");
        saveButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
            }
        });
        addComponent(saveButton);
    }

    private boolean save() {
        String licenseText = (String) licenseArea.getValue();

        LicenseManager licenseManager = services.getLicenseManager();
        licenseManager.setNewLicense(licenseText.trim());

        if(licenseManager.isTaskAdapterLicenseOk()) {
            licenseManager.installLicense();
            getWindow().showNotification("Successfully registered to: " + licenseManager.getLicense().getCustomerName());

        } else {
            getWindow().showNotification("License validation error", "The license text is invalid");
        }

        return licenseManager.isTaskAdapterLicenseOk();
    }
}
