package com.taskadapter.license;

import com.taskadapter.license.LicenseManager.Product;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;

public class License {
    private final SimpleDateFormat licenseDateFormatter = new SimpleDateFormat(LICENSE_DATE_FORMAT);

    private Product product;
    private String customerName;
    private String email;
    private int usersNumber;
    private String createdOn;
    private Date expiresOn;

    public Product getProduct() {
        return product;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public int getUsersNumber() {
        return usersNumber;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsersNumber(int usersNumber) {
        this.usersNumber = usersNumber;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

    @Override
    public String toString() {
        return "License{" +
                "product='" + product + '\'' +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", usersNumber='" + usersNumber + '\'' +
                ", createdOn='" + createdOn +
                ", expiresOn='" + expiresOn +
                "'}";
    }

    public void validate() throws LicenseException {
        if (isExpired()) {
            throw new LicenseExpiredException("This license has expired. Today is " + licenseDateFormatter.format(new Date())
                    + " and the license expiration date is " + licenseDateFormatter.format(expiresOn)
                    + " (Date format is " + LICENSE_DATE_FORMAT + ")");
        }
    }

    public boolean isExpired() {
        Calendar expirationDateCalendar = Calendar.getInstance();
        expirationDateCalendar.setTime(expiresOn);
        Calendar now = Calendar.getInstance();
        return now.after(expirationDateCalendar);
    }
}
