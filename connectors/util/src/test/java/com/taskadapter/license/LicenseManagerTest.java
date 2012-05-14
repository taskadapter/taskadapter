package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class LicenseManagerTest {
    @Test
    public void testValidSingleLicense() throws IOException {
        LicenseManager licenseManager = new LicenseManager();
        try {
            licenseManager.setNewLicense(getValidSingleUserLicense());
        } catch (LicenseException e) {
            fail("License is expected to be valid");
        }
        Assert.assertTrue("License is expected to be valid", licenseManager.isSomeValidLicenseInstalled());

        License license = licenseManager.getLicense();
        Assert.assertNotNull("License must not be null", license);
        Assert.assertEquals("Product type is expected to be " + LicenseManager.Product.TASK_ADAPTER_WEB, LicenseManager.Product.TASK_ADAPTER_WEB, license.getProduct());
        Assert.assertEquals("License type is expected to be " + License.Type.SINGLE.getText(), License.Type.SINGLE, license.getType());
    }

    private String getValidSingleUserLicense() throws IOException {
        return MyIOUtils.getResourceAsString("taskadapterweb.license.single");
    }

    @Test
    public void testValidMultiLicense() throws IOException {
        String validMultiUserLicense = MyIOUtils.getResourceAsString("taskadapterweb.license.multi");
        LicenseManager licenseManager = new LicenseManager();
        try {
            licenseManager.setNewLicense(validMultiUserLicense);
        } catch (LicenseException e) {
            fail("License is expected to be valid");
        }
        Assert.assertTrue("License is expected to be valid", licenseManager.isSomeValidLicenseInstalled());

        License license = licenseManager.getLicense();
        Assert.assertNotNull("License must not be null", license);
        Assert.assertEquals("Product type is expected to be " + LicenseManager.Product.TASK_ADAPTER_WEB, LicenseManager.Product.TASK_ADAPTER_WEB, license.getProduct());
        Assert.assertEquals("License type is expected to be " + License.Type.MULTI.getText(), License.Type.MULTI, license.getType());
    }

    @Test
    public void licenseBecomesInvalidAfterRemoval() throws IOException {
        LicenseManager licenseManager = new LicenseManager();
        License oldLicense = licenseManager.getLicense();
        try {
            licenseManager.setNewLicense(getValidSingleUserLicense());
        } catch (LicenseException e) {
            fail("License is expected to be valid");
        }
        assertTrue("License is expected to be valid", licenseManager.isSomeValidLicenseInstalled());
        try {
            licenseManager.removeTaskAdapterLicenseFromThisComputer();
            assertFalse("License must be INVALID after removal", licenseManager.isSomeValidLicenseInstalled());
        } finally {
            if (oldLicense != null) {
                try {
                    licenseManager.applyLicense(oldLicense);
                } catch (LicenseException e) {
                    fail("License must be valid at this point");
                }
                licenseManager.installLicense();
            }
        }

    }
}
