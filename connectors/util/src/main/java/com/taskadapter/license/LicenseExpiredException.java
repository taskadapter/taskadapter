package com.taskadapter.license;

public class LicenseExpiredException extends LicenseValidationException {
    public LicenseExpiredException(String message) {
        super(message);
    }
}
