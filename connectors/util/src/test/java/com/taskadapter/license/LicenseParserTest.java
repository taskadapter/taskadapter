package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.junit.Test;

import java.io.IOException;

public class LicenseParserTest {
    @Test
    public void expiredLicenseParsedOK() throws IOException, LicenseParseException {
        String expiredLicenseText = MyIOUtils.getResourceAsString("taskadapterweb.license.expired");
        new LicenseParser().parseLicense(expiredLicenseText);
    }
}
