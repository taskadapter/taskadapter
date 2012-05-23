package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;

public class LicenseGenerator {
    private static final String FILE_NAME_TA_WEB = "taskadapterweb.license";
    static final int DEFAULT_USERS_NUMBER = 1;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("LicenseGenerator:" +
                    "\nExpected args: customer_name email [users_number]" +
                    "\n[users_number] can be omitted, 1 will be used by default");
            return;
        }

        RequestedLicense requestedLicense = parseArgs(args);

        printInfoToConsole(requestedLicense);

        License license = createLicenseObject(requestedLicense);

        try {
            MyIOUtils.writeToFile(FILE_NAME_TA_WEB, new LicenseTextGenerator(license).generateLicenseText());
            System.out.println("\nSaved to " + FILE_NAME_TA_WEB);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printInfoToConsole(RequestedLicense requestedLicense) {
        System.out.println("Generating license for:");
        System.out.println("Customer: " + requestedLicense.getCustomerName());
        System.out.println("Email:    " + requestedLicense.getEmail());
        System.out.println("Users:    " + requestedLicense.getUsersNumber());
    }

    private static License createLicenseObject(RequestedLicense requestedLicense) {
        Calendar calendar = Calendar.getInstance();
        String createdOn = new SimpleDateFormat(LICENSE_DATE_FORMAT).format(calendar.getTime());
        calendar.add(Calendar.YEAR, 1);

        License license = new License();
        license.setProduct(LicenseManager.Product.TASK_ADAPTER_WEB);
        license.setCustomerName(requestedLicense.getCustomerName());
        license.setEmail(requestedLicense.getEmail());
        license.setUsersNumber(requestedLicense.getUsersNumber());
        license.setCreatedOn(createdOn);
        license.setExpiresOn(calendar.getTime());
        return license;
    }

    // TODO add unit tests
    static RequestedLicense parseArgs(String[] args) {
        String customerName = "";
        String email = "";
        int usersNumber = DEFAULT_USERS_NUMBER;

        int i;
        for (i = 0; i < args.length; i++) {
            if (!args[i].contains("@")) {
                customerName += args[i] + " ";
            } else {
                email = args[i];
                break;
            }
        }
        if (i < args.length-1) {
            usersNumber = Integer.parseInt(args[args.length-1]);
        }

        customerName = customerName.trim();
        return new RequestedLicense(customerName, email, usersNumber);
    }

}
