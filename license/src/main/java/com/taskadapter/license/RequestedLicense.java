package com.taskadapter.license;

public class RequestedLicense {
    private String customerName;
    private String email;
    private int usersNumber;

    public RequestedLicense(String customerName, String email, int usersNumber) {
        this.customerName = customerName;
        this.email = email;
        this.usersNumber = usersNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEmail() {
        return email;
    }

    public int getUsersNumber() {
        return usersNumber;
    }
}
