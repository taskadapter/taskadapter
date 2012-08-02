package com.taskadapter.license;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArgumentsParserTest {
    @Test
    public void nameInQuotesParsed() {
        RequestedLicense requestedLicense = new ArgumentsParser().parseArgs("\"De Smedt Johannes\" J.Desmedt@televic.com".split(" "));
        assertEquals("De Smedt Johannes", requestedLicense.getCustomerName());
    }

    @Test
    public void defaultNumberOfUsersIsSetWhenNotProvidedInArgs() {
        RequestedLicense requestedLicense = new ArgumentsParser().parseArgs("De Smedt Johannes J.Desmedt@televic.com".split(" "));
        assertEquals(RequestedLicense.DEFAULT_USERS_NUMBER, requestedLicense.getUsersNumber());
    }

    @Test
    public void fullNameIsParsedWithoutQuotes() {
        RequestedLicense requestedLicense = new ArgumentsParser().parseArgs("De Smedt Johannes J.Desmedt@televic.com".split(" "));
        assertEquals("De Smedt Johannes", requestedLicense.getCustomerName());
    }

    @Test
    public void emailIsParsed() {
        RequestedLicense requestedLicense = new ArgumentsParser().parseArgs("De Smedt Johannes J.Desmedt@televic.com".split(" "));
        assertEquals("J.Desmedt@televic.com", requestedLicense.getEmail());
    }

    @Test
    public void numberOfUsersParsedWhenNoMonthsProvided() {
        RequestedLicense requestedLicense = new ArgumentsParser().parseArgs("De Smedt Johannes J.Desmedt@televic.com 4".split(" "));
        assertEquals(4, requestedLicense.getUsersNumber());
    }

    @Test
    public void numberOfUsersParsedWhenMonthsProvided() {
        RequestedLicense requestedLicense = new ArgumentsParser().parseArgs("De Smedt Johannes J.Desmedt@televic.com 4 12".split(" "));
        assertEquals(4, requestedLicense.getUsersNumber());
    }

    @Test
    public void numberOfMonthsParsed() {
        RequestedLicense requestedLicense = new ArgumentsParser().parseArgs("De Smedt Johannes J.Desmedt@televic.com 7 6".split(" "));
        assertEquals(6, requestedLicense.getMonthsValid());
    }

    @Test
    public void nameWithoutQuotesIsNotModifiedByStripQuotesIfNeeded() {
        assertEquals("my name", new ArgumentsParser().stripQuotesIfNeeded("my name"));
    }

    @Test
    public void quotesAreRemovedFromNameByStripQuotesIfNeeded() {
        assertEquals("my name", new ArgumentsParser().stripQuotesIfNeeded("\"my name\""));
    }
}
