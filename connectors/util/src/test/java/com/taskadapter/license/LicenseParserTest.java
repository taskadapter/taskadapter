package com.taskadapter.license;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;

public class LicenseParserTest {
    @Test
    public void expiredLicenseParsedOK() throws IOException, LicenseParseException {
        String expiredLicenseText = Resources.toString(Resources.getResource("taskadapterweb.license.expired"), Charsets.UTF_8);
        new LicenseParser().parseLicense(expiredLicenseText);
    }
}
