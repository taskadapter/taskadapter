package com.taskadapter.license;

class RequestedLicense {
    static final int DEFAULT_USERS_NUMBER = 1;
    static final int DEFAULT_NUMBER_OF_MONTHS_VALID = 12;

    private String customerName;
    private String email;
    private int usersNumber;
    private int monthsValid;

    RequestedLicense(String customerName, String email) {
        this.customerName = customerName;
        this.email = email;
        this.usersNumber = DEFAULT_USERS_NUMBER;
        this.monthsValid = DEFAULT_NUMBER_OF_MONTHS_VALID;
    }

    String getCustomerName() {
        return customerName;
    }

    String getEmail() {
        return email;
    }

    int getUsersNumber() {
        return usersNumber;
    }

    int getMonthsValid() {
        return monthsValid;
    }

    void setUsersNumber(int usersNumber) {
        this.usersNumber = usersNumber;
    }

    void setMonthsValid(int monthsValid) {
        this.monthsValid = monthsValid;
    }
}
