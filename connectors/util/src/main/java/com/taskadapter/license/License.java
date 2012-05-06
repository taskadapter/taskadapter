package com.taskadapter.license;

import com.taskadapter.license.LicenseManager.Product;

public class License {
    private Product product;
    private String customerName;
    private String email;
    private String createdOn;
    private String completeText;

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

    public String getCompleteText() {
        return completeText;
    }

    public License(Product product, String customerName, String email, String createdOn, String completeText) {
        super();
        this.customerName = customerName;
        this.email = email;
        this.createdOn = createdOn;
        this.product = product;
        this.completeText = completeText;
    }

    @Override
    public String toString() {
        return "License{" +
                "product='" + product + '\'' +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", createdOn='" + createdOn +
                "'}";
    }
}
