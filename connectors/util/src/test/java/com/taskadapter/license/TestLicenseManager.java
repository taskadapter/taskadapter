package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.taskadapter.license.LicenseManager.Product.TASK_ADAPTER;
import static com.taskadapter.license.LicenseManager.checkLicense;

public class TestLicenseManager {
    @Test
    public void testValidTALicense() {
        try {
            License license = checkLicense(MyIOUtils.getResourceAsString("taskadapter.license.valid"));

            Assert.assertNotNull("license must not be null", license);
            Assert.assertEquals("product type must be " + TASK_ADAPTER, TASK_ADAPTER, license.getProduct());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidTALicense() {
        try {
            checkLicense(MyIOUtils.getResourceAsString("taskadapter.license.invalid"));
            Assert.fail("This function call must have failed with LicenseValidationException");

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());

        } catch (LicenseValidationException e) {
            System.out.println("Got expected LicenseValidationException");
        }
    }
}
