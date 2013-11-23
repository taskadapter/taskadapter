package com.taskadapter.webui.license;

import com.taskadapter.data.DataCallback;
import com.taskadapter.license.License;
import com.taskadapter.license.LicenseException;
import com.taskadapter.license.LicenseExpiredException;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public final class LicensePanel {

    private final LicenseFacade licenseManager;
    private final VerticalLayout contentPane;
    private final Panel ui;

    private LicensePanel(LicenseFacade licenseManager) {
        this.licenseManager = licenseManager;

        ui = new Panel("License Information");

        contentPane = new VerticalLayout();
        contentPane.setMargin(true);
        ui.setContent(contentPane);

        showLicense(licenseManager.getLicense().get());
    }

    /**
     * Applies a new license.
     * 
     * @param licenseText
     *            license text.
     */
    private void applyNewLicense(String licenseText) {
        try {
            licenseManager.install(licenseText);

            final License newLicense = licenseManager.getLicense().get();
            showLicense(newLicense);
            if (newLicense != null) {
                Notification.show("Successfully registered to: "
                        + newLicense.getCustomerName());
            }
        } catch (LicenseExpiredException e) {
            Notification.show("License not accepted", e.getMessage(),
                    Notification.Type.ERROR_MESSAGE);
        } catch (LicenseException e) {
            Notification.show("License not accepted", "The license is invalid",
                    Notification.Type.ERROR_MESSAGE);
        }
    }

    /**
     * Uninstalls a license.
     */
    private void uninstallLicense() {
        licenseManager.uninstall();
        Notification.show("Removed the license info");
    }

    /**
     * Shows a license.
     * 
     * @param license
     *            license to show.
     */
    private void showLicense(License license) {
        contentPane.removeAllComponents();
        if (license != null) {
            contentPane.addComponent(LicensePanels.renderLicense(license,
                    new Runnable() {
                        @Override
                        public void run() {
                            uninstallLicense();
                        }
                    }));
        } else {
            contentPane.addComponent(LicensePanels
                    .createLicenseInputPanel(new DataCallback<String>() {
                        @Override
                        public void callBack(String data) {
                            applyNewLicense(data);
                        }
                    }));
        }
    }

    /**
     * Renders a new license panel for the model.
     * 
     * @param model
     *            model to render a panel for.
     * @return license UI.
     */
    public static Component renderLicensePanel(LicenseFacade model) {
        return new LicensePanel(model).ui;
    }
}
