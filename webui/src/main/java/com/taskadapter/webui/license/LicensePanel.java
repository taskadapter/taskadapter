package com.taskadapter.webui.license;

import com.taskadapter.data.DataCallback;
import com.taskadapter.license.License;
import com.taskadapter.license.LicenseException;
import com.taskadapter.license.LicenseExpiredException;
import com.taskadapter.webui.EventTracker;
import com.taskadapter.webui.LicenseCategory$;
import com.taskadapter.webui.TALog;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;

public final class LicensePanel {
    private static final String HTTP_WWW_TASKADAPTER_COM_BUY = "http://www.taskadapter.com/buy";

    // TODO move to "taskadapter.properties" file
    private static final String LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI = "(year-month-day)";

    private Logger log = TALog.log;

    private final LicenseFacade licenseManager;
    private final VerticalLayout contentPane;

    private LicensePanel(LicenseFacade licenseManager) {
        this.licenseManager = licenseManager;

        contentPane = new VerticalLayout();
        contentPane.setMargin(true);

        showLicense(licenseManager.getLicense().get());
    }

    private void applyNewLicense(String licenseText) {
        try {
            licenseManager.install(licenseText);

            final License newLicense = licenseManager.getLicense().get();
            showLicense(newLicense);
            if (newLicense != null) {
                String text = "Successfully registered to: " + newLicense.getCustomerName();
                log.info(text);
                EventTracker.trackEvent(LicenseCategory$.MODULE$, "install", "success_valid");
                Notification.show(text)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        } catch (LicenseExpiredException e) {
            EventTracker.trackEvent(LicenseCategory$.MODULE$, "install", "failed_expired");
            log.error("Cannot install license: it is expired. License text: " + licenseText);
            Notification.show("License not accepted: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (LicenseException e) {
            EventTracker.trackEvent(LicenseCategory$.MODULE$, "install", "failed_validation");
            log.error("Cannot install license because of exception. "
                    + e.toString() + "\nLicense text:\n" + licenseText);
            Notification.show("License not accepted: the license is invalid")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Uninstalls a license.
     */
    private void uninstallLicense() {
        licenseManager.uninstall();
        String string = "Removed license info";
        log.info(string);
        EventTracker.trackEvent(LicenseCategory$.MODULE$, "removed", "");
        Notification.show(string);
    }

    private void showLicense(License license) {
        contentPane.removeAll();
        contentPane.add(new Html("<b>License Information</b>"));
        if (license != null) {
            contentPane.add(renderLicense(license,
                    this::uninstallLicense));
        } else {
            contentPane.add(createLicenseInputPanel(this::applyNewLicense));
        }
    }

    public static Component renderLicensePanel(LicenseFacade licenseFacade) {
        return new LicensePanel(licenseFacade).contentPane;
    }

    /**
     * Creates a panel for the new license input.
     */
    private static Component createLicenseInputPanel(
            final DataCallback<String> newLicenseCallback) {
        final VerticalLayout res = new VerticalLayout();

        res.add(new Label("NO LICENSE INSTALLED."));
        final Button button = new Button("Buy license", new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                UI.getCurrent().getPage().executeJs("window.open('" + HTTP_WWW_TASKADAPTER_COM_BUY + "');");
            }
        });

        res.add(button);

        final TextArea licenseArea = new TextArea(
                "Paste the complete contents of the license file here");
        licenseArea.setClassName("license-area");
        res.add(licenseArea);

        final Button saveButton = new Button("Save license");
        saveButton.addClickListener(event -> newLicenseCallback.callBack(licenseArea.getValue()));
        res.add(saveButton);

        return res;
    }

    /**
     * Renders a license information.
     *
     * @param license               license information.
     * @param removeLicenseCallback license uninstall callback.
     * @return license information UI.
     */
    private static Component renderLicense(License license,
                                           final Runnable removeLicenseCallback) {
        FormLayout res = new FormLayout();

        res.add(new Label("Registered to:"));
        res.add(new Label(license.getCustomerName()));
        res.add(new Label("Users number:"));
        res.add(new Label(Integer.toString(license.getUsersNumber())));

        res.add(new Label("License created "
                + LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI));
        res.add(new Label(license.getCreatedOn()));

        res.add(new Label("License expires "
                + LICENSE_DATE_FORMAT_DESCRIPTION_FOR_GUI));
        final String formattedExpireDate = new SimpleDateFormat(
                LICENSE_DATE_FORMAT).format(license.getExpiresOn());

        if (license.isExpired()) {
            final Label expLabel = new Label(formattedExpireDate + " - EXPIRED");
            expLabel.addClassName("expiredLicenseLabel");
            res.add(expLabel);
        } else {
            res.add(new Label(formattedExpireDate));
        }

        Button removeLicenseButton = new Button("Remove license info", event -> removeLicenseCallback.run());

        VerticalLayout layout = new VerticalLayout();
        layout.add(res);
        layout.add(removeLicenseButton);

        return layout;
    }
}
