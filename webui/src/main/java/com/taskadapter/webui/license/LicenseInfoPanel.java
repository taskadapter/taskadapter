package com.taskadapter.webui.license;

import com.taskadapter.license.License;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class LicenseInfoPanel extends Panel {
    private Label registeredTo;
    private Label licenseType;
    private Label licenseCreatedOn;
    private Label licenseExpiresOn;
    private Services services;

    public LicenseInfoPanel(Services services) {
        super("License Information");
        this.services = services;
        buildUI();
    }

    private void buildUI() {
        addStyleName("panelexample");

        GridLayout layout = new GridLayout();
        layout.setColumns(2);
        layout.setSpacing(true);
        layout.setWidth(350, UNITS_PIXELS);

        layout.addComponent(new Label("Registered to:"));
        registeredTo = new Label();
        layout.addComponent(registeredTo);

        layout.addComponent(new Label("Type:"));
        licenseType = new Label();
        layout.addComponent(licenseType);

        layout.addComponent(new Label("License created on:"));
        licenseCreatedOn = new Label();
        layout.addComponent(licenseCreatedOn);

        layout.addComponent(new Label("License expires on: "));
        licenseExpiresOn = new Label();
        layout.addComponent(licenseExpiresOn);
        addComponent(layout);

        Button clearLicenseButton = new Button("Remove license info");
        clearLicenseButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                clearLicenseInfo();
            }
        });
        addComponent(clearLicenseButton);
    }

    public void setLicense(License license) {
        registeredTo.setValue(license.getCustomerName());
        licenseType.setValue(license.getType().getText());
        licenseCreatedOn.setValue(license.getCreatedOn());
        licenseExpiresOn.setValue("NEVER");
    }

    private void clearLicenseInfo() {
        services.getLicenseManager().removeTaskAdapterLicenseFromThisComputer();
        getWindow().showNotification("Removed the license info");
    }
}
