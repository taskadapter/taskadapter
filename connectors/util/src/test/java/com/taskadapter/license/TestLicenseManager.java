package com.taskadapter.license;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.taskadapter.util.MyIOUtils;

public class TestLicenseManager {

    @Test
    public void testValidTALicense() {
        String licenseText;
        try {
            licenseText = MyIOUtils
                    .getResourceAsString("taskadapter.license.valid");
            License license = LicenseManager.checkLicense(licenseText);
            Assert.assertNotNull("license must be not null", license);
            Assert.assertEquals("product type must be "
                    + LicenseManager.PRODUCT.TASK_ADAPTER,
                    LicenseManager.PRODUCT.TASK_ADAPTER, license.getProduct());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidTALicense() {
        String licenseText;
        try {
            licenseText = MyIOUtils
                    .getResourceAsString("taskadapter.license.invalid");
            LicenseManager.checkLicense(licenseText);
            Assert.fail("This function call must have failed with LicenseValidationException");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (LicenseValidationException e) {
            System.out.println("Got expected LicenseValidationException");
        }
    }


    @Test
    public void testValidTALicenseNewDateFormat() {
        // the new Task Adapter license date format was introduced
        // on June 30, 2011. Version 1.1.1.
        String licenseText;
        try {
            licenseText = MyIOUtils
                    .getResourceAsString("ta_new_date_format.valid");
            License license = LicenseManager.checkLicense(licenseText);
            Assert.assertNotNull("license must be not null", license);
            Assert.assertEquals("product type must be "
                    + LicenseManager.PRODUCT.TASK_ADAPTER,
                    LicenseManager.PRODUCT.TASK_ADAPTER, license.getProduct());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
