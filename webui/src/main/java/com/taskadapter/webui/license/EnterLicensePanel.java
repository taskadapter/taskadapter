package com.taskadapter.webui.license;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.license.LicenseValidationException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class EnterLicensePanel extends VerticalLayout {
    private TextArea licenseArea;

    public EnterLicensePanel() {
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
        licenseText = licenseText.trim();

        License license;
        boolean allOK = false;
        try {
            license = LicenseManager.checkLicense(licenseText);
            LicenseManager.installLicense(LicenseManager.PRODUCT.TASK_ADAPTER,
                    licenseText);
            getWindow().showNotification("Successfully registered to: " + license.getCustomerName());
            allOK = true;
        } catch (LicenseValidationException e) {
            getWindow().showNotification("License validation error",
                    "The license text is invalid");
        }
        return allOK;
    }

}
