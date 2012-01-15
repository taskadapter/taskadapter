package com.taskadapter;

import com.taskadapter.license.License;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.license.LicenseValidationException;

public class Startup {

    public void checkLicense() {
        License license;
        String message;
        try {
            license = LicenseManager.getTaskAdapterLicense();
            message = license.toString();
        } catch (LicenseValidationException e) {
            message = "A valid license is not found. Working in ----TRIAL MODE----";
        }
        System.out.println(message);
    }
}
