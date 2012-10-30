package com.taskadapter.license;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;
import static org.junit.Assert.assertEquals;

public class LicenseGeneratorTest {
    private SimpleDateFormat licenseDateFormatter = new SimpleDateFormat(LICENSE_DATE_FORMAT);

    @Test
    public void monthsValidParameterIsAppliedOK() {
        RequestedLicense requestedLicense = new RequestedLicense("my name", "my.mail@domain-something.com");
        requestedLicense.setMonthsValid(3);
        License license = LicenseGenerator.createLicenseObject(requestedLicense);
        Calendar shouldBeTime = Calendar.getInstance();
        shouldBeTime.add(Calendar.MONTH, 3);
        assertEquals(licenseDateFormatter.format(shouldBeTime.getTime()), licenseDateFormatter.format(license.getExpiresOn()));
    }


}
