package com.taskadapter.webui.license;

import com.taskadapter.license.LicenseException;
import com.taskadapter.license.LicenseExpiredException;
import com.taskadapter.license.LicenseManager;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class EnterLicensePanel extends VerticalLayout {
    // TODO move to "taskadapter.properties" file
    private static final String HTTP_WWW_TASKADAPTER_COM_BUY = "http://www.taskadapter.com/buy";
    private final LicenseManager licenseManager;
    private TextArea licenseArea;

    public EnterLicensePanel(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
        buildUI();
    }

    private void buildUI() {
        addComponent(new Label("NO LICENSE INSTALLED."));
        Button buyLink = new Button("Buy license");
        buyLink.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().open(new ExternalResource(HTTP_WWW_TASKADAPTER_COM_BUY), "_blank");
            }
        });
        addComponent(buyLink);

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

        try {
            licenseManager.setNewLicense(licenseText.trim());
            licenseManager.copyLicenseToConfigFolder();
            getWindow().showNotification("Successfully registered to: " + licenseManager.getLicense().getCustomerName());
        } catch (LicenseExpiredException e) {
            getWindow().showNotification("License not accepted", e.getMessage());
        } catch (LicenseException e) {
            getWindow().showNotification("License not accepted", "The license is invalid");
        }

        return licenseManager.isSomeValidLicenseInstalled();
    }

    public void clearLicenseTextArea() {
        licenseArea.setValue("");
    }
}
