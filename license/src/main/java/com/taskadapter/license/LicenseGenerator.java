package com.taskadapter.license;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.codec.binary.Base64;

import com.taskadapter.license.LicenseManager.PRODUCT;
import com.taskadapter.util.MyIOUtils;

public class LicenseGenerator {
	private static final String FILE_NAME_TA = "taskadapter.license";

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err
					.println("LicenseGenerator:\nExpected args: customerName email");
			return;
		}
		String customerName = args[0];
		String email = args[1];
		System.out.println("Generating license for:");
		System.out.println("Customer:" + customerName);
		System.out.println("Email:" + email);

		String licenseTAText = generateLicenseText(PRODUCT.TASK_ADAPTER,
				customerName, email);
		try {
			MyIOUtils.writeToFile(FILE_NAME_TA, licenseTAText);
			System.out.println("Saved: " + FILE_NAME_TA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String generateLicenseText(LicenseManager.PRODUCT type,
			String customerName, String email) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat f = new SimpleDateFormat(LicenseManager.LICENSE_DATE_FORMAT);
		String createdOn = f.format(c.getTime());
		String license = LicenseManager.PREFIX_PRODUCT + type
				+ LicenseManager.LINE_DELIMITER;
		license += LicenseManager.PREFIX_REGISTERED_TO + customerName;
		license += LicenseManager.LINE_DELIMITER + LicenseManager.PREFIX_EMAIL
				+ email;
		license += LicenseManager.LINE_DELIMITER + LicenseManager.PREFIX_DATE
				+ createdOn;
		String mergedStr = customerName + email + createdOn;

		String key = LicenseManager.chiper(mergedStr, LicenseManager.PASSWORD);
		String base64EncodedKey = new String(
				Base64.encodeBase64(key.getBytes()));

		license += LicenseManager.LINE_DELIMITER + LicenseManager.KEY_STR
				+ base64EncodedKey;
		return license;
	}

}
