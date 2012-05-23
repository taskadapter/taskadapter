package com.taskadapter.license;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LicenseGeneratorTest {
    @Test
    public void defaultNumberOfUsersIsSetWhenNotProvidedInArgs() {
        RequestedLicense requestedLicense = LicenseGenerator.parseArgs("De Smedt Johannes J.Desmedt@televic.com".split(" "));
        assertEquals(LicenseGenerator.DEFAULT_USERS_NUMBER, requestedLicense.getUsersNumber());
    }

    @Test
    public void fullNameIsParsedWithoutQuotes() {
        RequestedLicense requestedLicense = LicenseGenerator.parseArgs("De Smedt Johannes J.Desmedt@televic.com".split(" "));
        assertEquals("De Smedt Johannes", requestedLicense.getCustomerName());
    }

    @Test
    public void emailIsParsed() {
        RequestedLicense requestedLicense = LicenseGenerator.parseArgs("De Smedt Johannes J.Desmedt@televic.com".split(" "));
        assertEquals("J.Desmedt@televic.com", requestedLicense.getEmail());
    }

    @Test
    public void numberOfUsersParsed() {
        RequestedLicense requestedLicense = LicenseGenerator.parseArgs("De Smedt Johannes J.Desmedt@televic.com 4".split(" "));
        assertEquals(4, requestedLicense.getUsersNumber());
    }


}
