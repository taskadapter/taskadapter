package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.apache.commons.codec.binary.Base64;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.taskadapter.license.LicenseManager.*;

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

        String licenseTypeStr = License.Type.getByCode(args[0]).getText();

        String customerName = "";
        String email = "";

        for(int i = 1; i < args.length; i++) {
            if (!args[i].contains("@")) {
                customerName += args[i] + " ";
            } else {
                email = args[i];
            }
        }

        customerName = customerName.trim();

        System.out.println("Generating license (" + licenseTypeStr + ") for:");
		System.out.println("Customer: " + customerName);
		System.out.println("Email:    " + email);

        String licenseText = generateLicenseText(Product.TASK_ADAPTER_WEB, licenseTypeStr, customerName, email);

		try {
			MyIOUtils.writeToFile(FILE_NAME_TA_WEB, licenseText);
			System.out.println("\nSaved to " + FILE_NAME_TA_WEB);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private static String generateLicenseText(Product productType, String licenseTypeStr, String customerName, String email) {
        String createdOn = new SimpleDateFormat(LICENSE_DATE_FORMAT).format(Calendar.getInstance().getTime());
        String key = chiper(licenseTypeStr + customerName + email + createdOn, PASSWORD);
		String base64EncodedKey = new String(Base64.encodeBase64(key.getBytes()));

        StringBuilder license = new StringBuilder()
            .append(PREFIX_PRODUCT).append(productType)
            .append(LINE_DELIMITER).append(PREFIX_LICENSE_TYPE).append(licenseTypeStr)
            .append(LINE_DELIMITER).append(PREFIX_REGISTERED_TO).append(customerName)
            .append(LINE_DELIMITER).append(PREFIX_EMAIL).append(email)
            .append(LINE_DELIMITER).append(PREFIX_DATE).append(createdOn)
            .append(LINE_DELIMITER).append(KEY_STR).append(base64EncodedKey);

		return license.toString();
	}
}
