package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.junit.Test;

import java.io.IOException;

public class LicenseParserTest {
    @Test (expected = LicenseExpiredException.class)
    public void testCheckLicense() throws IOException, LicenseValidationException {
        String expiredLicenseText = MyIOUtils.getResourceAsString("taskadapterweb.license.expired");

        new LicenseParser().checkLicense(expiredLicenseText);
    }
}
