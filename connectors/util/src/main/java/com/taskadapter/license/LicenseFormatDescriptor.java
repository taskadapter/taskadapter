package com.taskadapter.license;

abstract class LicenseFormatDescriptor {
    static final String PREFIX_PRODUCT = "Product: ";
    static final String PREFIX_LICENSE_TYPE = "License type: ";
    static final String PREFIX_REGISTERED_TO = "Registered to: ";
    static final String PREFIX_EMAIL = "Email: ";
    static final String PREFIX_DATE = "Date: ";

    static final int LINE_PRODUCT = 0;
    static final int LINE_LICENSE_TYPE = 1;
    static final int LINE_CUSTOMER_NAME = 2;
    static final int LINE_EMAIL = 3;
    static final int LINE_DATE = 4;
    static final int LINE_KEY = 6;

    static final String LICENSE_DATE_FORMAT = "yyyy-MM-dd";
    // TODO this is not very secure, but should be OK for the prototype
    static final String PASSWORD = "$z823nV_sz#84";
}
