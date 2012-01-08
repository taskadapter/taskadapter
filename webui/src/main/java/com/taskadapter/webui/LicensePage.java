package com.taskadapter.webui;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.license.LicenseValidationException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class LicensePage extends Page {
    private TextArea licenseArea;
    private Label currentLicenseInfo;

    public LicensePage() {
        buildUI();
        setDataToForm();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        currentLicenseInfo = new Label();
        layout.addComponent(currentLicenseInfo);
        licenseArea = new TextArea("Paste the complete contents of the license file here");

        layout.addComponent(licenseArea);
        Button saveButton = new Button("Save");
        saveButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
            }
        });

        layout.addComponent(saveButton);
        setCompositionRoot(layout);
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
            getWindow().showNotification("Successfully registered to: " + license.getCustomerName()
                    + "\nPlease, RESTART the application.");
            allOK = true;
        } catch (LicenseValidationException e) {
            getWindow().showNotification("License validation error",
                    "The license text is invalid");
        }
        return allOK;
    }


    private void setDataToForm() {
        String currentLicense;
        try {
            License l = LicenseManager.getTaskAdapterLicense();
            currentLicense = "Registered to: " + l.getCustomerName();
        } catch (LicenseValidationException e) {
            currentLicense = " UNREGISTERED";
        }
        currentLicenseInfo.setValue(currentLicense);
    }

    @Override
    public String getNavigationPanelTitle() {
        return "Info";
    }
}
