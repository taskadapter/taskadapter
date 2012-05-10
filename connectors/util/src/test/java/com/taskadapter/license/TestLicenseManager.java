package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


public class TestLicenseManager {
    @Test
    public void testValidSingleLicense() {
        try {
            LicenseManager licenseManager = new LicenseManager(MyIOUtils.getResourceAsString("taskadapterweb.license.single"));
            Assert.assertTrue("License is expected to be valid", licenseManager.isTaskAdapterLicenseOk());

            License license = licenseManager.getLicense();
            Assert.assertNotNull("License must not be null", license);
            Assert.assertEquals("Product type is expected to be " + LicenseManager.Product.TASK_ADAPTER_WEB, LicenseManager.Product.TASK_ADAPTER_WEB, license.getProduct());
            Assert.assertEquals("License type is expected to be " + License.Type.SINGLE.getText(), License.Type.SINGLE, license.getType());

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testValidMultiLicense() {
        try {
            LicenseManager licenseManager = new LicenseManager(MyIOUtils.getResourceAsString("taskadapterweb.license.multi"));
            Assert.assertTrue("License is expected to be valid", licenseManager.isTaskAdapterLicenseOk());

            License license = licenseManager.getLicense();
            Assert.assertNotNull("License must not be null", license);
            Assert.assertEquals("Product type is expected to be " + LicenseManager.Product.TASK_ADAPTER_WEB, LicenseManager.Product.TASK_ADAPTER_WEB, license.getProduct());
            Assert.assertEquals("License type is expected to be " + License.Type.MULTI.getText(), License.Type.MULTI, license.getType());

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidLicense() {
        try {
            LicenseManager licenseManager = new LicenseManager(MyIOUtils.getResourceAsString("taskadapter.license.invalid"));
            Assert.assertFalse("License is expected to be invalid", licenseManager.isTaskAdapterLicenseOk());

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
