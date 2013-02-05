package com.taskadapter.webui.license;

import com.taskadapter.license.LicenseException;
import com.taskadapter.license.LicenseExpiredException;
import com.taskadapter.license.LicenseManager;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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
        BrowserWindowOpener opener = new BrowserWindowOpener(HTTP_WWW_TASKADAPTER_COM_BUY);
        opener.setFeatures("height=800,width=1200,resizable");
        Button button = new Button("Buy license");
        opener.extend(button);
        addComponent(button);

        licenseArea = new TextArea("Paste the complete contents of the license file here");
        licenseArea.setStyleName("license-area");

        addComponent(licenseArea);

        Button saveButton = new Button("Save license");
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
            }
        });
        addComponent(saveButton);
    }

    private boolean save() {
        String licenseText = licenseArea.getValue();

        try {
            licenseManager.setNewLicense(licenseText.trim());
            licenseManager.copyLicenseToConfigFolder();
            Notification.show("Successfully registered to: " + licenseManager.getLicense().getCustomerName());
        } catch (LicenseExpiredException e) {
            Notification.show("License not accepted", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        } catch (LicenseException e) {
            Notification.show("License not accepted", "The license is invalid", Notification.Type.ERROR_MESSAGE);
        }

        return licenseManager.isSomeValidLicenseInstalled();
    }

    public void clearLicenseTextArea() {
        licenseArea.setValue("");
    }
}
