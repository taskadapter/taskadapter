package com.taskadapter.webui.license;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseManager;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

import java.text.SimpleDateFormat;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;

public class LicenseInfoPanel extends GridLayout {
    private final LicenseManager licenseManager;
    private SimpleDateFormat licenseDateFormatter = new SimpleDateFormat(LICENSE_DATE_FORMAT);
    private static final String LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI = "(year-month-day)";

    private Label registeredTo;
    private Label usersNumberLabel;
    private Label licenseCreatedOn;
    private Label licenseExpiresOn;

    public LicenseInfoPanel(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
        buildUI();
    }

    private void buildUI() {
        setColumns(2);
        setSpacing(true);
        setWidth(350, UNITS_PIXELS);

        addComponent(new Label("Registered to:"));
        registeredTo = new Label();
        addComponent(registeredTo);

        addComponent(new Label("Users number:"));
        usersNumberLabel = new Label();
        addComponent(usersNumberLabel);

        addComponent(new Label("License created " + LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI));
        licenseCreatedOn = new Label();
        addComponent(licenseCreatedOn);

        addComponent(new Label("License expires " + LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI));
        licenseExpiresOn = new Label();
        addComponent(licenseExpiresOn);

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
        usersNumberLabel.setValue(license.getUsersNumber());
        licenseCreatedOn.setValue(license.getCreatedOn());
        String formattedExpirationDateString = licenseDateFormatter.format(license.getExpiresOn());
        if (license.isExpired()) {
            licenseExpiresOn.setValue(formattedExpirationDateString + " - EXPIRED");
            licenseExpiresOn.addStyleName("expiredLicenseLabel");
        } else {
            licenseExpiresOn.removeStyleName("expiredLicenseLabel");
            licenseExpiresOn.setValue(formattedExpirationDateString);
        }
    }

    private void clearLicenseInfo() {
        licenseManager.removeTaskAdapterLicenseFromConfigFolder();
        getWindow().showNotification("Removed the license info");
    }
}
