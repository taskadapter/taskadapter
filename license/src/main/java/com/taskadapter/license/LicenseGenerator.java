package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.apache.commons.codec.binary.Base64;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.taskadapter.license.LicenseManager.*;

public class LicenseGenerator {
	private static final String FILE_NAME_TA = "taskadapter.license";

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("LicenseGenerator:\nExpected args: customerName email");
			return;
		}

		String customerName = args[0];
		String email = args[1];

        System.out.println("Generating license for:");
		System.out.println("Customer: " + customerName);
		System.out.println("Email:    " + email);

		String licenseTAText = generateLicenseText(Product.TASK_ADAPTER, customerName, email);

		try {
			MyIOUtils.writeToFile(FILE_NAME_TA, licenseTAText);
			System.out.println("Saved: " + FILE_NAME_TA);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private static String generateLicenseText(Product type, String customerName, String email) {
        String createdOn = new SimpleDateFormat(LICENSE_DATE_FORMAT).format(Calendar.getInstance().getTime());
        String key = chiper(customerName + email + createdOn, PASSWORD);
		String base64EncodedKey = new String(Base64.encodeBase64(key.getBytes()));

        StringBuilder license = new StringBuilder()
            .append(PREFIX_PRODUCT).append(type)
            .append(LINE_DELIMITER).append(PREFIX_REGISTERED_TO).append(customerName)
            .append(LINE_DELIMITER).append(PREFIX_EMAIL).append(email)
            .append(LINE_DELIMITER).append(PREFIX_DATE).append(createdOn)
            .append(LINE_DELIMITER).append(KEY_STR).append(base64EncodedKey);

		return license.toString();
	}
}
