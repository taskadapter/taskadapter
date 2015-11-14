package com.taskadapter.webui.license;

import com.taskadapter.data.DataCallback;
import com.taskadapter.license.License;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import java.text.SimpleDateFormat;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;

public final class LicensePanels {
    // TODO move to "taskadapter.properties" file
    private static final String HTTP_WWW_TASKADAPTER_COM_BUY = "http://www.taskadapter.com/buy";
    private static final String LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI = "(year-month-day)";

    /**
     * Renders a license information.
     * 
     * @param license
     *            license information.
     * @param uninstallCallback
     *            license uninstall callback.
     * @return license information UI.
     */
    public static Component renderLicense(License license,
            final Runnable uninstallCallback) {
        final GridLayout res = new GridLayout();

        res.setColumns(2);
        res.setSpacing(true);
        res.setWidth(350, Unit.PIXELS);

        res.addComponent(new Label("Registered to:"));
        res.addComponent(new Label(license.getCustomerName()));
        res.addComponent(new Label("Users number:"));
        res.addComponent(new Label(Integer.toString(license.getUsersNumber())));

        res.addComponent(new Label("License created "
                + LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI));
        res.addComponent(new Label(license.getCreatedOn()));

        res.addComponent(new Label("License expires "
                + LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI));
        final String formattedExpireDate = new SimpleDateFormat(
                LICENSE_DATE_FORMAT).format(license.getExpiresOn());

        if (license.isExpired()) {
            final Label expLabel = new Label(formattedExpireDate + " - EXPIRED");
            expLabel.addStyleName("expiredLicenseLabel");
            res.addComponent(expLabel);
        } else {
            res.addComponent(new Label(formattedExpireDate));
        }

        final Button clearLicenseButton = new Button("Remove license info");
        clearLicenseButton.addClickListener((Button.ClickListener) event -> uninstallCallback.run());
        res.addComponent(clearLicenseButton);

        return res;
    }

    /**
     * Creates a panel for the new license input.
     */
    public static Component createLicenseInputPanel(
            final DataCallback<String> newLicenseCallback) {
        final VerticalLayout res = new VerticalLayout();

        res.addComponent(new Label("NO LICENSE INSTALLED."));
        final BrowserWindowOpener opener = new BrowserWindowOpener(
                LicensePanels.HTTP_WWW_TASKADAPTER_COM_BUY);
        opener.setFeatures("height=800,width=1200,resizable");
        final Button button = new Button("Buy license");
        opener.extend(button);
        res.addComponent(button);

        final TextArea licenseArea = new TextArea(
                "Paste the complete contents of the license file here");
        licenseArea.setStyleName("license-area");
        res.addComponent(licenseArea);

        final Button saveButton = new Button("Save license");
        saveButton.addClickListener((Button.ClickListener) event -> newLicenseCallback.callBack(licenseArea.getValue()));
        res.addComponent(saveButton);

        return res;
    }
}
