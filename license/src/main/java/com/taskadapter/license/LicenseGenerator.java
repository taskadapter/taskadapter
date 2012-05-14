package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;

public class LicenseGenerator {
    private static final String FILE_NAME_TA_WEB = "taskadapterweb.license";

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println(
                    "LicenseGenerator:\n" +
                            "  Expected args: license_type customer_name email\n\n" +
                            "license_type: \n" +
                            "  " + License.Type.SINGLE.getCode() + " - " + License.Type.SINGLE.getText() + "\n" +
                            "  " + License.Type.MULTI.getCode() + " - " + License.Type.MULTI.getText() + "\n"
            );
            return;
        }

        License.Type licenseType = License.Type.getByCode(args[0]);

        String customerName = "";
        String email = "";

        for (int i = 1; i < args.length; i++) {
            if (!args[i].contains("@")) {
                customerName += args[i] + " ";
            } else {
                email = args[i];
            }
        }

        customerName = customerName.trim();

        System.out.println("Generating license (" + licenseType.getText() + ") for:");
        System.out.println("Customer: " + customerName);
        System.out.println("Email:    " + email);

        Calendar calendar = Calendar.getInstance();
        String createdOn = new SimpleDateFormat(LICENSE_DATE_FORMAT).format(calendar.getTime());
        calendar.add(Calendar.MONDAY, 1);

        License license = new License(LicenseManager.Product.TASK_ADAPTER_WEB, licenseType, customerName, email, createdOn, calendar.getTime());

        try {
            MyIOUtils.writeToFile(FILE_NAME_TA_WEB, new LicenseTextGenerator(license).generateLicenseText());
            System.out.println("\nSaved to " + FILE_NAME_TA_WEB);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
