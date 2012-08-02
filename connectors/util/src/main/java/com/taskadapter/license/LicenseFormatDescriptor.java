package com.taskadapter.license;

public abstract class LicenseFormatDescriptor {
    static final String PREFIX_PRODUCT = "Product: ";
    static final String PREFIX_USERS_NUMBER = "Users number: ";
    static final String PREFIX_REGISTERED_TO = "Registered to: ";
    static final String PREFIX_EMAIL = "Email: ";
    static final String PREFIX_CREATED_ON = "Created on: ";
    static final String PREFIX_EXPIRES_ON = "Expires on: ";

    static final int LINE_PRODUCT = 0;
    static final int LINE_USERS_NUMBER = 1;
    static final int LINE_CUSTOMER_NAME = 2;
    static final int LINE_EMAIL = 3;
    static final int LINE_CREATED_ON_DATE = 4;
    static final int LINE_EXPIRES_ON_DATE = 5;
    static final int LINE_KEY = 7;

    public static final String LICENSE_DATE_FORMAT = "yyyy-MM-dd";
}
