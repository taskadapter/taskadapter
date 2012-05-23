package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;

public class LicenseGenerator {
    private static final String FILE_NAME_TA_WEB = "taskadapterweb.license";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("LicenseGenerator:" +
                    "\nExpected args: customer_name email [users_number number_of_months_valid]" +
                    "\nIf users_number and months are not provided, " + RequestedLicense.DEFAULT_USERS_NUMBER +
                    " and " + RequestedLicense.DEFAULT_NUMBER_OF_MONTHS_VALID +
                    " will be used.");
            return;
        }

        RequestedLicense requestedLicense = new ArgumentsParser().parseArgs(args);

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
        System.out.println("Customer:      " + requestedLicense.getCustomerName());
        System.out.println("Email:         " + requestedLicense.getEmail());
        System.out.println("Users:         " + requestedLicense.getUsersNumber());
        System.out.println("Months valid : " + requestedLicense.getMonthsValid());
    }

    static License createLicenseObject(RequestedLicense requestedLicense) {
        Calendar calendar = Calendar.getInstance();
        String createdOn = new SimpleDateFormat(LICENSE_DATE_FORMAT).format(calendar.getTime());
        calendar.add(Calendar.MONTH, requestedLicense.getMonthsValid());

        License license = new License();
        license.setProduct(LicenseManager.Product.TASK_ADAPTER_WEB);
        license.setCustomerName(requestedLicense.getCustomerName());
        license.setEmail(requestedLicense.getEmail());
        license.setUsersNumber(requestedLicense.getUsersNumber());
        license.setCreatedOn(createdOn);
        license.setExpiresOn(calendar.getTime());
        return license;
    }

}
