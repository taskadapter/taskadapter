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

        addDebugButtons();
    }

    // TODO delete before the release!
    private void addDebugButtons() {
        Button saveSingleUserLicenseButton = new Button("DEBUG: 1-user license");
        saveSingleUserLicenseButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                licenseArea.setValue("Product: TASK_ADAPTER_WEB\n" +
                        "License type: local / single user\n" +
                        "Registered to: TA-testing\n" +
                        "Email: nomail@nodomain.com\n" +
                        "Date: 2012-05-10\n" +
                        "-------------- Key --------------\n" +
                        "SBVbU19OeX8AE01fWEFaTUFWHAIeXg5GS0BNFF9cXAM3Nh86TVdQSxdZW11ANTAeSBMJBglKDR8CXg==");
                save();
            }
        });
        addComponent(saveSingleUserLicenseButton);

        Button saveServerLicenseButton = new Button("DEBUG: server license");
        saveServerLicenseButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                licenseArea.setValue("Product: TASK_ADAPTER_WEB\n" +
                        "License type: server / many users\n" +
                        "Registered to: Alex\n" +
                        "Email: my@mail\n" +
                        "Date: 2012-05-10\n" +
                        "-------------- Key --------------\n" +
                        "Vx9KRFYcdnBTF0JWTQQPS1dBHRczFgJOQXRJG1FeAV5nbV5KFhUFFA==");
                save();
            }
        });
        addComponent(saveServerLicenseButton);
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
