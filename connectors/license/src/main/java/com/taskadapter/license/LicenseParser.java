package com.taskadapter.license;

import org.apache.commons.codec.binary.Base64;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.taskadapter.license.LicenseFormatDescriptor.*;

public final class LicenseParser {

    private static final int LICENSE_BODY_LINES_NUMBER = 7;
    private SimpleDateFormat licenseDateFormatter = new SimpleDateFormat(LICENSE_DATE_FORMAT);

    /**
     * @param licenseText license as text
     * @return the valid License object
     * @throws LicenseParseException if license can't be parsed. we don't check whether or not the license is expired here
     */
    public License parseLicense(String licenseText) throws LicenseParseException {
        if (licenseText == null) {
            throw new LicenseParseException("license body is NULL");
        }

        //---FORMAT START-----------
        //0: Product: TASK_ADAPTER_WEB
        //1: Users number: 5
        //2: Registered to: Alexey Skorokhodov
        //3: Email: mail@server.com
        //4: Date: 2010-07-25
        // .. expiration
        //5: -----Key-----
        //6: 12313123..........
        //---FORMAT END-------------

        String lines[] = licenseText.split("\\r?\\n");

        if (lines.length < LICENSE_BODY_LINES_NUMBER) {
            throw new LicenseParseException("Please provide the complete license text. License body must have " + LICENSE_BODY_LINES_NUMBER + " lines.");
        }

        String productName = lines[LINE_PRODUCT].substring(PREFIX_PRODUCT.length());

        LicenseManager.Product product;

        if (productName.equals(LicenseManager.Product.TASK_ADAPTER_WEB.toString())) {
            product = LicenseManager.Product.TASK_ADAPTER_WEB;
        } else {
            // we used to support Redmine API as a product with its own license. not anymore.
            throw new RuntimeException("Unknown product: " + productName);
        }

        String usersNumberStr = lines[LINE_USERS_NUMBER].substring(PREFIX_USERS_NUMBER.length());

        int usersNumber;
        try {
            usersNumber = Integer.parseInt(usersNumberStr);
        } catch (NumberFormatException e) {
            throw new LicenseParseException("Invalid users number in the license : " + usersNumberStr);
        }
        String customerName = lines[LINE_CUSTOMER_NAME].substring(PREFIX_REGISTERED_TO.length());
        String email = lines[LINE_EMAIL].substring(PREFIX_EMAIL.length());
        String createdOn = lines[LINE_CREATED_ON_DATE].substring(PREFIX_CREATED_ON.length());
        String expiresOnString = lines[LINE_EXPIRES_ON_DATE].substring(PREFIX_EXPIRES_ON.length());
        String key = lines[LINE_KEY];

        String decodedBase64Text = new String(Base64.decodeBase64(key.getBytes()));
        String xoredText = LicenseEncryptor.chiper(decodedBase64Text);
        String mergedStr = usersNumber + customerName + email + createdOn + expiresOnString;

        License license;

        if (mergedStr.equals(xoredText)) {
            Date expiresOn;
            try {
                expiresOn = licenseDateFormatter.parse(expiresOnString);
            } catch (ParseException e) {
                throw new LicenseParseException("Invalid license expiration date: " + expiresOnString + ". Valid format: " + LICENSE_DATE_FORMAT);
            }
            license = new License();
            license.setProduct(product);
            license.setCustomerName(customerName);
            license.setEmail(email);
            license.setUsersNumber(usersNumber);
            license.setCreatedOn(createdOn);
            license.setExpiresOn(expiresOn);
        } else {
            throw new LicenseParseException("License is not recognized");
        }

        return license;
    }

}
