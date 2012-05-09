package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


public class TestLicenseManager {
    @Test
    public void testValidTALicense() {
        try {
            License license = LicenseManager.checkLicense(MyIOUtils.getResourceAsString("taskadapter.license.valid"));

            Assert.assertNotNull("license must not be null", license);
            Assert.assertEquals("product type must be " + LicenseManager.Product.TASK_ADAPTER, LicenseManager.Product.TASK_ADAPTER, license.getProduct());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidTALicense() {
        try {
            LicenseManager.checkLicense(MyIOUtils.getResourceAsString("taskadapter.license.invalid"));
            Assert.fail("This function call must have failed with LicenseValidationException");

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());

        } catch (LicenseValidationException e) {
            System.out.println("Got expected LicenseValidationException");
        }
    }
}
